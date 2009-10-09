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
package org.apache.click.examples.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.click.Context;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormat;

/**
 * This example provides a table that can be exported to an Excel spreadsheet.
 */
public class ExportTable extends Table {

    final static int ALL_ROWS = -1;

    public static final int EXPORTER_ATTACHED = 1;

    public static final int EXPORTER_INLINE = 2;

    private static final Map<Currency, String> CURRENCY_SYMBOLS = new HashMap<Currency, String>();

    protected ActionLink excelLink;

    protected boolean exportTableHeaders = true;

    protected int firstExportRow;

    protected int lastExportRow = ALL_ROWS;

    protected int exportBannerPosition = POSITION_BOTTOM;

    protected int exportAttachment = EXPORTER_ATTACHED;

    protected List excludedColumns;

    protected List exportColumnList;

    public ExportTable() {
    }

    public ExportTable(String name) {
        super(name);
    }

    public void setName(String name) {
        super.setName(name);
        getExcelLink().setName(getName() + "-excelLink");
    }

    /**
     * @return the excelLink
     */
    public ActionLink getExcelLink() {
        if (excelLink == null) {
            excelLink = new ActionLink();
            excelLink.setLabel("Excel");
            excelLink.setImageSrc("/assets/images/page_excel.png");
            excelLink.setRenderLabelAndImage(true);
            excelLink.addStyleClass("image-link");
        }
        return excelLink;
    }

    /**
     * @param excelLink the excelLink to set
     */
    public void setExcelLink(ActionLink excelLink) {
        this.excelLink = excelLink;
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

    /**
     * @return the excludedColumns
     */
    public List getExcludedColumns() {
        if (excludedColumns == null) {
            excludedColumns = new ArrayList();
        }
        return excludedColumns;
    }

    /**
     * @param excludedColumns the excludedColumns to set
     */
    public void setExcludedColumns(List excludedColumns) {
        this.excludedColumns = excludedColumns;
    }

    public List getExportColumnList() {
        if (exportColumnList == null) {
            exportColumnList = new ArrayList();
            List<Column> columns = getColumnList();
            List<String> excludes = getExcludedColumns();

            for (Column column : columns) {
                String name = column.getName();
                if (!excludes.contains(name)) {
                    exportColumnList.add(column);
                }
            }
        }
        return exportColumnList;
    }

    public void onInit() {
        super.onInit();
        getExcelLink().onInit();
    }

    public void onRender() {
        super.onRender();
        getExcelLink().onRender();
        if (excelLink.isClicked()) {
            export();
        }
    }

    public boolean onProcess() {
        boolean continueProcessing = super.onProcess();
        ActionLink excelLink = getExcelLink();
        if (!excelLink.onProcess()) {
            continueProcessing = false;
        }
        return continueProcessing;
    }

    public void render(HtmlStringBuffer buffer) {
        if (getExportAttachment() == EXPORTER_ATTACHED) {
            if (getExportBannerPosition() == POSITION_BOTH ||
                getExportBannerPosition() == POSITION_TOP) {
                buffer.append("<div class=\"export-attached\">");
                renderExportBanner(buffer);
                buffer.append("</div>");
            }
        }

        super.render(buffer);

        if (getExportAttachment() == EXPORTER_ATTACHED) {
            if (getExportBannerPosition() == POSITION_BOTH ||
                getExportBannerPosition() == POSITION_BOTTOM) {
                buffer.append("<div class=\"export-attached\">");
                renderExportBanner(buffer);
                buffer.append("</div>");
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
                buffer.append(getColumnList().size());
                buffer.append("\">");

                renderExportBanner(buffer);

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
                buffer.append(getColumnList().size());
                buffer.append("\">");

                renderExportBanner(buffer);

                buffer.append("</td></tr>\n");
                buffer.append("</tbody>\n");
            }
        }
    }

    protected void renderExportBanner(HtmlStringBuffer buffer) {
        buffer.append("<span class=\"export-actions\">");
        buffer.append("Export options: [").append(getExcelLink()).append("]");
        buffer.append("</span>");
    }

    public void export() {
        Context context = getContext();
        HttpServletResponse response = context.getResponse();

        ExcelExporter exporter = new ExcelExporter(context);
        HSSFWorkbook wb = exporter.export();

        // Set response headers
        String mimeType = ClickUtils.getMimeType(".xls");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.xls\"");
        response.setContentType(mimeType);
        response.setHeader("Pragma", "no-cache");

        OutputStream outputStream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);

            byte[] bytes = baos.toByteArray();

            response.setContentLength(bytes.length);

            outputStream = response.getOutputStream();

            // Write out Excel Workbook to response stream
            outputStream.write(bytes);
            outputStream.flush();

