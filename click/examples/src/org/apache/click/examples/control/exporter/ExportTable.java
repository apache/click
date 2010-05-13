/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.examples.control.exporter;

import java.util.ArrayList;
import java.util.List;
import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.util.HtmlStringBuffer;

/**
 * This example provides a table that can be exported to an Excel spreadsheet.
 */
public class ExportTable extends Table {

    private static final long serialVersionUID = 1L;

    final static int ALL_ROWS = Integer.MAX_VALUE;

    public static final int EXPORTER_ATTACHED = 1;

    public static final int EXPORTER_DETACHED = 2;

    public static final int EXPORTER_INLINE = 3;

    protected boolean exportTableHeaders = true;

    protected int firstExportRow;

    protected int lastExportRow = ALL_ROWS;

    protected int exportBannerPosition = POSITION_BOTTOM;

    protected int exportAttachment = EXPORTER_ATTACHED;

    protected List<String> excludedColumns;

    protected List<String> excludedExportColumns;

    protected TableExportContainer exportContainer;

    private List exportColumnList;

    public ExportTable() {
    }

    public ExportTable(String name) {
        super(name);
    }

    public TableExportContainer getExportContainer() {
        if (exportContainer == null) {
            exportContainer = new TableExportContainer(this);
        }
        return exportContainer;
    }

    public void setExportContainer(TableExportContainer exportContainer) {
        this.exportContainer = exportContainer;
    }

    /**
     * @return the exportTableHeaders
     */
    public boolean isExportTableHeaders() {
        return exportTableHeaders;
    }

    /**
     * @return the firstExportRow
     */
    public int getFirstExportRow() {
        return firstExportRow;
    }

    /**
     * @param firstExportRow the firstExportRow to set
     */
    public void setFirstExportRow(int firstExportRow) {
        this.firstExportRow = firstExportRow;
    }

    /**
     * @return the lastExportRow
     */
    public int getLastExportRow() {
        return lastExportRow;
    }

    /**
     * @param lastExportRow the lastExportRow to set
     */
    public void setLastExportRow(int lastExportRow) {
        this.lastExportRow = lastExportRow;
    }

    /**
     * @param exportTableHeaders the exportTableHeaders to set
     */
    public void setExportTableHeaders(boolean exportTableHeaders) {
        this.exportTableHeaders = exportTableHeaders;
    }

    /**
     * @return the exportBannerPosition
     */
    public int getExportBannerPosition() {
        return exportBannerPosition;
    }

    /**
     * @param exportBannerPosition the exportBannerPosition to set
     */
    public void setExportBannerPosition(int exportBannerPosition) {
        this.exportBannerPosition = exportBannerPosition;
    }

    public List<String> getExcludedColumns() {
        if (excludedColumns == null) {
            excludedColumns = new ArrayList<String>();
        }
        return excludedColumns;
    }

    public void setExcludedColumns(List<String> excludedColumns) {
        this.excludedColumns = excludedColumns;
    }

    public List<String> getExcludedExportColumns() {
        if (excludedExportColumns == null) {
            excludedExportColumns = new ArrayList<String>();
        }
        return excludedExportColumns;
    }

    public void setExcludedExportColumns(List<String> excludedExportColumns) {
        this.excludedExportColumns = excludedExportColumns;
    }

    @SuppressWarnings("unchecked")
    public List getExportColumnList() {
        if (exportColumnList == null) {
            exportColumnList = new ArrayList<String>();
            List<Column> columns = getColumnList();
            List<String> excludes = getExcludedExportColumns();

            for (Column column : columns) {
                String name = column.getName();
                if (!excludes.contains(name)) {
                    exportColumnList.add(column);
                }
            }
        }
        return exportColumnList;
    }

    public int getExportAttachment() {
        return exportAttachment;
    }

    public void setExportAttachment(int exportAttachment) {
        this.exportAttachment = exportAttachment;
    }

