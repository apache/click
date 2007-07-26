/*
 * Copyright 2007 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.graph;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.ArrayList;

import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.control.AbstractControl;

/**
 * Provides a Bar Chart control based on JavaScript only.
 *
 * <p/>
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2' src='bar-chart.png' title='Line Chart'/>
 * </td>
 * </tr>
 * </table>
 *
 * This control uses the <a href="http://www.walterzorn.com/jsgraphics">JSGraphics</a>
 * library.
 *
 * @author Ahmed Mohombe
 */
public class JSBarChart extends AbstractControl {

    private static final long serialVersionUID = 1L;

    /** The HTML imports statements. */
    protected static final String CHART_IMPORTS =
            "<script type=\"text/javascript\" src=\"$/click/graph/jsgraph/wz_jsgraphics.js\"></script>\n"
            + "<script type=\"text/javascript\" src=\"$/click/graph/jsgraph/bar.js\"></script>\n";

    /** Chart resource file names. */
    protected static final String[] CHART_RESOURCES = {
            "/net/sf/click/extras/graph/jsgraph/wz_jsgraphics.js",
            "/net/sf/click/extras/graph/jsgraph/bar.js"
    };

    // ----------------------------------------------------- Instance Variables

    /** Width of the DIV element that encloses this chart. */
    private int chartWidth = 400; // default value for width

    /** Height of the DIV element that encloses this chart. */
    private int chartHeight = 300; // default value for height

    private List xLabels = new ArrayList();
    private List yValues = new ArrayList();

    /** The chart display label. */
    private String label = "Bar Chart"; // default label

    // ----------------------------------------------------------- Constructors

    /**
     * Create a bar chart with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public JSBarChart() {
    }

    /**
     * Create a bar chart with the given name.
     *
     * @param name the button name
     */
    public JSBarChart(String name) {
        super.setName(name);
    }

    /**
     * Create a bar chart with the given name and label.
     *
     * @param name the name of the chart control
     * @param label the label of the chart that will be displayed
     */
    public JSBarChart(String name, String label) {
        super.setName(name);
        setLabel(label);
    }

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
     * Adds a "point" to the grapic/chart at a specified index in the list.
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
        String path = getContext().getRequest().getContextPath();
        StringBuffer buffer = new StringBuffer(100);
        buffer.append(StringUtils.replace(CHART_IMPORTS, "$", path));
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
        // do nothing
    }

    /**
     * Deploys the javascript files of this control to the <code>[click/graph/jsgraph]</code> directory.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the webapplication's servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFiles(servletContext, CHART_RESOURCES, "click/graph/jsgraph");
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
     * Return the HTML rendered bar chart.
     *
     * @return the HTML rendered bar chart string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.elementStart("div");
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("style", "overflow: auto; position:relative;height:" + getChartHeight() + "px;width:" + getChartWidth() + "px;");
        buffer.closeTag();
        buffer.elementEnd("div");

        String var = "g_" + getId();
        buffer.append("\n<script type=\"text/javascript\">\n");
        buffer.append("var " + var + " = new graph();\n");
        for (int i = 0; i < xLabels.size(); i++) {
            String pointLabel = (String) xLabels.get(i);
            Integer pointValue = (Integer) yValues.get(i);
            buffer.append(var + ".add('" + pointLabel + "'," + pointValue + ");\n");
        }
        buffer.append(var + ".render('" + getId() + "','" + getLabel() + "');\n");
        buffer.append("</script> \n");

        return buffer.toString();
    }

}
