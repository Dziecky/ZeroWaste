package projekt.zespolowy.zero_waste.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projekt.zespolowy.zero_waste.dto.OrderStatsDTO;
import projekt.zespolowy.zero_waste.entity.ProductCategory;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PdfReportServiceTest {

    private StatisticsService statisticsService;
    private PdfReportService pdfReportService;

    @BeforeEach
    public void setUp() {
        statisticsService = mock(StatisticsService.class);
        pdfReportService = new PdfReportService(statisticsService);
    }

    @Test
    public void testGenerateBusinessReport_returnsNonEmptyPdf() throws Exception {
        // given
        OrderStatsDTO dto1 = new OrderStatsDTO(
                ProductCategory.FOOD,
                10,
                25.5,
                "kg",
                100.0
        );

        when(statisticsService.getQuarterlyOrderStats()).thenReturn(List.of(dto1));

        // when
        byte[] pdfBytes = pdfReportService.generateBusinessReport();

        // then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);


        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            String text = new PDFTextStripper().getText(document);

          //  System.out.println("PDF CONTENT:\n" + text);

            assertTrue(text.toLowerCase().contains("business statistics report"));
            assertTrue(text.toLowerCase().contains("food"));
            assertTrue(text.contains("10"));
            assertTrue(text.contains("kg"));
            assertTrue(text.contains("25,50"));
            assertTrue(text.contains("100,00 PLN"));
        }


        verify(statisticsService, times(1)).getQuarterlyOrderStats();
    }

}
