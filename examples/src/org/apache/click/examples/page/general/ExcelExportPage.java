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
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.ActionResult;
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

        // A "pageAction" is set as a parameter on the link. The "pageAction"
        // value is set to the Page method: "renderSpreadsheet"
        link.setParameter(PAGE_ACTION, "renderSpreadsheet");

        addControl(link);
    }

    /**
     * This is a "pageAction" method and will render the Excel spreadsheet.
     *
     * Note the signature of the pageAction: a public, no-argument method
     * returning an ActionResult instance.
     */
    public ActionResult renderSpreadsheet() {
        HttpServletResponse response = getContext().getResponse();

        HSSFWorkbook wb = createWorkbook();

        // Set response headers
        response.setHeader("Content-Disposition", "attachment; filename=\"report.xls\"");

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);

            String contentTyype = ClickUtils.getMimeType(".xls");
            ActionResult actionResult = new ActionResult(baos.toByteArray(), contentTyype);

            return actionResult;

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
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
        for (Customer customer : customers) {
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
