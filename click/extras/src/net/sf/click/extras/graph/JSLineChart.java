package net.sf.click.extras.graph;

import net.sf.click.control.AbstractControl;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * Control that displays a Line Chart based on JavaScript only.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2' src='line-chart.png' title='Line Chart'/>
 * </td>
 * </tr>
 * </table>
 *
 * @author Ahmed Mohombe
 */
public class JSLineChart extends AbstractControl {

    protected static final String CHART_IMPORTS =
            "<script type=\"text/javascript\" src=\"$/click/graph/jsgraph/wz_jsgraphics.js\"></script>\n" +
                    "<script type=\"text/javascript\" src=\"$/click/graph/jsgraph/line.js\"></script>\n";


    protected static final String[] CHART_RESOURCES = {
            "/net/sf/click/extras/graph/jsgraph/wz_jsgraphics.js",
            "/net/sf/click/extras/graph/jsgraph/line.js"
    };


    private int chartWidth = 400; // default width
    private int chartHeight = 300; // default height

    private List xLabels = new ArrayList();
    private List yValues = new ArrayList();

    private String label = "Line Chart"; // default label
    
    public JSLineChart() {
    }

    public JSLineChart(String name) {
        super.setName(name);
    }

    public JSLineChart(String name, String label) {
        super.setName(name);
        setLabel(label);
    }

    
    public void addPoint(String pointLabel, Integer pointValue){
        xLabels.add(pointLabel);
        yValues.add(pointValue);
    }

    public void addPoint(int position, String pointLabel, Integer pointValue){
        xLabels.add(position, pointLabel);
        yValues.add(position, pointValue);
    }
 
    public int getChartWidth() {
        return chartWidth;
    }

    public void setChartWidth(int chartWidth) {
        this.chartWidth = chartWidth;
    }

    public int getChartHeight() {
        return chartHeight;
    }

    public void setChartHeight(int chartHeight) {
        this.chartHeight = chartHeight;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHtmlImports() {
        String path = getContext().getRequest().getContextPath();
        StringBuffer buffer = new StringBuffer(100);
        buffer.append(StringUtils.replace(CHART_IMPORTS, "$", path));
        return buffer.toString();
    }

    public void setListener(Object listener, String method) {
        // do nothing
    }

    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFiles(servletContext, CHART_RESOURCES, "click/graph/jsgraph");
    }

    public boolean onProcess() {
        return true;
    }

    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        buffer.elementStart("div");
        buffer.appendAttribute("id",getId());
        buffer.appendAttribute("style","overflow: auto; position:relative;height:"+getChartHeight()+"px;width:"+getChartWidth()+"px;");
        buffer.closeTag();
        buffer.elementEnd("div");

        String var = "g_"+getId();
        buffer.append("\n<script type=\"text/javascript\">\n");
        buffer.append("var "+var+" = new line_graph();\n");
        for (int i = 0; i < xLabels.size(); i++) {
            String pointLabel = (String) xLabels.get(i);
            Integer pointValue = (Integer) yValues.get(i);
            buffer.append(var+".add('"+pointLabel+"',"+pointValue+");\n");
        }
        buffer.append(var+".render('"+getId()+"','"+getLabel()+"');\n");
        buffer.append("</script> \n");
        return buffer.toString();
    }
}
