package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SolarBlueDark
import com.example.ui.theme.SolarBluePrimary
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SolarGoldDark
import com.example.ui.theme.SolarGreenSecondary
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        DelightElectricalsApp()
      }
    }
  }
}

enum class AppLanguage(val code: String, val label: String) {
  EN("en", "English"),
  HI("hi", "हिंदी"),
  UR("ur", "اردو")
}

enum class NavigationTab {
  HOME,
  LEARN,
  CALC,
  CONTACT
}

data class TechSpecRow(
  val feature: String,
  val mono: String,
  val topCon: String
)

data class AppContent(
  val name: String,
  val welcome: String,
  val guideTitle: String,
  val avoid: String,
  val choose: String,
  val waareeTitle: String,
  val waareeDesc: String,
  val havellsTitle: String,
  val havellsDesc: String,
  val techHeader: String,
  val techRows: List<TechSpecRow>,
  val techSummary: String,
  val calcHeader: String,
  val billLabel: String,
  val calculateBtn: String,
  val resKw: String,
  val resSv: String,
  val note: String,
  val callText: String,
  val addrHeader: String,
  val nav: List<String>
)

val LocalizedData = mapOf(
  AppLanguage.EN to AppContent(
    name = "Arshad Ahamed Sheikh",
    welcome = "Expert Solar Solutions",
    guideTitle = "Vendor Choice Guide",
    avoid = "Avoid local vendors who use low-grade wiring and poor mounting to reduce costs. This leads to system failure and electrical hazards.",
    choose = "We provide high-quality insulation, professional wiring, and premium Tier-1 components to ensure your system lasts 25+ years.",
    waareeTitle = "Waaree Solar",
    waareeDesc = "As a premier Indian brand, Waaree modules are built for the harsh Khargone heat, offering the best performance warranty in the industry.",
    havellsTitle = "Havells Inverters",
    havellsDesc = "We trust Havells for their superior build quality and the fastest on-site service network in Madhya Pradesh.",
    techHeader = "Solar Technology",
    techRows = listOf(
      TechSpecRow("Efficiency", "19-21%", "22-24%"),
      TechSpecRow("Heat Rating", "Standard", "Excellent"),
      TechSpecRow("Service Life", "20 Years", "25+ Years")
    ),
    techSummary = "While Mono is popular, TOPCon technology generates more power during peak summers and low light conditions.",
    calcHeader = "Solar Savings",
    billLabel = "Monthly Bill (₹)",
    calculateBtn = "Calculate",
    resKw = "System Required:",
    resSv = "Estimated Savings:",
    note = "Note: This is a standard estimate. Actual requirements may vary based on your connected load and roof space.",
    callText = "Call Arshad Ahamed Sheikh",
    addrHeader = "Office Location",
    nav = listOf("HOME", "LEARN", "SAVINGS", "PROFILE")
  ),
  AppLanguage.HI to AppContent(
    name = "अरशद एहमद शेख",
    welcome = "विशेषज्ञ सोलर समाधान",
    guideTitle = "वेंडर चयन गाइड",
    avoid = "सस्ते के चक्कर में लोकल वायरिंग और घटिया फिटिंग करने वाले वेंडरों से बचें। इससे सिस्टम जल्दी खराब हो सकता है।",
    choose = "हम सर्वोत्तम गुणवत्ता वाले इंसुलेशन, प्रोफेशनल वायरिंग और प्रीमियम घटकों का उपयोग करते हैं ताकि आपका सिस्टम 25+ वर्षों तक चले।",
    waareeTitle = "वारी (Waaree) सोलर",
    waareeDesc = "Waaree भारत का सबसे भरोसेमंद ब्रांड है। इनके पैनल खरगोन की तेज गर्मी में भी बेहतरीन काम करते हैं।",
    havellsTitle = "हेवेल्स (Havells) इन्वर्टर",
    havellsDesc = "हम Havells की सलाह देते हैं क्योंकि उनकी सर्विस मध्य प्रदेश में सबसे तेज और इन्वर्टर की क्वालिटी लाजवाब है।",
    techHeader = "सोलर टेक्नोलॉजी",
    techRows = listOf(
      TechSpecRow("क्षमता", "19-21%", "22-24%"),
      TechSpecRow("गर्मी में काम", "सामान्य", "शानदार"),
      TechSpecRow("जीवन काल", "20 वर्ष", "25+ वर्ष")
    ),
    techSummary = "मोनो पैनल के मुकाबले TOPCon टेक्नोलॉजी गर्मियों और कम रोशनी में ज्यादा बिजली बनाती है।",
    calcHeader = "सोलर बचत कैलकुलेटर",
    billLabel = "मासिक बिजली बिल (₹)",
    calculateBtn = "गणना करें",
    resKw = "आवश्यक सोलर सिस्टम:",
    resSv = "अनुमानित मासिक बचत:",
    note = "नोट: यह एक मानक अनुमान है। वास्तविक आवश्यकता आपके लोड और जगह के अनुसार बदल सकती है।",
    callText = "कॉल करें: अरशद एहमद शेख",
    addrHeader = "कार्यालय का पता",
    nav = listOf("होम", "सीखें", "बचत", "प्रोफाइल")
  ),
  AppLanguage.UR to AppContent(
    name = "ارشاد احمد شیخ",
    welcome = "ماہر سولر سلوشنز",
    guideTitle = "وینڈر گائیڈ",
    avoid = "سستے داموں کے لیے گھٹیا وائرنگ اور لوکل فٹنگ استعمال کرنے والے وینڈرز سے بچیں۔",
    choose = "ہم بہترین کوالٹی کی وائرنگ اور بہتر سروس فراہم کرتے ہیں تاکہ آپ کا سسٹم 25 سال سے زائد چلے۔",
    waareeTitle = "واری (Waaree) سولر",
    waareeDesc = "واری ہندوستان کا بہترین برانڈ ہے جس کے پینلز کھرگون کی شدید گرمی برداشت کرنے کی صلاحیت رکھتے ہیں۔",
    havellsTitle = "ہیویلز (Havells) انورٹر",
    havellsDesc = "ہیویلز اپنی بہترین کوالٹی اور مدھیہ پردیش میں تیز ترین سروس کی وجہ سے جانا جاتا ہے۔",
    techHeader = "سولر ٹیکنالوجی",
    techRows = listOf(
      TechSpecRow("کارکردگی", "19-21%", "22-24%"),
      TechSpecRow("گرمی میں کام", "عام", "بہترین"),
      TechSpecRow("زندگی", "20 سال", "25+ سال")
    ),
    techSummary = "ٹاپ کون (TOPCon) ٹیکنالوجی مونو کے مقابلے میں گرمیوں میں زیادہ بجلی بناتی ہے۔",
    calcHeader = "سولر بچت",
    billLabel = "ماہانہ بل (₹)",
    calculateBtn = "حساب لگائیں",
    resKw = "ضروری سسٹم:",
    resSv = "ماہانہ بچت:",
    note = "نوٹ: یہ ایک تخمینہ ہے۔ اصل ضرورت لوڈ اور جگہ کے مطابق مختلف ہو سکتی ہے۔",
    callText = "کال: ارشاد احمد شیخ",
    addrHeader = "دفتر کا پتہ",
    nav = listOf("ہوم", "معلومات", "بچت", "پروفائل")
  )
)

