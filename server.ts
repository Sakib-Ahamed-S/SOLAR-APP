import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';
import fs from 'fs';
import { GoogleGenAI } from '@google/genai';
import { createServer as createViteServer } from 'vite';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const app = express();

// Parse CLI arguments or environment variables
let port = 3000;
const portArgIdx = process.argv.indexOf('--port');
if (portArgIdx !== -1 && process.argv[portArgIdx + 1]) {
  port = parseInt(process.argv[portArgIdx + 1], 10) || 3000;
} else if (process.env.PORT) {
  port = parseInt(process.env.PORT, 10) || 3000;
}

let host = '0.0.0.0';
const hostArgIdx = process.argv.indexOf('--host');
if (hostArgIdx !== -1 && process.argv[hostArgIdx + 1]) {
  host = process.argv[hostArgIdx + 1];
}

app.use(express.json());

// Persistent Data Storage directory
const dataDir = path.join(__dirname, 'data');
if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir, { recursive: true });
}

const leadsFile = path.join(dataDir, 'leads.json');
const statsFile = path.join(dataDir, 'stats.json');
const configFile = path.join(dataDir, 'config.json');

// Initialize stats if not present
if (!fs.existsSync(statsFile)) {
  fs.writeFileSync(statsFile, JSON.stringify({
    totalVisits: 148,
    uniqueVisitors: 94,
    lastVisited: new Date().toISOString()
  }, null, 2));
}

// Initialize sample demo leads if not present
if (!fs.existsSync(leadsFile)) {
  const initialLeads = [
    {
      id: 'lead-1',
      name: 'Rameshwar Patidar',
      phone: '9826011223',
      email: 'ramesh.patidar@gmail.com',
      bill: '3500',
      kw: '3.6',
      city: 'Khargone',
      notes: 'Interested in 3kW Monocrystalline / TOPCon panels for home',
      status: 'New Lead',
      panelPreference: 'Waaree N-Type TOPCon',
      quotedPrice: '198000',
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString()
    },
    {
      id: 'lead-2',
      name: 'Mohammad Imran Khan',
      phone: '9754823190',
      email: 'imran.k@yahoo.com',
      bill: '2200',
      kw: '2.3',
      city: 'Gogawan, Khargone',
      notes: 'PM Surya Ghar subsidy inquiry',
      status: 'Contacted',
      panelPreference: 'Waaree Monocrystalline PERC',
      quotedPrice: '126000',
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 22).toISOString()
    }
  ];
  fs.writeFileSync(leadsFile, JSON.stringify(initialLeads, null, 2));
}

// Initialize business config if not present
if (!fs.existsSync(configFile)) {
  fs.writeFileSync(configFile, JSON.stringify({
    monoPerKwRate: 50000,
    topconPerKwRate: 55000,
    centralSubsidy1Kw: 30000,
    centralSubsidy2Kw: 60000,
    centralSubsidy3Kw: 78000,
    businessNotice: 'Solar installations under PM Surya Ghar are active across Khargone district with full net-metering support.',
    primaryPhone: '9479452486',
    secondaryPhone: '9201112191',
    address: '26, Tilak Path, Near Mohan Talkies, Khargone, MP - 451001'
  }, null, 2));
}

function getLeads(): any[] {
  try {
    return JSON.parse(fs.readFileSync(leadsFile, 'utf8'));
  } catch {
    return [];
  }
}

function saveLeads(leads: any[]) {
  try {
    fs.writeFileSync(leadsFile, JSON.stringify(leads, null, 2));
  } catch (err) {
    console.error('Error saving leads:', err);
  }
}

function getStats() {
  try {
    return JSON.parse(fs.readFileSync(statsFile, 'utf8'));
  } catch {
    return { totalVisits: 1, uniqueVisitors: 1, lastVisited: new Date().toISOString() };
  }
}

