package com.exist;

import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TableUtils {

    public static Table readFileFromResource(String fileName) throws IOException {
        Table table = new Table(); 
        char entryDelimiter = '\u001E'; // Non-printable delimiter for entries (Record Separator)
        char pairDelimiter = '\u001F';  // Non-printable delimiter for key-value pairs (Unit Separator)

        try (InputStream inputStream = TableUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            
            if (inputStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }

            List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);

            for (String line : lines) {
                Row row = new Row(); 
                String[] entries = line.split(String.valueOf(entryDelimiter)); 

                for (String entry : entries) {
                    if (!entry.isBlank() && entry.startsWith("(") && entry.endsWith(")")) { 
                        // Extract content inside parentheses
                        String content = entry.substring(1, entry.length() - 1); 

                        String[] keyValue = content.split(String.valueOf(pairDelimiter), 2);
                        if (keyValue.length == 2) { 
                            String key = keyValue[0];   
                            String value = keyValue[1]; 
                            row.addCell(new Cell(key, value)); 
                        }
                    }
                }

                table.addRow(row); 
            }
        }

        return table; 
    }

    public static Table readFile(String fileName) throws IOException {
        Table table = new Table(); 
        char entryDelimiter = '\u001E'; // Non-printable delimiter for entries (Record Separator)
        char pairDelimiter = '\u001F';  // Non-printable delimiter for key-value pairs (Unit Separator)

        try (InputStream inputStream = new FileInputStream(fileName)) {
            List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);

            for (String line : lines) {
                Row row = new Row(); 
                String[] entries = line.split(String.valueOf(entryDelimiter)); 

                for (String entry : entries) {
                    if (!entry.isBlank() && entry.startsWith("(") && entry.endsWith(")")) { 
                        // Extract content inside parentheses
                        String content = entry.substring(1, entry.length() - 1); 

                        String[] keyValue = content.split(String.valueOf(pairDelimiter), 2);
                        if (keyValue.length == 2) { 
                            String key = keyValue[0];   
                            String value = keyValue[1]; 
                            row.addCell(new Cell(key, value)); 
                        }
                    }
                }

                table.addRow(row); 
            }
        }

        return table; 
    }

    public static void writeTableToFile(Table table, String fileName) throws IOException {
        char entryDelimiter = '\u001E'; // Record Separator
        char pairDelimiter = '\u001F';  // Unit Separator

        // Optionally, you can write to a temporary file or the working directory
        File outputFile = new File(fileName); // Using the same name to write to the same file.
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        try (Writer writer = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
            for (Row row : table.getRows()) {
                StringBuilder line = new StringBuilder(); 

                for (Cell cell : row.getCells()) { 
                    line.append("(").append(cell.getKey()).append(pairDelimiter).append(cell.getValue()).append(")").append(entryDelimiter);
                }

                IOUtils.write(line.append(System.lineSeparator()).toString(), writer);
            }
        }
    }
}