const val PHONE_NUMBER = "9479452486"
const val MAPS_LINK = "https://share.google/NNyjdFnoARXirRbOt"
const val OFFICE_ADDRESS = "26, Tilak Path,\nNear Mohan Talkies, Khargone.\nMadhya Pradesh - 451001"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelightElectricalsApp() {
  val context = LocalContext.current
  var currentLanguage by remember { mutableStateOf(AppLanguage.EN) }
  var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

  // Modal dialog state
  var activeModalTitle by remember { mutableStateOf<String?>(null) }
  var activeModalDesc by remember { mutableStateOf<String?>(null) }

  val content = LocalizedData[currentLanguage] ?: LocalizedData[AppLanguage.EN]!!
  val isUrdu = currentLanguage == AppLanguage.UR
  val layoutDirection = if (isUrdu) LayoutDirection.Rtl else LayoutDirection.Ltr

  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color(0xFFF3F4F6),
      topBar = {
        TopBrandedHeader(
          currentLanguage = currentLanguage,
          onLanguageSelected = { currentLanguage = it }
        )
      },
      bottomBar = {
        BottomNavigationPanel(
          currentTab = currentTab,
          onTabSelected = { currentTab = it },
          navTitles = content.nav
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          when (currentTab) {
            NavigationTab.HOME -> HomeSection(
              content = content,
              onOpenWaaree = {
                activeModalTitle = content.waareeTitle
                activeModalDesc = content.waareeDesc
              },
              onOpenHavells = {
                activeModalTitle = content.havellsTitle
                activeModalDesc = content.havellsDesc
              },
              onQuickCall = { launchDial(context, PHONE_NUMBER) },
              onOpenCalculator = { currentTab = NavigationTab.CALC }
            )
            NavigationTab.LEARN -> LearnSection(content = content)
            NavigationTab.CALC -> CalculatorSection(
              content = content,
              onContactForQuote = { bill, kw, savings ->
                val msg = "Hello Arshad ji, I checked my solar estimate on Delight Electricals app for Monthly Bill of ₹$bill. Required: $kw kW (Estimated Monthly Savings: ₹$savings). Please share quote."
                launchWhatsApp(context, PHONE_NUMBER, msg)
              }
            )
            NavigationTab.CONTACT -> ContactSection(
              content = content,
              onCallClick = { launchDial(context, PHONE_NUMBER) },
              onWhatsAppClick = {
                val msg = "Hello Arshad ji, I would like to inquire about solar installation from Delight Electricals Khargone."
                launchWhatsApp(context, PHONE_NUMBER, msg)
              },
              onMapsClick = { launchUrl(context, MAPS_LINK) }
            )
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
      }

      // Brand details modal dialog
      if (activeModalTitle != null && activeModalDesc != null) {
        AlertDialog(
          onDismissRequest = {
            activeModalTitle = null
            activeModalDesc = null
          },
          shape = RoundedCornerShape(24.dp),
          containerColor = Color.White,
          title = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(SolarBluePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ElectricBolt,
                  contentDescription = null,
                  tint = SolarBluePrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Text(
                text = activeModalTitle!!,
                fontWeight = FontWeight.Bold,
                color = SolarBluePrimary,
                fontSize = 19.sp
              )
            }
          },
          text = {
            Text(
              text = activeModalDesc!!,
              color = Color(0xFF4B5563),
              fontSize = 14.sp,
              lineHeight = 22.sp
            )
          },
          confirmButton = {
            Button(
              onClick = {
                val brandName = activeModalTitle ?: "Solar"
                val msg = "Hello Arshad ji, I would like to learn more about $brandName installations by Delight Electricals Khargone."
                launchWhatsApp(context, PHONE_NUMBER, msg)
                activeModalTitle = null
                activeModalDesc = null
              },
              colors = ButtonDefaults.buttonColors(containerColor = SolarGreenSecondary),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Inquire via WhatsApp", fontWeight = FontWeight.Bold)
            }
          },
          dismissButton = {
            TextButton(
              onClick = {
                activeModalTitle = null
                activeModalDesc = null
              }
            ) {
              Text("Close", color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
            }
          }
        )
      }
    }
  }
}

