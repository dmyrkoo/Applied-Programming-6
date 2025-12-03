package utils;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DEFAULT_FILE = "potiag_data.txt";

    public static void zberegty(Potiag potiag, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("NAZVA:" + potiag.getNazva());
            writer.newLine();

            for (Vagon v : potiag.getSklad()) {
                writer.write(v.toFileString());
                writer.newLine();
            }
        }
    }

    public static void zberegty(Potiag potiag) throws IOException {
        zberegty(potiag, DEFAULT_FILE);
    }

    public static Potiag zavantazhyty(String filename) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        if (lines.isEmpty()) {
            throw new IOException("Файл порожній");
        }

        String nazva = "Potiag";
        if (lines.get(0).startsWith("NAZVA:")) {
            nazva = lines.get(0).substring(6);
            lines.remove(0);
        }

        Potiag potiag = new Potiag(nazva);

        for (String line : lines) {
            Vagon v = parseVagon(line);
            if (v != null) {
                potiag.dodatyVagon(v);
            }
        }

        return potiag;
    }

    public static Potiag zavantazhyty() throws IOException {
        return zavantazhyty(DEFAULT_FILE);
    }

    private static Vagon parseVagon(String line) {
        String[] parts = line.split(";");
        if (parts.length < 2) return null;

        try {
            String type = parts[0];

            if (type.equals("PASAZHYRSKY") && parts.length >= 7) {
                int id = Integer.parseInt(parts[1]);
                int komf = Integer.parseInt(parts[2]);
                int bagazh = Integer.parseInt(parts[3]);
                KlasKomfortu klas = KlasKomfortu.fromString(parts[4]);
                int pasazhyriv = Integer.parseInt(parts[5]);
                int riven = Integer.parseInt(parts[6]);

                return new PasazhyrskyVagon(id, komf, bagazh, klas, pasazhyriv, riven);

            } else if (type.equals("SLYZHBOVY") && parts.length >= 6) {
                int id = Integer.parseInt(parts[1]);
                int komf = Integer.parseInt(parts[2]);
                int bagazh = Integer.parseInt(parts[3]);
                int personal = Integer.parseInt(parts[4]);
                String pryznachennya = parts[5];

                return new SlyzhbovyVagon(id, komf, bagazh, personal, pryznachennya);
            }
        } catch (NumberFormatException e) {
            System.err.println("Помилка парсингу: " + line);
        }

        return null;
    }

    public static boolean fileExists() {
        return new File(DEFAULT_FILE).exists();
    }
}