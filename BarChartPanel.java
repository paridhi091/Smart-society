import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple bar chart rendered directly with Java2D - no external
 * charting library required. Call setData() with an ordered map of
 * label -> value whenever the underlying data changes.
 */
public class BarChartPanel extends JPanel {
    private Map<String, Double> data = new LinkedHashMap<>();
    private final String title;
    private final Color barColor = new Color(70, 130, 180);

    public BarChartPanel(String title) {
        this.title = title;
        setPreferredSize(new Dimension(480, 300));
        setBackground(Color.WHITE);
    }

    public void setData(Map<String, Double> newData) {
        this.data = newData;
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

        if (data.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString("No data yet - generate some bills first.", 20, height / 2);
            return;
        }

        double maxValue = 0;
        for (double v : data.values()) maxValue = Math.max(maxValue, v);
        if (maxValue <= 0) maxValue = 1.0;

        int padding = 45;
        int labelPadding = 22;
        int chartTop = 40;
        int chartBottom = height - padding - labelPadding;
        int chartHeight = chartBottom - chartTop;
        int chartLeft = padding;
        int chartRight = width - 20;
        int chartWidth = chartRight - chartLeft;

        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(chartLeft, chartTop, chartLeft, chartBottom);
        g2.drawLine(chartLeft, chartBottom, chartRight, chartBottom);

        int n = data.size();
        int gap = 18;
        int barWidth = Math.max(10, (chartWidth - gap * (n + 1)) / Math.max(n, 1));

        int x = chartLeft + gap;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue();
            int barHeight = (int) ((value / maxValue) * chartHeight);
            int y = chartBottom - barHeight;

            g2.setColor(barColor);
            g2.fillRect(x, y, barWidth, barHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);

            String valueLabel = String.format("%.0f", value);
            int labelWidth = g2.getFontMetrics().stringWidth(valueLabel);
            g2.drawString(valueLabel, x + barWidth / 2 - labelWidth / 2, Math.max(y - 5, 15));

            String catLabel = entry.getKey();
            int catLabelWidth = g2.getFontMetrics().stringWidth(catLabel);
            g2.drawString(catLabel, x + barWidth / 2 - catLabelWidth / 2, chartBottom + 15);

            x += barWidth + gap;
        }
    }
}
