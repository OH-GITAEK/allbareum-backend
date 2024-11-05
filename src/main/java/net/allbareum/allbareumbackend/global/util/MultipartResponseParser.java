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
        String[] parts = new String(responseBody, "ISO_8859_1").split(boundaryLine);

        for (String part : parts) {
            if (part.contains("Content-Disposition: form-data; name=")) {
                String name = extractName(part);

                if (part.contains("Content-Type: image/")) {
                    String filename = extractFilename(part);
                    byte[] fileData = extractFileData(responseBody, part, boundaryLine);
                    fileParts.put(name, new MockMultipartFile(name, filename, "image/png", fileData));
                } else {
                    // 텍스트 데이터를 UTF-8로 추출하도록 수정
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
        int startIndex = part.indexOf("\r\n\r\n") + 4; // 이미지 데이터의 시작 지점
        int partStartIndex = new String(responseBody, "ISO_8859_1").indexOf(part);
        int fileDataStart = partStartIndex + startIndex;
        int fileDataEnd = fileDataStart + (part.length() - startIndex) - boundaryLine.length() - 6;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(responseBody, fileDataStart, fileDataEnd - fileDataStart);
        return outputStream.toByteArray();
    }

    private String extractTextContent(String part) {
        int startIndex = part.indexOf("\r\n\r\n") + 4;
        int endIndex = part.lastIndexOf("\r\n");
        // 텍스트를 UTF-8로 인코딩하여 반환
        return new String(part.substring(startIndex, endIndex).trim().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public String getTextPart(String name) {
        return textParts.get(name);
    }

    public MultipartFile getFilePart(String name) {
        return fileParts.get(name);
    }
}


