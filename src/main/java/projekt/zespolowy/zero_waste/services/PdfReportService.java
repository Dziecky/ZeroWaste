package projekt.zespolowy.zero_waste.services;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.OrderStatsDTO;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {
    private static final Logger logger = LoggerFactory.getLogger(PdfReportService.class);
    private final StatisticsService statisticsService;

    public byte[] generateBusinessReport() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDType1Font font = PDType1Font.HELVETICA_BOLD;
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float rowHeight = 20;
                float currentY = yStart;

                // Report header
                contentStream.setFont(PDType1Font.TIMES_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Business Statistics Report");
                contentStream.endText();
                currentY -= 40;

                // Table headers
                String[] headers = {"Category", "Orders", "Quantity", "Unit", "Amount"};
                double[] columnWidths = {150.0, 80.0, 80.0, 80.0, 100.0};
                contentStream.setFont(font, 12);
                drawRow(contentStream, margin, currentY, columnWidths, headers, true, false);
                currentY -= rowHeight;

                // Table data
                List<OrderStatsDTO> stats = statisticsService.getQuarterlyOrderStats();
                for (OrderStatsDTO stat : stats) {
                    String[] row = {
                            stat.getCategory().toString(),
                            String.valueOf(stat.getOrderCount()),
                            String.format("%.2f", stat.getTotalQuantity()),
                            stat.getUnitOfMeasure(),
                            String.format("%.2f PLN", stat.getTotalAmount())
                    };
                    drawRow(contentStream, margin, currentY, columnWidths, row, false, false);
                    currentY -= rowHeight;
                }

                // Total row
                String[] totalRow = {
                        "Total",
                        String.valueOf(stats.stream().mapToLong(OrderStatsDTO::getOrderCount).sum()),
                        "",
                        "",
                        String.format("%.2f PLN", stats.stream().mapToDouble(OrderStatsDTO::getTotalAmount).sum())
                };
                drawRow(contentStream, margin, currentY, columnWidths, totalRow, false, true);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    private void drawRow(PDPageContentStream contentStream, float x, float y, double[] widths, String[] texts,
                         boolean isHeader, boolean isTotalRow) throws IOException {
        float rowHeight = 20;
        float cellPadding = 5;
        float fontSize = 12;
        PDType1Font font = isHeader || isTotalRow ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;

        contentStream.setFont(font, fontSize);

        if (isHeader || isTotalRow) {
            contentStream.setNonStrokingColor(isHeader ? Color.LIGHT_GRAY : new Color(230, 230, 230));
            contentStream.addRect(x, y - rowHeight, (float) Arrays.stream(widths).sum(), rowHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);
        }

        float currentX = x;
        for (int i = 0; i < widths.length; i++) {
            float width = (float) widths[i];

            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(currentX, y - rowHeight, width, rowHeight);
            contentStream.stroke();

            String text = texts[i] != null ? texts[i] : "";
            float textWidth = font.getStringWidth(text) * fontSize / 1000;
            float textX = (i > 0 && i != 3) ? currentX + width - textWidth - cellPadding : currentX + cellPadding;

            contentStream.beginText();
            contentStream.newLineAtOffset(textX, y - 15);
            contentStream.showText(text);
            contentStream.endText();

            currentX += width;
        }
    }
}