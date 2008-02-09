package net.sf.click.extras.graph;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.click.control.AbstractControl;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a abstract JavaScript Chart control.
 *
 * @author Ahmed Mohombe
 * @author Malcolm Edgar
 */
public abstract class JSChart extends AbstractControl {

    // ----------------------------------------------------- Instance Variables

    /**
     * Height of the DIV element that encloses this chart, default height 350 px.
     */
    protected int chartHeight = 350;

    /**
     * Width of the DIV element that encloses this chart, default width 380 px.
     */
    protected int chartWidth = 380;

    /** The chart display label. */
    protected String label = "Chart";

    /** The list of X-Axis labels. */
    protected List xLabels = new ArrayList();

    /** The list of Y-Axis values. */
    protected List yValues = new ArrayList();

    // --------------------------------------------------------- Public Methods

    /**
     * Adds a "point" to the grapic/chart at the end of the list.
     *
     * @param pointLabel the displayed label of the "point"
     * @param pointValue the value of the "point".
     */
    public void addPoint(String pointLabel, Integer pointValue) {
        xLabels.add(pointLabel);
        yValues.add(pointValue);
    }

    /**
     * Adds a "point" to the grapic/chart at a specified position in the list.
     *
     * @param index index at which the specified point is to be inserted
     * @param pointLabel the displayed label of the "point"
     * @param pointValue the value of the "point".
     */
    public void addPoint(int index, String pointLabel, Integer pointValue) {
        xLabels.add(index, pointLabel);
        yValues.add(index, pointValue);
    }

    /**
     * Return the width of the chart (the enclosing DIV element).
     *
     * @return the width of the chart
     */
    public int getChartWidth() {
        return chartWidth;
    }

    /**
     * Set the width of the chart (of the enclosing DIV element), as a
     * pixel value.
     *
     * @param chartWidth the chart width in pixels.
     */
    public void setChartWidth(int chartWidth) {
        this.chartWidth = chartWidth;
    }

    /**
     * Return the height of the chart (the enclosing DIV element).
     *
     * @return the height of the chart
     */
    public int getChartHeight() {
        return chartHeight;
    }

    /**
     * Set the height of the chart (of the enclosing DIV element), as a
     * pixel value.
     *
     * @param chartHeight the chart height in pixels.
     */
    public void setChartHeight(int chartHeight) {
        this.chartHeight = chartHeight;
    }

    /**
     * Return the label of the chart.
     *
     * @return the label of the chart
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the chart display caption.
     *
     * @param label the display label of the chart
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Return the HTML head import statements for the javascript files
     * used by this control.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return the HTML head import statements for the javascript files
     * used by this control.
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(512);

        buffer.append(ClickUtils.createHtmlImport(getHtmlImportPattern(), getContext()));

        String var = "g_" + getId();
        buffer.append("<script type=\"text/javascript\">var ");
        buffer.append(var);
        buffer.append(" = new ");
        buffer.append(getJSChartType());
        buffer.append("(); ");

        for (int i = 0; i < xLabels.size(); i++) {
            String pointLabel = (String) xLabels.get(i);
            Integer pointValue = (Integer) yValues.get(i);
            buffer.append(var);
            buffer.append(".add('");
            buffer.append(pointLabel);
            buffer.append("',");
            buffer.append(pointValue);
            buffer.append("); ");
        }

        buffer.append("addLoadEvent(function(){");
        buffer.append(var);
        buffer.append(".render('");
        buffer.append(getId());
        buffer.append("','");
        buffer.append(getLabel());
        buffer.append("');});</script>\n");

        return buffer.toString();
    }

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
    }

    /**
     * Return the HTML rendered bar chart.
     *
     * @return the HTML rendered bar chart string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.elementStart("div");
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("style", "overflow:auto; position:relative; height:" + getChartHeight() + "px; width:" + getChartWidth() + "px;");
        buffer.elementEnd();

        return buffer.toString();
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * Deploys the javascript files of this control to the <code>[click/graph/jsgraph]</code> directory.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the webapplication's servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFiles(servletContext,
                               getChartResources(),
                               "click/graph/jsgraph");
    }

    /**
     * This method does nothing. Subclasses may override this method to perform
     * additional initialization.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
    }

    /**
     * Returns true, as javascript charts perform no server side logic.
     *
     * @return true
     */
    public boolean onProcess() {
        return true;
    }

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
    }

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
    }

    // ------------------------------------------------------- Abstract Methods

    /**
     * Return the list of static chart resources to deploy.
     *
     * @return the list of static chart resources to deploy
     */
    protected abstract String[] getChartResources();

    /**
     * Return the HTML imports pattern string.
     *
     * @return the HTML imports pattern string
     */
    protected abstract String getHtmlImportPattern();

    /**
     * Return the JavaScript Chart type.
     *
     * @return the JavaScript Chart type
     */
    protected abstract String getJSChartType();

}
