package com.interview.platform.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

public class PdfExtractor {
    
    public static String extractTextFromPdf(String filePath) throws IOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(filePath));
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
}
