package model;

import java.util.Objects;

public class User {
    public enum Role { GENERAL, VERIFIER }

    private final String userId;
    private final String name;
    private final Role role;

    public User(String userId, String name, Role role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public Role getRole() { return role; }

    @Override
    public String toString() {
        // Show name and role
        return name + " (" + userId + ", " + role + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        return Objects.equals(userId, ((User)o).userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
