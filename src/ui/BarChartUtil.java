package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.title.TextTitle;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BarChartUtil {

    public static JPanel crearPanelBarras(String titulo, String ejeY, Map<String, Double> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        datos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> dataset.addValue(e.getValue(), "Promedio", e.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
                titulo,
                "Ciudad",
                ejeY,
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

        TextTitle title = chart.getTitle();
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setPaint(new Color(0, 51, 102));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200)); // líneas grises suaves
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(0, 153, 255)); // azul moderno
        renderer.setShadowVisible(true);
        renderer.setShadowPaint(new Color(180, 180, 180, 100));
        renderer.setDrawBarOutline(false);

        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
        renderer.setDefaultItemLabelPaint(new Color(40, 40, 40));

        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        renderer.setMaximumBarWidth(0.08);

        ChartPanel panel = new ChartPanel(chart);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(900, 500));
        return panel;
    }
}

