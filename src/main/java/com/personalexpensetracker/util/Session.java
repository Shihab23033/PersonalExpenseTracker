package com.personalexpensetracker.util;

import com.personalexpensetracker.model.User;

/**
 * Simple in-memory session holder for current logged-in user.
 * Note: this is a minimal approach for a desktop app.
 */
public class Session {
    private static User currentUser;

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static void clear() { currentUser = null; }
}
