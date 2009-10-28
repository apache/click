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
import org.apache.commons.collections.CollectionUtils;

/**
 *
 */
public class TableExportBanner implements Renderable {

    private List<AbstractTableExporter> exportFormats = new ArrayList<AbstractTableExporter>();

    protected String separator;

    private ExportTable table;

    public TableExportBanner(ExportTable table) {
        this.table = table;
    }

    public void add(AbstractTableExporter exportFormat) {
        getExportFormats().add(exportFormat);
    }

    public void remove(AbstractTableExporter exportFormat) {
        getExportFormats().remove(exportFormat);
    }

    public void render(HtmlStringBuffer buffer) {
        renderExportBanner(buffer);
    }

    public List<AbstractTableExporter> getExportFormats() {
        return exportFormats;
    }

    public void setExportFormats(List<AbstractTableExporter> exporters) {
        this.exportFormats = exporters;
    }

    public void onInit() {
        String tableName = table.getName();
        for (AbstractTableExporter format : getExportFormats()) {
            format.setName(tableName);
            Control control = format.getExportLink();
            control.onInit();
        }
    }

    public void onRender() {
        AbstractTableExporter selectedFormat = null;
        for (AbstractTableExporter format : getExportFormats()) {
            format.getExportLink().onRender();
            if (format.isSelected()) {
                selectedFormat = format;
            }
        }
        if(selectedFormat != null) {
            export(selectedFormat);
        }
    }

    public boolean onProcess() {
        boolean continueProcessing = true;
        for (AbstractTableExporter format : getExportFormats()) {
            if (!format.getExportLink().onProcess()) {
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

    public void export(AbstractTableExporter format) {
        Context context = table.getContext();
        format.export(table, context);
    }

    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        render(buffer);

        return buffer.toString();
    }

    /**
     * Render the table export banner.
     * <p/>
     * See the <tt>TableExportBanner.properies</tt> for the HTML template:
     * <tt>table-export-banner</tt>.
     *
     * @param buffer the StringBuffer to render the paging controls to
     */
    protected void renderExportBanner(HtmlStringBuffer buffer) {
        List exportFormats = getExportFormats();
        if (exportFormats == null || exportFormats.isEmpty()) {
            return;
        }

        HtmlStringBuffer banner = new HtmlStringBuffer();
        Iterator<AbstractTableExporter> it = getExportFormats().iterator();
        while(it.hasNext()) {
            AbstractTableExporter format = it.next();
            format.getExportLink().render(banner);
            if (it.hasNext()) {
                banner.append(getSeparator());
            }
        }
        String[] args = { getStyleClass(), banner.toString()};
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
