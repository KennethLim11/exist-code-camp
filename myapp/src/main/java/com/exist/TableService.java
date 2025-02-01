package com.exist;

import java.util.Random;
import java.util.Comparator;

public class TableService {

	public void editTable(String input, String choice, String key, String value, Table table) throws IndexOutOfBoundsException {
        String[] dimensions = input.split("x");
        int rowIndex = Integer.parseInt(dimensions[0]);
        int colIndex = Integer.parseInt(dimensions[1]);

        Row row = table.getRow(rowIndex);
        Cell cell = row.getCell(colIndex);

        String oldKey = cell.getKey();
        String oldValue = cell.getValue();

        switch (choice.toLowerCase()) {
            case "key":
                cell.setKey(key); 
                System.out.printf("Old key: [%s] --> New key: [%s]%n", oldKey, value);
                break;

            case "value":
                cell.setValue(value);  
                System.out.printf("Old value: [%s] --> New value: [%s]%n", oldValue, value);
                break;

            case "both":
                cell.setKey(key);   
                cell.setValue(value); 
                System.out.printf("Old key-value: ([%s], [%s]) --> New key-value: ([%s], [%s])%n", oldKey, oldValue, key, value);
                break;

            default:
                System.out.println("Invalid choice! Please choose 'key', 'value', or 'both'.");
                return;
        }
    }

    public Table resetTable(String dimension, Random random) {
        String[] dimensions = dimension.split("x");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        Table table = new Table(); 

        for (int i = 0; i < rows; i++) {
            Row row = new Row(); 
            for (int j = 0; j < cols; j++) {
                String key = generateRandomAscii(random);
                String value = generateRandomAscii(random);
                row.addCell(new Cell(key, value)); 
            }
            table.addRow(row); 
        }

        return table; 
    }

    public static String generateRandomAscii(Random random) {
        String asciiString = "";
        for (int i = 0; i < 3; i++) {
            int asciiValue = 33 + random.nextInt(94); // Printable ASCII range 33-126
            asciiString += (char) asciiValue;
        }
        return asciiString;
    }

    public void sortRow(int rowIndex, String order, Table table) throws IndexOutOfBoundsException {
        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException();
        }

        Row row = table.getRow(rowIndex);

        // Define a comparator to sort the cells based on the key
        Comparator<Cell> comparator = Comparator.comparing(Cell::getKeyValue);

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        row.getCells().sort(comparator);

        System.out.println("Row " + rowIndex + " sorted in " + order + " order.");
    }

    public void addRow(int rowIndex, int numCells, Table table, Random random) throws IndexOutOfBoundsException {
        Row newRow = new Row();
        for (int i = 0; i < numCells; i++) {
            String key = generateRandomAscii(random);
            String value = generateRandomAscii(random);
            newRow.addCell(new Cell(key, value)); 
        }

        // Insert the new row after the specified row index
        table.addRowAt(rowIndex, newRow); 
        System.out.println("New row added after index " + rowIndex);
    }

    public void searchPattern(Table table, String pattern) {
        int totalOccurrences = 0;

        System.out.println("\nSearch Results:");

        // Iterate through each row and cell in the table
        for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
            Row row = table.getRow(rowIndex);
            for (int colIndex = 0; colIndex < row.getCells().size(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                String key = cell.getKey();
                String value = cell.getValue();

                // Count occurrences in key and value
                int keyOccurrences = countOccurrences(key, pattern);
                int valueOccurrences = countOccurrences(value, pattern);

                if (keyOccurrences > 0) {
                    totalOccurrences += keyOccurrences;
                    System.out.printf("%d occurrence(s) in key of [ %d, %d ]%n", keyOccurrences, rowIndex, colIndex);
                }
                if (valueOccurrences > 0) {
                    totalOccurrences += valueOccurrences;
                    System.out.printf("%d occurrence(s) in value of [ %d, %d ]%n", valueOccurrences, rowIndex, colIndex);
                }
            }
        }

        if (totalOccurrences == 0) {
            System.out.println("No occurrences found.");
        } else {
            System.out.println("Total occurrences: " + totalOccurrences);
        }
    }

    private static int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = text.indexOf(pattern);
        while (index != -1) {
            count++;
            index = text.indexOf(pattern, index + 1); // Find the next occurrence
        }
        return count;
    }

}