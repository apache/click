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
import java.util.Iterator;
import java.util.List;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.control.Renderable;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a table container exporter class.
 */
public class TableExportContainer implements Renderable {

    private static final long serialVersionUID = 1L;

    private List<AbstractTableExporter> exporters = new ArrayList<AbstractTableExporter>();

    protected String separator;

    private ExportTable table;

    public TableExportContainer(ExportTable table) {
        this.table = table;
    }

    public void add(AbstractTableExporter exporter) {
        getExporters().add(exporter);
    }

    public void remove(AbstractTableExporter exporter) {
        getExporters().remove(exporter);
    }

    public void render(HtmlStringBuffer buffer) {
        renderExportBanner(buffer);
    }

    public List<AbstractTableExporter> getExporters() {
        return exporters;
    }

    public void setExporters(List<AbstractTableExporter> exporters) {
        this.exporters = exporters;
    }

    public void onInit() {
        String tableName = table.getName();
        for (AbstractTableExporter exporter : getExporters()) {
            exporter.setName(tableName);
            Control control = exporter.getExportLink();
            control.onInit();
        }
    }

    public void onRender() {
        AbstractTableExporter selectedExporter = null;
        for (AbstractTableExporter exporter : getExporters()) {
            exporter.getExportLink().onRender();
            if (exporter.isSelected()) {
                selectedExporter = exporter;
            }
        }
        if(selectedExporter != null) {
            export(selectedExporter);
        }
    }

    public boolean onProcess() {
        boolean continueProcessing = true;
        for (AbstractTableExporter exporter : getExporters()) {
            if (!exporter.getExportLink().onProcess()) {
                continueProcessing = false;
            }
        }
        return continueProcessing;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void export(AbstractTableExporter exporter) {
        Context context = table.getContext();
        exporter.export(table, context);
    }

    @Override
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        render(buffer);

        return buffer.toString();
    }

    /**
     * Render the table export banner.
     * <p/>
     * See the <tt>TableExportContainer.properies</tt> for the HTML template:
     * <tt>table-export-banner</tt>.
     *
     * @param buffer the StringBuffer to render the paging controls to
     */
    protected void renderExportBanner(HtmlStringBuffer buffer) {
        List exporters = getExporters();
        if (exporters == null || exporters.isEmpty()) {
            return;
        }

        HtmlStringBuffer banner = new HtmlStringBuffer();
        Iterator<AbstractTableExporter> it = getExporters().iterator();
        while(it.hasNext()) {
            AbstractTableExporter exporter = it.next();
            exporter.getExportLink().render(banner);
            if (it.hasNext()) {
                banner.append(getSeparator());
            }
        }
        Object[] args = { getStyleClass(), banner.toString()};
        buffer.append(table.getMessage("table-export-banner", args));
    }

    protected String getStyleClass() {
        if (table.getExportAttachment() == ExportTable.EXPORTER_ATTACHED) {
            return "export-attached";
        } else if (table.getExportAttachment() == ExportTable.EXPORTER_DETACHED) {
            return "export-detached";
        } else if (table.getExportAttachment() == ExportTable.EXPORTER_INLINE) {
            return "export-inline";
        }
        return "export";
    }
}
