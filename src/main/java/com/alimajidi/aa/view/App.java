package com.alimajidi.aa.view;


import com.alimajidi.aa.controller.LoginMenuController;
import com.alimajidi.aa.model.User;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class App extends Application {
    private static final ObjectProperty<String> language = new SimpleObjectProperty<>("en");
    private static final Media[] mediaTracks = new Media[]{
            new Media(Objects.requireNonNull(App.class.getResource("/media/track1.mp3")).toExternalForm()),
            new Media(Objects.requireNonNull(App.class.getResource("/media/track2.mp3")).toExternalForm()),
            new Media(Objects.requireNonNull(App.class.getResource("/media/track3.mp3")).toExternalForm())};
    private static MediaPlayer mediaPlayer = new MediaPlayer(mediaTracks[0]);
    private static boolean persian = false, darkMode = false;

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean darkMode) {
        App.darkMode = darkMode;
    }

    public static boolean isPersian() {
        return persian;
    }

    public static void setPersian(boolean persian) {
        App.persian = persian;
        language.set(persian ? "fa" : "en");
        language.get();
    }

    public static void setSceneStyle(Class<?> menuClass, Scene scene) {
        String menuCustomName = menuClass.getName().replace("com.alimajidi.aa.view.", "");
        menuCustomName = menuCustomName.replace(".", "/");
        scene.getStylesheets().clear();
        scene.getStylesheets()
                .add(Objects.requireNonNull(menuClass
                        .getResource("/css/" + menuCustomName + "/General.css")).toExternalForm());
        if (darkMode)
            scene.getStylesheets()
                    .add(Objects.requireNonNull(menuClass
                            .getResource("/css/" + menuCustomName + "/DarkMode.css")).toExternalForm());
        else
            scene.getStylesheets()
                    .add(Objects.requireNonNull(menuClass
                            .getResource("/css/" + menuCustomName + "/LightMode.css")).toExternalForm());
    }

    public static StringBinding createBiding(MultiLanguage multiLanguage) {
        return Bindings.createStringBinding(multiLanguage::getText, language);
    }

    private static void loadFonts() {
        File file;
        try {
            file = new File(Objects.requireNonNull(App.class.getResource("/fonts")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        for (File fontFile : Objects.requireNonNull(file.listFiles()))
            Font.loadFont("file:/" + fontFile.getPath(), 10);
    }

    public static void playMusic(Media media) {
        double volume = mediaPlayer.getVolume();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.seconds(1));
            mediaPlayer.play();
        });
        mediaPlayer.play();
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static Media[] getMediaTracks() {
        return mediaTracks;
    }

    @Override
    public void start(Stage primaryStage) {
        loadFonts();
//        fakeDatabase();
        LoginMenuController.fetchDatabase();

        new LoginMenu().start(primaryStage);
    }

    public void fakeDatabase() {
        User.getUsers().add(new User("ali1", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(0).setCurrentDifficulty(1);
        User.getUsers().get(0).addHighScore(100, 20);
        User.getUsers().add(new User("ali2", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(1).setCurrentDifficulty(1);
        User.getUsers().get(1).addHighScore(100, 30);
        User.getUsers().add(new User("ali3", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(2).setCurrentDifficulty(1);
        User.getUsers().get(2).addHighScore(200, 30);
        User.getUsers().add(new User("ali4", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(3).setCurrentDifficulty(1);
        User.getUsers().get(3).addHighScore(150, 10);
        User.getUsers().add(new User("ali5", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(4).setCurrentDifficulty(1);
        User.getUsers().get(4).addHighScore(10, 5);
        User.getUsers().add(new User("ali6", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(5).setCurrentDifficulty(1);
        User.getUsers().get(5).addHighScore(150, 10);
        User.getUsers().add(new User("ali7", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(6).setCurrentDifficulty(1);
        User.getUsers().get(6).addHighScore(400, 100);
        User.getUsers().add(new User("ali8", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(7).setCurrentDifficulty(1);
        User.getUsers().get(7).addHighScore(50, 100);
        User.getUsers().get(7).setCurrentDifficulty(3);
        User.getUsers().get(7).addHighScore(5000, 100);
        User.getUsers().add(new User("ali9", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(8).setCurrentDifficulty(1);
        User.getUsers().get(8).addHighScore(0, 100);
        User.getUsers().add(new User("ali10", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(9).setCurrentDifficulty(1);
        User.getUsers().get(9).addHighScore(500, 200);
        User.getUsers().add(new User("ali11", LoginMenuController.generatePasswordHash("ali")));
        User.getUsers().get(10).setCurrentDifficulty(1);
        User.getUsers().get(10).addHighScore(600, 900);
        LoginMenuController.updateDatabase();
    }
}
