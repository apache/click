/*
 * Copyright 2007-2008 Malcolm A. Edgar
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
public class JSBarChart extends JSChart {

    private static final long serialVersionUID = 1L;

    /** The HTML imports statements. */
    public static final String HTML_IMPORTS =
        "<script type=\"text/javascript\" src=\"{0}/click/graph/jsgraph/wz_jsgraphics{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/graph/jsgraph/bar{1}.js\"></script>\n";

    /** Chart resource file names. */
    protected static final String[] CHART_RESOURCES = {
        "/net/sf/click/extras/graph/jsgraph/wz_jsgraphics.js",
        "/net/sf/click/extras/graph/jsgraph/bar.js"
    };

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

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the list of static chart resources to deploy.
     *
     * @see JSChart#getChartResources()
     *
     * @return the list of static chart resources to deploy
     */
    protected String[] getChartResources() {
        return CHART_RESOURCES;
    }

    /**
     * Return the HTML imports pattern string.
     *
     * @see JSChart#getHtmlImportPattern()
     *
     * @return the HTML imports pattern string
     */
    protected String getHtmlImportPattern() {
        return HTML_IMPORTS;
    }

    /**
     * Return the JavaScript Chart type.
     *
     * @see JSChart#getJSChartType()
     *
     * @return the JavaScript Chart type
     */
    protected String getJSChartType() {
        return "graph";
    }

}