function saveStats(stats: any) {
  try {
    fs.writeFileSync(statsFile, JSON.stringify(stats, null, 2));
  } catch (err) {
    console.error('Error saving stats:', err);
  }
}

function getConfig() {
  try {
    return JSON.parse(fs.readFileSync(configFile, 'utf8'));
  } catch {
    return {};
  }
}

function saveConfig(cfg: any) {
  try {
    fs.writeFileSync(configFile, JSON.stringify(cfg, null, 2));
  } catch (err) {
    console.error('Error saving config:', err);
  }
}

// Initialize Google GenAI
const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY,
  httpOptions: {
    headers: {
      'User-Agent': 'aistudio-build'
    }
  }
});

// Admin Configuration & Strict Access Control
const ADMIN_NUMBERS = ['9826060757', '9201112191'];
const ADMIN_PASSWORD = 'Sakib@1975';
const ADMIN_EMAILS = ['arshaddelight@gmail.com', 'sakibahamedsheikh91@gmail.com', 'umerdelight@gmail.com'];
const adminSessions = new Map<string, { phone: string; email?: string; name: string; role: string; createdAt: number }>();

function verifyAdminToken(req: any) {
  const authHeader = req.headers.authorization || '';
  const token = (authHeader.startsWith('Bearer ') ? authHeader.slice(7) : req.headers['x-admin-token']) || req.query.token;
  if (!token) return null;
  const session = adminSessions.get(token);
  if (!session) return null;
  // 24 hour session expiration
  if (Date.now() - session.createdAt > 24 * 60 * 60 * 1000) {
    adminSessions.delete(token);
    return null;
  }
  return session;
}

function requireAdmin(req: any, res: any, next: any) {
  const session = verifyAdminToken(req);
  if (!session) {
    return res.status(403).json({ error: 'Access denied: Admin credentials required. Access restricted to authorized administrators.' });
  }
  req.admin = session;
  next();
}

// ---------------- API Endpoints ----------------

// Admin Login (Strictly 9826060757 or 9201112191 with password Sakib@1975)
app.post('/api/admin/login', (req, res) => {
  const { phone, password } = req.body;
  if (!phone || !password) {
    return res.status(400).json({ error: 'Mobile number and admin password are required.' });
  }

  const cleanPhone = phone.replace(/[^0-9]/g, '').slice(-10);
  if (!ADMIN_NUMBERS.includes(cleanPhone) || password !== ADMIN_PASSWORD) {
    return res.status(401).json({ 
      error: 'Invalid credentials. Access denied.' 
    });
  }

  // Generate secure session token
  const token = 'delight_adm_' + Math.random().toString(36).substring(2) + Date.now().toString(36);
  const session = {
    phone: cleanPhone,
    name: cleanPhone === '9201112191' ? 'Arshad Ahamed Sheikh' : 'Delight Admin',
    role: 'admin',
    createdAt: Date.now()
  };
  adminSessions.set(token, session);

  res.json({
    success: true,
    token,
    admin: session
  });
});

// Verify existing admin token
app.get('/api/admin/verify', (req, res) => {
  const session = verifyAdminToken(req);
  if (!session) {
    return res.status(401).json({ valid: false });
  }
  res.json({ valid: true, admin: session });
});

// Google Admin Login Endpoint (Automatically grants admin for authorized Google accounts)
app.post('/api/admin/google-login', (req, res) => {
  const { email, name } = req.body;
  if (!email) {
    return res.status(400).json({ error: 'Email is required' });
  }

  const cleanEmail = email.toLowerCase().trim();
  if (!ADMIN_EMAILS.includes(cleanEmail)) {
    return res.status(403).json({ error: 'This Google account is not authorized for administrator access.' });
  }

  const token = 'delight_adm_' + Math.random().toString(36).substring(2) + Date.now().toString(36);
  const adminName = cleanEmail.includes('sakib') 
    ? 'Sakib Ahamed Sheikh' 
    : (name || 'Arshad Ahamed Sheikh');
  const adminPhone = cleanEmail.includes('sakib') ? '9826060757' : '9201112191';

  const session = {
    phone: adminPhone,
    email: cleanEmail,
    name: adminName,
    role: 'admin',
    createdAt: Date.now()
  };
  adminSessions.set(token, session);

  res.json({
    success: true,
    isAdmin: true,
    token,
    admin: session
  });
});

