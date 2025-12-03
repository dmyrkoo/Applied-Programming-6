package services;

import model.*;
import utils.FileManager;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SkladService {
    private Potiag potiag;
    private Scanner scanner = new Scanner(System.in);
    private int nextId = 1;

    public SkladService(Potiag potiag) {
        this.potiag = potiag;
    }

    public void dodatyVagon() {
        System.out.println("\n=== Додавання вагону ===");
        System.out.println("1 - Пасажирський");
        System.out.println("2 - Службовий");
        System.out.print("Ваш вибір: ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            dodatyPasazhyrskyVagon();
        } else if (choice.equals("2")) {
            dodatySlyzhbovyVagon();
        } else {
            System.out.println("❌ Невірний вибір!");
        }
    }

    private void dodatyPasazhyrskyVagon() {
        try {
            System.out.print("Комфортність (1-10): ");
            int komf = Integer.parseInt(scanner.nextLine());

            System.out.print("Кількість багажу: ");
            int bagazh = Integer.parseInt(scanner.nextLine());

            System.out.println("Клас (VIP/KUPE/PLATSKART/ZAHALNYI): ");
            String klasStr = scanner.nextLine().toUpperCase();
            KlasKomfortu klas = KlasKomfortu.fromString(klasStr);

            System.out.print("Кількість пасажирів: ");
            int pasazhyriv = Integer.parseInt(scanner.nextLine());

            System.out.print("Рівень обслуговування (1-10): ");
            int riven = Integer.parseInt(scanner.nextLine());

            PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                    nextId++, komf, bagazh, klas, pasazhyriv, riven
            );

            potiag.dodatyVagon(vagon);
            System.out.println("✅ Додано: " + vagon);

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка введення!");
        }
    }

    private void dodatySlyzhbovyVagon() {
        try {
            System.out.print("Комфортність (1-10): ");
            int komf = Integer.parseInt(scanner.nextLine());

            System.out.print("Кількість багажу: ");
            int bagazh = Integer.parseInt(scanner.nextLine());

            System.out.print("Кількість персоналу: ");
            int personal = Integer.parseInt(scanner.nextLine());

            System.out.print("Тип (Restoran/Poshta/...): ");
            String typ = scanner.nextLine();

            SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                    nextId++, komf, bagazh, personal, typ
            );

            potiag.dodatyVagon(vagon);
            System.out.println("✅ Додано: " + vagon);

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка введення!");
        }
    }

    public void vydalytyVagon() {
        pokazatySklad();
        if (potiag.getSklad().isEmpty()) {
            return;
        }

        System.out.print("\nВведіть ID для видалення: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (potiag.vydalytyVagon(id)) {
                System.out.println("✅ Вагон #" + id + " видалено");
            } else {
                System.out.println("❌ Вагон не знайдено");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректний ID!");
        }
    }

    public void redaguvatyVagon() {
        pokazatySklad();
        if (potiag.getSklad().isEmpty()) {
            return;
        }

        System.out.print("\nВведіть ID для редагування: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Vagon vagon = potiag.znaytyVagonById(id);

            if (vagon == null) {
                System.out.println("❌ Вагон не знайдено");
                return;
            }

            System.out.println("Редагування: " + vagon);

            System.out.print("Нова комфортність (Enter - skip): ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                vagon.setKomfortnist(Integer.parseInt(input));
            }

            System.out.print("Новий багаж (Enter - skip): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                vagon.setBagazhKilkist(Integer.parseInt(input));
            }

            if (vagon instanceof PasazhyrskyVagon) {
                PasazhyrskyVagon pv = (PasazhyrskyVagon) vagon;

                System.out.print("Нова к-ть пасажирів (Enter - skip): ");
                input = scanner.nextLine();
                if (!input.isEmpty()) {
                    pv.setKilkistPasazhyriv(Integer.parseInt(input));
                }

                System.out.print("Новий рівень (Enter - skip): ");
                input = scanner.nextLine();
                if (!input.isEmpty()) {
                    pv.setRivenObslugovuvannya(Integer.parseInt(input));
                }
            }

            System.out.println("✅ Оновлено: " + vagon);

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка!");
        }
    }

    public void sortuvatyVagony() {
        List<Vagon> sklad = potiag.getSklad();

        if (sklad.isEmpty()) {
            System.out.println("❌ Склад порожній!");
            return;
        }

        sklad.sort(Comparator
                .comparingInt(Vagon::getKomfortnist).reversed()
                .thenComparing((v1, v2) -> {
                    if (v1 instanceof PasazhyrskyVagon && v2 instanceof PasazhyrskyVagon) {
                        return Integer.compare(
                                ((PasazhyrskyVagon)v2).getRivenObslugovuvannya(),
                                ((PasazhyrskyVagon)v1).getRivenObslugovuvannya()
                        );
                    }
                    return 0;
                })
        );

        System.out.println("✅ Відсортовано за рівнем комфортності");
        pokazatySklad();
    }

    public void pokazatySklad() {
        System.out.println("\n=== " + potiag + " ===");

        if (potiag.getSklad().isEmpty()) {
            System.out.println("Склад порожній");
            return;
        }

        for (Vagon v : potiag.getSklad()) {
            System.out.println(v);
        }
    }

    public void pidrakhuvatyPasazhyriv() {
        int total = potiag.getZagalnaKilkistPasazhyriv();
        System.out.println("✅ Загальна к-ть пасажирів: " + total);
    }

    public void pidrakhuvatyBagazh() {
        int total = potiag.getZagalnyiBagazh();
        System.out.println("✅ Загальний багаж: " + total);
    }

    public void znaytyVagon() {
        try {
            System.out.print("Мін. к-ть пасажирів: ");
            int min = Integer.parseInt(scanner.nextLine());

            System.out.print("Макс. к-ть пасажирів: ");
            int max = Integer.parseInt(scanner.nextLine());

            List<Vagon> znaideni = potiag.getSklad().stream()
                    .filter(v -> v.getPasazhyrskaMistkist() >= min && v.getPasazhyrskaMistkist() <= max)
                    .collect(Collectors.toList());

            System.out.println("\n=== Знайдено: " + znaideni.size() + " ===");
            for (Vagon v : znaideni) {
                System.out.println(v);
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка!");
        }
    }

    public void zberegtyUFile() {
        try {
            FileManager.zberegty(potiag);
            System.out.println("✅ Збережено у potiag_data.txt");
        } catch (IOException e) {
            System.out.println("❌ Помилка: " + e.getMessage());
        }
    }

    public void zavantazhytyZFile() {
        try {
            Potiag loaded = FileManager.zavantazhyty();
            potiag.ochystyty();
            for (Vagon v : loaded.getSklad()) {
                potiag.dodatyVagon(v);
                if (v.getId() >= nextId) {
                    nextId = v.getId() + 1;
                }
            }
            System.out.println("✅ Завантажено");
            pokazatySklad();
        } catch (IOException e) {
            System.out.println("❌ Помилка: " + e.getMessage());
        }
    }

    public void vykhid() {
        System.out.println("До побачення!");
    }

    public Potiag getPotiag() {
        return potiag;
    }
}