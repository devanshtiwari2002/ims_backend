package com.inventory.imsbackend.sales.util;

import com.inventory.imsbackend.sales.dto.SaleResponse;
import com.inventory.imsbackend.sales.dto.SaleItemResponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PdfGenerator {
    public void generate(SaleResponse sale,HttpServletResponse response) throws IOException{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Title
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        Paragraph paragraph = new Paragraph("INVOICE - IMS System", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        // Coustumer details
        document.add(new Paragraph("Invoice No: " + sale.getInvoiceNumber()));
        document.add(new Paragraph("Customer: " + sale.getCustomerName()));
        document.add(new Paragraph("Date: " + sale.getSaleDate().toString()));
        document.add(new Paragraph(" "));

        // Table creation
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell("Product");
        table.addCell("Quantity");
        table.addCell("Unit Price");
        table.addCell("Subtotal");

        for (SaleItemResponse item : sale.getItems()){
            table.addCell(item.getProductName());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(item.getUnitPrice().toString());
            table.addCell(item.getSubtotal().toString());
        }
        document.add(table);

        // Total
        Paragraph total = new Paragraph("Total Amount: Rs" + sale.getTotalAmount(), fontTitle);
        total.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(total);

        document.close();

    }
}
