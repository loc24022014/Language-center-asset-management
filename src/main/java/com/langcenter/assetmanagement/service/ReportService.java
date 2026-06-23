package com.langcenter.assetmanagement.service;

import com.langcenter.assetmanagement.entity.Asset;
import com.langcenter.assetmanagement.repository.AssetRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AssetRepository assetRepository;

    public ByteArrayInputStream exportAssetsToExcel() throws IOException {
        List<Asset> assets = assetRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Assets Report");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Asset Code");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Category");
            headerRow.createCell(4).setCellValue("Total Quantity");
            headerRow.createCell(5).setCellValue("Available Quantity");
            headerRow.createCell(6).setCellValue("Status");

            int rowIdx = 1;
            for (Asset asset : assets) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(asset.getId());
                row.createCell(1).setCellValue(asset.getAssetCode());
                row.createCell(2).setCellValue(asset.getName());
                row.createCell(3).setCellValue(asset.getCategory());
                row.createCell(4).setCellValue(asset.getTotalQuantity());
                row.createCell(5).setCellValue(asset.getAvailableQuantity());
                row.createCell(6).setCellValue(asset.getStatus());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream exportAssetsToPDF() throws DocumentException {
        List<Asset> assets = assetRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("ASSETS INVENTORY REPORT"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        table.addCell("Code");
        table.addCell("Name");
        table.addCell("Category");
        table.addCell("Total");
        table.addCell("Available");
        table.addCell("Status");

        for (Asset asset : assets) {
            table.addCell(asset.getAssetCode() != null ? asset.getAssetCode() : "");
            table.addCell(asset.getName() != null ? asset.getName() : "");
            table.addCell(asset.getCategory() != null ? asset.getCategory() : "");
            table.addCell(String.valueOf(asset.getTotalQuantity()));
            table.addCell(String.valueOf(asset.getAvailableQuantity()));
            table.addCell(asset.getStatus() != null ? asset.getStatus() : "");
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
