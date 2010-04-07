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
package org.apache.click.examples.page.general;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.ClickUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Provides a Excel Export page example using the Apache POI library.
 */
@Component
public class ExcelExportPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Resource(name="customerService")
    private CustomerService customerService;

    // -------------------------------------------------------- Event Handlers

    @Override
    public void onInit() {
        super.onInit();

        ActionLink link = new ActionLink("export");
        link.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                export();
                return false;
            }
        });

        addControl(link);
    }

    /**
     * Export the spreadsheet.
     */
    public void export() {

        HttpServletResponse response = getContext().getResponse();

        HSSFWorkbook wb = createWorkbook();

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
            setPath(null);

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);

        } finally {
            ClickUtils.close(outputStream);
        }
    }

    // -------------------------------------------------------- Private Methods

    @SuppressWarnings("deprecation")
    private HSSFWorkbook createWorkbook() {
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFSheet worksheet = wb.createSheet("Customers");
        worksheet.setColumnWidth(0, (20 * 256));
        worksheet.setColumnWidth(1, (30 * 256));
        worksheet.setColumnWidth(4, (20 * 256));

        HSSFRow row = worksheet.createRow(0);

        HSSFRichTextString value = new HSSFRichTextString("Customers");
        value.applyFont(font);
        row.createCell(0).setCellValue(value);

        row = worksheet.createRow(1);
        row.createCell(0).setCellValue(new HSSFRichTextString("Customer Account Details"));

        worksheet.createRow(2);

        row = worksheet.createRow(3);

        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        value = new HSSFRichTextString("Name");
        value.applyFont(font);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        value = new HSSFRichTextString("Email");
        value.applyFont(font);
        cell = row.createCell(1);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        value = new HSSFRichTextString("Age");
        value.applyFont(font);
        cell = row.createCell(2);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        value = new HSSFRichTextString("Holdings");
        value.applyFont(font);
        cell = row.createCell(3);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        value = new HSSFRichTextString("Investments");
        value.applyFont(font);
        cell = row.createCell(4);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        int rowIndex = 4;

        List<Customer> customers = customerService.getCustomers();
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = (Customer) customers.get(i);

            row = worksheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(new HSSFRichTextString(customer.getName()));
            row.createCell(1).setCellValue(new HSSFRichTextString(customer.getEmail()));

            if (customer.getAge() != null) {
                row.createCell(2).setCellValue(customer.getAge().intValue());
            }

            if (customer.getHoldings() != null) {
                row.createCell(3).setCellValue(customer.getHoldings().doubleValue());
            }

            row.createCell(4).setCellValue(new HSSFRichTextString(customer.getInvestments()));
        }

        return wb;
    }

}
