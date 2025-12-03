package main;

import commands.*;
import model.Potiag;
import services.SkladService;
import utils.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private List<Command> commands = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private SkladService service;

    public Main() {
        Potiag potiag = new Potiag("Lviv-Kyiv Express");
        service = new SkladService(potiag);

        if (FileManager.fileExists()) {
            System.out.println("📂 Знайдено збережені дані. Завантажую...");
            service.zavantazhytyZFile();
        }

        commands.add(new AddVagonCommand(service));
        commands.add(new ShowSkladCommand(service));
        commands.add(new SortVagonsCommand(service));
        commands.add(new DeleteVagonCommand(service));
        commands.add(new EditVagonCommand(service));
        commands.add(new CalculatePasazhyryCommand(service));
        commands.add(new CalculateBagazhCommand(service));
        commands.add(new FindVagonCommand(service));
        commands.add(new SaveToFileCommand(service));
        commands.add(new LoadFromFileCommand(service));
        commands.add(new ExitCommand(service));
    }

    public void run() {
        printWelcome();

        while (true) {
            printMenu();
            System.out.print("Ваш вибір: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                executeCommand("0");
                System.out.println("Зберігаю дані...");
                service.zberegtyUFile();
                break;
            }

            executeCommand(choice);
            waitForEnter();
        }
    }

    private void printWelcome() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   СИСТЕМА УПРАВЛІННЯ РУХОМИМ СКЛАДОМ ПОТЯГА      ║");
        System.out.println("║              Лабораторна робота № 6               ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────┐");
        System.out.println("│          МЕНЮ КЕРУВАННЯ ПОТЯГОМ                    │");
        System.out.println("└────────────────────────────────────────────────────┘");
        for (Command cmd : commands) {
            System.out.println("  " + cmd.getDescription());
        }
        System.out.println("────────────────────────────────────────────────────");
    }

    private void executeCommand(String choice) {
        for (Command cmd : commands) {
            if (cmd.getDescription().startsWith(choice + ".")) {
                cmd.execute();
                return;
            }
        }
        System.out.println("Невірний вибір!");
    }

    private void waitForEnter() {
        System.out.println("\n[Натисніть Enter для продовження...]");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}