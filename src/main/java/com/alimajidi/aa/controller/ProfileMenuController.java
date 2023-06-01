package com.alimajidi.aa.controller;

import com.alimajidi.aa.model.User;
import com.alimajidi.aa.view.App;
import javafx.scene.control.Label;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class ProfileMenuController {

    public static void logout() {
        MainMenuController.setCurrentUser(null);
        LoginMenuController.updateDatabase();
    }

    public static void deleteAcc() {
        LoginMenuController.removeUser(MainMenuController.getCurrentUser());
        MainMenuController.setCurrentUser(null);
        LoginMenuController.updateDatabase();
    }

    public static void changeUserPass(String username, String password, Label responseLabel) {
        if (MainMenuController.getCurrentUser().equals(User.getGuestUser()))
            responseLabel.textProperty().bind(App.createBiding(GUEST_CHANGE_USER_PASS));
        else {
            if (LoginMenuController.isUsernameExists(username)
                    && !username.equals(MainMenuController.getCurrentUser().getUsername()))
                responseLabel.textProperty().bind(App.createBiding(USER_UNAVAILABLE));
            else {
                MainMenuController.getCurrentUser().setUsername(username);
                MainMenuController.getCurrentUser().setPasswordHash(LoginMenuController.generatePasswordHash(password));
                responseLabel.textProperty().bind(App.createBiding(CHANGE_USER_PASS_SUCCESS));
            }
        }
        LoginMenuController.updateDatabase();
    }
}
