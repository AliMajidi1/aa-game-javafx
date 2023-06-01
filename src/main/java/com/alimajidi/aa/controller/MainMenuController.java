package com.alimajidi.aa.controller;

import com.alimajidi.aa.model.User;

public class MainMenuController {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        MainMenuController.currentUser = currentUser;
    }
}
