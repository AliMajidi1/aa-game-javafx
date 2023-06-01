package com.alimajidi.aa.controller;

import com.alimajidi.aa.model.User;
import com.alimajidi.aa.view.App;
import javafx.scene.control.Label;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class LoginMenuController {

    public static String generatePasswordHash(String password) {
        return new DigestUtils("SHA3-256").digestAsHex(password);
    }

    public static void fetchDatabase() {
        String path = Objects.requireNonNull(
                User.class.getResource("/data/database.bin")).toExternalForm().substring(6);
        if (new File(path).length() == 0) return;
        ArrayList<User> users = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            while (true) {
                Object userObject;
                try {
                    userObject = in.readObject();
                } catch (Exception e) {
                    break;
                }
                if (userObject instanceof User user) users.add(user);
            }
            User.setUsers(users);
        } catch (Exception e) {
            System.out.println("fetch failed: " + e.getMessage());
        }
    }

    public static void updateDatabase() {
        String path = Objects.requireNonNull(
                User.class.getResource("/data/database.bin")).toExternalForm().substring(6);
        try (FileOutputStream fileOutputStream = new FileOutputStream(path, false);
             ObjectOutputStream out = new ObjectOutputStream(fileOutputStream)) {
            for (User user : User.getUsers()) out.writeObject(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String username, String password, boolean persian, boolean darkMode) {
        User user = new User(username, generatePasswordHash(password));
        User.getUsers().add(user);
    }

    public static void removeUser(User user) {
        User.getUsers().remove(user);
    }

    public static User getUserByUsername(String username) {
        return User.getUsers().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    public static boolean isUsernameExists(String username) {
        return User.getUsers().stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public static boolean checkPassword(String username, String password) {
        return getUserByUsername(username).getPasswordHash().equals(generatePasswordHash(password));
    }

    public static void login(Label label, String username, String password) {
        if (!isUsernameExists(username) || !checkPassword(username, password))
            label.textProperty().bind(App.createBiding(USER_PASS_INCORRECT));
        else {
            label.textProperty().bind(App.createBiding(LOGIN_SUCCESS));
            User userByUsername = getUserByUsername(username);
            App.getMediaPlayer().setVolume(userByUsername.getVolume());
            MainMenuController.setCurrentUser(userByUsername);
            setUserPreference();
            LoginMenuController.updateDatabase();
        }
    }

    public static void setUserPreference() {
        App.setPersian(MainMenuController.getCurrentUser().isPersian());
        App.setDarkMode(MainMenuController.getCurrentUser().isDarkMode());
    }

    public static void register(Label label, String username, String password) {
        if (isUsernameExists(username) || username.equals(""))
            label.textProperty().bind(App.createBiding(USER_UNAVAILABLE));
        else {
            label.textProperty().bind(App.createBiding(REGISTER_SUCCESS));
            addUser(username, password, App.isPersian(), App.isDarkMode());
            MainMenuController.setCurrentUser(getUserByUsername(username));
            LoginMenuController.updateDatabase();
        }
    }

    public static void guest() {
        MainMenuController.setCurrentUser(User.getGuestUser());
    }
}
