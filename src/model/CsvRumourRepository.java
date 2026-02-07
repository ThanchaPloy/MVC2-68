package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class CsvRumourRepository {
    private final File file;

    public CsvRumourRepository(String path) {
        this.file = new File(path);
    }

    public List<Rumour> findAll() throws IOException {
        List<Rumour> rumours = new ArrayList<>();
        if (!file.exists()) return rumours;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String header = br.readLine();
            if (header == null) return rumours;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String id = p[0].trim();
                String topic = p[1].trim();
                String source = p[2].trim();
                LocalDate created = LocalDate.parse(p[3].trim());
                int cred = Integer.parseInt(p[4].trim());
                Rumour.Status status = Rumour.Status.valueOf(p[5].trim());
                Rumour.VerifiedResult vr = Rumour.VerifiedResult.valueOf(p[6].trim());

                rumours.add(new Rumour(id, topic, source, created, cred, status, vr));
            }
        }
        return rumours;
    }

    public Optional<Rumour> findById(String rumourId) throws IOException {
        return findAll().stream().filter(r -> r.getRumourId().equals(rumourId)).findFirst();
    }

    public void saveAll(List<Rumour> rumours) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();

        // validate rumourId for each item before writing
        for (Rumour r : rumours) {
            if (!isValidRumourId(r.getRumourId())) {
                throw new IllegalArgumentException("Invalid rumourId format: " + r.getRumourId());
            }
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            bw.write("rumourId,topic,source,createdDate,credibilityScore,status,verifiedResult\n");
            for (Rumour r : rumours) {
                bw.write(String.join(",",
                        r.getRumourId(),
                        escape(r.getTopic()),
                        escape(r.getSource()),
                        r.getCreatedDate().toString(),
                        String.valueOf(r.getCredibilityScore()),
                        r.getStatus().name(),
                        r.getVerifiedResult().name()
                ));
                bw.write("\n");
            }
        }
    }

    public void update(Rumour updated) throws IOException {
        List<Rumour> all = findAll();
        boolean found = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getRumourId().equals(updated.getRumourId())) {
                all.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) all.add(updated);

        saveAll(all);
    }

    private boolean isValidRumourId(String id) {
        if (id == null) return false;
        // exactly 8 digits and first digit not '0'
        return id.matches("[1-9][0-9]{7}");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}
