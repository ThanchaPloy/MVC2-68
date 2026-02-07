package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvUserRepository {
    private final File file;

    public CsvUserRepository(String path) {
        this.file = new File(path);
    }

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = br.readLine(); // header
            if (line == null) return users;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);

                String userId = p[0].trim();
                String name = p[1].trim();
                User.Role role = User.Role.valueOf(p[2].trim());

                users.add(new User(userId, name, role));
            }
        }
        return users;
    }

    public Optional<User> findById(String userId) throws IOException {
        return findAll().stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }
}
