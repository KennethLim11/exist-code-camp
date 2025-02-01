package com.exist.app;

import com.exist.service.TableService;
import com.exist.utility.TableUtils;
import com.exist.model.Table;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.Random;
import java.util.InputMismatchException;

public class App {
    public static void main(String[] args) {
        TableService tableService = new TableService();
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        String fileName = null;
        Table table = null;

        if (args.length > 0) {
            System.out.println("Read from file system? y/n ");
            String readInput = scanner.nextLine();

            if (readInput.equalsIgnoreCase("y")) {
                System.out.println("Reading from files...");
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
                System.out.println("Reading from resources...");
                fileName = args[0];
                boolean validFile = false;

                while(!validFile) {
                    try {
                        table = TableUtils.readFileFromResource(fileName);
                        System.out.println("Table loaded successfully: ");
                        System.out.println(table);
                        break;
                    } catch (IOException e) {
                        System.out.println("Something went wrong in reading the file: " + e.getMessage());
                    }
                    System.out.println("Enter a valid file: ");
                    fileName = scanner.nextLine();
                }
            }
            
        } else {
            try {
                table = TableUtils.readFileFromResource("default.txt"); // Assuming this works if the file is on filesystem
                System.out.println("Table loaded successfully: ");
                System.out.println(table);
            } catch (IOException e) {
                System.out.println("Something went wrong in reading the file: " + e.getMessage());
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
                    tableService.searchPattern(table, searchPattern);
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
                        tableService.editTable(editIndex, editChoice, key, value, table);
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
                        tableService.addRow(insertIndex, cells, table, random);
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
                        tableService.sortRow(rowIndex, order, table);
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
                    table = tableService.resetTable(dimension, random);
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