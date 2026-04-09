package main;

public final class AdminAuth {

    private static final String ADMIN_PASSWORD = "admin123";

    private AdminAuth() {
    }

    public static boolean isValidPassword(String password) {
        return ADMIN_PASSWORD.equals(password);
    }
}
