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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.click.ActionListener;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.control.AbstractControl;
import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Provides an abstract table data exporter class.
 */
public abstract class AbstractTableExporter {

    protected String label;

    protected String imageSrc;

    protected AbstractLink exportLink;

    protected boolean selected;

    public AbstractTableExporter() {
    }

    public AbstractTableExporter(String label) {
        this.label = label;
    }

    public AbstractTableExporter(String label, String imageSrc) {
        this.label = label;
        this.imageSrc = imageSrc;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setExportLink(AbstractLink exportLink) {
        this.exportLink = exportLink;
        if (exportLink.getActionListener() == null) {
            setActionListener(this.exportLink);
        }
    }

    public AbstractLink getExportLink() {
        if (exportLink == null) {
            exportLink = new ActionLink();
            exportLink.setLabel(getLabel());
            exportLink.setImageSrc(getImageSrc());

            if (StringUtils.isNotBlank(getLabel()) && StringUtils.isNotBlank(
                getImageSrc())) {
                exportLink.setRenderLabelAndImage(true);
            }

            setActionListener(exportLink);
        }
        return exportLink;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public abstract void setName(String name);

    public void export(ExportTable table, Context context) {
        HttpServletResponse response = context.getResponse();

        // Set response headers
        String mimeType = getMimeType();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + getFilename() + "\"");
        response.setContentType(mimeType);
        response.setHeader("Pragma", "no-cache");

        OutputStream outputStream = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            export(table, context, output);

            byte[] bytes = output.toByteArray();

            response.setContentLength(bytes.length);

            outputStream = response.getOutputStream();

            // Write out Excel Workbook to response stream
            outputStream.write(bytes);
            outputStream.flush();

            // Specify no further rendering required
            table.getPage().setPath(null);

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);

        } finally {
            ClickUtils.close(outputStream);
        }
    }

    protected abstract String getMimeType();

    protected abstract void export(ExportTable table, Context context, OutputStream output)
        throws IOException;

    protected abstract String getFilename();

    protected void setActionListener(AbstractControl control) {
        control.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                setSelected(true);
                return true;
            }
        });
    }
}