@Composable
fun TopBrandedHeader(
  currentLanguage: AppLanguage,
  onLanguageSelected: (AppLanguage) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.White.copy(alpha = 0.96f))
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .shadow(elevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Branded Logo / Emblem
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(SolarBluePrimary, Color(0xFF1D4ED8)))
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = "Delight Electricals",
            tint = SolarGold,
            modifier = Modifier.size(26.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "DELIGHT ELECTRICALS",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SolarBluePrimary,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "Solar Power & Electrical Engineering • Khargone",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
          )
        }
      }

      // Language Switcher Pill
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(50.dp))
          .background(Color(0xFFF1F5F9))
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        AppLanguage.values().forEach { lang ->
          val isSelected = currentLanguage == lang
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(50.dp))
              .background(if (isSelected) SolarBluePrimary else Color.Transparent)
              .clickable { onLanguageSelected(lang) }
              .padding(vertical = 7.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = lang.label,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Color(0xFF64748B)
            )
          }
        }
      }
    }
  }
}

@Composable
fun HomeSection(
  content: AppContent,
  onOpenWaaree: () -> Unit,
  onOpenHavells: () -> Unit,
  onQuickCall: () -> Unit,
  onOpenCalculator: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    // Welcome & Full Name Title
    Text(
      text = content.welcome,
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = SolarBluePrimary,
      lineHeight = 30.sp
    )
    Text(
      text = content.name,
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF1D4ED8),
      modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    // Vendor Quality Guide Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFFEF3C7)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = Color(0xFFD97706),
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = content.guideTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color(0xFF1E293B)
          )
        }

        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        Spacer(modifier = Modifier.height(14.dp))

        // Avoid item
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(Color(0xFFFEE2E2)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Avoid",
              tint = Color(0xFFDC2626),
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = content.avoid,
            fontSize = 13.5.sp,
            color = Color(0xFF4B5563),
            lineHeight = 20.sp
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Choose item
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Choose",
              tint = Color(0xFF16A34A),
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = content.choose,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827),
            lineHeight = 20.sp
          )
        }
      }
    }

    // Brand Cards (Waaree & Havells)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Waaree Solar Card
      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onOpenWaaree() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WbSunny,
              contentDescription = "Waaree",
              tint = SolarBluePrimary,
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "WAAREE",
            fontWeight = FontWeight.ExtraBold,
            color = SolarBluePrimary,
            fontSize = 16.sp
          )
          Text(
            text = "Solar Modules",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "VIEW DETAILS →",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB),
            letterSpacing = 1.sp
          )
        }
      }

      // Havells Card
      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onOpenHavells() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color(0xFFF0FDF4)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ElectricBolt,
              contentDescription = "Havells",
              tint = SolarGreenSecondary,
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "HAVELLS",
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF166534),
            fontSize = 16.sp
          )
          Text(
            text = "Smart Inverters",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "VIEW DETAILS →",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF15803D),
            letterSpacing = 1.sp
          )
        }
      }
    }

    // Quick Action Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .clickable { onOpenCalculator() },
      colors = CardDefaults.cardColors(containerColor = SolarBluePrimary)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Calculate Your Solar Savings",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "See kW needed & estimated bill reduction",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
          )
        }
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Calculate,
            contentDescription = null,
            tint = Color.White
          )
        }
      }
    }
  }
}