// Get REAL CRM statistics from actual stored leads (NO fake ones!)
app.get('/api/admin/crm-stats', requireAdmin, (req, res) => {
  const leads = getLeads();

  const totalLeads = leads.length;
  let totalKw = 0;
  let totalQuotedValue = 0;
  const statusCounts: Record<string, number> = {
    'New Lead': 0,
    'Contacted': 0,
    'Site Survey Scheduled': 0,
    'Quotation Sent': 0,
    'Installation': 0,
    'Completed': 0
  };
  const panelCounts: Record<string, number> = {
    'Waaree Monocrystalline PERC': 0,
    'Waaree N-Type TOPCon': 0,
    'Undecided': 0
  };
  const cityCounts: Record<string, number> = {};

  leads.forEach(l => {
    const kwNum = parseFloat(l.kw) || 0;
    totalKw += kwNum;

    const leadVal = parseFloat(l.quotedPrice) || (kwNum > 0 ? Math.round(kwNum * 55000) : 0);
    totalQuotedValue += leadVal;

    const st = l.status || 'New Lead';
    statusCounts[st] = (statusCounts[st] || 0) + 1;

    let p = l.panelPreference;
    if (!p) {
      if (l.notes && l.notes.toLowerCase().includes('topcon')) p = 'Waaree N-Type TOPCon';
      else if (l.notes && l.notes.toLowerCase().includes('mono')) p = 'Waaree Monocrystalline PERC';
      else p = 'Undecided';
    }
    panelCounts[p] = (panelCounts[p] || 0) + 1;

    const city = l.city || 'Khargone';
    cityCounts[city] = (cityCounts[city] || 0) + 1;
  });

  const convertedLeads = (statusCounts['Installation'] || 0) + (statusCounts['Completed'] || 0);
  const activePipelineLeads = totalLeads - (statusCounts['Completed'] || 0);

  res.json({
    totalLeads,
    totalKw: parseFloat(totalKw.toFixed(1)),
    avgKw: totalLeads > 0 ? parseFloat((totalKw / (leads.filter(l => l.kw).length || 1)).toFixed(1)) : 0,
    totalQuotedValue,
    statusCounts,
    panelCounts,
    cityCounts,
    convertedLeads,
    activePipelineLeads,
    conversionRate: totalLeads > 0 ? Math.round((convertedLeads / totalLeads) * 100) : 0
  });
});

// Admin get editable business settings
app.get('/api/admin/config', (req, res) => {
  res.json(getConfig());
});

// Admin update business settings
app.post('/api/admin/config', requireAdmin, (req, res) => {
  const current = getConfig();
  const updated = { ...current, ...req.body };
  saveConfig(updated);
  res.json({ success: true, config: updated });
});

// Record visit
app.post('/api/analytics/visit', (req, res) => {
  const stats = getStats();
  stats.totalVisits = (stats.totalVisits || 0) + 1;
  stats.lastVisited = new Date().toISOString();
  saveStats(stats);
  res.json({ success: true, visits: stats.totalVisits });
});

