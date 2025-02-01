import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.Random;
import java.util.InputMismatchException;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        String fileName = null;
        Table table = null;

        if (args.length > 0) {
            File file = new File(args[0]);

            while (!file.exists()) {
                System.out.println("Enter a valid file name: ");
                fileName = scanner.nextLine(); 
                file = new File(fileName);   
            }

            fileName = file.getAbsolutePath();
            try {
			    table = TableUtils.readFile(fileName);
			    System.out.println("Table loaded successfully: ");
			    System.out.println(table);
			} catch (IOException e) {
			    System.out.println("Something went wrong in reading the file: " + e.getMessage());
			}

        } else {
            System.out.println("Enter a file name: ");
            fileName = scanner.nextLine();
            table = TableUtils.resetTable("3x3", random);
            try {
                TableUtils.writeTableToFile(table, fileName);
            } catch (IOException e) {
                System.out.println("Something went wrong in writing to the file. " + e);
            }
        }       

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
                    TableUtils.searchPattern(table, searchPattern);
                    break;

                case "2":
                    System.out.println("Edit:");
                    String editIndex = validateInput(scanner);
                    System.out.println("key, value, or both? ");
                    String editChoice = scanner.nextLine();
                    String key = null;
                    String value = null;
                    if (editChoice.equalsIgnoreCase("both")) {
                        System.out.println("Enter key: ");
                        key = scanner.nextLine();
                        System.out.println("Enter value: ");
                        value = scanner.nextLine();
                    } else if (editChoice.equalsIgnoreCase("key")) {
                        System.out.println("Enter key: ");
                        key = scanner.nextLine();
                    } else if (editChoice.equalsIgnoreCase("value")) {
                        System.out.println("Enter value: ");
                        value = scanner.nextLine();
                    } else {
                        System.out.println("Invalid choice.");
                        break;
                    }
                    try {
                        TableUtils.editTable(editIndex, editChoice, key, value, table);
                        TableUtils.writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file. " + e);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Invalid cell entered: " + e);
                    } catch (IllegalArgumentException e) {
                        System.out.println("For 'both', provide two values separated by a space. " + e);
                    }
                    System.out.println(table);
                    break;

                case "3":
                    try {
                        System.out.println("Enter row index to insert new row at: ");
                        int insertIndex = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter number of cells: ");
                        int cells = scanner.nextInt();
                        scanner.nextLine();
                        TableUtils.addRow(insertIndex, cells, table, random);
                        TableUtils.writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file. " + e);
                    } catch (InputMismatchException e) {
                        System.out.println("Enter a number only. " + e);
                        scanner.nextLine();
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Invalid row index entered. " + e);
                    } 
                    System.out.println(table);
                    break;

                case "4":
                    try {
                        System.out.println("Enter index of row to sort: ");
                        int rowIndex = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("\"asc\" or \"desc\"?: ");
                        String order = scanner.nextLine();
                        TableUtils.sortRow(rowIndex, order, table);
                        TableUtils.writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file. " + e);
                    } catch (InputMismatchException e) {
                        System.out.println("Enter a number only. " + e);
                        scanner.nextLine();
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Invalid row index entered. " + e);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid order. Enter \"asc\" or \"desc\". " + e);
                    }
                    System.out.println(table);
                    break;

                case "5":
                    try {
                        TableUtils.writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file: " + e.getMessage());
                    }
                    System.out.println(table);
                    break;

                case "6":
                    String dimension = validateInput(scanner);
                    table = TableUtils.resetTable(dimension, random);
                    try {
                        TableUtils.writeTableToFile(table, fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong in writing to the file: " + e.getMessage());
                    }
                    System.out.println(table);
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

    
}