@Composable
fun LearnSection(content: AppContent) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = content.techHeader,
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      color = SolarBluePrimary,
      modifier = Modifier.padding(bottom = 14.dp)
    )

    // Comparison Table Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Table Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(SolarBluePrimary)
            .padding(vertical = 12.dp, horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Feature",
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
          )
          Text(
            text = "Mono",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
          )
          Text(
            text = "TOPCon",
            modifier = Modifier
              .weight(1.1f)
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF1E40AF))
              .padding(vertical = 4.dp),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
          )
        }

        // Table Rows
        content.techRows.forEachIndexed { index, row ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(if (index % 2 == 0) Color.White else Color(0xFFF8FAFC))
              .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = row.feature,
              modifier = Modifier.weight(1.2f),
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF475569),
              fontSize = 13.5.sp
            )
            Text(
              text = row.mono,
              modifier = Modifier.weight(1f),
              color = Color(0xFF334155),
              fontSize = 13.5.sp,
              textAlign = TextAlign.Center
            )
            Text(
              text = row.topCon,
              modifier = Modifier.weight(1.1f),
              fontWeight = FontWeight.Bold,
              color = SolarBluePrimary,
              fontSize = 14.sp,
              textAlign = TextAlign.Center
            )
          }
          if (index < content.techRows.size - 1) {
            HorizontalDivider(color = Color(0xFFF1F5F9))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Tech Summary Quote Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = SolarBluePrimary,
          modifier = Modifier
            .size(20.dp)
            .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = content.techSummary,
          fontSize = 13.sp,
          color = Color(0xFF1E3A8A),
          lineHeight = 20.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Why Delight Electricals in Khargone
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Text(
          text = "Khargone Climate Advantage",
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F172A),
          fontSize = 16.sp,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
          text = "Khargone experiences high temperatures during summer. TOPCon panels feature a superior temperature coefficient (-0.30%/°C vs -0.35%/°C on standard Mono), meaning your Delight Electricals installation generates up to 10% more power when you need ACs the most.",
          fontSize = 13.sp,
          color = Color(0xFF64748B),
          lineHeight = 19.sp
        )
      }
    }
  }
}