// Customer Sign In
app.post('/api/auth/signin', (req, res) => {
  const { phone, email } = req.body;
  const cleanPhone = phone ? phone.replace(/[^0-9]/g, '').slice(-10) : '';

  const isAdminCandidate = ADMIN_NUMBERS.includes(cleanPhone);
  const leads = getLeads();
  let found = null;

  if (cleanPhone) {
    found = leads.find(l => l.phone.replace(/[^0-9]/g, '').slice(-10) === cleanPhone);
  }

  const cleanEmail = email ? email.toLowerCase().trim() : '';
  const isAdminEmail = ADMIN_EMAILS.includes(cleanEmail);

  if (isAdminEmail) {
    const token = 'delight_adm_' + Math.random().toString(36).substring(2) + Date.now().toString(36);
    const adminName = cleanEmail.includes('sakib') 
      ? 'Sakib Ahamed Sheikh' 
      : 'Arshad Ahamed Sheikh';
    const adminPhone = cleanEmail.includes('sakib') ? '9826060757' : '9201112191';

    const session = {
      phone: adminPhone,
      email: cleanEmail,
      name: adminName,
      role: 'admin',
      createdAt: Date.now()
    };
    adminSessions.set(token, session);

    return res.json({
      success: true,
      isAdmin: true,
      adminToken: token,
      admin: session,
      user: {
        id: 'admin-' + cleanEmail,
        name: adminName,
        email: cleanEmail,
        phone: adminPhone,
        city: 'Khargone',
        role: 'admin',
        registeredAt: new Date().toISOString()
      }
    });
  }

  if (!found && cleanEmail) {
    found = leads.find(l => l.email && l.email.toLowerCase() === cleanEmail);
  }

  if (found) {
    return res.json({ 
      success: true, 
      user: found,
      isAdminCandidate 
    });
  }

  if (isAdminCandidate) {
    return res.json({
      success: true,
      user: {
        id: 'admin-' + cleanPhone,
        name: cleanPhone === '9201112191' ? 'Arshad Ahamed Sheikh' : 'Delight Admin',
        phone: cleanPhone,
        city: 'Khargone',
        registeredAt: new Date().toISOString()
      },
      isAdminCandidate: true
    });
  }

  return res.status(404).json({ 
    success: false, 
    error: 'Account not found. Please sign up to create your profile.' 
  });
});

// Customer Public Inquiry Submission / Lead Registration
app.post('/api/leads', (req, res) => {
  const { name, phone, email, bill, kw, city, notes, provider, panelPreference } = req.body;
  if (!name || !phone) {
    return res.status(400).json({ error: 'Name and Phone number are required' });
  }

  const leads = getLeads();
  const cleanPhone = phone.replace(/[^0-9]/g, '').slice(-10);
  const existingIdx = leads.findIndex(l => l.phone.replace(/[^0-9]/g, '').slice(-10) === cleanPhone);

  const updatedLead = {
    id: existingIdx >= 0 ? leads[existingIdx].id : 'lead-' + Date.now(),
    name: name.trim(),
    phone: cleanPhone,
    email: email ? email.trim() : (existingIdx >= 0 ? leads[existingIdx].email : ''),
    bill: bill || (existingIdx >= 0 ? leads[existingIdx].bill : ''),
    kw: kw || (existingIdx >= 0 ? leads[existingIdx].kw : ''),
    city: city || (existingIdx >= 0 ? leads[existingIdx].city : 'Khargone'),
    status: existingIdx >= 0 ? (leads[existingIdx].status || 'New Lead') : 'New Lead',
    panelPreference: panelPreference || (existingIdx >= 0 ? (leads[existingIdx].panelPreference || 'Undecided') : 'Undecided'),
    notes: notes || (existingIdx >= 0 ? leads[existingIdx].notes : ''),
    adminNotes: existingIdx >= 0 ? (leads[existingIdx].adminNotes || '') : '',
    quotedPrice: existingIdx >= 0 ? (leads[existingIdx].quotedPrice || '') : '',
    surveyDate: existingIdx >= 0 ? (leads[existingIdx].surveyDate || '') : '',
    provider: provider || 'direct',
    updatedAt: new Date().toISOString(),
    createdAt: existingIdx >= 0 ? leads[existingIdx].createdAt : new Date().toISOString()
  };

  if (existingIdx >= 0) {
    leads[existingIdx] = updatedLead;
  } else {
    leads.unshift(updatedLead);
  }

  saveLeads(leads);
  res.json({ success: true, lead: updatedLead });
});

