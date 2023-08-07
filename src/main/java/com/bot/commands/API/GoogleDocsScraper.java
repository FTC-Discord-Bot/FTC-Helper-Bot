package com.bot.commands.API;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GoogleDocsScraper {


    public static String extractDocumentContents(String documentId) throws IOException {
        String docUrl = "https://docs.google.com/document/d/" + documentId + "/export?format=html";
        Document doc = Jsoup.connect(docUrl).get();
        Elements paragraphs = doc.select("p"); // Select paragraph elements

        StringBuilder extractedText = new StringBuilder();
        for (Element paragraph : paragraphs) {
            extractedText.append(paragraph.text()); // Extract the text of each paragraph
            extractedText.append("\n");
        }

        return extractedText.toString();
    }

    public static String extractDocumentIdFromLink(String sharingLink) {
        String[] parts = sharingLink.split("/");
        String documentId = parts[parts.length - 2];
        return documentId;
    }
}
