package com.caresync.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DocumentTextExtractionService {

    public String extractText(Path filePath) throws IOException {

        String fileName =
                filePath.getFileName()
                        .toString()
                        .toLowerCase();

        if (fileName.endsWith(".pdf")) {

            try (PDDocument document =
                         Loader.loadPDF(
                                 filePath.toFile())) {

                PDFTextStripper stripper =
                        new PDFTextStripper();

                return stripper.getText(document);
            }
        }

        if (fileName.endsWith(".txt")) {
            return Files.readString(filePath);
        }

        return "";
    }
}