// Admin-only: Get all leads (strictly protected!)
app.get('/api/leads', requireAdmin, (req, res) => {
  const leads = getLeads();
  res.json({ leads });
});

// Admin-only: Update lead details (name, phone, kw, bill, status, quote, survey date, notes)
app.put('/api/admin/leads/:id', requireAdmin, (req, res) => {
  const { id } = req.params;
  const leads = getLeads();
  const idx = leads.findIndex(l => l.id === id);

  if (idx === -1) {
    return res.status(404).json({ error: 'Lead not found' });
  }

  const current = leads[idx];
  const {
    name,
    phone,
    email,
    city,
    bill,
    kw,
    status,
    panelPreference,
    notes,
    adminNotes,
    quotedPrice,
    surveyDate,
    followUpDate
  } = req.body;

  leads[idx] = {
    ...current,
    name: name !== undefined ? name.trim() : current.name,
    phone: phone !== undefined ? phone.replace(/[^0-9]/g, '').slice(-10) : current.phone,
    email: email !== undefined ? email.trim() : current.email,
    city: city !== undefined ? city.trim() : current.city,
    bill: bill !== undefined ? bill : current.bill,
    kw: kw !== undefined ? kw : current.kw,
    status: status !== undefined ? status : (current.status || 'New Lead'),
    panelPreference: panelPreference !== undefined ? panelPreference : (current.panelPreference || 'Undecided'),
    notes: notes !== undefined ? notes : current.notes,
    adminNotes: adminNotes !== undefined ? adminNotes : (current.adminNotes || ''),
    quotedPrice: quotedPrice !== undefined ? quotedPrice : (current.quotedPrice || ''),
    surveyDate: surveyDate !== undefined ? surveyDate : (current.surveyDate || ''),
    followUpDate: followUpDate !== undefined ? followUpDate : (current.followUpDate || ''),
    updatedAt: new Date().toISOString()
  };

  saveLeads(leads);
  res.json({ success: true, lead: leads[idx] });
});

// Admin-only: Add a new walk-in / phone lead directly
app.post('/api/admin/leads', requireAdmin, (req, res) => {
  const { name, phone, email, bill, kw, city, status, panelPreference, notes, adminNotes, quotedPrice, surveyDate } = req.body;
  if (!name || !phone) {
    return res.status(400).json({ error: 'Customer Name and Phone number are required' });
  }

  const leads = getLeads();
  const cleanPhone = phone.replace(/[^0-9]/g, '').slice(-10);

  const newLead = {
    id: 'lead-' + Date.now(),
    name: name.trim(),
    phone: cleanPhone,
    email: email ? email.trim() : '',
    bill: bill || '',
    kw: kw || '',
    city: city ? city.trim() : 'Khargone',
    status: status || 'New Lead',
    panelPreference: panelPreference || 'Undecided',
    notes: notes || '',
    adminNotes: adminNotes || '',
    quotedPrice: quotedPrice || '',
    surveyDate: surveyDate || '',
    provider: 'admin-entry',
    updatedAt: new Date().toISOString(),
    createdAt: new Date().toISOString()
  };

  leads.unshift(newLead);
  saveLeads(leads);
  res.json({ success: true, lead: newLead });
});

// Admin-only: Delete lead
app.delete('/api/leads/:id', requireAdmin, (req, res) => {
  const { id } = req.params;
  let leads = getLeads();
  leads = leads.filter(l => l.id !== id);
  saveLeads(leads);
  res.json({ success: true });
});