    @Override
    public void onInit() {
        super.onInit();
        getExportContainer().onInit();
    }

    @Override
    public void onRender() {
        super.onRender();
        getExportContainer().onRender();
    }

    @Override
    public boolean onProcess() {
        boolean continueProcessing = super.onProcess();
        TableExportContainer exportContainer = getExportContainer();
        if (!exportContainer.onProcess()) {
            continueProcessing = false;
        }
        return continueProcessing;
    }

    public void render(HtmlStringBuffer buffer) {
        if (getExportAttachment() == EXPORTER_ATTACHED) {
            if (getExportBannerPosition() == POSITION_BOTH ||
                getExportBannerPosition() == POSITION_TOP) {
                getExportContainer().render(buffer);
            }
        }

        super.render(buffer);

        if (getExportAttachment() == EXPORTER_ATTACHED) {
            if (getExportBannerPosition() == POSITION_BOTH ||
                getExportBannerPosition() == POSITION_BOTTOM) {
                getExportContainer().render(buffer);
            }
        }
    }

    protected void renderBodyRows(HtmlStringBuffer buffer) {
        if (getExportAttachment() == EXPORTER_INLINE) {
            if (getExportBannerPosition() == POSITION_TOP
                || getExportBannerPosition() == POSITION_BOTH) {
                buffer.append("<tbody>\n");
                buffer.append("<tr class=\"export-inline\">\n");
                buffer.append("<td class=\"export-inline\" colspan=\"");
                buffer.append(getColumnList().size() - getExcludedExportColumns().size());
                buffer.append("\">");

                getExportContainer().render(buffer);

                buffer.append("</td></tr>\n");
                buffer.append("</tbody>\n");
            }
        }

        super.renderBodyRows(buffer);

        if (getExportAttachment() == EXPORTER_INLINE) {
            if (getExportBannerPosition() == POSITION_BOTTOM
                || getExportBannerPosition() == POSITION_BOTH) {
                buffer.append("<tbody>\n");
                buffer.append("<tr class=\"export-inline\">\n");
                buffer.append("<td class=\"export-inline\" colspan=\"");
                buffer.append(getColumnList().size() - getExcludedExportColumns().size());
                buffer.append("\">");

                getExportContainer().render(buffer);

                buffer.append("</td></tr>\n");
                buffer.append("</tbody>\n");
            }
        }
    }

    @Override
    protected void renderHeaderRow(HtmlStringBuffer buffer) {
        buffer.append("<thead>\n<tr>\n");

        final List tableColumns = getColumnList();
        final List excludedColumns = getExcludedColumns();

        for (int j = 0; j < tableColumns.size(); j++) {
            Column column = (Column) tableColumns.get(j);
            if (!excludedColumns.contains(column.getName())) {
                column.renderTableHeader(buffer, getContext());
                if (j < tableColumns.size() - 1) {
                    buffer.append("\n");
                }
            }
        }

        buffer.append("</tr></thead>\n");
    }

    @Override
    protected void renderBodyRowColumns(HtmlStringBuffer buffer, int rowIndex) {
        Object row = getRowList().get(rowIndex);

        final List tableColumns = getColumnList();
        final List excludedColumns = getExcludedColumns();

        for (int j = 0; j < tableColumns.size(); j++) {
            Column column = (Column) tableColumns.get(j);
            if (!excludedColumns.contains(column.getName())) {
                column.renderTableData(row, buffer, getContext(), rowIndex);
                if (j < tableColumns.size() - 1) {
                    buffer.append("\n");
                }
            }
        }
    }

    @Override
    protected void renderBodyNoRows(HtmlStringBuffer buffer) {
        buffer.append("<tr class=\"odd\"><td colspan=\"");
        buffer.append(getColumns().size() - getExcludedColumns().size());
        buffer.append("\" class=\"error\">");
        buffer.append(getMessage("table-no-rows-found"));
        buffer.append("</td></tr>\n");
    }
}
