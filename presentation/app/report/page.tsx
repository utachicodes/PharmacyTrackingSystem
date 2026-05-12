'use client'

import Link from 'next/link'

// ── Shared primitives ────────────────────────────────────────────────────────

function H2({ id, n, children }: { id: string; n: string; children: React.ReactNode }) {
  return (
    <h2 id={id} className="text-xl font-bold text-gray-900 mt-12 mb-4 pb-2 border-b border-gray-300 scroll-mt-16 flex items-baseline gap-3">
      <span className="text-gray-400 font-normal text-base w-8 shrink-0">{n}</span>
      {children}
    </h2>
  )
}
function H3({ id, n, children }: { id?: string; n: string; children: React.ReactNode }) {
  return (
    <h3 id={id} className="text-base font-bold text-gray-800 mt-8 mb-2 scroll-mt-16 flex items-baseline gap-2">
      <span className="text-gray-400 font-normal text-sm w-8 shrink-0">{n}</span>
      {children}
    </h3>
  )
}
function P({ children }: { children: React.ReactNode }) {
  return <p className="text-gray-700 leading-[1.85] mb-4 text-[14.5px]">{children}</p>
}
function Note({ children }: { children: React.ReactNode }) {
  return (
    <div className="border-l-4 border-green-600 bg-green-50 px-4 py-2.5 my-4 rounded-r">
      <p className="text-gray-700 text-sm leading-relaxed">{children}</p>
    </div>
  )
}
function Warn({ children }: { children: React.ReactNode }) {
  return (
    <div className="border-l-4 border-red-500 bg-red-50 px-4 py-2.5 my-4 rounded-r">
      <p className="text-gray-700 text-sm leading-relaxed">{children}</p>
    </div>
  )
}
function Code({ label, children }: { label?: string; children: string }) {
  return (
    <div className="my-5">
      {label && (
        <p className="bg-gray-800 text-gray-400 text-[11px] font-mono px-4 py-1.5 rounded-t border-b border-gray-700">
          {label}
        </p>
      )}
      <pre className={`bg-[#1e1e2e] text-[12.5px] font-mono text-[#cdd6f4] overflow-auto leading-relaxed p-4 whitespace-pre ${label ? 'rounded-b' : 'rounded'}`}>
        <code>{children}</code>
      </pre>
    </div>
  )
}
function IC({ children }: { children: React.ReactNode }) {
  return <code className="bg-gray-100 border border-gray-300 text-gray-800 text-[12px] font-mono px-1.5 py-0.5 rounded">{children}</code>
}
function UL({ items }: { items: (string | React.ReactNode)[] }) {
  return (
    <ul className="space-y-1.5 mb-4 mt-1">
      {items.map((item, i) => (
        <li key={i} className="flex gap-2.5 text-gray-700 text-[14.5px] leading-relaxed">
          <span className="text-gray-400 mt-1 shrink-0 text-xs">•</span>
          <span>{item}</span>
        </li>
      ))}
    </ul>
  )
}
function FigCaption({ n, children }: { n: string; children: string }) {
  return <p className="text-center text-[12px] text-gray-500 mt-2 mb-6 italic">{n}: {children}</p>
}
function DataTable({ headers, rows }: { headers: string[]; rows: (string | React.ReactNode)[][] }) {
  return (
    <div className="overflow-x-auto my-4">
      <table className="w-full text-[13px] border border-gray-300">
        <thead>
          <tr className="bg-gray-900 text-white">
            {headers.map((h) => (
              <th key={h} className="text-left px-3 py-2 font-semibold border-r border-gray-700 last:border-0">{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={i} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
              {row.map((cell, j) => (
                <td key={j} className="px-3 py-2 text-gray-700 border border-gray-200 align-top">{cell}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

// ── DIAGRAM 1: UML Class Diagram ──────────────────────────────────────────────

function UMLClassDiagram() {
  return (
    <figure className="my-6">
      <div className="border border-gray-300 rounded bg-white overflow-x-auto">
        <svg viewBox="0 0 860 445" xmlns="http://www.w3.org/2000/svg" className="w-full min-w-[600px]"
          style={{ fontFamily: 'ui-monospace, SFMono-Regular, Menlo, Monaco, monospace' }}>
          <defs>
            <marker id="dep" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto">
              <polyline points="0,0.5 8,4 0,7.5" fill="none" stroke="#9ca3af" strokeWidth="1.5"/>
            </marker>
          </defs>

          {/* ── JFrame ── */}
          <rect x="315" y="10" width="230" height="52" fill="#f9fafb" stroke="#6b7280" strokeWidth="1.5" rx="2"/>
          <line x1="315" y1="24" x2="545" y2="24" stroke="#6b7280" strokeWidth="1"/>
          <text x="430" y="20" textAnchor="middle" fontSize="9" fill="#9ca3af" fontStyle="italic">«external framework»</text>
          <text x="430" y="37" textAnchor="middle" fontSize="11" fontWeight="bold" fill="#374151">javax.swing.JFrame</text>
          <text x="430" y="51" textAnchor="middle" fontSize="8" fill="#9ca3af">+setVisible()  +dispose()  +pack()  +setTitle()</text>

          {/* ── Inheritance fan-out ── */}
          <polygon points="420,62 440,62 430,74" fill="white" stroke="#374151" strokeWidth="1.5"/>
          <line x1="430" y1="74" x2="430" y2="120" stroke="#374151" strokeWidth="1.5"/>
          <line x1="83"  y1="120" x2="755" y2="120" stroke="#374151" strokeWidth="1.5"/>
          {[83, 248, 413, 578, 755].map((cx) => (
            <line key={cx} x1={cx} y1="120" x2={cx} y2="132" stroke="#374151" strokeWidth="1.5"/>
          ))}

          {/* ── LoginFrame  x=10 w=145 center=83 ── */}
          <rect x="10" y="132" width="145" height="88" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <line x1="10" y1="148" x2="155" y2="148" stroke="#374151" strokeWidth="1"/>
          <text x="83" y="144" textAnchor="middle" fontSize="10.5" fontWeight="bold" fill="#111827">LoginFrame</text>
          <line x1="10" y1="160" x2="155" y2="160" stroke="#374151" strokeWidth="1"/>
          <text x="14" y="173" fontSize="8.5" fill="#374151">- Con: Connection</text>
          <text x="14" y="187" fontSize="8.5" fill="#374151">+ LoginFrame()</text>
          <text x="14" y="200" fontSize="8.5" fill="#374151">- btnLoginMouseClicked()</text>
          <text x="14" y="214" fontSize="8.5" fill="#374151">- btnClearMouseClicked()</text>

          {/* ── DashboardFrame  x=165 w=165 center=248 ── */}
          <rect x="165" y="132" width="165" height="95" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <line x1="165" y1="148" x2="330" y2="148" stroke="#374151" strokeWidth="1"/>
          <text x="248" y="144" textAnchor="middle" fontSize="10" fontWeight="bold" fill="#111827">DashboardFrame</text>
          <line x1="165" y1="160" x2="330" y2="160" stroke="#374151" strokeWidth="1"/>
          <text x="169" y="173" fontSize="8.5" fill="#374151">- userRole: String</text>
          <text x="169" y="187" fontSize="8.5" fill="#374151">+ DashboardFrame(role:String)</text>
          <text x="169" y="200" fontSize="8.5" fill="#374151">- applyRolePermissions()</text>
          <text x="169" y="213" fontSize="8.5" fill="#374151">- loadAlerts()</text>
          <text x="169" y="226" fontSize="8.5" fill="#374151">- initComponents()</text>

          {/* ── MedicineFrame  x=340 w=145 center=413 ── */}
          <rect x="340" y="132" width="145" height="88" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <line x1="340" y1="148" x2="485" y2="148" stroke="#374151" strokeWidth="1"/>
          <text x="413" y="144" textAnchor="middle" fontSize="10.5" fontWeight="bold" fill="#111827">MedicineFrame</text>
          <line x1="340" y1="160" x2="485" y2="160" stroke="#374151" strokeWidth="1"/>
          <text x="344" y="173" fontSize="8.5" fill="#374151">-</text>
          <text x="344" y="187" fontSize="8.5" fill="#374151">+ MedicineFrame()</text>
          <text x="344" y="200" fontSize="8.5" fill="#374151">+ SelectMed()</text>
          <text x="344" y="214" fontSize="8.5" fill="#374151">- applyTableHighlighters()</text>

          {/* ── SellingFrame  x=495 w=155 center=578 ── */}
          <rect x="495" y="132" width="155" height="88" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <line x1="495" y1="148" x2="650" y2="148" stroke="#374151" strokeWidth="1"/>
          <text x="578" y="144" textAnchor="middle" fontSize="10.5" fontWeight="bold" fill="#111827">SellingFrame</text>
          <line x1="495" y1="160" x2="650" y2="160" stroke="#374151" strokeWidth="1"/>
          <text x="499" y="173" fontSize="8.5" fill="#374151">- price: double</text>
          <text x="499" y="187" fontSize="8.5" fill="#374151">+ SellingFrame()</text>
          <text x="499" y="200" fontSize="8.5" fill="#374151">+ updateQty(): boolean</text>
          <text x="499" y="214" fontSize="8.5" fill="#374151">+ recordSale()</text>

          {/* ── PurchaseOrderFrame  x=660 w=190 center=755 ── */}
          <rect x="660" y="132" width="192" height="88" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <line x1="660" y1="148" x2="852" y2="148" stroke="#374151" strokeWidth="1"/>
          <text x="756" y="144" textAnchor="middle" fontSize="9" fontWeight="bold" fill="#111827">PurchaseOrderFrame</text>
          <line x1="660" y1="160" x2="852" y2="160" stroke="#374151" strokeWidth="1"/>
          <text x="664" y="173" fontSize="8.5" fill="#374151">-</text>
          <text x="664" y="187" fontSize="8.5" fill="#374151">+ PurchaseOrderFrame()</text>
          <text x="664" y="200" fontSize="8.5" fill="#374151">- createPO()</text>
          <text x="664" y="214" fontSize="8.5" fill="#374151">- receiveStock()</text>

          {/* omission note */}
          <text x="430" y="243" textAnchor="middle" fontSize="8" fill="#9ca3af" fontStyle="italic">
            SplashFrame, AgentsFrame, CompanyFrame also extend JFrame (omitted for brevity)
          </text>

          {/* ── Dependency arrows (DashboardFrame → helpers) ── */}
          <line x1="248" y1="227" x2="110" y2="332" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4" markerEnd="url(#dep)"/>
          <text x="148" y="278" fontSize="8" fill="#9ca3af" fontStyle="italic">«uses»</text>

          <line x1="300" y1="227" x2="748" y2="332" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4" markerEnd="url(#dep)"/>
          <text x="540" y="262" fontSize="8" fill="#9ca3af" fontStyle="italic">«uses»</text>

          <text x="430" y="270" textAnchor="middle" fontSize="7.5" fill="#c4b5a0" fontStyle="italic">
            All frame classes also use DatabaseHelper (individual arrows omitted for clarity)
          </text>

          {/* ── DatabaseHelper ── */}
          <rect x="10" y="332" width="200" height="103" fill="#f0fdf4" stroke="#166534" strokeWidth="1.5" rx="2"/>
          <line x1="10" y1="349" x2="210" y2="349" stroke="#166534" strokeWidth="1"/>
          <text x="110" y="345" textAnchor="middle" fontSize="8.5" fill="#166534" fontStyle="italic">«utility»</text>
          <text x="110" y="362" textAnchor="middle" fontSize="10.5" fontWeight="bold" fill="#14532d">DatabaseHelper</text>
          <line x1="10" y1="367" x2="210" y2="367" stroke="#166534" strokeWidth="1"/>
          <text x="14" y="380" fontSize="8.5" fill="#374151">«static» +getConnection()</text>
          <text x="14" y="393" fontSize="8.5" fill="#374151">«static» +initializeDatabase()</text>
          <text x="14" y="406" fontSize="8.5" fill="#374151">«static» +resultSetToTableModel(rs)</text>
          <text x="14" y="419" fontSize="8.5" fill="#6b7280">              : DefaultTableModel</text>
          <text x="14" y="432" fontSize="8" fill="#9ca3af" fontStyle="italic">URL: jdbc:derby://localhost:1527/PharmaDb</text>

          {/* ── ForecastingHelper ── */}
          <rect x="648" y="332" width="204" height="103" fill="#eff6ff" stroke="#1d4ed8" strokeWidth="1.5" rx="2"/>
          <line x1="648" y1="349" x2="852" y2="349" stroke="#1d4ed8" strokeWidth="1"/>
          <text x="750" y="345" textAnchor="middle" fontSize="8.5" fill="#1d4ed8" fontStyle="italic">«utility»</text>
          <text x="750" y="362" textAnchor="middle" fontSize="10.5" fontWeight="bold" fill="#1e3a8a">ForecastingHelper</text>
          <line x1="648" y1="367" x2="852" y2="367" stroke="#1d4ed8" strokeWidth="1"/>
          <text x="652" y="380" fontSize="8.5" fill="#374151">«static» +predictDemand(name:String)</text>
          <text x="652" y="393" fontSize="8.5" fill="#374151">«static» +getLowStockAlerts()</text>
          <text x="652" y="406" fontSize="8.5" fill="#374151">«static» +getExpirationAlerts()</text>
          <text x="652" y="419" fontSize="8.5" fill="#374151">«static» +getInventoryValue()</text>
          <text x="652" y="432" fontSize="8" fill="#9ca3af" fontStyle="italic">SMA formula: ⌈ Σ(30d sales) ÷ 30 × 7 ⌉</text>

          {/* ── Legend ── */}
          <g transform="translate(240,340)">
            <rect width="172" height="78" fill="white" stroke="#d1d5db" strokeWidth="1.5" rx="3"/>
            <text x="8" y="14" fontSize="9" fontWeight="bold" fill="#374151">Legend</text>
            <line x1="8" y1="20" x2="164" y2="20" stroke="#d1d5db" strokeWidth="1"/>
            <line x1="8" y1="34" x2="38" y2="34" stroke="#374151" strokeWidth="1.5"/>
            <polygon points="38,29 38,39 48,34" fill="white" stroke="#374151" strokeWidth="1.5"/>
            <text x="54" y="38" fontSize="8.5" fill="#374151">Inheritance (extends)</text>
            <line x1="8" y1="54" x2="46" y2="54" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="4,3" markerEnd="url(#dep)"/>
            <text x="54" y="58" fontSize="8.5" fill="#374151">Dependency («uses»)</text>
            <text x="8" y="73" fontSize="7.5" fill="#9ca3af" fontStyle="italic">Hollow triangle = inheritance arrowhead</text>
          </g>
        </svg>
      </div>
      <FigCaption n="Figure 1" children="Simplified UML Class Diagram — PharmTrack" />
    </figure>
  )
}

// ── DIAGRAM 2: Database Schema ────────────────────────────────────────────────

function DBSchemaDiagram() {
  return (
    <figure className="my-6">
      <div className="border border-gray-300 rounded bg-white overflow-x-auto">
        <svg viewBox="0 0 820 400" xmlns="http://www.w3.org/2000/svg" className="w-full min-w-[560px]"
          style={{ fontFamily: 'ui-monospace, SFMono-Regular, Menlo, Monaco, monospace' }}>
          <defs>
            <marker id="rel" markerWidth="8" markerHeight="7" refX="7" refY="3.5" orient="auto">
              <polyline points="0,0.5 6,3.5 0,6.5" fill="none" stroke="#9ca3af" strokeWidth="1.5"/>
            </marker>
          </defs>

          {/* ── MEDICINE (center, large) ── */}
          <rect x="280" y="10" width="260" height="185" fill="white" stroke="#374151" strokeWidth="2" rx="2"/>
          <rect x="280" y="10" width="260" height="22" fill="#1f2937" rx="2"/>
          <rect x="280" y="30" width="260" height="2" fill="#1f2937"/>
          <text x="410" y="25" textAnchor="middle" fontSize="11" fontWeight="bold" fill="white">MEDICINE</text>
          <text x="284" y="48" fontSize="9" fill="#166534" fontWeight="bold">PK  M_ID         INT</text>
          <line x1="280" y1="53" x2="540" y2="53" stroke="#e5e7eb" strokeWidth="1"/>
          <text x="284" y="67" fontSize="9" fill="#374151">    M_NAME        VARCHAR(50)</text>
          <text x="284" y="80" fontSize="9" fill="#374151">    M_QUANTITY     INT</text>
          <text x="284" y="93" fontSize="9" fill="#374151">    M_PRICE        DOUBLE</text>
          <text x="284" y="106" fontSize="9" fill="#374151">    M_EXPDATE      DATE</text>
          <text x="284" y="119" fontSize="9" fill="#374151">    M_MFTDATE      DATE</text>
          <text x="284" y="132" fontSize="9" fill="#374151">    M_COMPANY      VARCHAR(50)</text>
          <text x="284" y="145" fontSize="9" fill="#374151">    M_UNIT_COST    DOUBLE</text>
          <text x="284" y="158" fontSize="9" fill="#374151">    M_THRESHOLD    INT DEFAULT 10</text>
          <text x="284" y="171" fontSize="9" fill="#374151">    M_BATCH        VARCHAR(50)</text>
          <text x="284" y="189" fontSize="8" fill="#9ca3af" fontStyle="italic">    + M_CATEGORY, M_STRENGTH, M_DOSAGE...</text>

          {/* ── AGENTS ── */}
          <rect x="10" y="10" width="220" height="130" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <rect x="10" y="10" width="220" height="22" fill="#374151" rx="2"/>
          <rect x="10" y="30" width="220" height="2" fill="#374151"/>
          <text x="120" y="25" textAnchor="middle" fontSize="11" fontWeight="bold" fill="white">AGENTS</text>
          <text x="14" y="48" fontSize="9" fill="#166534" fontWeight="bold">PK  A_ID         INT</text>
          <line x1="10" y1="53" x2="230" y2="53" stroke="#e5e7eb" strokeWidth="1"/>
          <text x="14" y="67" fontSize="9" fill="#374151">    A_NAME        VARCHAR(50)</text>
          <text x="14" y="80" fontSize="9" fill="#374151">    A_AGE         INT</text>
          <text x="14" y="93" fontSize="9" fill="#374151">    A_PASSWORD    VARCHAR(50)</text>
          <text x="14" y="106" fontSize="9" fill="#374151">    A_PHONE       VARCHAR(20)</text>
          <text x="14" y="119" fontSize="9" fill="#374151">    A_EMAIL       VARCHAR(50)</text>
          <text x="14" y="132" fontSize="9" fill="#dc2626">    A_ROLE        VARCHAR(20)</text>

          {/* ── COMPANY ── */}
          <rect x="590" y="10" width="220" height="130" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <rect x="590" y="10" width="220" height="22" fill="#374151" rx="2"/>
          <rect x="590" y="30" width="220" height="2" fill="#374151"/>
          <text x="700" y="25" textAnchor="middle" fontSize="11" fontWeight="bold" fill="white">COMPANY</text>
          <text x="594" y="48" fontSize="9" fill="#166534" fontWeight="bold">PK  C_ID         INT</text>
          <line x1="590" y1="53" x2="810" y2="53" stroke="#e5e7eb" strokeWidth="1"/>
          <text x="594" y="67" fontSize="9" fill="#374151">    C_NAME        VARCHAR(50)</text>
          <text x="594" y="80" fontSize="9" fill="#374151">    C_ADDRESS     VARCHAR(100)</text>
          <text x="594" y="93" fontSize="9" fill="#374151">    C_EXP         INT</text>
          <text x="594" y="106" fontSize="9" fill="#374151">    C_LEADTIME    INT DEFAULT 7</text>
          <text x="594" y="119" fontSize="9" fill="#374151">    C_PREFERRED   VARCHAR(10)</text>
          <text x="594" y="132" fontSize="9" fill="#374151">    C_EMAIL       VARCHAR(50)</text>

          {/* ── SALES ── */}
          <rect x="10" y="255" width="220" height="120" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <rect x="10" y="255" width="220" height="22" fill="#374151" rx="2"/>
          <rect x="10" y="275" width="220" height="2" fill="#374151"/>
          <text x="120" y="270" textAnchor="middle" fontSize="11" fontWeight="bold" fill="white">SALES</text>
          <text x="14" y="292" fontSize="9" fill="#166534" fontWeight="bold">PK  S_ID    INT GENERATED ALWAYS</text>
          <line x1="10" y1="297" x2="230" y2="297" stroke="#e5e7eb" strokeWidth="1"/>
          <text x="14" y="311" fontSize="9" fill="#374151">    S_MED_NAME  VARCHAR(100)</text>
          <text x="14" y="324" fontSize="9" fill="#374151">    S_DATE      DATE</text>
          <text x="14" y="337" fontSize="9" fill="#374151">    S_QTY       INT</text>
          <text x="14" y="350" fontSize="9" fill="#374151">    S_TOTAL     DOUBLE</text>
          <text x="14" y="370" fontSize="8" fill="#9ca3af" fontStyle="italic">    Used by ForecastingHelper SMA</text>

          {/* ── PURCHASE_ORDERS ── */}
          <rect x="590" y="255" width="220" height="120" fill="white" stroke="#374151" strokeWidth="1.5" rx="2"/>
          <rect x="590" y="255" width="220" height="22" fill="#374151" rx="2"/>
          <rect x="590" y="275" width="220" height="2" fill="#374151"/>
          <text x="700" y="270" textAnchor="middle" fontSize="11" fontWeight="bold" fill="white">PURCHASE_ORDERS</text>
          <text x="594" y="292" fontSize="9" fill="#166534" fontWeight="bold">PK  PO_ID    INT GENERATED ALWAYS</text>
          <line x1="590" y1="297" x2="810" y2="297" stroke="#e5e7eb" strokeWidth="1"/>
          <text x="594" y="311" fontSize="9" fill="#374151">    PO_MED_NAME  VARCHAR(100)</text>
          <text x="594" y="324" fontSize="9" fill="#374151">    PO_SUPPLIER  VARCHAR(100)</text>
          <text x="594" y="337" fontSize="9" fill="#374151">    PO_QTY       INT</text>
          <text x="594" y="350" fontSize="9" fill="#dc2626">    PO_STATUS    VARCHAR(20)  'Pending'</text>
          <text x="594" y="363" fontSize="9" fill="#374151">    PO_DATE      DATE</text>

          {/* ── Relationship lines ── */}
          {/* MEDICINE bottom-left → SALES top (M_NAME = S_MED_NAME) */}
          <line x1="340" y1="195" x2="340" y2="235" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4"/>
          <line x1="340" y1="235" x2="120" y2="235" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4"/>
          <line x1="120" y1="235" x2="120" y2="255" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4" markerEnd="url(#rel)"/>
          <text x="195" y="230" fontSize="8" fill="#9ca3af" fontStyle="italic">M_NAME = S_MED_NAME</text>
          <text x="260" y="218" fontSize="8" fill="#f87171" fontWeight="bold">⚠ text match, no FK</text>

          {/* MEDICINE bottom-right → PO (M_NAME = PO_MED_NAME) */}
          <line x1="480" y1="195" x2="480" y2="235" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4"/>
          <line x1="480" y1="235" x2="700" y2="235" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4"/>
          <line x1="700" y1="235" x2="700" y2="255" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4" markerEnd="url(#rel)"/>
          <text x="490" y="230" fontSize="8" fill="#9ca3af" fontStyle="italic">M_NAME = PO_MED_NAME</text>

          {/* MEDICINE right → COMPANY (M_COMPANY = C_NAME) */}
          <line x1="540" y1="100" x2="590" y2="100" stroke="#9ca3af" strokeWidth="1.5" strokeDasharray="5,4" markerEnd="url(#rel)"/>
          <text x="543" y="94" fontSize="8" fill="#9ca3af" fontStyle="italic">M_COMPANY = C_NAME</text>

          {/* AGENTS standalone note */}
          <text x="120" y="155" textAnchor="middle" fontSize="8" fill="#9ca3af" fontStyle="italic">standalone</text>
          <text x="120" y="166" textAnchor="middle" fontSize="8" fill="#9ca3af" fontStyle="italic">(used by LoginFrame)</text>
        </svg>
      </div>
      <FigCaption n="Figure 2" children="Database Entity-Relationship Diagram — 5 tables, text-based relationships (no FK constraints)" />
    </figure>
  )
}

// ── DIAGRAM 3: Authentication Flow ───────────────────────────────────────────

function AuthFlowDiagram() {
  const step = (label: string, sub?: string, type: 'rect'|'diamond'|'term' = 'rect', color = 'border-gray-400 bg-white text-gray-800') => ({ label, sub, type, color })
  const steps = [
    step('START: User opens LoginFrame', undefined, 'term', 'border-gray-700 bg-gray-800 text-white'),
    step('User enters A_NAME + Password', 'txtUserName + l_password fields'),
    step('btnLoginMouseClicked() fires', 'Builds SQL via string concat'),
    step('SELECT * FROM User1.AGENTS\nWHERE A_NAME=? AND A_PASSWORD=?', 'DatabaseHelper.getConnection()', 'rect', 'border-blue-300 bg-blue-50 text-blue-900'),
    step('Result found?', undefined, 'diamond', 'border-amber-400 bg-amber-50 text-amber-900'),
    step('Extract A_ROLE from ResultSet', 'Admin / Pharmacist / Technician'),
    step('new DashboardFrame(role).setVisible(true)', 'LoginFrame.dispose()', 'rect', 'border-green-400 bg-green-50 text-green-900'),
    step('applyRolePermissions()', 'Disables buttons per role'),
    step('loadAlerts() — Dashboard ready', undefined, 'term', 'border-gray-700 bg-gray-800 text-white'),
  ]
  return (
    <figure className="my-6">
      <div className="border border-gray-300 rounded bg-white p-6">
        <div className="flex flex-col items-center gap-0 max-w-md mx-auto">
          {steps.map((s, i) => (
            <div key={i} className="flex flex-col items-center w-full">
              {s.type === 'diamond' ? (
                <div className="w-full flex flex-col items-center">
                  <div className={`border-2 ${s.color} px-4 py-2 text-[12px] font-semibold text-center w-64 rotate-0 relative`}
                    style={{ clipPath: 'polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%)' }}>
                    <div className="invisible">{s.label}</div>
                  </div>
                  <div className={`border-2 ${s.color} px-5 py-2.5 text-[12px] font-semibold text-center -mt-1`}
                    style={{ transform: 'skewX(-5deg)' }}>
                    {s.label}
                  </div>
                  <div className="flex w-full justify-around mt-1 mb-1">
                    <div className="flex flex-col items-center">
                      <div className="w-px h-5 bg-gray-300"/>
                      <span className="text-[10px] text-red-500 font-medium mb-1">No</span>
                      <div className={`border-2 border-red-300 bg-red-50 text-red-800 px-4 py-2 text-[11px] rounded text-center`}>
                        Show &quot;Invalid Username/Password&quot;<br/>
                        <span className="text-[10px] text-red-500">JOptionPane.showMessageDialog()</span>
                      </div>
                    </div>
                    <div className="flex flex-col items-center">
                      <div className="w-px h-5 bg-gray-300"/>
                      <span className="text-[10px] text-green-600 font-medium mb-1">Yes ↓</span>
                    </div>
                  </div>
                </div>
              ) : (
                <div className={`border-2 ${s.color} px-4 py-2.5 text-[12px] font-semibold text-center w-full rounded ${s.type === 'term' ? 'rounded-full' : ''}`}>
                  <div className="whitespace-pre-line">{s.label}</div>
                  {s.sub && <div className="text-[10px] font-normal opacity-60 mt-0.5">{s.sub}</div>}
                </div>
              )}
              {i < steps.length - 1 && s.type !== 'diamond' && (
                <div className="flex flex-col items-center">
                  <div className="w-px h-4 bg-gray-300"/>
                  <div className="w-0 h-0 border-l-4 border-r-4 border-t-4 border-l-transparent border-r-transparent border-t-gray-400"/>
                </div>
              )}
              {s.type === 'diamond' && (
                <div className="flex flex-col items-center self-center">
                  <div className="w-px h-4 bg-gray-300 ml-36"/>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
      <FigCaption n="Figure 3" children="Authentication and RBAC Flow — LoginFrame to DashboardFrame" />
    </figure>
  )
}

// ── TOC ───────────────────────────────────────────────────────────────────────

const TOC = [
  { id: 'intro',       n: '1.',    label: 'Introduction' },
  { id: 'arch',        n: '2.',    label: 'System Architecture' },
  { id: 'oop',         n: '3.',    label: 'OOP Concepts Applied' },
  { id: 'oop-inh',     n: '3.1',   label: 'Inheritance',           indent: true },
  { id: 'oop-enc',     n: '3.2',   label: 'Encapsulation',         indent: true },
  { id: 'oop-poly',    n: '3.3',   label: 'Polymorphism',          indent: true },
  { id: 'oop-abs',     n: '3.4',   label: 'Abstraction',           indent: true },
  { id: 'database',    n: '4.',    label: 'Database Design' },
  { id: 'auth',        n: '5.',    label: 'Authentication & RBAC' },
  { id: 'forecast',    n: '6.',    label: 'Demand Forecasting' },
  { id: 'tx',          n: '7.',    label: 'Transaction Management' },
  { id: 'pos',         n: '8.',    label: 'Point-of-Sale Module' },
  { id: 'weak',        n: '9.',    label: 'Known Weaknesses' },
  { id: 'test',        n: '10.',   label: 'Testing' },
  { id: 'conc',        n: '11.',   label: 'Conclusion' },
  { id: 'app',         n: '12.',   label: 'Appendix' },
]

// ── REPORT ────────────────────────────────────────────────────────────────────

export default function Report() {
  return (
    <div className="min-h-screen bg-gray-100 text-gray-900">
      {/* Nav bar */}
      <header className="sticky top-0 z-10 bg-white border-b border-gray-300 flex items-center justify-between px-6 h-11 print:hidden">
        <span className="font-bold text-sm tracking-tight">PharmTrack — Project Report</span>
        <div className="flex gap-3 items-center">
          <Link href="/" className="text-gray-500 text-xs hover:text-gray-900">← Slides</Link>
          <button onClick={() => window.print()} className="text-xs bg-gray-900 text-white px-3 py-1 rounded hover:bg-gray-700">
            Print / PDF
          </button>
        </div>
      </header>

      <div className="max-w-5xl mx-auto flex gap-0">
        {/* Sidebar */}
        <aside className="w-52 shrink-0 hidden xl:block print:hidden">
          <div className="sticky top-14 pt-6 pb-6 pr-3">
            <p className="text-[9px] font-bold uppercase tracking-widest text-gray-400 mb-2">Contents</p>
            {TOC.map((t) => (
              <a key={t.id} href={`#${t.id}`}
                className={`flex gap-1.5 text-[11px] py-0.5 text-gray-500 hover:text-gray-900 transition-colors ${t.indent ? 'pl-4' : 'font-medium'}`}>
                <span className="text-gray-300 w-5 shrink-0">{t.n}</span>
                {t.label}
              </a>
            ))}
          </div>
        </aside>

        {/* Document */}
        <main className="flex-1 min-w-0">

          {/* ── COVER PAGE ── */}
          <div className="bg-white border-x border-gray-300 mb-0 print:border-0">
            <div className="px-16 py-14 text-center border-b-2 border-gray-900">
              <p className="text-xs font-bold uppercase tracking-[0.25em] text-gray-500 mb-1">
                Object-Oriented Programming with Java
              </p>
              <p className="text-xs text-gray-400 mb-8">Course Project — Final Report</p>

              <div className="w-20 h-1 bg-gray-900 mx-auto mb-8"/>

              <h1 className="text-3xl font-black text-gray-900 leading-tight mb-2">PharmTrack</h1>
              <h2 className="text-lg font-normal text-gray-600 mb-10">
                Pharmacy Inventory Management System
              </h2>

              <div className="w-20 h-px bg-gray-300 mx-auto mb-10"/>

              <table className="mx-auto text-sm border border-gray-300 w-80">
                <tbody>
                  {[
                    ['Student', '[Your Name]'],
                    ['Course', 'OOP with Java'],
                    ['Semester', 'Spring 2026'],
                    ['Language', 'Java 17 + Apache Derby'],
                  ].map(([k, v]) => (
                    <tr key={k} className="border-b border-gray-200 last:border-0">
                      <td className="px-4 py-2 font-semibold text-gray-600 text-left w-28">{k}</td>
                      <td className="px-4 py-2 text-gray-800 text-left">{v}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* TOC inline */}
            <div className="px-16 py-10 border-b border-gray-200">
              <h2 className="text-base font-bold text-gray-900 mb-4 uppercase tracking-widest text-xs">Table of Contents</h2>
              <div className="grid grid-cols-2 gap-x-8 gap-y-0.5">
                {TOC.map((t) => (
                  <a key={t.id} href={`#${t.id}`}
                    className={`flex items-baseline gap-2 text-[13px] text-gray-600 hover:text-gray-900 py-0.5 ${t.indent ? 'pl-5' : ''}`}>
                    <span className={`shrink-0 ${t.indent ? 'text-gray-400 text-[11px]' : 'font-semibold'}`}>{t.n}</span>
                    <span className="flex-1 border-b border-dotted border-gray-300">{t.label}</span>
                  </a>
                ))}
              </div>
            </div>
          </div>

          {/* ── MAIN CONTENT ── */}
          <div className="bg-white border-x border-b border-gray-300 px-16 py-10 print:border-0">

            {/* 1 */}
            <H2 id="intro" n="1.">Introduction</H2>
            <P>PharmTrack is a desktop application developed in Java to address operational challenges in retail pharmacy inventory management. A typical pharmacy stocks hundreds of distinct medications — each with its own stock level, unit cost, expiration date, reorder threshold, dosage form, and supplier relationship. Without structured tooling, staff must rely on manual checks and spreadsheets, which scale poorly and fail to provide proactive alerts.</P>
            <P>The application addresses three core operational problems: stock-outs (running out of critical medication before restocking), expired medication on shelves (a safety and compliance risk), and reactive rather than predictive inventory management. PharmTrack solves all three through a demand forecasting engine, automated alerts, and structured procurement workflows.</P>
            <P>The system is implemented in a single Java package (<IC>pharmacyinventorymanagement</IC>) containing eleven classes: two static utility helpers, one application entry point, and eight Swing frame classes — one for each functional screen. The database is Apache Derby, accessed via JDBC and managed entirely by <IC>DatabaseHelper</IC>.</P>

            {/* 2 */}
            <H2 id="arch" n="2.">System Architecture</H2>
            <P>PharmTrack uses a simplified two-layer architecture. The <strong>presentation layer</strong> consists of eight Swing frames that handle user interaction and display. The <strong>business logic / data access layer</strong> is concentrated in two utility classes: <IC>DatabaseHelper</IC> for all database operations, and <IC>ForecastingHelper</IC> for analytics. No formal separation of model from view exists within individual frames — a known limitation addressed in section 9.</P>
            <H3 id="arch-startup" n="2.1">Startup Sequence</H3>
            <P><IC>PharmacyInventoryManagement.main()</IC> calls <IC>DatabaseHelper.initializeDatabase()</IC>, which creates all five tables if they do not exist, then launches <IC>SplashFrame</IC>. The splash screen animates a progress bar from 0–100% in 25ms increments before opening <IC>LoginFrame</IC>. All subsequent navigation follows a dispose-and-open pattern: each frame disposes itself and instantiates the next, keeping exactly one window active at a time.</P>
            <DataTable
              headers={['Class', 'Type', 'Responsibility']}
              rows={[
                ['PharmacyInventoryManagement', 'Entry point', 'Calls initializeDatabase(), launches SplashFrame'],
                ['SplashFrame', 'JFrame subclass', 'Animated loading screen → opens LoginFrame'],
                ['LoginFrame', 'JFrame subclass', 'Credential validation, role extraction, RBAC handoff'],
                ['DashboardFrame', 'JFrame subclass', 'Navigation hub, RBAC enforcement, live alert list'],
                ['MedicineFrame', 'JFrame subclass', 'Medicine CRUD, custom row colour renderer'],
                ['AgentsFrame', 'JFrame subclass', 'Staff account CRUD on AGENTS table'],
                ['CompanyFrame', 'JFrame subclass', 'Supplier directory with lead-time tracking'],
                ['SellingFrame', 'JFrame subclass', 'POS billing, stock deduction, sale logging'],
                ['PurchaseOrderFrame', 'JFrame subclass', 'PO lifecycle + atomic goods receiving'],
                ['DatabaseHelper', 'Utility class', 'Connection management, schema init, ResultSet adapter'],
                ['ForecastingHelper', 'Utility class', 'SMA forecasting, low-stock + expiry alerts, valuation'],
              ]}
            />

            {/* 3 */}
            <H2 id="oop" n="3.">OOP Concepts Applied</H2>
            <P>The UML class diagram below shows the inheritance hierarchy and key dependencies between classes.</P>
            <UMLClassDiagram />

            <H3 id="oop-inh" n="3.1">Inheritance</H3>
            <P>All eight UI frame classes extend <IC>javax.swing.JFrame</IC>. This gives every frame window management capabilities — <IC>setVisible()</IC>, <IC>dispose()</IC>, <IC>pack()</IC>, <IC>setLocationRelativeTo()</IC>, event dispatch, and layout management — without any additional code. The hierarchy is deliberately flat: no shared intermediate class was created between JFrame and the frames because each screen has a completely different form layout and business domain.</P>
            <Code label="Class declaration pattern (all 8 frames)">{`public class DashboardFrame    extends javax.swing.JFrame { ... }
public class LoginFrame         extends javax.swing.JFrame { ... }
public class MedicineFrame      extends javax.swing.JFrame { ... }
public class SellingFrame       extends javax.swing.JFrame { ... }
public class PurchaseOrderFrame extends javax.swing.JFrame { ... }
// ... AgentsFrame, CompanyFrame, SplashFrame`}</Code>
            <Note><strong>Why this matters:</strong> Without JFrame inheritance, each frame would need hundreds of lines of low-level AWT windowing code. Inheritance eliminates this entirely.</Note>

            <H3 id="oop-enc" n="3.2">Encapsulation</H3>
            <P><strong>DatabaseHelper.getConnection()</strong> hides whether Derby is running as a network server or embedded. Callers never know which mode is active:</P>
            <Code label="DatabaseHelper.java — lines 18–25">{`public static Connection getConnection() throws SQLException {
    try {
        return DriverManager.getConnection(DB_URL, USER, PASS);
        // DB_URL = "jdbc:derby://localhost:1527/PharmaDb"
    } catch (SQLException e) {
        // Network server not running — fall back to embedded
        return DriverManager.getConnection(
            "jdbc:derby:PharmaDb;create=true", USER, PASS
        );
    }
}`}</Code>
            <P><strong>DashboardFrame.applyRolePermissions()</strong> encapsulates all RBAC logic in one private method called once from the constructor. All access rules are auditable in 10 lines:</P>
            <Code label="DashboardFrame.java — lines 22–30">{`private void applyRolePermissions() {
    if ("Technician".equalsIgnoreCase(userRole)) {
        btnAgents.setEnabled(false);
        btnCompany.setEnabled(false);
        btnPO.setEnabled(false);
    } else if ("Pharmacist".equalsIgnoreCase(userRole)) {
        btnAgents.setEnabled(false);
    }
    // Admin: all buttons remain enabled
}`}</Code>
            <P><strong>ForecastingHelper</strong> encapsulates SQL queries, date arithmetic, and the SMA algorithm behind clean method names. Callers interact with business concepts, not implementation details.</P>

            <H3 id="oop-poly" n="3.3">Polymorphism</H3>
            <P>The primary example is the custom <IC>TableCellRenderer</IC> in <IC>MedicineFrame</IC>. Swing's <IC>JTable</IC> calls <IC>getTableCellRendererComponent()</IC> on the renderer for every cell it paints. PharmTrack overrides this method to apply pharmacy-specific visual rules at runtime:</P>
            <Code label="MedicineFrame.java — applyTableHighlighters()">{`medicine_table.setDefaultRenderer(Object.class,
    new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            // col 2 = M_QUANTITY, col 12 = M_THRESHOLD
            int qty = Integer.parseInt(table.getValueAt(row, 2).toString());
            int threshold = 10;
            try { threshold = Integer.parseInt(
                    table.getValueAt(row, 12).toString()); }
            catch (Exception e) { /* use default 10 */ }

            java.sql.Date expDate = (java.sql.Date) table.getValueAt(row, 4);

            if (!isSelected) {
                if (qty <= threshold)
                    c.setBackground(new Color(255, 200, 200)); // red
                else if (expDate != null &&
                         expDate.toLocalDate().isBefore(
                             LocalDate.now().plusDays(30)))
                    c.setBackground(new Color(255, 255, 180)); // yellow
                else
                    c.setBackground(Color.WHITE);
            }
            return c;
        }
    });`}</Code>
            <P>This is runtime polymorphism: when JTable calls the method, the JVM dispatches to the overridden version based on the actual object type. The <IC>isSelected</IC> guard prevents the custom colours from fighting with Swing's row-selection highlight.</P>

            <H3 id="oop-abs" n="3.4">Abstraction</H3>
            <P><strong>DatabaseHelper.resultSetToTableModel()</strong> abstracts converting a JDBC <IC>ResultSet</IC> into a Swing <IC>DefaultTableModel</IC> — reading column count from <IC>ResultSetMetaData</IC>, iterating rows, building nested Vectors — behind one method call:</P>
            <Code label="DatabaseHelper.java — lines 27–53">{`public static DefaultTableModel resultSetToTableModel(ResultSet rs) {
    try {
        ResultSetMetaData metaData = rs.getMetaData();
        int numberOfColumns = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= numberOfColumns; column++)
            columnNames.add(metaData.getColumnLabel(column));

        Vector<Vector<Object>> rows = new Vector<>();
        while (rs.next()) {
            Vector<Object> newRow = new Vector<>();
            for (int i = 1; i <= numberOfColumns; i++)
                newRow.add(rs.getObject(i));
            rows.add(newRow);
        }
        return new DefaultTableModel(rows, columnNames);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}`}</Code>
            <P><strong>ForecastingHelper.predictDemand()</strong> abstracts the SMA algorithm. Callers pass a medicine name and receive an integer — no awareness of SQL, date windows, or the <IC>Math.ceil()</IC> rounding:</P>
            <Code label="ForecastingHelper.java — lines 13–43">{`public static int predictDemand(String medicineName) {
    int totalQty = 0, count = 0;
    try (Connection conn = DatabaseHelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(
             "SELECT S_QTY FROM User1.SALES " +
             "WHERE S_MED_NAME = ? AND S_DATE >= ?")) {

        pstmt.setString(1, medicineName);
        pstmt.setDate(2, java.sql.Date.valueOf(
                LocalDate.now().minusDays(30)));

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { totalQty += rs.getInt("S_QTY"); count++; }
        }
        if (count == 0) return 0;

        double avgDaily = (double) totalQty / 30.0;
        return (int) Math.ceil(avgDaily * 7); // conservative ceiling
    } catch (SQLException e) { e.printStackTrace(); return 0; }
}`}</Code>

            {/* 4 */}
            <H2 id="database" n="4.">Database Design</H2>
            <P>The application uses five tables in Apache Derby, all created by <IC>DatabaseHelper.initializeDatabase()</IC> on first run. All tables are in the <IC>User1</IC> schema. Relationships between tables are enforced by application logic rather than foreign key constraints — a known limitation discussed in section 9.</P>
            <DBSchemaDiagram />
            <H3 id="db-migration" n="4.1">Schema Auto-Migration</H3>
            <P><IC>initializeDatabase()</IC> uses a try/catch pattern: it attempts <IC>CREATE TABLE</IC> first; if the table already exists, it attempts to add new columns via <IC>ALTER TABLE ADD COLUMN</IC> in a loop. Each column addition is wrapped in its own try/catch so that already-existing columns are silently skipped. This allows the schema to evolve across application versions without dropping and recreating tables.</P>

            {/* 5 */}
            <H2 id="auth" n="5.">Authentication & Role-Based Access Control</H2>
            <P>The diagram below shows the complete authentication and RBAC flow from login to dashboard:</P>
            <AuthFlowDiagram />
            <P>On login, <IC>LoginFrame</IC> queries the AGENTS table for a matching username/password using string concatenation (see section 9.1 for the security implication). If a row is found, the <IC>A_ROLE</IC> field is extracted and passed to <IC>DashboardFrame</IC>'s constructor. The role is stored in a private field and used once by <IC>applyRolePermissions()</IC>.</P>
            <DataTable
              headers={['Role', 'Medicines', 'Selling', 'Agents', 'Companies', 'Purchase Orders']}
              rows={[
                ['Admin',      '✓', '✓', '✓', '✓', '✓'],
                ['Pharmacist', '✓', '✓', '✗', '✓', '✓'],
                ['Technician', '✓', '✓', '✗', '✗', '✗'],
              ]}
            />

            {/* 6 */}
            <H2 id="forecast" n="6.">Demand Forecasting Algorithm</H2>
            <P>PharmTrack uses a Simple Moving Average (SMA) with a 30-day lookback window and 7-day forward projection. The formula is:</P>
            <div className="bg-gray-50 border border-gray-200 rounded p-4 my-4 text-center font-mono text-sm">
              predicted_demand = ⌈ Σ(sales in last 30 days) ÷ 30 × 7 ⌉
            </div>
            <P>The use of <IC>Math.ceil()</IC> rather than truncation introduces a deliberate conservative bias: it is better to slightly overestimate demand (and hold a small buffer) than to underestimate and run short. An alert is raised when either <IC>currentQty &lt; predicted</IC> or <IC>currentQty &lt; 10</IC> (hardcoded floor). The <IC>getInventoryValue()</IC> method computes Σ(M_QUANTITY × M_UNIT_COST) across all medicine records.</P>

            {/* 7 */}
            <H2 id="tx" n="7.">Transaction Management</H2>
            <P>Receiving a purchase order requires updating two records atomically: the PO status must change to &quot;Received&quot; and the medicine quantity must increment. If either write fails, both must roll back. <IC>PurchaseOrderFrame.receiveStock()</IC> uses a JDBC transaction:</P>
            <Code label="PurchaseOrderFrame.java — receiveStock(), lines 70–101">{`try (Connection conn = DatabaseHelper.getConnection()) {
    conn.setAutoCommit(false);   // BEGIN TRANSACTION
    try {
        PreparedStatement updatePO = conn.prepareStatement(
            "UPDATE User1.PURCHASE_ORDERS " +
            "SET PO_STATUS = 'Received' WHERE PO_ID = ?");
        updatePO.setInt(1, poId);
        updatePO.executeUpdate();

        PreparedStatement updateMed = conn.prepareStatement(
            "UPDATE User1.MEDICINE " +
            "SET M_QUANTITY = M_QUANTITY + ? WHERE M_NAME = ?");
        updateMed.setInt(1, qty);
        updateMed.setString(2, medName);
        int updatedRows = updateMed.executeUpdate();

        if (updatedRows == 0)
            // Medicine name mismatch — warn but still commit PO
            JOptionPane.showMessageDialog(this,
                "Warning: Medicine not found in inventory.");

        conn.commit();   // BOTH writes persist atomically
    } catch (SQLException e) {
        conn.rollback(); // NEITHER write persists
        throw e;
    }
}`}</Code>
            <P>An idempotency guard (<IC>if ("Received".equals(status)) return;</IC>) at the top of the method prevents a PO from being received twice.</P>

            {/* 8 */}
            <H2 id="pos" n="8.">Point-of-Sale Module</H2>
            <P><IC>SellingFrame.updateQty()</IC> validates stock availability before deducting, then calls <IC>recordSale()</IC> to log the transaction. Every sale feeds the forecasting engine — <IC>ForecastingHelper.predictDemand()</IC> reads the same SALES table.</P>
            <Code label="SellingFrame.java — updateQty() + recordSale()">{`public boolean updateQty() {
    int orderQty = Integer.valueOf(b_quantity.getText());
    if (mQty >= orderQty) {
        int newQty = mQty - orderQty;
        String updateQ = "Update User1.MEDICINE set M_QUANTITY = "
            + newQty + " where M_ID = " + medId; // ← SQL injection risk
        Statement add = Con.createStatement();
        add.executeUpdate(updateQ);
        recordSale(medId, b_medName.getText(), orderQty, price * orderQty);
        return true;
    }
    JOptionPane.showMessageDialog(this, "Insufficient stock!");
    return false;
}

public void recordSale(int medId, String medName, int qty, double total) {
    try (PreparedStatement pstmt = conn.prepareStatement(
             "INSERT INTO User1.SALES (S_MED_NAME,S_DATE,S_QTY,S_TOTAL)"
           + " VALUES (?,?,?,?)")) {
        pstmt.setString(1, medName);
        pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
        pstmt.setInt(3, qty);
        pstmt.setDouble(4, total);
        pstmt.executeUpdate();
    }
}`}</Code>

            {/* 9 */}
            <H2 id="weak" n="9.">Known Weaknesses</H2>
            <Warn>This section documents real vulnerabilities and design issues. Identifying them honestly demonstrates engineering maturity.</Warn>

            <H3 id="w1" n="9.1">SQL Injection in LoginFrame</H3>
            <P>The login query on line 238 builds SQL via string concatenation of raw user input:</P>
            <Code label="LoginFrame.java line 238 — VULNERABLE">{`String selectQ = "select * from User1.AGENTS " +
    "where A_NAME='" + txtUserName.getText() + "'" +
    " and A_PASSWORD = '" + pwd + "'";`}</Code>
            <P>Entering <IC>admin&apos; --</IC> as the username produces a valid SQL query that bypasses the password check entirely. The fix is one line:</P>
            <Code label="Fixed version — PreparedStatement">{`PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM User1.AGENTS WHERE A_NAME=? AND A_PASSWORD=?");
ps.setString(1, txtUserName.getText());
ps.setString(2, pwd);`}</Code>

            <H3 id="w2" n="9.2">Plain-text Password Storage</H3>
            <P><IC>AGENTS.A_PASSWORD</IC> is <IC>VARCHAR(50)</IC> storing plain text. The default admin password <IC>admin123</IC> is visible in the source code of <IC>initializeDatabase()</IC>. BCrypt with a per-user salt is the minimum requirement for any real deployment.</P>

            <H3 id="w3" n="9.3">No Foreign Key Constraints</H3>
            <P>SALES and PURCHASE_ORDERS reference MEDICINE by the medicine name string rather than by <IC>M_ID</IC>. Renaming or deleting a medicine silently orphans its historical records. The forecasting engine will silently miss sales data for renamed medicines.</P>

            <H3 id="w4" n="9.4">Business Logic Mixed with UI</H3>
            <P>Methods like <IC>updateQty()</IC> mix stock validation rules with <IC>JOptionPane</IC> dialogs. This makes the business logic untestable in isolation. A service layer would separate the two concerns.</P>

            <H3 id="w5" n="9.5">Forecasting Threshold Inconsistency</H3>
            <P><IC>ForecastingHelper.getLowStockAlerts()</IC> uses a hardcoded threshold of 10, while <IC>MedicineFrame.applyTableHighlighters()</IC> reads <IC>M_THRESHOLD</IC> from column 12. The dashboard alerts and the table colours use different threshold sources for the same concept.</P>

            {/* 10 */}
            <H2 id="test" n="10.">Testing</H2>
            <P>The application was verified through structured manual end-to-end testing. No automated unit tests were written.</P>
            <DataTable
              headers={['Test Scenario', 'Expected Result', 'Pass']}
              rows={[
                ['Login with valid Admin credentials', 'Dashboard opens, all buttons enabled', '✓'],
                ['Login with invalid password', 'JOptionPane: "Invalid Username/Password"', '✓'],
                ['Login as Technician', 'Agents, Company, PO buttons disabled', '✓'],
                ['Login as Pharmacist', 'Agents button disabled, others enabled', '✓'],
                ['Add new medicine (all fields)', 'Row appears in table immediately', '✓'],
                ['Update medicine quantity', 'Table reflects new value', '✓'],
                ['Delete medicine by ID', 'Row removed from table', '✓'],
                ['Sell with sufficient stock', 'M_QUANTITY decrements, sale logged to SALES', '✓'],
                ['Sell with insufficient stock', 'Error dialog, no database change', '✓'],
                ['Create purchase order', 'PO appears with status "Pending"', '✓'],
                ['Receive pending PO', 'Status = "Received", M_QUANTITY incremented', '✓'],
                ['Receive already-received PO', '"This order is already received" message', '✓'],
                ['Medicine qty ≤ threshold', 'Row highlighted red in table', '✓'],
                ['Medicine expiring < 30 days', 'Row highlighted yellow in table', '✓'],
                ['Dashboard alert list', 'Shows expiry + low-stock alerts + inventory value', '✓'],
              ]}
            />

            {/* 11 */}
            <H2 id="conc" n="11.">Conclusion</H2>
            <P>PharmTrack demonstrates how the four OOP pillars solve concrete engineering problems:</P>
            <UL items={[
              <><strong>Inheritance</strong> eliminated all window management boilerplate by deriving all 8 frames from JFrame.</>,
              <><strong>Encapsulation</strong> in DatabaseHelper, ForecastingHelper, and applyRolePermissions() created stable, auditable interfaces that hide complexity from callers.</>,
              <><strong>Polymorphism</strong> (TableCellRenderer override) enabled context-sensitive visual behaviour that adapts to each row's data at runtime without conditional logic in the caller.</>,
              <><strong>Abstraction</strong> in resultSetToTableModel() and predictDemand() decoupled business logic from its implementation details, reducing repetition across all frames.</>,
            ]} />
            <P>The project also revealed the gap between a working application and a production-ready one. The SQL injection vulnerability, plain-text passwords, and missing foreign key constraints would be unacceptable in deployment — but identifying and explaining them demonstrates a more complete understanding than code that never examines its own flaws.</P>
            <P>Future work would prioritise: replacing all string-concatenated SQL with PreparedStatements, BCrypt password hashing, foreign key constraints, a proper service layer to enable unit testing, and JUnit tests for the forecasting algorithm.</P>

            {/* 12 */}
            <H2 id="app" n="12.">Appendix</H2>
            <H3 n="A." id="app-creds">Default Configuration</H3>
            <DataTable
              headers={['Setting', 'Value']}
              rows={[
                ['Default Admin username', 'Admin'],
                ['Default Admin password', 'admin123'],
                ['Default Admin ID', '1'],
                ['Derby schema', 'User1'],
                ['Derby network URL', 'jdbc:derby://localhost:1527/PharmaDb'],
                ['Derby embedded fallback', 'jdbc:derby:PharmaDb;create=true'],
                ['Forecasting window', '30 days lookback, 7 days forward'],
                ['Expiry alert threshold', '30 days from today'],
                ['Low-stock hardcoded floor', '10 units'],
                ['Default supplier lead time', '7 days'],
              ]}
            />
            <H3 n="B." id="app-build">Build & Run</H3>
            <Code>{`# Requirements: Java 17+, Apache Derby in classpath

# Using NetBeans:
#   File → Open Project → Run (F6)

# Using Ant CLI:
ant run

# Database is created automatically on first launch.
# To run Derby as a network server (optional):
java -jar derby/lib/derbyrun.jar server start`}</Code>

            <div className="mt-14 pt-5 border-t border-gray-300 text-gray-400 text-xs text-center">
              PharmTrack · Pharmacy Inventory Management System · OOP Java Project · Spring 2026
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}