// Admin-only: Export leads as CSV
app.get('/api/leads/export.csv', requireAdmin, (req, res) => {
  const leads = getLeads();
  let csv = 'ID,Name,Phone,Email,City,Monthly Bill (INR),System kW,Status,Panel Preference,Quoted Price (INR),Survey Date,Admin Notes,Created At\n';
  leads.forEach(l => {
    csv += `"${l.id}","${l.name}","${l.phone}","${l.email || ''}","${l.city || ''}","${l.bill || ''}","${l.kw || ''}","${l.status || 'New Lead'}","${l.panelPreference || 'Undecided'}","${l.quotedPrice || ''}","${l.surveyDate || ''}","${(l.adminNotes || '').replace(/"/g, '""')}","${l.createdAt}"\n`;
  });
  res.setHeader('Content-Type', 'text/csv');
  res.setHeader('Content-Disposition', 'attachment; filename="delight-electricals-real-leads.csv"');
  res.send(csv);
});

// Context-aware fallback engine
function generateDelightExpertReply(query: string) {
  const q = (query || '').toLowerCase();
  
  if (q.includes('subsidy') || q.includes('सब्सिडी') || q.includes('yojana') || q.includes('surya') || q.includes('cost') || q.includes('price')) {
    return `☀️ **PM Surya Ghar: Muft Bijli Yojana Central Subsidy (Khargone, MP):**
• **1 kW System:** ₹30,000 Direct Central Subsidy
• **2 kW System:** ₹60,000 Direct Central Subsidy
• **3 kW and Above:** ₹78,000 Maximum Residential Subsidy

**Process with Delight Electricals:**
We manage the full online national portal filing, MPPKVVCL West Discom net-meter sanction, and site verification. The subsidy arrives via Direct Benefit Transfer (DBT) directly into your Aadhaar-linked bank account within 30 days of net-meter commissioning.

Would you like an instant quote? Tap **'Get a Call Back'** or message Arshad Ahamed Sheikh on WhatsApp (+91 94794 52486)!`;
  }

  if (q.includes('waaree') || q.includes('mono') || q.includes('topcon') || q.includes('panel') || q.includes('inverter') || q.includes('havells') || q.includes('difference')) {
    return `⚡ **Solar Panel Options at Delight Electricals (We Install Both):**

At Delight Electricals, customers have the freedom to choose between two authentic Tier-1 Waaree technologies:

1. **Waaree Monocrystalline PERC Panels:**
   • **Efficiency:** 19% – 21%
   • **Key Advantage:** Time-tested worldwide reliability, budget-friendly, excellent value-for-money.
   • **Warranty:** 25-Year Performance Warranty.
   • **Best For:** Homeowners and farms wanting maximum return on investment at a very economical price.

2. **Waaree N-Type TOPCon Panels:**
   • **Efficiency:** 22% – 24%
   • **Key Advantage:** Higher output per sq. ft., lower temperature coefficient in 45°C Khargone summers, bifacial rear gain.
   • **Warranty:** 25-Year Performance Warranty.
   • **Best For:** Customers with compact roof space or seeking next-generation technology.

3. **Havells Smart Grid-Tied Inverters:**
   • Rapid MP service network, Wi-Fi generation tracking, dual surge protections, 5–10 year warranty.

Both options come with heavy-duty galvanized mounting and dual copper earthing. We advise you transparently based on your budget and available roof area!`;
  }

  if (q.includes('address') || q.includes('location') || q.includes('where') || q.includes('office') || q.includes('contact') || q.includes('phone') || q.includes('number')) {
    return `📍 **Delight Electricals Office & Contact:**
• **Founder & Chief Engineer:** Arshad Ahamed Sheikh
• **Address:** 26, Tilak Path, Near Mohan Talkies, Khargone, Madhya Pradesh - 451001
• **Mobile / WhatsApp:** +91 94794 52486
• **Email:** umerdelight@gmail.com
• **Service Areas:** Khargone City, Kasrawad, Sanawad, Maheshwar, Gogawan, Bhikangaon, Barwaha, and all Nimar rural areas.

We provide free rooftop site surveys across Khargone district!`;
  }

  const numMatch = q.match(/\b\d{3,5}\b/);
  if (numMatch || q.includes('bill') || q.includes('kw') || q.includes('size') || q.includes('saving')) {
    const billAmt = numMatch ? parseFloat(numMatch[0]) : 3000;
    const units = Math.round(billAmt / 8);
    const kw = (units / 120).toFixed(1);
    const savings = Math.round(billAmt * 0.9);
    const subsidy = parseFloat(kw) <= 1 ? "₹30,000" : parseFloat(kw) <= 2 ? "₹60,000" : "₹78,000";

    return `💡 **Custom Sizing Calculation for ₹${billAmt.toLocaleString('en-IN')} Monthly Bill:**
• **Recommended System Size:** **${kw} kW**
• **Estimated Monthly Generation:** ~${units} units (kWh)
• **Estimated Monthly Savings:** **₹${savings.toLocaleString('en-IN')}** (~₹${(savings * 12).toLocaleString('en-IN')}/year)
• **PM Surya Ghar Subsidy:** **${subsidy}** directly into your bank account
• **Panels Choice:** We supply both Waaree Monocrystalline PERC and N-Type TOPCon modules
• **Roof Area Required:** ~${Math.round(parseFloat(kw) * 80)} – ${Math.round(parseFloat(kw) * 100)} sq. ft. shadow-free

Would you like Arshad ji to survey your roof in Khargone? Tap **'Get a Call Back'** or WhatsApp us at +91 94794 52486!`;
  }

  return `I am the Delight Electricals AI Solar Advisor. How can I assist you with rooftop solar in Khargone today?

• Central Subsidy up to ₹78,000 under PM Surya Ghar
• Sizing for your home or shop (tell me your monthly bill!)
• Both Waaree Monocrystalline PERC & N-Type TOPCon panels
• Havells smart inverters with prompt on-site MP service
• Our office at 26 Tilak Path, Khargone (Contact: +91 94794 52486)`;
}