            // Specify no further rendering required
            getPage().setPath(null);

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);

        } finally {
            ClickUtils.close(outputStream);
        }
    }

    /**
     * @return the exportAttachment
     */
    public int getExportAttachment() {
        return exportAttachment;
    }

    /**
     * @param exportAttachment the exportAttachment to set
     */
    public void setExportAttachment(int exportAttachment) {
        this.exportAttachment = exportAttachment;
    }

    class ExcelExporter {

        private DataFormat cellFormat;

        private HSSFWorkbook wb;

        private int currentRow;

        private Locale locale;

        private Currency currency;

        private Map<String, HSSFCellStyle> cellStyles = new HashMap();

        public ExcelExporter(Context context) {
            wb = createWorkbook();
            cellFormat = wb.createDataFormat();
            locale = context.getLocale();
            currency = Currency.getInstance(locale);
        }

        protected HSSFWorkbook export() {
            currentRow = 0;

            HSSFSheet sheet = wb.createSheet(StringUtils.capitalize(getName()));
            createCellStyles();
            exportTableHeader(sheet);
            exportTableBody(sheet);

            int count = 0;
            while (count <= getColumnList().size()) {
                sheet.autoSizeColumn(count++);
            }

            return wb;
        }

        protected void exportTableHeader(HSSFSheet sheet) {
            if (exportTableHeaders) {

                HSSFRow row = createRow(sheet);
                HSSFCellStyle style = createHeaderStyle(sheet);
                final List tableColumns = getExportColumnList();

                for (int i = 0, size = tableColumns.size(); i < size; i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(style);

                    Column column = (Column) tableColumns.get(i);
                    String title = column.getHeaderTitle();
                    cell.setCellValue(title);
                }
            }
        }

        protected void exportTableBody(HSSFSheet sheet) {
            int start = getFirstExportRow();
            int end = getLastExportRow();
            if (end == ALL_ROWS) {
                end = getRowList().size();
            }

            Context context = getContext();
            final List tableColumns = getExportColumnList();

            for (int rowIndex = start; rowIndex < end; rowIndex++) {
                HSSFRow excelRow = createRow(sheet);

                Object row = getRowList().get(rowIndex);

                for (int columnIndex = 0, size = tableColumns.size(); columnIndex < size; columnIndex++) {
                    HSSFCell cell = excelRow.createCell(columnIndex);
                    Column column = (Column) tableColumns.get(columnIndex);
                    HSSFCellStyle style = cellStyles.get(column.getName());
                    if (style != null) {
                        cell.setCellStyle(style);
                    }

                    Object columnValue =
                        getColumnValue(row, column, context);

                    setCellValue(columnValue, cell);
                }
            }
        }

        protected Object getColumnValue(Object row, Column column, Context context) {
            Object value = null;
            if (column.getDecorator() != null) {
                value = column.getDecorator().render(row, context);
            } else {
                value = column.getProperty(row);
            }
            return value;
        }

        protected HSSFWorkbook createWorkbook() {
            HSSFWorkbook wb = new HSSFWorkbook();
            return wb;
        }

        protected void createCellStyles() {
            final List<Column> tableColumns = getExportColumnList();
            for(Column column : tableColumns) {
                MessageFormat messageFormat = column.getMessageFormat();
                String format = column.getFormat();
                if (messageFormat == null && format != null) {
                    messageFormat = new MessageFormat(format, locale);
                }

                if (messageFormat != null) {
                    HSSFCellStyle style = createCellStyle(messageFormat);
                    cellStyles.put(column.getName(), style);
                }
            }
        }

        protected HSSFRow createRow(HSSFSheet sheet) {
            HSSFRow row = sheet.createRow(currentRow);
            currentRow++;
            return row;
        }

        protected HSSFCellStyle createHeaderStyle(HSSFSheet sheet) {
            HSSFCellStyle style = wb.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            HSSFFont font = wb.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            return style;
        }

        protected void setCellValue(Object value, HSSFCell cell) {
            if (value instanceof Number) {
                Number num = (Number) value;
                cell.setCellValue(num.doubleValue());
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof Calendar) {
                cell.setCellValue((Calendar) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else {
                cell.setCellValue(new HSSFRichTextString(ObjectUtils.toString(value)));
            }
        }

        protected HSSFCellStyle createCellStyle(MessageFormat messageFormat) {
            HSSFCellStyle cellStyle = null;
            String pattern = messageFormat.toPattern();
            cellStyle = wb.createCellStyle();
            short format = parseCellFormat(pattern);
            cellStyle.setDataFormat(format);

            return cellStyle;
        }

        protected short parseCellFormat(String pattern) {
            short format = 0;
            if (pattern.indexOf("date") != -1) {
                if (pattern.indexOf("short") != -1) {
                    format = cellFormat.getFormat(cellFormat.getFormat((short) 15));
                } else if (pattern.indexOf("medium") != -1) {
                    format = cellFormat.getFormat("mmm dd, yyyy");
                } else if (pattern.indexOf("long") != -1) {
                    format = cellFormat.getFormat("mmmm dd, yyyy");
                } else if (pattern.indexOf("full") != -1) {
                    format = cellFormat.getFormat("dddd, mmmm dd, yyyy");
                } else {
                    String custom = extractCustomPattern(pattern, "date");
                    if (StringUtils.isNotBlank(custom)) {
                        format = cellFormat.getFormat(custom);
                    } else {
                        format = cellFormat.getFormat("mmm dd, yyyy");
                    }
                }
            } else if (pattern.indexOf("time") != -1) {
                if (pattern.indexOf("short") != -1) {
                    format = cellFormat.getFormat("hh:mm");
                } else if (pattern.indexOf("medium") != -1) {
                    format = cellFormat.getFormat("hh:mm:ss");
                } else if (pattern.indexOf("long") != -1) {
                    format = cellFormat.getFormat("hh:mm:ss AM/PM");
                } else if (pattern.indexOf("full") != -1) {
                    format = cellFormat.getFormat("hh:mm:ss AM/PM");
                } else {
                    String custom = extractCustomPattern(pattern, "time");
                    if (StringUtils.isNotBlank(custom)) {
                        format = cellFormat.getFormat(custom);
                    } else {
                        format = cellFormat.getFormat("hh:mm:ss");
                    }
                }
            } else if (pattern.indexOf("number") != -1) {
                if (pattern.indexOf("integer") != -1) {
                    format = cellFormat.getFormat("0");
                } else if (pattern.indexOf("currency") != -1) {
                    String currencyPattern = "#,##0.00";
                    if (currency != null) {
                        currencyPattern = getCurrencySymbol(currency) + currencyPattern;
                    }
                    format = cellFormat.getFormat(currencyPattern);
                } else if (pattern.indexOf("percent") != -1) {
                    format = cellFormat.getFormat("0%");
                } else {
                    String custom = extractCustomPattern(pattern, "number");
                    if (StringUtils.isNotBlank(custom)) {
                        format = cellFormat.getFormat(custom);
                    } else {
                        format = cellFormat.getFormat("0");
                    }
                }
            } else {
                format = cellFormat.getFormat(pattern);
            }
            return format;
        }

        private String extractCustomPattern(String fullPattern, String type) {
            int openBrace = fullPattern.indexOf("{");
            int closeBrace = fullPattern.lastIndexOf("}");
            String prefix = null;
            String postfix = null;
            String custom = fullPattern;

            if (openBrace != -1 && closeBrace != -1) {
                prefix = fullPattern.substring(0, openBrace);
                if (closeBrace < fullPattern.length() - 1) {
                    postfix = fullPattern.substring(closeBrace + 1);
                }
            }

            int start = fullPattern.indexOf(type);
            int end = fullPattern.lastIndexOf('}');
            if (end == -1) {
                end = fullPattern.length();
            }

            if (start != -1) {
                // Move start after last comma
                int lastComma = fullPattern.indexOf(",", start);
                if (lastComma != -1) {
                    start = ++lastComma;
                } else {
                    start = start + type.length();
                }

                custom = fullPattern.substring(start, end);

                StringBuilder buffer = new StringBuilder();
                if (StringUtils.isNotBlank(prefix)) {
                    buffer.append(prefix);
                }
                buffer.append(custom);
                if (StringUtils.isNotBlank(postfix)) {
                    buffer.append(postfix);
                }
                custom = buffer.toString();
            }

            return StringUtils.trim(custom);
        }
    }

    public static String getCurrencySymbol(Currency currency) {
        if(currency == null) {
            return "";
        }

        String symbol = CURRENCY_SYMBOLS.get(currency);
        if(symbol != null) {
            return symbol;
        }

        String currencyCode = currency.getCurrencyCode();

        Locale locale = Locale.getDefault();
        symbol = currency.getSymbol(locale);
        if(!symbol.equals(currencyCode)) {
            CURRENCY_SYMBOLS.put(currency, symbol);
            return symbol;
        }

        Locale[] allLocales = Locale.getAvailableLocales();
        for (int i = 0; i < allLocales.length; i++) {
            symbol = currency.getSymbol(allLocales[i]);
            if(!symbol.equals(currencyCode)) {
                CURRENCY_SYMBOLS.put(currency, symbol);
                return symbol;
            }
        }

        CURRENCY_SYMBOLS.put(currency, currencyCode);
        return currencyCode;
    }
}
