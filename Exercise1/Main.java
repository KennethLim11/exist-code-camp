import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        String fileName = null;
        List<List<Map<String,String>>> table = new ArrayList<>();

        if (args.length > 0) {
            File file = new File(args[0]);

            while (!file.exists()) {
                System.out.println("Enter a valid file name: ");
                fileName = scanner.nextLine(); 
                file = new File(fileName);   
            }

            fileName = file.getAbsolutePath();
            try {
                table = readFile(table, fileName);
            } catch (IOException e) {
                System.out.println("Something went wrong in reading the file. " + e);
            }

        } else {
            System.out.println("Enter a file name: ");
            fileName = scanner.nextLine();
            table = resetTable("3x3", random);
            try {
                writeTableToFile(table, fileName);
            } catch (IOException e) {
                System.out.println("Something went wrong in writing to the file. " + e);
            }
        }

        printTable(table);        

        String choice;
        do {
            System.out.println("Menu:");
            System.out.println("[ 1 ] - Search");
            System.out.println("[ 2 ] - Edit");
            System.out.println("[ 3 ] - Add Row");
            System.out.println("[ 4 ] - Sort Row");
            System.out.println("[ 5 ] - Print");
            System.out.println("[ 6 ] - Reset");
            System.out.println("[ x ] - Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter string pattern to search: ");
                    String searchPattern = scanner.nextLine();
                    searchPattern(table, searchPattern);
                    break;

                case "2":
                    System.out.println("Edit:");
                    String editIndex = validateInput(scanner);
                    System.out.println("key, value, or both? ");
                    String editChoice = scanner.nextLine();
                    System.out.println("Value:");
                    String value = scanner.nextLine();
                    try {
                        editTable(editIndex, editChoice, value, table);
                        writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file. " + e);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Invalid cell entered: " + e);
                    } catch (IllegalArgumentException e) {
                        System.out.println("For 'both', provide two values separated by a space. " + e);
                    }
                    printTable(table);
                    break;

                case "3":
                    try {
                        System.out.println("Enter number of cells: ");
                        int cells = scanner.nextInt();
                        scanner.nextLine();
                        addRow(cells, table, random);
                        writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file. " + e);
                    } catch (InputMismatchException e) {
                        System.out.println("Enter a number only.");
                    }
                    printTable(table);
                    break;

                case "4":
                    System.out.println("Enter index of row to sort: ");
                    int rowIndex = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("\"asc\" or \"desc\"?: ");
                    String order = scanner.nextLine();
                    sortRow(rowIndex, order, table);
                    printTable(table);
                    try {
                        writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file. " + e);
                    }
                    break;

                case "5":
                    printTable(table);
                    break;

                case "6":
                    String dimension = validateInput(scanner);
                    table = resetTable(dimension, random);
                    printTable(table);
                    try {
                        writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file. " + e);
                    }
                    break;

                case "x":
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (!choice.equals("x"));

        scanner.close();
    }

    private static String validateInput(Scanner scanner) {
        String input;
        while (true) {
            System.out.println("Input table dimension: ");
            input = scanner.nextLine();

            if (input.matches("\\d+x\\d+")) {
                break;
            } else {
                System.out.println("Invalid input format. Please enter in the format NxM (e.g., 2x3).");
            }
        }
        return input;
    }

    private static String generateRandomAscii(Random random) {
        String asciiString = "";
        for (int i = 0; i < 3; i++) {
            int asciiValue = 33 + random.nextInt(94); // Printable ASCII range 33-126
            asciiString += ((char) asciiValue);
        }
        return asciiString;
    }

    private static void printTable(List<List<Map<String,String>>> table) {
        System.out.println("Table: ");
        for (List<Map<String, String>> row : table) {
            for (Map<String, String> column : row) {
                column.forEach((key, value) -> System.out.print("(" + key + "," + value + ") "));
            }
            System.out.println();
        }
    }

    private static void addRow(int numCells, List<List<Map<String, String>>> table, Random random) {
        List<Map<String, String>> newRow = new ArrayList<>();
        
        for (int i = 0; i < numCells; i++) {
            Map<String, String> keyValue = new HashMap<>();
            String key = generateRandomAscii(random);
            String value = generateRandomAscii(random);
            keyValue.put(key, value);
            newRow.add(keyValue);
        }

        table.add(newRow);
    }

    private static void sortRow(int rowIndex, String order, List<List<Map<String, String>>> table) {
        // Validate the row index
        if (rowIndex < 0 || rowIndex >= table.size()) {
            System.out.println("Invalid row index: " + rowIndex);
            return;
        }

        // Get the specified row
        List<Map<String, String>> row = table.get(rowIndex);

        // Define a comparator to sort the cells by their keys
        Comparator<Map<String, String>> comparator = (cell1, cell2) -> {
            // Extract keys from both cells (each cell is assumed to contain one key-value pair)
            String key1 = cell1.keySet().iterator().next();
            String key2 = cell2.keySet().iterator().next();
            return key1.compareTo(key2); // Compare keys
        };

        // Reverse the comparator if the order is descending
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        // Sort the row directly using List.sort
        row.sort(comparator);

        System.out.println("Row " + rowIndex + " sorted in " + order + " order.");
    }

    private static void searchPattern(List<List<Map<String,String>>> table, String pattern) {
        int totalOccurrences = 0;

        System.out.println("\nSearch Results:");
        
        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); j++) {
                Map<String, String> keyValue = table.get(i).get(j);
                
                for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    
                    int occurrencesInKey = countOccurrences(key, pattern);
                    int occurrencesInValue = countOccurrences(value, pattern);

                    if (occurrencesInKey > 0) {
                        totalOccurrences += occurrencesInKey;
                        System.out.println(occurrencesInKey + " occurrence/s in key of [ " + i + ", " + j + " ]");
                    }

                    if (occurrencesInValue > 0) {
                        totalOccurrences += occurrencesInValue;
                        System.out.println(occurrencesInValue + " occurrence/s in value of [ " + i + ", " + j + " ]");
                    }
                }
            }
        }

        if (totalOccurrences == 0) {
            System.out.println("No occurrences found.");
        } else {
            System.out.println("Total occurrences: " + totalOccurrences);
        }
    }

    private static int countOccurrences(String value, String pattern) {
        int count = 0;
        int index = value.indexOf(pattern);
        while (index != -1) {
            count++;
            index = value.indexOf(pattern, index + 1); // Search for the next occurrence
        }
        return count;
    }

    private static void editTable(String input, String choice, String value, List<List<Map<String, String>>> table) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] dimensions = input.split("x");
        int row = Integer.parseInt(dimensions[0]);
        int col = Integer.parseInt(dimensions[1]);
        
        Map<String, String> keyValue = table.get(row).get(col);
        String oldKey = keyValue.keySet().iterator().next();
        String oldValue = keyValue.values().iterator().next();
        
        if ("key".equals(choice)) {
            value = value.replaceAll("\\s+", "");
            oldValue = keyValue.remove(oldKey); 
            keyValue.put(value, oldValue); 
            System.out.println("Old key: " + oldKey + " --> New key: " + keyValue.keySet());
        
        } else if ("value".equals(choice)) {
            value = value.replaceAll("\\s+", "");
            keyValue.put(oldKey, value);
            System.out.println("Old value: " + oldValue + " --> New value: " + keyValue.values());
        
        } else if ("both".equals(choice)) {
            String[] values = value.split(" ");
            if (values.length != 2) {
                throw new IllegalArgumentException();
            }
            keyValue.clear();
            keyValue.put(values[0], values[1]); 
            System.out.println("Old key-value: (" + oldKey + "," + oldValue + ") --> New key-value: (" + keyValue.keySet() + "," + keyValue.values() + ")");
        
        } else {
            System.out.println("Invalid choice! Please choose 'key', 'value', or 'both'.");
        }
    }

    private static List<List<Map<String, String>>> resetTable(String input, Random random) {
        String[] dimensions = input.split("x");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        List<List<Map<String, String>>> table = new ArrayList<>();

        // Fill the table with random key-value pairs
        for (int i = 0; i < rows; i++) {
            List<Map<String, String>> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                // Generate random key-value pair for each cell
                Map<String, String> keyValue = new HashMap<>();
                String key = generateRandomAscii(random);
                String value = generateRandomAscii(random);
                keyValue.put(key, value);
                row.add(keyValue);
            }
            table.add(row);
        }
        return table;
    }

    private static void writeTableToFile(List<List<Map<String, String>>> table, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (List<Map<String, String>> row : table) {
                for (Map<String, String> cell : row) {
                    cell.forEach((key, value) -> {
                        try {
                            writer.write("(" + key + "," + value + ") ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                writer.newLine();
            }
        } 
    }

    private static List<List<Map<String, String>>> readFile(List<List<Map<String, String>>> table, String fileName) throws IOException {
        // Pattern pattern = Pattern.compile("\\((.{3}),(.{3})\\)");
        Pattern pattern = Pattern.compile("\\((\\S+),(\\S+)\\)");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                List<Map<String, String>> row = new ArrayList<>();

                // Find all key-value pairs in the line
                while (matcher.find()) {
                    String key = matcher.group(1);  // Extract the key
                    String value = matcher.group(2); // Extract the value

                    Map<String, String> keyValue = new HashMap<>();
                    keyValue.put(key, value);
                    row.add(keyValue);
                }
                table.add(row);
            }
        } 
        return table;
    }
}