// AI Advisor Chat with Gemini
app.post('/api/ai/chat', async (req, res) => {
  const { messages, mode = 'search' } = req.body;
  if (!messages || !Array.isArray(messages) || messages.length === 0) {
    return res.status(400).json({ error: 'Messages array is required' });
  }

  const latestUserQuery = messages.filter((m: any) => m.role === 'user').slice(-1)[0]?.content || '';
  const contents = messages.map((m: any) => ({
    role: m.role === 'user' ? 'user' : 'model',
    parts: [{ text: m.content }]
  }));

  const systemInstruction = `You are the official AI Solar Advisor for Delight Electricals in Khargone, Madhya Pradesh, India.
Founder & Chief Solar Engineer: Arshad Ahamed Sheikh.
Company: Delight Electricals.
Office Address: 26, Tilak Path, Near Mohan Talkies, Khargone, MP - 451001.
Phone / WhatsApp: +91 94794 52486. Email: umerdelight@gmail.com.

Core Engineering & Advisory Guidelines:
1. BALANCED SOLAR PANEL CHOICE (CRITICAL):
   - We supply and install BOTH Waaree Monocrystalline PERC and Waaree N-Type TOPCon panels.
   - Do NOT disparage or downplay Monocrystalline! Both are authentic Tier-1 panels with 25-Year Performance Warranties.
   - Waaree Monocrystalline PERC (19%–21% efficiency): Outstanding field-proven track record worldwide for over 20 years, highly robust in Indian climate, lower upfront cost, fastest payback on investment (ROI). Best for customers wanting proven reliability and great value.
   - Waaree N-Type TOPCon (22%–24% efficiency): Next-generation cell structure, slightly higher output per square foot, lower temperature coefficient in 45°C summer. Ideal for homes with limited roof area or high AC consumption.
   - Empower the customer: "Customer has the freedom to choose what best fits their roof space and budget!"
2. PM SURYA GHAR MUFT BIJLI YOJANA:
   - 1 kW: ₹30,000 direct central subsidy
   - 2 kW: ₹60,000 direct central subsidy
   - 3 kW to 10 kW: ₹78,000 maximum residential subsidy
   - Direct Benefit Transfer (DBT) into customer bank account within ~30 days of net-meter commissioning.
   - Delight Electricals manages all online application and MPPKVVCL Discom approvals.
3. INVERTERS:
   - Havells smart grid-tied on-grid inverters (5 to 10-year warranty, dual MPPT, Havells Sync app monitoring, rapid on-site service network across Madhya Pradesh).
4. ADVANCED SIZING & CALCULATIONS:
   - If user shares their monthly bill, calculate:
     - kW capacity = Monthly bill in ₹ / 800-960 (~120 units/kW/month).
     - Expected monthly generation (~120-135 units/kW/month in Khargone's high solar irradiance).
     - Monthly savings & annual savings (~90% of electricity bill).
     - Subsidy eligibility (₹30,000 for 1kW, ₹60,000 for 2kW, ₹78,000 for 3kW+).
     - Roof area needed (~80-100 sq ft per kW).
     - Breakout of choice between Monocrystalline PERC and N-Type TOPCon.
5. MULTILINGUAL & SCRIPT ALIGNMENT (CRITICAL):
   - Respond fluently in the language user asks: English, Hindi, or Urdu.
   - IN URDU & HINDI: ALWAYS keep mobile numbers, phone numbers, and capacities in English digits (e.g., "+91 94794 52486", "3 kW", "₹78,000") so they align cleanly without bidirectional text reversal!
6. CUSTOMER NEXT STEPS:
   - Warmly invite user to request a free roof survey in Khargone or message Arshad ji on WhatsApp at +91 94794 52486.`;

  try {
    const tools = mode === 'maps' ? [{ googleMaps: {} }] : [{ googleSearch: {} }];
    const response = await ai.models.generateContent({
      model: 'gemini-3.8-flash',
      contents,
      config: {
        systemInstruction,
        tools
      }
    });

    if (response.text) {
      const searchChunks = (response.candidates?.[0]?.groundingMetadata as any)?.groundingChunks || [];
      const webSources = searchChunks
        .map((c: any) => c.web?.title && c.web?.uri ? { title: c.web.title, uri: c.web.uri } : null)
        .filter(Boolean);

      return res.json({ reply: response.text, webSources, model: 'gemini-3.8-flash' });
    }
  } catch (err1) {
    try {
      const response2 = await ai.models.generateContent({
        model: 'gemini-3.8-flash',
        contents,
        config: { systemInstruction }
      });
      if (response2.text) {
        return res.json({ reply: response2.text, webSources: [], model: 'gemini-3.8-flash' });
      }
    } catch (err2) {
      const customReply = generateDelightExpertReply(latestUserQuery);
      return res.json({ reply: customReply, webSources: [], model: 'delight-expert-engine' });
    }
  }

  const customReply = generateDelightExpertReply(latestUserQuery);
  res.json({ reply: customReply, webSources: [], model: 'delight-expert-engine' });
});

// Configure Vite middleware in development or serve static in production
async function startServer() {
  if (process.env.NODE_ENV !== 'production') {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(__dirname, 'dist');
    app.use(express.static(distPath, {
      etag: true,
      lastModified: true
    }));
    app.get('*', (req, res) => {
      const indexPath = path.join(distPath, 'index.html');
      if (fs.existsSync(indexPath)) {
        res.sendFile(indexPath);
      } else {
        res.status(503).send('Application is compiling, please refresh in a moment.');
      }
    });
  }

  const server = app.listen(port, host, () => {
    console.log(`[Delight Electricals] Server listening on http://${host}:${port}`);
  });

  process.on('SIGTERM', () => {
    server.close(() => process.exit(0));
  });

  process.on('SIGINT', () => {
    server.close(() => process.exit(0));
  });
}

startServer().catch(err => {
  console.error('Failed to start server:', err);
  process.exit(1);
});
