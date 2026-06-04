package com.lims.util;

import com.lims.model.User;

/**
 * SessionManager.java
 *
 * PURPOSE: Holds the currently logged-in user in memory while the app is running.
 * Think of it like a global variable that any screen can read.
 *
 * WHY 'static'?
 * Static fields belong to the class itself, not to any one object.
 * So SessionManager.currentUser is shared across the whole application —
 * only one value exists at a time, accessible from anywhere.
 *
 * HOW TO USE (for Members 2 and 3):
 *
 *   // After login succeeds:
 *   SessionManager.setCurrentUser(loggedInUser);
 *
 *   // On any screen, to get who is logged in:
 *   User me = SessionManager.getCurrentUser();
 *   System.out.println(me.getName());
 *
 *   // On logout:
 *   SessionManager.clearSession();
 */
public class SessionManager {

    // 'private static' = one shared value, not accessible from outside
    private static User currentUser;

    // Private constructor prevents anyone from doing "new SessionManager()"
    private SessionManager() {}

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // Call this when the user logs out
    public static void clearSession() {
        currentUser = null;
    }

    // Convenience check used in controllers to guard screens
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equals(role);
    }
}
