package com.alimajidi.aa.model;

import com.alimajidi.aa.view.App;
import com.alimajidi.aa.view.Game;
import javafx.scene.input.KeyCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class User implements Serializable {
    private final static User guestUser = new User("", "");
    private static ArrayList<User> users = new ArrayList<>();
    private final int[][] highScoreArray = new int[3][2];
    private boolean persian = false, darkMode = false;
    private String username, passwordHash;
    private String avatarPath;
    private int currentDifficulty = 2, currentMapNumber = 2, currentBallCount = 5;
    private double volume = 1;
    private KeyCode shootKey = KeyCode.SPACE, freezeKey = KeyCode.TAB;
    private transient Game savedGame;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        avatarPath = Objects.requireNonNull(User.class
                .getResource("/images/avatars/default/" + new Random().nextInt(1, 5) + ".png")).toExternalForm();
    }

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static void setUsers(ArrayList<User> users) {
        User.users = users;
    }

    public static User getGuestUser() {
        return guestUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isPersian() {
        return persian;
    }

    public void setPersian(boolean persian) {
        this.persian = persian;
        App.setPersian(persian);
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        App.setDarkMode(darkMode);
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void addHighScore(int score, int time) {
        if (highScoreArray[currentDifficulty - 1][0] == score) {
            if (highScoreArray[currentDifficulty - 1][1] > time)
                highScoreArray[currentDifficulty - 1][1] = time;
        } else if (highScoreArray[currentDifficulty - 1][0] < score) {
            highScoreArray[currentDifficulty - 1][0] = score;
            highScoreArray[currentDifficulty - 1][1] = time;
        }
    }

    public int getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(int currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public Integer getHighScoreByDifficulty(int difficulty) {
        return highScoreArray[difficulty - 1][0];
    }

    public Integer getHighScore() {
        return Math.max(highScoreArray[0][0], Math.max(highScoreArray[1][0], highScoreArray[2][0]));
    }

    public Integer getHighScoreTime() {
        for (int i = 0; i < 3; i++)
            if (getHighScore() == highScoreArray[i][0]) return highScoreArray[i][1];
        return 0;
    }

    public Integer getHighScoreDifficulty() {
        for (int i = 0; i < 3; i++)
            if (getHighScore() == highScoreArray[i][0]) return i + 1;
        return 0;
    }

    public Integer getBestTimeByDifficulty(int difficulty) {
        return highScoreArray[difficulty - 1][1];
    }

    public int getCurrentMapNumber() {
        return currentMapNumber;
    }

    public void setCurrentMapNumber(int currentMapNumber) {
        this.currentMapNumber = currentMapNumber;
    }

    public void setCurrentBallCount(int currentBallCount) {
        this.currentBallCount = currentBallCount;
    }

    public int getCurrentBallCount() {
        return currentBallCount;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public KeyCode getShootKey() {
        return shootKey;
    }

    public void setShootKey(KeyCode shootKey) {
        this.shootKey = shootKey;
    }

    public KeyCode getFreezeKey() {
        return freezeKey;
    }

    public void setFreezeKey(KeyCode freezeKey) {
        this.freezeKey = freezeKey;
    }

    public void setSavedGame(Game savedGame) {
        this.savedGame = savedGame;
    }

    public Game getSavedGame() {
        return savedGame;
    }
}
