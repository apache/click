package net.sf.click.examples.page.graph;

import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.graph.JSLineChart;

/**
 * Example usage of the JSLineChart.
 * <p/>  
 *
 * @author Ahmed Mohombe
 */
public class LineChart extends BorderPage {
    public JSLineChart chart = new JSLineChart("chart","Line Graph");


    public void onInit() {
        chart.setChartHeight(300);
        chart.setChartWidth(400);

        chart.addPoint("1",new Integer(145));
        chart.addPoint("2", new Integer(0));
        chart.addPoint("3", new Integer(175));
        chart.addPoint("4", new Integer(130));
        chart.addPoint("5", new Integer(150));
        chart.addPoint("6", new Integer(175));
        chart.addPoint("7", new Integer(205));
        chart.addPoint("8", new Integer(125));
        chart.addPoint("9", new Integer(125));
        chart.addPoint("10", new Integer(135));
        chart.addPoint("11", new Integer(125));

    }
}
