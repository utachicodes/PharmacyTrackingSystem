'use client'

import { useState, useEffect, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import {
  Package,
  TrendingUp,
  Bell,
  ShieldCheck,
  ShoppingCart,
  BarChart3,
  ChevronLeft,
  ChevronRight,
  Database,
  Layers,
  AlertTriangle,
  Lock,
  Users,
  Building2,
  ArrowRight,
  Activity,
  ClipboardList,
  Boxes,
  Server,
  FileCode2,
  Cpu,
  CheckCircle2,
  Circle,
} from 'lucide-react'
import Image from 'next/image'

// ── animation presets ────────────────────────────────────────────────────────

const fadeUp = {
  hidden: { opacity: 0, y: 18 },
  show:   { opacity: 1, y: 0,  transition: { duration: 0.38, ease: 'easeOut' } },
}
const fadeIn = {
  hidden: { opacity: 0 },
  show:   { opacity: 1, transition: { duration: 0.35 } },
}
const stagger = (delay = 0.07) => ({
  hidden: {},
  show:   { transition: { staggerChildren: delay, delayChildren: 0.05 } },
})
const slideLeft = {
  hidden: { opacity: 0, x: -20 },
  show:   { opacity: 1, x: 0, transition: { duration: 0.35, ease: 'easeOut' } },
}

function Anim({
  children,
  variants = fadeUp,
  className = '',
}: {
  children: React.ReactNode
  variants?: typeof fadeUp
  className?: string
}) {
  return (
    <motion.div variants={variants} className={className}>
      {children}
    </motion.div>
  )
}

// ── shared label ─────────────────────────────────────────────────────────────

function Label({ n, text }: { n: string; text: string }) {
  return (
    <motion.div variants={fadeUp} className="flex items-center gap-3 mb-5">
      <span className="text-[10px] font-mono font-bold text-[#15803d] tabular-nums">{n}</span>
      <div className="w-1 h-1 rounded-full bg-[#15803d]/40" />
      <span className="text-[10px] font-mono text-gray-400 uppercase tracking-[0.18em]">{text}</span>
    </motion.div>
  )
}

// ── code window ──────────────────────────────────────────────────────────────

function CodeWindow({ filename, code }: { filename: string; code: string }) {
  return (
    <div className="rounded-xl overflow-hidden border border-gray-800 shadow-lg text-left">
      <div className="bg-[#0d1117] px-4 py-2.5 flex items-center gap-2 border-b border-white/5">
        <div className="flex gap-1.5">
          <div className="w-2.5 h-2.5 rounded-full bg-[#ff5f57]" />
          <div className="w-2.5 h-2.5 rounded-full bg-[#febc2e]" />
          <div className="w-2.5 h-2.5 rounded-full bg-[#28c840]" />
        </div>
        <FileCode2 size={11} className="text-gray-500 ml-1" />
        <span className="text-[#4d5566] text-[11px] font-mono">{filename}</span>
      </div>
      <pre className="bg-[#0d1117] px-5 py-4 text-[11px] font-mono text-[#e6edf3] overflow-auto leading-relaxed whitespace-pre max-h-[310px]">
        <code>{code}</code>
      </pre>
    </div>
  )
}

// ── code snippets ────────────────────────────────────────────────────────────

const CODE_CONN = `public static Connection getConnection()
    throws SQLException {
  // Connect to local MySQL instance
  String url = "jdbc:mysql://localhost:3306/PharmaDb";
  try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(url, USER, PASS);
  } catch (ClassNotFoundException e) {
    throw new SQLException("MySQL Driver not found", e);
  }
}`

const CODE_RBAC = `private void applyRolePermissions() {
  if ("Technician".equalsIgnoreCase(userRole)) {
    btnAgents.setEnabled(false);
    btnCompany.setEnabled(false);
    btnPO.setEnabled(false);
  } else if ("Pharmacist".equalsIgnoreCase(userRole)) {
    btnAgents.setEnabled(false);
  }
  // Admin: all buttons remain enabled
}`

const CODE_FORECAST = `public static int predictDemand(String name) {
  // SMA: sum of last 30 days / 30 * 7
  LocalDate ago = LocalDate.now().minusDays(30);
  pstmt.setString(1, name);
  pstmt.setDate(2, java.sql.Date.valueOf(ago));
  // ... accumulate totalQty from SALES
  double avgDaily = (double) totalQty / 30.0;
  return (int) Math.ceil(avgDaily * 7);
}`

const CODE_TXN = `conn.setAutoCommit(false);
try {
  // 1. Mark PO as Received
  updatePO.executeUpdate();
  // 2. Increment medicine stock
  updateMed.executeUpdate();
  conn.commit();         // both succeed
} catch (SQLException e) {
  conn.rollback();       // or both roll back
  throw e;
}`

// ── SLIDE 01 — TITLE ────────────────────────────────────────────────────────

function Slide01() {
  return (
    <motion.div className="flex h-full" variants={stagger(0.1)} initial="hidden" animate="show">
      {/* left panel */}
      <Anim variants={fadeIn} className="w-[42%] bg-[#0d2818] flex flex-col items-center justify-between py-14 px-10 shrink-0 relative overflow-hidden">
        <span className="absolute inset-0 flex items-center justify-center text-white/[0.04] text-[260px] font-black select-none leading-none pointer-events-none">
          Rx
        </span>
        <div className="relative z-10 w-full">
          <Image
            src="/daust-logo.png"
            alt="DAUST"
            width={160}
            height={50}
            className="object-contain brightness-0 invert opacity-80"
          />
        </div>
        <div className="relative z-10 text-center">
          <p className="text-white/30 text-[10px] font-mono uppercase tracking-[0.4em]">
            CS &middot; Software Engineering
          </p>
        </div>
      </Anim>

      {/* right panel */}
      <div className="flex-1 flex flex-col justify-center px-14 bg-[#f9f8f5]">
        <Anim>
          <p className="text-[#15803d] text-[10px] font-mono uppercase tracking-[0.3em] mb-7">
            Final Project Presentation
          </p>
        </Anim>
        <Anim>
          <h1 className="text-[76px] font-black text-[#0a0a0a] leading-[0.88] tracking-tight mb-6">
            Pharm<br />
            <span className="text-[#15803d]">Track</span>
          </h1>
        </Anim>
        <Anim>
          <div className="w-12 h-[3px] bg-[#15803d] mb-6 rounded-full" />
        </Anim>
        <Anim>
          <p className="text-[17px] text-gray-400 mb-3 font-light leading-relaxed">
            Pharmacy Inventory Management System
          </p>
          <p className="text-sm text-gray-400 mb-10">
            Abdoullah Ndao &nbsp;&middot;&nbsp; Junior II &nbsp;&middot;&nbsp; DAUST
          </p>
        </Anim>
        <Anim>
          <div className="flex gap-2 flex-wrap">
            {['Java 17', 'FlatIntelliJ Theme', 'MySQL 8', 'JDBC', 'Maven', 'Dark UI'].map((t) => (
              <span key={t} className="border border-gray-200 bg-white text-gray-500 text-[11px] px-3 py-1 rounded-full font-mono">
                {t}
              </span>
            ))}
          </div>
        </Anim>
      </div>
    </motion.div>
  )
}

// ── SLIDE 02 — PROBLEM ───────────────────────────────────────────────────────

function Slide02() {
  const problems = [
    {
      icon: <Package size={18} className="text-red-500" />,
      bg: 'bg-red-50 border-red-100',
      bar: 'bg-red-400',
      title: 'Stock shortages go unnoticed',
      desc: 'Without real-time tracking, staff only discover a medicine is out when a patient asks for it. There is no system to flag low levels before they become a problem.',
    },
    {
      icon: <AlertTriangle size={18} className="text-amber-500" />,
      bg: 'bg-amber-50 border-amber-100',
      bar: 'bg-amber-400',
      title: 'Expired medicines stay on shelves',
      desc: 'Manual expiry checks are inconsistent. Expired drugs dispensed to patients are a direct safety issue and a regulatory liability for the pharmacy.',
    },
    {
      icon: <ClipboardList size={18} className="text-sky-500" />,
      bg: 'bg-sky-50 border-sky-100',
      bar: 'bg-sky-400',
      title: 'No access controls or audit trail',
      desc: 'Spreadsheets let any staff member change or delete records with no log of who did what. Procurement decisions rely on guesswork rather than sales history.',
    },
  ]
  return (
    <motion.div className="h-full flex flex-col justify-center px-16 max-w-4xl mx-auto w-full" variants={stagger(0.12)} initial="hidden" animate="show">
      <Label n="02" text="Problem Statement" />
      <Anim>
        <h2 className="text-[3.2rem] font-black text-[#0a0a0a] mb-9 leading-[1.05]">
          Why does this<br />need to exist?
        </h2>
      </Anim>
      <motion.div className="space-y-4" variants={stagger(0.13)}>
        {problems.map((p) => (
          <Anim key={p.title} variants={slideLeft}>
            <div className={`flex gap-5 border ${p.bg} rounded-2xl px-6 py-5`}>
              <div className={`w-1 rounded-full ${p.bar} shrink-0`} />
              <div className="flex items-start gap-4">
                <div className="mt-0.5 shrink-0">{p.icon}</div>
                <div>
                  <p className="font-bold text-[#0a0a0a] mb-1.5 text-[15px]">{p.title}</p>
                  <p className="text-gray-600 text-sm leading-relaxed">{p.desc}</p>
                </div>
              </div>
            </div>
          </Anim>
        ))}
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 03 — OVERVIEW ──────────────────────────────────────────────────────

function Slide03() {
  const features = [
    { icon: <Boxes size={20} className="text-[#15803d]" />,       label: 'Inventory',      desc: 'Full CRUD with batch numbers, categories, and per-medicine reorder thresholds' },
    { icon: <TrendingUp size={20} className="text-sky-600" />,    label: 'Forecasting',    desc: '30-day Simple Moving Average predicts seven-day demand per medicine' },
    { icon: <Bell size={20} className="text-amber-500" />,        label: 'Alerts',         desc: 'Low-stock and near-expiry warnings shown every time the dashboard loads' },
    { icon: <ShieldCheck size={20} className="text-violet-600" />,label: 'Access Control', desc: 'Three roles: Admin, Pharmacist, Technician, each with distinct permissions' },
    { icon: <ShoppingCart size={20} className="text-rose-500" />, label: 'Procurement',    desc: 'Purchase orders with transactional stock receiving via JDBC transactions' },
    { icon: <BarChart3 size={20} className="text-teal-500" />,    label: 'Valuation',      desc: 'Live total inventory value calculated as quantity times unit cost per medicine' },
  ]
  return (
    <motion.div className="h-full flex flex-col justify-center px-16 max-w-5xl mx-auto w-full" variants={stagger(0.09)} initial="hidden" animate="show">
      <Label n="03" text="System Overview" />
      <Anim>
        <h2 className="text-[3rem] font-black text-[#0a0a0a] mb-7 leading-[1.05]">
          Six core features.
        </h2>
      </Anim>
      <motion.div className="grid grid-cols-3 gap-3 mb-6" variants={stagger(0.08)}>
        {features.map((f) => (
          <Anim key={f.label} variants={fadeUp}>
            <motion.div
              className="bg-white border border-gray-200 rounded-2xl p-5 shadow-sm h-full"
              whileHover={{ y: -3, boxShadow: '0 8px 24px rgba(0,0,0,0.08)' }}
              transition={{ duration: 0.2 }}
            >
              <div className="mb-3">{f.icon}</div>
              <p className="font-bold text-[#0a0a0a] text-sm mb-1.5">{f.label}</p>
              <p className="text-gray-500 text-xs leading-relaxed">{f.desc}</p>
            </motion.div>
          </Anim>
        ))}
      </motion.div>
      <Anim>
        <div className="flex gap-2 flex-wrap">
          {['Java 17', 'Java Swing (FlatLaf)', 'MySQL', 'JDBC', 'JCalendar', 'Maven'].map((t) => (
            <span key={t} className="border border-gray-200 bg-white text-gray-400 text-[10px] font-mono px-2.5 py-1 rounded-full">
              {t}
            </span>
          ))}
        </div>
      </Anim>
    </motion.div>
  )
}

// ── SLIDE 04 — TECH RATIONALE ───────────────────────────────────────────────

function Slide04() {
  const choices = [
    {
      tech: 'MySQL',
      icon: <Database size={22} className="text-[#00758f]" />,
      color: 'border-[#00758f]/30 bg-[#e8f6f8]',
      badge: 'bg-[#00758f] text-white',
      reasons: [
        {
          title: 'ACID transactions',
          desc: 'Stock receiving must update two tables atomically. MySQL InnoDB guarantees that if either write fails, both roll back — something a flat file or embedded database cannot reliably provide.',
        },
        {
          title: 'Industry-standard relational model',
          desc: 'Pharmacy data is inherently relational: medicines link to sales, suppliers link to purchase orders. MySQL's foreign-key model maps directly to this domain without workarounds.',
        },
        {
          title: 'JDBC ecosystem',
          desc: 'MySQL Connector/J is a mature, officially maintained driver. Every SQL query in the codebase is portable to any JDBC-compatible database with zero business-logic changes.',
        },
        {
          title: 'Free and production-ready',
          desc: 'MySQL Community Edition is free for use in academic projects and small clinics. It runs on any operating system, scales to millions of rows, and has decades of documentation.',
        },
      ],
    },
    {
      tech: 'Java Swing',
      icon: <Cpu size={22} className="text-[#5382a1]" />,
      color: 'border-[#5382a1]/30 bg-[#eef2f7]',
      badge: 'bg-[#5382a1] text-white',
      reasons: [
        {
          title: 'Bundled with the JDK — zero extra dependencies',
          desc: 'Swing ships inside the Java runtime. There is no framework to install, no build plugin to configure, and no runtime licensing fee. The JAR runs on any machine with Java 17.',
        },
        {
          title: 'Rich native widget set',
          desc: 'JTable, JDatePicker, and JComboBox provide the exact controls a pharmacy clerk needs — tabular data, calendar pickers, and constrained dropdowns — all backed by the Swing MVC model.',
        },
        {
          title: 'FlatLaf modernises the look without changing the API',
          desc: 'FlatLaf is a single-JAR look-and-feel that replaces the dated Metal theme with a clean, flat UI. Dropping it in required four lines of code and no changes to any existing component.',
        },
        {
          title: 'Offline desktop requirement',
          desc: 'The target environment is a small pharmacy without guaranteed internet access. A desktop Swing app runs entirely locally — no server, no browser, no network dependency.',
        },
      ],
    },
  ]

  return (
    <motion.div className="h-full flex flex-col justify-center px-14 max-w-6xl mx-auto w-full" variants={stagger(0.09)} initial="hidden" animate="show">
      <Label n="04" text="Technology Choices" />
      <Anim>
        <h2 className="text-[2.8rem] font-black text-[#0a0a0a] mb-6 leading-[1.05]">
          Why MySQL and Java Swing?
        </h2>
      </Anim>
      <motion.div className="grid grid-cols-2 gap-5" variants={stagger(0.1)}>
        {choices.map((c) => (
          <Anim key={c.tech} variants={fadeUp}>
            <div className={`border rounded-2xl p-5 h-full ${c.color}`}>
              <div className="flex items-center gap-3 mb-4">
                {c.icon}
                <span className={`text-[11px] font-black uppercase tracking-[0.2em] px-3 py-1 rounded-full ${c.badge}`}>
                  {c.tech}
                </span>
              </div>
              <motion.div className="space-y-3" variants={stagger(0.07)}>
                {c.reasons.map((r) => (
                  <Anim key={r.title} variants={slideLeft}>
                    <div className="flex gap-3">
                      <CheckCircle2 size={13} className="mt-0.5 shrink-0 text-gray-400" />
                      <div>
                        <span className="font-semibold text-[#0a0a0a] text-[12px]">{r.title} — </span>
                        <span className="text-gray-600 text-[12px] leading-relaxed">{r.desc}</span>
                      </div>
                    </div>
                  </Anim>
                ))}
              </motion.div>
            </div>
          </Anim>
        ))}
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 05 — ARCHITECTURE ──────────────────────────────────────────────────

function Slide05() {
  const layers = [
    {
      label: 'Presentation',
      color: 'bg-[#0d2818] text-white',
      border: 'border-[#0d2818]',
      items: ['SplashFrame', 'LoginFrame', 'DashboardFrame', 'MedicineFrame', 'AgentsFrame', 'CompanyFrame', 'SellingFrame', 'PurchaseOrderFrame'],
      icon: <Layers size={14} />,
    },
    {
      label: 'Business Logic',
      color: 'bg-[#1e3a5f] text-white',
      border: 'border-[#1e3a5f]',
      items: ['DatabaseHelper', 'ForecastingHelper'],
      icon: <Cpu size={14} />,
    },
    {
      label: 'Data',
      color: 'bg-[#4a1a6b] text-white',
      border: 'border-[#4a1a6b]',
      items: ['MEDICINE', 'AGENTS', 'SALES', 'PURCHASE_ORDERS', 'COMPANY'],
      icon: <Database size={14} />,
    },
  ]
  return (
    <motion.div className="h-full flex gap-12 items-center px-14 max-w-6xl mx-auto w-full" variants={stagger(0.1)} initial="hidden" animate="show">
      <div className="flex-1">
        <Label n="05" text="Architecture" />
        <Anim>
          <h2 className="text-[2.8rem] font-black text-[#0a0a0a] mb-3 leading-[1.05]">
            Three layers.<br />One direction of flow.
          </h2>
        </Anim>
        <Anim>
          <p className="text-gray-500 text-sm leading-relaxed max-w-sm">
            Swing frames own display and input only. All SQL lives in DatabaseHelper. All forecasting logic lives in ForecastingHelper. No frame class touches the database directly.
          </p>
        </Anim>
      </div>

      <motion.div className="w-[420px] shrink-0 space-y-1" variants={stagger(0.15)}>
        {layers.map((l, i) => (
          <Anim key={l.label} variants={fadeUp}>
            <div className="rounded-xl overflow-hidden border border-gray-200 shadow-sm">
              <div className={`${l.color} px-4 py-2.5 flex items-center gap-2`}>
                {l.icon}
                <span className="text-[11px] font-bold uppercase tracking-[0.15em]">{l.label} Layer</span>
              </div>
              <div className="bg-white px-4 py-3 flex flex-wrap gap-1.5">
                {l.items.map((item) => (
                  <span key={item} className="text-[10px] font-mono border border-gray-200 rounded-md px-2 py-0.5 text-gray-600 bg-gray-50">
                    {item}
                  </span>
                ))}
              </div>
            </div>
            {i < layers.length - 1 && (
              <div className="flex justify-center py-0.5">
                <ChevronRight size={14} className="text-gray-300 rotate-90" />
              </div>
            )}
          </Anim>
        ))}
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 05 — OOP DESIGN ────────────────────────────────────────────────────

function Slide06() {
  const pillars = [
    {
      name: 'Inheritance',
      bg: 'bg-sky-50 border-sky-200',
      badge: 'bg-sky-100 text-sky-800',
      icon: <Layers size={14} className="text-sky-600" />,
      desc: 'All eight UI frames extend javax.swing.JFrame, inheriting window management, layout engine, and event dispatch.',
    },
    {
      name: 'Encapsulation',
      bg: 'bg-green-50 border-green-200',
      badge: 'bg-green-100 text-green-800',
      icon: <Lock size={14} className="text-green-600" />,
      desc: 'DatabaseHelper hides all JDBC logic. ForecastingHelper hides the SMA algorithm. RBAC is one private method in DashboardFrame.',
    },
    {
      name: 'Polymorphism',
      bg: 'bg-amber-50 border-amber-200',
      badge: 'bg-amber-100 text-amber-800',
      icon: <Activity size={14} className="text-amber-600" />,
      desc: 'MedicineFrame overrides getTableCellRendererComponent() so each row gets a different background based on stock level and expiry date at runtime.',
    },
    {
      name: 'Abstraction',
      bg: 'bg-violet-50 border-violet-200',
      badge: 'bg-violet-100 text-violet-800',
      icon: <Server size={14} className="text-violet-600" />,
      desc: 'resultSetToTableModel() converts any ResultSet to a Swing table in one call. predictDemand() hides all SQL and math behind a readable method signature.',
    },
  ]
  const frames = ['SplashFrame', 'LoginFrame', 'DashboardFrame', 'MedicineFrame', 'AgentsFrame', 'CompanyFrame', 'SellingFrame', 'PurchaseOrderFrame']

  return (
    <motion.div className="h-full flex items-center gap-10 px-14 max-w-6xl mx-auto w-full" variants={stagger(0.08)} initial="hidden" animate="show">
      {/* hierarchy */}
      <div className="w-60 shrink-0">
        <Label n="06" text="OOP Design" />
        <div className="font-mono">
          <div className="border-2 border-gray-200 rounded-lg px-3 py-1.5 inline-block text-gray-400 text-[10px] bg-gray-50 mb-2">
            javax.swing.JFrame
          </div>
          <motion.div className="ml-3 border-l-2 border-dashed border-gray-200 pl-3 space-y-1" variants={stagger(0.06)}>
            {frames.map((f) => (
              <Anim key={f} variants={slideLeft}>
                <div className="flex items-center gap-1.5">
                  <div className="w-3 h-px bg-gray-200 shrink-0" />
                  <span className="border border-[#15803d]/25 rounded px-2 py-0.5 bg-[#f0fdf4] text-[#15803d] text-[9.5px]">
                    {f}
                  </span>
                </div>
              </Anim>
            ))}
            <div className="pt-2 mt-1 border-t border-gray-100 space-y-1">
              <p className="text-[8px] uppercase tracking-wider text-gray-400 mb-1">Utility classes</p>
              {['DatabaseHelper', 'ForecastingHelper'].map((h) => (
                <Anim key={h} variants={slideLeft}>
                  <div className="flex items-center gap-1.5">
                    <div className="w-3 h-px bg-gray-200 shrink-0" />
                    <span className="border border-gray-200 rounded px-2 py-0.5 bg-gray-50 text-gray-400 text-[9.5px]">
                      {h}
                    </span>
                  </div>
                </Anim>
              ))}
            </div>
          </motion.div>
        </div>
      </div>

      {/* pillars */}
      <motion.div className="flex-1 grid grid-cols-2 gap-3" variants={stagger(0.1)}>
        {pillars.map((p) => (
          <Anim key={p.name} variants={fadeUp}>
            <motion.div
              className={`border rounded-2xl p-5 h-full ${p.bg}`}
              whileHover={{ scale: 1.02 }}
              transition={{ duration: 0.18 }}
            >
              <div className={`inline-flex items-center gap-1.5 text-[9px] font-black uppercase tracking-[0.2em] px-2.5 py-1 rounded-full ${p.badge} mb-3`}>
                {p.icon}
                {p.name}
              </div>
              <p className="text-[12.5px] text-gray-700 leading-relaxed">{p.desc}</p>
            </motion.div>
          </Anim>
        ))}
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 06 — HELPER CLASSES ────────────────────────────────────────────────

function Slide07() {
  const db = [
    { name: 'getConnection()',         desc: 'Connects to a local MySQL instance with error handling for driver loading.' },
    { name: 'initializeDatabase()',    desc: 'Creates 5 tables and seeds a default admin on first launch.' },
    { name: 'resultSetToTableModel()', desc: 'Converts any ResultSet to a DefaultTableModel in one call.' },
  ]
  const fc = [
    { name: 'predictDemand(name)',      desc: 'SMA: total 30-day sales divided by 30, multiplied by 7.' },
    { name: 'getLowStockAlerts()',      desc: 'Flags any medicine where stock is below forecast or below 10.' },
    { name: 'getExpirationAlerts()',    desc: 'Finds medicines expiring within the next 30 calendar days.' },
    { name: 'getInventoryValue()',      desc: 'Sums quantity times unit cost across all medicines.' },
  ]
  return (
    <motion.div className="h-full flex flex-col justify-center px-16 max-w-5xl mx-auto w-full" variants={stagger(0.09)} initial="hidden" animate="show">
      <Label n="07" text="Helper Classes" />
      <Anim>
        <h2 className="text-[2.9rem] font-black text-[#0a0a0a] mb-6 leading-[1.05]">
          All logic lives here.
        </h2>
      </Anim>
      <motion.div className="grid grid-cols-2 gap-5" variants={stagger(0.1)}>
        {/* DatabaseHelper */}
        <Anim variants={fadeUp}>
          <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm h-full">
            <div className="flex items-center justify-between mb-5">
              <div className="flex items-center gap-2.5">
                <Database size={16} className="text-[#15803d]" />
                <p className="font-black font-mono text-[#0a0a0a]">DatabaseHelper</p>
              </div>
              <span className="bg-green-100 text-green-800 text-[8.5px] font-black uppercase tracking-wider px-2 py-0.5 rounded-full">
                Encapsulation
              </span>
            </div>
            <motion.div className="space-y-4" variants={stagger(0.08)}>
              {db.map((m) => (
                <Anim key={m.name} variants={slideLeft}>
                  <div className="border-l-2 border-green-200 pl-3">
                    <p className="text-[#15803d] font-mono text-[11px] font-semibold mb-0.5">{m.name}</p>
                    <p className="text-gray-500 text-[11px] leading-relaxed">{m.desc}</p>
                  </div>
                </Anim>
              ))}
            </motion.div>
          </div>
        </Anim>
        {/* ForecastingHelper */}
        <Anim variants={fadeUp}>
          <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm h-full">
            <div className="flex items-center justify-between mb-5">
              <div className="flex items-center gap-2.5">
                <TrendingUp size={16} className="text-sky-600" />
                <p className="font-black font-mono text-[#0a0a0a]">ForecastingHelper</p>
              </div>
              <span className="bg-sky-100 text-sky-800 text-[8.5px] font-black uppercase tracking-wider px-2 py-0.5 rounded-full">
                Abstraction
              </span>
            </div>
            <motion.div className="space-y-4" variants={stagger(0.08)}>
              {fc.map((m) => (
                <Anim key={m.name} variants={slideLeft}>
                  <div className="border-l-2 border-sky-200 pl-3">
                    <p className="text-sky-700 font-mono text-[11px] font-semibold mb-0.5">{m.name}</p>
                    <p className="text-gray-500 text-[11px] leading-relaxed">{m.desc}</p>
                  </div>
                </Anim>
              ))}
            </motion.div>
          </div>
        </Anim>
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 07 — FRAME CLASSES ─────────────────────────────────────────────────

function Slide08() {
  const rows = [
    { icon: <Activity size={13} className="text-gray-400" />,     name: 'LoginFrame',          role: 'Drag-to-move undecorated window, PreparedStatement auth, auto-focuses username field',  tag: 'Encapsulation', tc: 'bg-green-100 text-green-800' },
    { icon: <Layers size={13} className="text-gray-400" />,       name: 'DashboardFrame',      role: 'Dark sidebar nav, RBAC role badge, live alert renderer, refresh button',               tag: 'Encapsulation', tc: 'bg-green-100 text-green-800' },
    { icon: <Package size={13} className="text-gray-400" />,      name: 'MedicineFrame',       role: 'Live search, row count, column sort, red/yellow stock highlights, validation borders', tag: 'Polymorphism',  tc: 'bg-amber-100 text-amber-800' },
    { icon: <ShoppingCart size={13} className="text-gray-400" />, name: 'SellingFrame',        role: 'POS billing, dark invoice panel, stock row count, Enter-to-add keyboard shortcut',     tag: 'Abstraction',   tc: 'bg-violet-100 text-violet-800' },
    { icon: <ClipboardList size={13} className="text-gray-400" />,name: 'PurchaseOrderFrame',  role: 'PO search filter, status badge renderer (green/orange), atomic receiving',            tag: 'Abstraction',   tc: 'bg-violet-100 text-violet-800' },
    { icon: <Users size={13} className="text-gray-400" />,        name: 'AgentsFrame',         role: 'Role column color-coded (red/blue/green), Enter-to-add, live staff count',            tag: 'Inheritance',   tc: 'bg-sky-100 text-sky-800' },
    { icon: <Building2 size={13} className="text-gray-400" />,    name: 'CompanyFrame',        role: 'Preferred supplier green highlight, live supplier count, F5 refresh',                 tag: 'Inheritance',   tc: 'bg-sky-100 text-sky-800' },
    { icon: <Server size={13} className="text-gray-400" />,       name: 'SplashFrame',         role: 'SwingWorker progress animation, DAUST branding, version label, dark theme',           tag: 'Inheritance',   tc: 'bg-sky-100 text-sky-800' },
  ]
  return (
    <motion.div className="h-full flex flex-col justify-center px-16 max-w-5xl mx-auto w-full" variants={stagger(0.07)} initial="hidden" animate="show">
      <Label n="08" text="Frame Classes" />
      <Anim>
        <h2 className="text-[2.8rem] font-black text-[#0a0a0a] mb-5 leading-[1.05]">
          Eight frames. One job each.
        </h2>
      </Anim>
      <motion.div className="space-y-1.5" variants={stagger(0.06)}>
        {rows.map((r) => (
          <Anim key={r.name} variants={slideLeft}>
            <div className="flex items-center gap-4 bg-white border border-gray-200 rounded-xl px-5 py-2.5">
              <div className="shrink-0">{r.icon}</div>
              <span className="font-mono font-semibold text-[#0a0a0a] text-[12.5px] w-52 shrink-0">{r.name}</span>
              <span className="text-gray-500 text-[12px] flex-1 min-w-0 truncate">{r.role}</span>
              <span className={`text-[8.5px] font-black uppercase tracking-wider px-2 py-0.5 rounded-full shrink-0 ${r.tc}`}>
                {r.tag}
              </span>
            </div>
          </Anim>
        ))}
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 08 — CODE IN ACTION ────────────────────────────────────────────────

function Slide09() {
  return (
    <motion.div className="h-full flex flex-col justify-center px-14 max-w-6xl mx-auto w-full" variants={stagger(0.1)} initial="hidden" animate="show">
      <Label n="09" text="Code in Action" />
      <Anim>
        <h2 className="text-[2.8rem] font-black text-[#0a0a0a] mb-5 leading-[1.05]">
          Key implementations.
        </h2>
      </Anim>
      <motion.div className="grid grid-cols-2 gap-4" variants={stagger(0.12)}>
        <Anim variants={fadeUp}>
          <p className="text-[10.5px] font-mono text-gray-400 mb-2">
            DatabaseHelper.java &nbsp;<span className="text-[#15803d]">MySQL connection</span>
          </p>
          <CodeWindow filename="getConnection()" code={CODE_CONN} />
        </Anim>
        <Anim variants={fadeUp}>
          <p className="text-[10.5px] font-mono text-gray-400 mb-2">
            DashboardFrame.java &nbsp;<span className="text-sky-600">RBAC enforcement</span>
          </p>
          <CodeWindow filename="applyRolePermissions()" code={CODE_RBAC} />
        </Anim>
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 09 — CODE PAGE 2 ───────────────────────────────────────────────────

function Slide10() {
  return (
    <motion.div className="h-full flex flex-col justify-center px-14 max-w-6xl mx-auto w-full" variants={stagger(0.1)} initial="hidden" animate="show">
      <Label n="10" text="Code in Action" />
      <Anim>
        <h2 className="text-[2.8rem] font-black text-[#0a0a0a] mb-5 leading-[1.05]">
          Forecasting and transactions.
        </h2>
      </Anim>
      <motion.div className="grid grid-cols-2 gap-4" variants={stagger(0.12)}>
        <Anim variants={fadeUp}>
          <p className="text-[10.5px] font-mono text-gray-400 mb-2">
            ForecastingHelper.java &nbsp;<span className="text-amber-600">30-day SMA</span>
          </p>
          <CodeWindow filename="predictDemand(String name)" code={CODE_FORECAST} />
        </Anim>
        <Anim variants={fadeUp}>
          <p className="text-[10.5px] font-mono text-gray-400 mb-2">
            PurchaseOrderFrame.java &nbsp;<span className="text-violet-600">atomic transaction</span>
          </p>
          <CodeWindow filename="receiveStock()" code={CODE_TXN} />
        </Anim>
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 10 — DATABASE SCHEMA ───────────────────────────────────────────────

function Slide11() {
  const tables = [
    {
      name: 'MEDICINE',
      color: 'border-[#15803d] bg-green-50',
      header: 'bg-[#0d2818] text-white',
      icon: <Package size={12} />,
      cols: ['M_ID (PK)', 'M_NAME', 'M_QUANTITY', 'M_PRICE', 'M_EXPDATE', 'M_THRESHOLD', 'M_BATCH', '+ 7 more'],
    },
    {
      name: 'AGENTS',
      color: 'border-sky-300 bg-sky-50',
      header: 'bg-[#0c2a4a] text-white',
      icon: <Users size={12} />,
      cols: ['A_ID (PK)', 'A_NAME', 'A_PASSWORD', 'A_ROLE', 'A_PHONE', 'A_EMAIL', 'A_GENDER', 'A_AGE'],
    },
    {
      name: 'SALES',
      color: 'border-amber-300 bg-amber-50',
      header: 'bg-[#4a2e00] text-white',
      icon: <ShoppingCart size={12} />,
      cols: ['S_ID (PK, IDENTITY)', 'S_MED_NAME', 'S_DATE', 'S_QTY', 'S_TOTAL'],
    },
    {
      name: 'PURCHASE_ORDERS',
      color: 'border-violet-300 bg-violet-50',
      header: 'bg-[#2e0a4a] text-white',
      icon: <ClipboardList size={12} />,
      cols: ['PO_ID (PK, IDENTITY)', 'PO_MED_NAME', 'PO_SUPPLIER', 'PO_QTY', 'PO_STATUS', 'PO_DATE'],
    },
    {
      name: 'COMPANY',
      color: 'border-rose-300 bg-rose-50',
      header: 'bg-[#4a0a1a] text-white',
      icon: <Building2 size={12} />,
      cols: ['C_ID (PK)', 'C_NAME', 'C_ADDRESS', 'C_PHONE', 'C_EMAIL', 'C_LEADTIME', 'C_PREFERRED'],
    },
  ]
  return (
    <motion.div className="h-full flex flex-col justify-center px-14 max-w-6xl mx-auto w-full" variants={stagger(0.09)} initial="hidden" animate="show">
      <Label n="11" text="Database Schema" />
      <Anim>
        <h2 className="text-[2.8rem] font-black text-[#0a0a0a] mb-6 leading-[1.05]">
          Five tables. MySQL Backend.
        </h2>
      </Anim>
      <motion.div className="grid grid-cols-5 gap-2.5" variants={stagger(0.08)}>
        {tables.map((t) => (
          <Anim key={t.name} variants={fadeUp}>
            <div className={`rounded-xl overflow-hidden border ${t.color} shadow-sm`}>
              <div className={`${t.header} px-3 py-2 flex items-center gap-1.5`}>
                {t.icon}
                <span className="text-[10px] font-bold font-mono">{t.name}</span>
              </div>
              <div className="px-3 py-2.5 space-y-1">
                {t.cols.map((c) => (
                  <p key={c} className="text-[9.5px] font-mono text-gray-600 leading-none">{c}</p>
                ))}
              </div>
            </div>
          </Anim>
        ))}
      </motion.div>
      <Anim>
        <p className="text-[11px] text-gray-400 mt-4 font-mono">
          SALES.S_MED_NAME and PURCHASE_ORDERS.PO_MED_NAME reference MEDICINE.M_NAME by value. No foreign key constraints in the current schema.
        </p>
      </Anim>
    </motion.div>
  )
}

// ── SLIDE 11 — CHALLENGES ────────────────────────────────────────────────────

function Slide12() {
  const items = [
    {
      n: '01',
      icon: <Database size={16} className="text-sky-600" />,
      title: 'MySQL Configuration',
      tag: 'Performance',
      tc: 'bg-sky-100 text-sky-700',
      sol: 'The system uses a robust MySQL backend for persistent storage. Database initialization scripts run automatically on first launch, ensuring the schema is ready without manual SQL execution.',
    },
    {
      n: '02',
      icon: <ShieldCheck size={16} className="text-green-600" />,
      title: 'Role enforcement across frames',
      tag: 'Security',
      tc: 'bg-green-100 text-green-700',
      sol: 'All RBAC logic lives in one private method: applyRolePermissions(). Ten lines, called once from the DashboardFrame constructor. Every access rule is visible in a single place.',
    },
    {
      n: '03',
      icon: <CheckCircle2 size={16} className="text-violet-600" />,
      title: 'PO receiving must be atomic',
      tag: 'Data Integrity',
      tc: 'bg-violet-100 text-violet-700',
      sol: 'setAutoCommit(false) wraps both the PO status update and the medicine quantity increment. If either fails, rollback() reverts both. Partial database state cannot persist.',
    },
  ]
  return (
    <motion.div className="h-full flex flex-col justify-center px-16 max-w-4xl mx-auto w-full" variants={stagger(0.1)} initial="hidden" animate="show">
      <Label n="12" text="Challenges" />
      <Anim>
        <h2 className="text-[3rem] font-black text-[#0a0a0a] mb-8 leading-[1.05]">
          Problems that needed<br />real solutions.
        </h2>
      </Anim>
      <motion.div className="space-y-4" variants={stagger(0.12)}>
        {items.map((c) => (
          <Anim key={c.n} variants={slideLeft}>
            <div className="flex gap-5 bg-white border border-gray-200 rounded-2xl px-6 py-5 shadow-sm">
              <span className="text-[2.8rem] font-black text-gray-100 shrink-0 leading-none select-none tabular-nums">
                {c.n}
              </span>
              <div>
                <div className="flex items-center gap-3 mb-2">
                  {c.icon}
                  <p className="font-bold text-[#0a0a0a] text-[15px]">{c.title}</p>
                  <span className={`text-[8.5px] font-black uppercase tracking-wider px-2 py-0.5 rounded-full ${c.tc}`}>
                    {c.tag}
                  </span>
                </div>
                <p className="text-gray-500 text-sm leading-relaxed">{c.sol}</p>
              </div>
            </div>
          </Anim>
        ))}
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 12 — ROADMAP ───────────────────────────────────────────────────────

function Slide13() {
  const items = [
    { sev: 'Done ✓',   sc: 'bg-green-100 text-green-700', icon: <CheckCircle2 size={13} className="text-green-500" />, title: 'SQL injection fully fixed',              desc: 'All 7 queries converted to PreparedStatements. Login, medicine, agent, and supplier updates are safe.' },
    { sev: 'Done ✓',   sc: 'bg-green-100 text-green-700', icon: <ShieldCheck size={13} className="text-green-500" />, title: 'Modern UI redesign complete',             desc: 'FlatIntelliJLaf, dark sidebar nav, live search, row counts, active highlights, and tooltips across all 8 frames.' },
    { sev: 'Next',     sc: 'bg-amber-100 text-amber-700', icon: <Lock size={13} className="text-amber-500" />,      title: 'Hash stored passwords',                  desc: 'A_PASSWORD is plain text VARCHAR(50). BCrypt with a per-user salt is the correct next security step.' },
    { sev: 'Next',     sc: 'bg-amber-100 text-amber-700', icon: <Layers size={13} className="text-amber-500" />,    title: 'Separate business logic from UI',         desc: 'updateQty() mixes stock rules with dialogs. A service layer makes it independently testable.' },
    { sev: 'Future',   sc: 'bg-sky-100 text-sky-700', icon: <CheckCircle2 size={13} className="text-sky-500" />,    title: 'Unit tests for ForecastingHelper',        desc: 'predictDemand() has no automated coverage. JUnit tests would catch regressions in the SMA.' },
  ]
  return (
    <motion.div className="h-full flex flex-col justify-center px-16 max-w-4xl mx-auto w-full" variants={stagger(0.09)} initial="hidden" animate="show">
      <Label n="13" text="Roadmap" />
      <Anim>
        <h2 className="text-[3rem] font-black text-[#0a0a0a] mb-7 leading-[1.05]">
          What comes next.
        </h2>
      </Anim>
      <motion.div className="space-y-2.5" variants={stagger(0.08)}>
        {items.map((item) => (
          <Anim key={item.title} variants={slideLeft}>
            <div className="flex items-start gap-4 bg-white border border-gray-200 rounded-xl px-5 py-3.5 shadow-sm">
              <span className={`text-[8.5px] font-black uppercase tracking-wider px-2 py-0.5 rounded-full shrink-0 mt-0.5 ${item.sc}`}>
                {item.sev}
              </span>
              <div className="shrink-0 mt-0.5">{item.icon}</div>
              <div className="min-w-0">
                <span className="font-semibold text-[#0a0a0a] text-sm">{item.title}</span>
                <span className="text-gray-500 text-sm"> {item.desc}</span>
              </div>
            </div>
          </Anim>
        ))}
      </motion.div>
    </motion.div>
  )
}

// ── SLIDE 13 — DEMO ──────────────────────────────────────────────────────────

function Slide14() {
  const steps = [
    { label: 'Login as Admin',           detail: 'Name: Admin   Password: admin123' },
    { label: 'Dashboard',                detail: 'Live alerts: expiry warnings and low-stock notices' },
    { label: 'Medicine tab',             detail: 'Add, edit, and delete a medicine record' },
    { label: 'Selling tab',              detail: 'Process a sale and watch the stock update in real time' },
    { label: 'Purchase Orders',          detail: 'Create a PO, then receive it and verify the atomic update' },
    { label: 'Back to Dashboard',        detail: 'Alerts reflect the updated inventory immediately' },
  ]
  return (
    <motion.div className="h-full flex flex-col items-center justify-center px-16 max-w-3xl mx-auto w-full" variants={stagger(0.1)} initial="hidden" animate="show">
      <div className="w-full">
        <Label n="14" text="Live Demo" />
        <Anim>
          <h2 className="text-[3rem] font-black text-[#0a0a0a] mb-8 leading-[1.05]">Live Demo</h2>
        </Anim>
        <motion.div className="space-y-0" variants={stagger(0.1)}>
          {steps.map((s, i) => (
            <Anim key={i} variants={fadeUp}>
              <div className="flex gap-5 items-start">
                <div className="flex flex-col items-center shrink-0">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-[11px] font-black font-mono shrink-0 ${i === 0 ? 'bg-[#0d2818] text-white' : 'bg-white border-2 border-gray-200 text-gray-400'}`}>
                    {String(i + 1).padStart(2, '0')}
                  </div>
                  {i < steps.length - 1 && <div className="w-px h-6 bg-gray-200" />}
                </div>
                <div className="pt-1.5 pb-1">
                  <p className={`text-sm font-semibold ${i === 0 ? 'text-[#0a0a0a]' : 'text-gray-600'}`}>
                    {s.label}
                  </p>
                  <p className="text-xs text-gray-400 font-mono mt-0.5">{s.detail}</p>
                </div>
              </div>
            </Anim>
          ))}
        </motion.div>
      </div>
    </motion.div>
  )
}

// ── SLIDE 14 — Q&A ───────────────────────────────────────────────────────────

function Slide15() {
  return (
    <motion.div className="flex h-full" variants={stagger(0.12)} initial="hidden" animate="show">
      <div className="flex-1 flex flex-col justify-center px-16 bg-[#f9f8f5]">
        <Anim>
          <p className="text-[#15803d] text-[10px] font-mono uppercase tracking-[0.3em] mb-8">
            PharmTrack &nbsp;&middot;&nbsp; CS &middot; Software Engineering
          </p>
        </Anim>
        <Anim>
          <h1 className="text-[80px] font-black text-[#0a0a0a] leading-[0.87] tracking-tight mb-7">
            Thank<br />You.
          </h1>
        </Anim>
        <Anim>
          <div className="w-12 h-[3px] bg-[#0d2818] mb-7 rounded-full" />
        </Anim>
        <Anim>
          <p className="text-2xl text-gray-400 font-light mb-2">Questions?</p>
          <p className="text-sm text-gray-400 font-mono">Abdoullah Ndao &nbsp;&middot;&nbsp; Junior II &nbsp;&middot;&nbsp; DAUST</p>
        </Anim>
      </div>

      <Anim variants={fadeIn} className="w-[38%] bg-[#0d2818] shrink-0 flex items-center justify-center relative overflow-hidden">
        <span className="absolute text-white/[0.05] text-[200px] font-black leading-none select-none pointer-events-none">?</span>
        <div className="relative z-10 opacity-60">
          <Image src="/daust-logo.png" alt="DAUST" width={140} height={44} className="object-contain brightness-0 invert" />
        </div>
      </Anim>
    </motion.div>
  )
}

// ── PRESENTATION SHELL ───────────────────────────────────────────────────────

const SLIDES = [
  Slide01, Slide02, Slide03, Slide04, Slide05, Slide06, Slide07, Slide08,
  Slide09, Slide10, Slide11, Slide12, Slide13, Slide14, Slide15,
]

const LABELS = [
  'Title', 'Problem', 'Overview', 'Tech Choices', 'Architecture', 'OOP Design',
  'Helper Classes', 'Frame Classes', 'Code (1)', 'Code (2)',
  'Database', 'Challenges', 'Roadmap', 'Demo', 'Q & A',
]

export default function Presentation() {
  const [idx, setIdx] = useState(0)

  const prev = useCallback(() => setIdx((i) => Math.max(0, i - 1)), [])
  const next = useCallback(() => setIdx((i) => Math.min(SLIDES.length - 1, i + 1)), [])

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (e.key === 'ArrowRight' || e.key === 'ArrowDown' || e.key === ' ') {
        e.preventDefault(); next()
      } else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') {
        e.preventDefault(); prev()
      }
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [next, prev])

  const SlideContent = SLIDES[idx]

  return (
    <div className="h-screen bg-[#f9f8f5] flex flex-col overflow-hidden">
      {/* header */}
      <header className="flex items-center justify-between px-8 h-10 bg-white border-b border-gray-200 shrink-0">
        <span className="text-[#0a0a0a] font-bold text-[13px] tracking-tight">PharmTrack</span>
        <span className="text-gray-400 text-[11px] font-mono">{LABELS[idx]}</span>
        <span className="text-gray-300 text-[11px] font-mono tabular-nums">
          {idx + 1} / {SLIDES.length}
        </span>
      </header>

      {/* slide area */}
      <main className="flex-1 overflow-hidden relative">
        <AnimatePresence mode="wait">
          <motion.div
            key={idx}
            className="absolute inset-0"
            initial={{ opacity: 0, y: 14 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -14 }}
            transition={{ duration: 0.28, ease: 'easeInOut' }}
          >
            <SlideContent />
          </motion.div>
        </AnimatePresence>
      </main>

      {/* footer */}
      <footer className="flex items-center justify-between px-8 h-10 bg-white border-t border-gray-200 shrink-0">
        <button
          onClick={prev}
          disabled={idx === 0}
          className="text-gray-400 hover:text-[#0a0a0a] disabled:opacity-20 transition-colors flex items-center gap-1.5"
        >
          <ChevronLeft size={15} />
          <span className="text-[11px] font-mono">Prev</span>
        </button>

        <div className="flex items-center gap-1.5">
          {SLIDES.map((_, i) => (
            <button
              key={i}
              onClick={() => setIdx(i)}
              aria-label={`Slide ${i + 1}`}
              className={`rounded-full transition-all duration-200 ${
                i === idx ? 'w-5 h-2 bg-[#15803d]' : 'w-2 h-2 bg-gray-200 hover:bg-gray-400'
              }`}
            />
          ))}
        </div>

        <button
          onClick={next}
          disabled={idx === SLIDES.length - 1}
          className="text-gray-400 hover:text-[#0a0a0a] disabled:opacity-20 transition-colors flex items-center gap-1.5"
        >
          <span className="text-[11px] font-mono">Next</span>
          <ChevronRight size={15} />
        </button>
      </footer>
    </div>
  )
}
