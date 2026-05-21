package pharmacyinventorymanagement;

// Programmatic icon library — draws scalable vector-style icons using Java2D.
// Icons read the parent component's foreground color at paint time, so they
// automatically match active (green), normal (slate), and disabled (dimmed) states.

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public final class PharmIcons {

    public static final int NAV  = 16;
    public static final int HDR  = 18;
    public static final int BTN  = 14;

    // ── Public factory ────────────────────────────────────────────────────────

    /** Returns an icon for the given sidebar key at nav size (16×16). */
    public static Icon nav(String key)  { return byKey(key, NAV); }

    /** Returns an icon for the given sidebar key at header size (18×18). */
    public static Icon hdr(String key)  { return byKey(key, HDR); }

    /** Returns an icon for a button at button size (14×14). */
    public static Icon btn(String key)  { return byKey(key, BTN); }

    /** Returns a larger icon (20×20) for decorative use such as login role cards. */
    public static Icon large(String key) { return byKey(key, 20); }

    /**
     * Convenience: sets the icon and gap on a JLabel, stripped of any emoji
     * already in the text. Call after navLabel() is created.
     */
    public static void apply(JLabel label, String key) {
        Icon icon = nav(key);
        if (icon == null) return;
        label.setIcon(icon);
        label.setIconTextGap(9);
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    // ── Key dispatch ──────────────────────────────────────────────────────────

    private static Icon byKey(String key, int size) {
        switch (key) {
            case "dash":    return home(size);
            case "med":     return pill(size);
            case "agents":  return person(size);
            case "comp":    return building(size);
            case "sell":    return receipt(size);
            case "po":      return box(size);
            case "sales":   return document(size);
            case "reports": return barChart(size);
            case "logout":  return exitArrow(size);
            case "add":     return plus(size);
            case "edit":    return pencil(size);
            case "delete":  return trash(size);
            case "refresh": return refresh(size);
            case "admin":   return shield(size);
            default:        return null;
        }
    }

    // ── Core helper — adaptive icon ───────────────────────────────────────────

    @FunctionalInterface
    private interface Painter {
        void paint(Graphics2D g, float x, float y, float s, Color c);
    }

    private static Icon make(int size, Painter p) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Color color = (c != null) ? c.getForeground() : new Color(203, 213, 225);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                p.paint(g2, x, y, size, color);
                g2.dispose();
            }
            @Override public int getIconWidth()  { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    // ── Icon drawings ─────────────────────────────────────────────────────────

    // House / Dashboard
    private static Icon home(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            float m  = sz * 0.08f;
            float cx = x + sz / 2f;
            float roofBot = y + sz * 0.52f;

            // Roof (filled triangle)
            Path2D roof = new Path2D.Float();
            roof.moveTo(x + m, roofBot);
            roof.lineTo(cx, y + m);
            roof.lineTo(x + sz - m, roofBot);
            roof.closePath();
            g.fill(roof);

            // Walls
            float wallW = sz * 0.62f;
            float wallH = sz * 0.40f;
            float wallX = cx - wallW / 2;
            g.setColor(c);
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Rectangle2D.Float(wallX, roofBot, wallW, wallH));

            // Door (filled)
            float dw = wallW * 0.28f, dh = wallH * 0.60f;
            g.fill(new Rectangle2D.Float(cx - dw / 2, roofBot + wallH - dh, dw, dh));
        });
    }

    // Capsule pill / Medicines
    private static Icon pill(int s) {
        return make(s, (g, x, y, sz, c) -> {
            float cx = x + sz / 2f, cy = y + sz / 2f;
            float rw = sz * 0.42f, rh = sz * 0.22f;
            float arc = rh * 2;

            // Left half filled
            g.setColor(c);
            g.fill(new Arc2D.Float(cx - rw, cy - rh, arc, arc, 90, 180, Arc2D.CHORD));

            // Right half outline only
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new RoundRectangle2D.Float(cx - rw, cy - rh, rw * 2, rh * 2, arc, arc));

            // Divider
            g.drawLine((int) cx, (int) (cy - rh), (int) cx, (int) (cy + rh));
        });
    }

    // Person silhouette / Agents
    private static Icon person(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float cx = x + sz / 2f;

            // Head (filled circle)
            float hr = sz * 0.17f;
            g.fill(new Ellipse2D.Float(cx - hr, y + sz * 0.06f, hr * 2, hr * 2));

            // Shoulders (arc)
            float sw = sz * 0.72f;
            g.draw(new Arc2D.Float(cx - sw / 2, y + sz * 0.46f, sw, sz * 0.46f, 0, 180, Arc2D.OPEN));
        });
    }

    // Building / Suppliers
    private static Icon building(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m  = sz * 0.08f;
            float bx = x + m, by = y + sz * 0.18f;
            float bw = sz - 2 * m, bh = sz - by + y - m;

            // Building body
            g.draw(new Rectangle2D.Float(bx, by, bw, bh));

            // Windows (2 columns × 2 rows)
            float ww = bw * 0.22f, wh = bh * 0.17f;
            float[] cols = {bx + bw * 0.14f, bx + bw * 0.58f};
            float[] rows = {by + bh * 0.18f, by + bh * 0.50f};
            for (float wy : rows)
                for (float wx : cols)
                    g.fill(new Rectangle2D.Float(wx, wy, ww, wh));

            // Door
            float dw = bw * 0.25f, dh = bh * 0.28f;
            g.draw(new Rectangle2D.Float(bx + bw / 2 - dw / 2, by + bh - dh, dw, dh));
        });
    }

    // Receipt / Billing
    private static Icon receipt(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float rw = sz * 0.70f, rh = sz * 0.82f;
            float rx = x + (sz - rw) / 2, ry = y + sz * 0.08f;

            // Body with jagged bottom (zigzag receipt tail)
            float zig = rw / 4;
            Path2D p = new Path2D.Float();
            p.moveTo(rx, ry);
            p.lineTo(rx + rw, ry);
            p.lineTo(rx + rw, ry + rh);
            p.lineTo(rx + rw - zig, ry + rh - zig * 0.5f);
            p.lineTo(rx + rw - 2 * zig, ry + rh);
            p.lineTo(rx + rw - 3 * zig, ry + rh - zig * 0.5f);
            p.lineTo(rx, ry + rh);
            p.closePath();
            g.draw(p);

            // Lines
            float lm = rw * 0.15f;
            float[] lineYs = {ry + rh * 0.28f, ry + rh * 0.50f, ry + rh * 0.68f};
            for (float ly : lineYs)
                g.drawLine((int) (rx + lm), (int) ly, (int) (rx + rw - lm), (int) ly);
        });
    }

    // Package box / Purchase Orders
    private static Icon box(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m  = sz * 0.08f;
            float bw = sz - 2 * m;
            float bh = sz * 0.58f;
            float bx = x + m, by = y + sz - m - bh;

            // Box body
            g.draw(new Rectangle2D.Float(bx, by, bw, bh));

            // Lid (open flap)
            float cx = bx + bw / 2;
            g.drawLine((int) bx,  (int) by, (int) cx, (int) (by - sz * 0.18f));
            g.drawLine((int) cx,  (int) (by - sz * 0.18f), (int) (bx + bw), (int) by);

            // Tape line down center of box
            g.drawLine((int) cx, (int) by, (int) cx, (int) (by + bh));
        });
    }

    // Document with folded corner / Sales History
    private static Icon document(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m    = sz * 0.10f;
            float dw   = sz * 0.70f, dh = sz * 0.82f;
            float dx   = x + (sz - dw) / 2, dy = y + m;
            float fold = dw * 0.28f;

            // Document outline with folded top-right corner
            Path2D doc = new Path2D.Float();
            doc.moveTo(dx, dy);
            doc.lineTo(dx + dw - fold, dy);
            doc.lineTo(dx + dw, dy + fold);
            doc.lineTo(dx + dw, dy + dh);
            doc.lineTo(dx, dy + dh);
            doc.closePath();
            g.draw(doc);

            // Corner fold crease
            g.drawLine((int) (dx + dw - fold), (int) dy, (int) (dx + dw - fold), (int) (dy + fold));
            g.drawLine((int) (dx + dw - fold), (int) (dy + fold), (int) (dx + dw), (int) (dy + fold));

            // Content lines
            float lm = dw * 0.16f;
            float[] lineYs = {dy + dh * 0.40f, dy + dh * 0.58f, dy + dh * 0.74f};
            for (float ly : lineYs)
                g.drawLine((int) (dx + lm), (int) ly, (int) (dx + dw - lm), (int) ly);
        });
    }

    // Bar chart / Reports
    private static Icon barChart(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            float m      = sz * 0.10f;
            float chartH = sz - 2 * m;
            float barW   = (sz - 2 * m - 2) / 4f;
            float gap    = barW * 0.45f;
            float baseY  = y + sz - m;
            float[] pct  = {0.50f, 0.85f, 0.65f};
            float startX = x + m;

            for (int i = 0; i < 3; i++) {
                float bh = chartH * pct[i];
                float bx = startX + i * (barW + gap);
                g.fill(new RoundRectangle2D.Float(bx, baseY - bh, barW, bh, 2, 2));
            }

            // Baseline
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine((int) (x + m - 1), (int) baseY, (int) (x + sz - m + 1), (int) baseY);
        });
    }

    // Exit arrow / Logout
    private static Icon exitArrow(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m  = sz * 0.12f;
            float cy = y + sz / 2f;
            float bh = sz - 2 * m;

            // Left frame (partial box — open on right)
            float doorW = sz * 0.46f;
            g.drawLine((int) (x + m + doorW), (int) (y + m), (int) (x + m), (int) (y + m));
            g.drawLine((int) (x + m),          (int) (y + m), (int) (x + m), (int) (y + m + bh));
            g.drawLine((int) (x + m),          (int) (y + m + bh), (int) (x + m + doorW), (int) (y + m + bh));

            // Horizontal arrow shaft
            float arrowStart = x + sz * 0.40f;
            float arrowEnd   = x + sz - m;
            g.drawLine((int) arrowStart, (int) cy, (int) arrowEnd, (int) cy);

            // Arrow head
            float ah = sz * 0.22f;
            g.drawLine((int) arrowEnd, (int) cy, (int) (arrowEnd - ah * 0.55f), (int) (cy - ah * 0.55f));
            g.drawLine((int) arrowEnd, (int) cy, (int) (arrowEnd - ah * 0.55f), (int) (cy + ah * 0.55f));
        });
    }

    // Plus sign / Add
    private static Icon plus(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float cx = x + sz / 2f, cy = y + sz / 2f, r = sz * 0.38f;
            g.drawLine((int) cx, (int) (cy - r), (int) cx, (int) (cy + r));
            g.drawLine((int) (cx - r), (int) cy, (int) (cx + r), (int) cy);
        });
    }

    // Pencil / Edit-Update
    private static Icon pencil(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m = sz * 0.1f;
            // Pencil body (rotated rectangle)
            float px = x + m, py = y + m;
            float pw = sz * 0.55f, ph = sz * 0.75f;
            AffineTransform orig = g.getTransform();
            g.rotate(Math.PI / 4, x + sz / 2.0, y + sz / 2.0);
            g.draw(new Rectangle2D.Float(x + sz * 0.22f, y + m, sz * 0.28f, sz * 0.65f));
            // Tip
            float tx = x + sz * 0.22f + sz * 0.14f;
            g.drawLine((int) tx, (int) (y + m + sz * 0.65f), (int) tx, (int) (y + m + sz * 0.78f));
            g.setTransform(orig);
        });
    }

    // Trash can / Delete
    private static Icon trash(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m   = sz * 0.10f;
            float cx  = x + sz / 2f;
            float tw  = sz * 0.62f, th = sz * 0.60f;
            float tx  = cx - tw / 2, ty = y + sz * 0.30f;

            // Lid
            g.drawLine((int) (tx - sz * 0.06f), (int) (ty - sz * 0.06f),
                       (int) (tx + tw + sz * 0.06f), (int) (ty - sz * 0.06f));
            // Handle on lid
            float hw = tw * 0.36f;
            g.drawLine((int) (cx - hw / 2), (int) (ty - sz * 0.06f),
                       (int) (cx - hw / 2), (int) (ty - sz * 0.18f));
            g.drawLine((int) (cx - hw / 2), (int) (ty - sz * 0.18f),
                       (int) (cx + hw / 2), (int) (ty - sz * 0.18f));
            g.drawLine((int) (cx + hw / 2), (int) (ty - sz * 0.18f),
                       (int) (cx + hw / 2), (int) (ty - sz * 0.06f));

            // Body (trapezoid)
            g.draw(new Rectangle2D.Float(tx, ty, tw, th));

            // Internal lines
            g.drawLine((int) cx, (int) (ty + th * 0.18f), (int) cx, (int) (ty + th * 0.82f));
            g.drawLine((int) (cx - tw * 0.22f), (int) (ty + th * 0.18f),
                       (int) (cx - tw * 0.22f), (int) (ty + th * 0.82f));
            g.drawLine((int) (cx + tw * 0.22f), (int) (ty + th * 0.18f),
                       (int) (cx + tw * 0.22f), (int) (ty + th * 0.82f));
        });
    }

    // Shield / Admin role
    private static Icon shield(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m  = sz * 0.10f;
            float cx = x + sz / 2f;
            Path2D sh = new Path2D.Float();
            sh.moveTo(x + m, y + m);
            sh.lineTo(x + sz - m, y + m);
            sh.lineTo(x + sz - m, y + sz * 0.55f);
            sh.quadTo(x + sz - m, y + sz * 0.82f, cx, y + sz - m);
            sh.quadTo(x + m, y + sz * 0.82f, x + m, y + sz * 0.55f);
            sh.closePath();
            g.draw(sh);
            // Inner check mark
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float ch = sz * 0.18f;
            g.drawLine((int)(cx - ch * 0.8f), (int)(y + sz * 0.50f),
                       (int)(cx - ch * 0.1f), (int)(y + sz * 0.62f));
            g.drawLine((int)(cx - ch * 0.1f), (int)(y + sz * 0.62f),
                       (int)(cx + ch * 0.8f), (int)(y + sz * 0.38f));
        });
    }

    // Circular arrow / Refresh-Clear
    private static Icon refresh(int s) {
        return make(s, (g, x, y, sz, c) -> {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float m  = sz * 0.12f;
            float cx = x + sz / 2f, cy = y + sz / 2f;
            float r  = sz / 2f - m;

            // 270° arc
            g.draw(new Arc2D.Float(cx - r, cy - r, r * 2, r * 2, 90, -270, Arc2D.OPEN));

            // Arrow head at end of arc (pointing right at top)
            float ah = sz * 0.20f;
            g.drawLine((int) cx, (int) (cy - r),
                       (int) (cx - ah * 0.7f), (int) (cy - r + ah * 0.7f));
            g.drawLine((int) cx, (int) (cy - r),
                       (int) (cx + ah * 0.7f), (int) (cy - r + ah * 0.7f));
        });
    }
}