@Composable
fun CalculatorSection(
  content: AppContent,
  onContactForQuote: (bill: String, kw: String, savings: String) -> Unit
) {
  val focusManager = LocalFocusManager.current
  var billInput by remember { mutableStateOf("") }
  var calculatedKw by remember { mutableStateOf<String?>(null) }
  var calculatedSavings by remember { mutableStateOf<String?>(null) }
  var annualSavings by remember { mutableStateOf<String?>(null) }
  var unitsEstimate by remember { mutableStateOf<Int?>(null) }

  fun executeCalculation(billText: String) {
    val bill = billText.toDoubleOrNull()
    if (bill != null && bill > 0) {
      // Standardized formula: ₹8/unit, 120 units/month per 1kW
      val units = (bill / 8.0).toInt()
      val kwValue = (bill / 8.0) / 120.0
      val kwFormatted = String.format(Locale.US, "%.1f", kwValue)
      val savingsValue = (bill * 0.9).toInt()
      val annualVal = savingsValue * 12

      unitsEstimate = units
      calculatedKw = kwFormatted
      calculatedSavings = savingsValue.toString()
      annualSavings = annualVal.toString()
    } else {
      calculatedKw = null
      calculatedSavings = null
      annualSavings = null
      unitsEstimate = null
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp)
      ) {
        Text(
          text = content.calcHeader,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = SolarBluePrimary,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
          text = content.billLabel,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF64748B),
          letterSpacing = 1.sp,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        // Bill input field
        OutlinedTextField(
          value = billInput,
          onValueChange = {
            billInput = it.filter { char -> char.isDigit() }
            if (billInput.isNotEmpty()) {
              executeCalculation(billInput)
            } else {
              calculatedKw = null
            }
          },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text("e.g. 2400", color = Color(0xFF94A3B8), fontSize = 18.sp) },
          leadingIcon = {
            Text(
              "₹",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SolarBluePrimary,
              modifier = Modifier.padding(start = 12.dp)
            )
          },
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
          ),
          keyboardActions = KeyboardActions(
            onDone = {
              focusManager.clearFocus()
              executeCalculation(billInput)
            }
          ),
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SolarBluePrimary,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedContainerColor = Color(0xFFF8FAFC),
            unfocusedContainerColor = Color(0xFFF8FAFC)
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Preset Bill Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("1500", "2500", "4000", "6000").forEach { amount ->
            SuggestionChip(
              onClick = {
                billInput = amount
                focusManager.clearFocus()
                executeCalculation(amount)
              },
              label = { Text("₹$amount", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
              colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = if (billInput == amount) SolarBluePrimary.copy(alpha = 0.1f) else Color(0xFFF1F5F9),
                labelColor = if (billInput == amount) SolarBluePrimary else Color(0xFF475569)
              ),
              border = null,
              shape = RoundedCornerShape(10.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calculate Button
        Button(
          onClick = {
            focusManager.clearFocus()
            executeCalculation(billInput)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SolarBluePrimary)
        ) {
          Icon(Icons.Default.Calculate, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = content.calculateBtn.uppercase(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
        }

        // Result Card (Standardized Calculation)
        AnimatedVisibility(
          visible = calculatedKw != null,
          enter = fadeIn() + slideInVertically()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 20.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(Color(0xFFF0FDF4))
              .border(1.dp, Color(0xFFDCFCE7), RoundedCornerShape(18.dp))
              .padding(18.dp)
          ) {
            // System Size
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = content.resKw,
                color = Color(0xFF4B5563),
                fontWeight = FontWeight.Medium,
                fontSize = 14.5.sp
              )
              Text(
                text = "${calculatedKw ?: 0} kW",
                color = SolarBluePrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Monthly Savings
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = content.resSv,
                color = Color(0xFF4B5563),
                fontWeight = FontWeight.Medium,
                fontSize = 14.5.sp
              )
              Text(
                text = "₹${calculatedSavings ?: 0}",
                color = Color(0xFF15803D),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Annual Savings
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Annual Estimated Savings:",
                color = Color(0xFF6B7280),
                fontSize = 13.sp
              )
              Text(
                text = "₹${annualSavings ?: 0}/yr",
                color = Color(0xFF166534),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFDCFCE7), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // PM Surya Ghar subsidy badge
            val kwNum = calculatedKw?.toDoubleOrNull() ?: 1.0
            val subsidyText = when {
              kwNum <= 1.5 -> "Central Subsidy up to ₹30,000"
              kwNum <= 2.5 -> "Central Subsidy up to ₹60,000"
              else -> "Central Subsidy up to ₹78,000 (PM Surya Ghar)"
            }
            Text(
              text = "⚡ $subsidyText",
              color = Color(0xFF047857),
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Calculator Note
            Text(
              text = content.note,
              fontSize = 11.sp,
              color = Color(0xFFDC2626),
              lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Send Quote Button
            Button(
              onClick = {
                onContactForQuote(
                  billInput,
                  calculatedKw ?: "",
                  calculatedSavings ?: ""
                )
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SolarGreenSecondary)
            ) {
              Text("Get Quote for ${calculatedKw} kW on WhatsApp", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun ContactSection(
  content: AppContent,
  onCallClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
  onMapsClick: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Avatar / Logo Badge
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(SolarBluePrimary, Color(0xFF1E40AF)))
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(38.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Delight Electricals",
          fontSize = 24.sp,
          fontWeight = FontWeight.ExtraBold,
          color = SolarBluePrimary
        )

        Text(
          text = content.name,
          fontSize = 17.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF2563EB),
          modifier = Modifier.padding(top = 4.dp, bottom = 22.dp)
        )

        // Call Button
        Button(
          onClick = onCallClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SolarGreenSecondary)
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call",
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = content.callText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // WhatsApp Chat Button
        Button(
          onClick = onWhatsAppClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "WhatsApp",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Chat on WhatsApp (9479452486)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Office Location Card (Interactive Google Maps Link)
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onMapsClick() },
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = content.addrHeader.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = OFFICE_ADDRESS,
              fontSize = 14.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF1E293B),
              textAlign = TextAlign.Center,
              lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Open in Google Maps",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("↗", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun BottomNavigationPanel(
  currentTab: NavigationTab,
  onTabSelected: (NavigationTab) -> Unit,
  navTitles: List<String>
) {
  NavigationBar(
    containerColor = Color.White,
    tonalElevation = 8.dp
  ) {
    val items = listOf(
      Triple(NavigationTab.HOME, Icons.Default.Home, navTitles.getOrNull(0) ?: "HOME"),
      Triple(NavigationTab.LEARN, Icons.Default.MenuBook, navTitles.getOrNull(1) ?: "LEARN"),
      Triple(NavigationTab.CALC, Icons.Default.Calculate, navTitles.getOrNull(2) ?: "SAVINGS"),
      Triple(NavigationTab.CONTACT, Icons.Default.Person, navTitles.getOrNull(3) ?: "PROFILE")
    )

    items.forEach { (tab, icon, title) ->
      val isSelected = currentTab == tab
      NavigationBarItem(
        selected = isSelected,
        onClick = { onTabSelected(tab) },
        icon = {
          Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp)
          )
        },
        label = {
          Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = SolarBluePrimary,
          selectedTextColor = SolarBluePrimary,
          indicatorColor = SolarBluePrimary.copy(alpha = 0.12f),
          unselectedIconColor = Color(0xFF94A3B8),
          unselectedTextColor = Color(0xFF94A3B8)
        )
      )
    }
  }
}

fun launchDial(context: Context, phone: String) {
  try {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    context.startActivity(intent)
  } catch (e: Exception) {
    // Fallback
  }
}

fun launchUrl(context: Context, url: String) {
  try {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
  } catch (e: Exception) {
    // Fallback
  }
}

fun launchWhatsApp(context: Context, phone: String, message: String) {
  try {
    val url = "https://api.whatsapp.com/send?phone=91$phone&text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
  } catch (e: Exception) {
    // If WhatsApp is not installed, fallback to normal dial or web
    launchDial(context, phone)
  }
}
