package net.allbareum.allbareumbackend.global.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultipartResponseParser {
    private final Map<String, String> textParts = new HashMap<>();
    private final Map<String, MultipartFile> fileParts = new HashMap<>();

    public MultipartResponseParser(byte[] responseBody, String boundary) throws IOException {
        parseResponse(responseBody, boundary);
    }

    private void parseResponse(byte[] responseBody, String boundary) throws IOException {
        String boundaryLine = "--" + boundary;
        String[] parts = new String(responseBody, StandardCharsets.UTF_8).split(boundaryLine);

        for (String part : parts) {
            if (part.contains("Content-Disposition: form-data; name=")) {
                String name = extractName(part);

                if (part.contains("Content-Type: image/")) {
                    String filename = extractFilename(part);
                    byte[] fileData = extractFileData(responseBody, part, boundaryLine);
                    fileParts.put(name, new MockMultipartFile(name, filename, "image/png", fileData));
                } else {
                    String textContent = extractTextContent(part);
                    textParts.put(name, textContent);
                }
            }
        }
    }

    private String extractName(String part) {
        int startIndex = part.indexOf("name=\"") + 6;
        int endIndex = part.indexOf("\"", startIndex);
        return part.substring(startIndex, endIndex);
    }

    private String extractFilename(String part) {
        int startIndex = part.indexOf("filename=\"") + 10;
        int endIndex = part.indexOf("\"", startIndex);
        return part.substring(startIndex, endIndex);
    }

    private byte[] extractFileData(byte[] responseBody, String part, String boundaryLine) throws IOException {
        // Find start and end of the file data in responseBody based on part's location
        int partStartIndex = new String(responseBody, StandardCharsets.UTF_8).indexOf(part);
        int startIndex = part.indexOf("\r\n\r\n") + 4; // Image data starts after this
        int fileDataStart = partStartIndex + startIndex;

        // Locate the boundary after the file data to find the file's end
        int nextBoundaryIndex = new String(responseBody, StandardCharsets.UTF_8).indexOf(boundaryLine, fileDataStart);
        int fileDataEnd = nextBoundaryIndex - 2; // Subtract 2 to remove the preceding \r\n

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(responseBody, fileDataStart, fileDataEnd - fileDataStart);
        return outputStream.toByteArray();
    }

    private String extractTextContent(String part) {
        int startIndex = part.indexOf("\r\n\r\n") + 4;
        int endIndex = part.lastIndexOf("\r\n");
        return part.substring(startIndex, endIndex).trim();
    }

    public String getTextPart(String name) {
        return textParts.get(name);
    }

    public MultipartFile getFilePart(String name) {
        return fileParts.get(name);
    }
}


