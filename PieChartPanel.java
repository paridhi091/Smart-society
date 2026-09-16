import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple pie chart with a legend, rendered directly with Java2D - no
 * external charting library required. Call setData() with an ordered
 * map of label -> value whenever the underlying data changes.
 */
public class PieChartPanel extends JPanel {
    private Map<String, Double> data = new LinkedHashMap<>();
    private final Map<String, Color> sliceColors = new LinkedHashMap<>();
    private final String title;

    private static final Color[] PALETTE = {
            new Color(76, 175, 80),   // green
            new Color(244, 67, 54),   // red
            new Color(255, 193, 7),   // amber
            new Color(33, 150, 243),  // blue
            new Color(156, 39, 176)   // purple
    };

    public PieChartPanel(String title) {
        this.title = title;
        setPreferredSize(new Dimension(420, 300));
        setBackground(Color.WHITE);
    }

    public void setData(Map<String, Double> newData) {
        this.data = newData;
        sliceColors.clear();
        int i = 0;
        for (String key : newData.keySet()) {
            sliceColors.put(key, PALETTE[i % PALETTE.length]);
            i++;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(title, width / 2 - g2.getFontMetrics().stringWidth(title) / 2, 20);

        double total = 0;
        for (double v : data.values()) total += v;

        if (data.isEmpty() || total <= 0) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString("No data yet - record some payments first.", 20, height / 2);
            return;
        }

        int diameter = Math.min(width - 160, height - 80);
        if (diameter < 50) diameter = 50;
        int pieX = 25;
        int pieY = 45;

        double startAngle = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = Math.max(0, entry.getValue());
            double angle = (value / total) * 360.0;
            g2.setColor(sliceColors.get(entry.getKey()));
            g2.fillArc(pieX, pieY, diameter, diameter, (int) Math.round(startAngle), (int) Math.round(angle) + 1);
            startAngle += angle;
        }
        g2.setColor(Color.DARK_GRAY);
        g2.drawOval(pieX, pieY, diameter, diameter);

        int legendX = pieX + diameter + 25;
        int legendY = pieY + 10;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            g2.setColor(sliceColors.get(entry.getKey()));
            g2.fillRect(legendX, legendY, 14, 14);
            g2.setColor(Color.BLACK);
            double pct = (entry.getValue() / total) * 100;
            g2.drawString(String.format("%s: %.2f (%.1f%%)", entry.getKey(), entry.getValue(), pct), legendX + 20, legendY + 12);
            legendY += 22;
        }
    }
}
