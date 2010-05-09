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

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.examples.util.ExampleUtils;
import org.apache.click.util.ClickUtils;
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
 * Provides an Microsoft Excel Table data exporter class.
 */
public class ExcelTableExporter extends AbstractTableExporter {

    private DataFormat cellFormat;

    private HSSFWorkbook wb;

    private int currentRow;

    private Locale locale;

    private Currency currency;

    private Map<String, HSSFCellStyle> cellStyles = new HashMap<String, HSSFCellStyle>();

    public ExcelTableExporter() {
    }

    public ExcelTableExporter(String label) {
        super(label);
    }

    public ExcelTableExporter(String label, String imageSrc) {
        super(label, imageSrc);
    }

    @Override
    public void setName(String name) {
        getExportLink().setName(name + "-excel");
    }

    @Override
    protected String getMimeType() {
        return ClickUtils.getMimeType(".xls");
    }

    @Override
    protected String getFilename() {
        return "report.xls";
    }

    @Override
    protected void export(ExportTable table, Context context,
        OutputStream output) throws IOException {

        wb = createWorkbook();
        cellFormat = wb.createDataFormat();
        locale = context.getLocale();
        try {
            currency = Currency.getInstance(locale);
        } catch (IllegalArgumentException e) {
            // locale doesn't specify a country
        }

        currentRow = 0;

        HSSFSheet sheet =
            wb.createSheet(StringUtils.capitalize(table.getName()));
        createCellStyles(table, sheet, context);
        exportTableHeader(table, sheet, context);
        exportTableBody(table, sheet, context);

        int count = 0;
        while (count <= table.getColumnList().size()) {
            sheet.autoSizeColumn(count++);
        }

        wb.write(output);
    }

    protected void exportTableHeader(ExportTable table, HSSFSheet sheet,
        Context context) {
        if (table.isExportTableHeaders()) {

            HSSFRow row = createRow(sheet);
            HSSFCellStyle style = createHeaderStyle(sheet);
            final List tableColumns = table.getExportColumnList();

            for (int i = 0, size = tableColumns.size(); i < size; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(style);

                Column column = (Column) tableColumns.get(i);
                String title = column.getHeaderTitle();
                cell.setCellValue(title);
            }
        }
    }

    protected void exportTableBody(ExportTable table, HSSFSheet sheet,
        Context context) {
        int start = table.getFirstExportRow();
        int end = table.getLastExportRow();
        if (end == ExportTable.ALL_ROWS) {
            end = table.getRowList().size();
        }

        final List tableColumns = table.getExportColumnList();

        for (int rowIndex = start; rowIndex < end; rowIndex++) {
            HSSFRow excelRow = createRow(sheet);

            Object row = table.getRowList().get(rowIndex);

            for (int columnIndex = 0, size = tableColumns.size(); columnIndex <
                size; columnIndex++) {
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
        return new HSSFWorkbook();
    }

    protected void createCellStyles(ExportTable table, HSSFSheet sheet,
        Context context) {

        final List<Column> tableColumns = table.getExportColumnList();
        for (Column column : tableColumns) {
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
            cell.setCellValue(
                new HSSFRichTextString(ObjectUtils.toString(value)));
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
                    currencyPattern = ExampleUtils.getCurrencySymbol(currency) +
                        currencyPattern;
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
        String custom = fullPattern;

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
        }

        return StringUtils.trim(custom);
    }
}
