package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.MainMenuController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class PauseMenu extends Application {
    private final AnchorPane pauseMenuPane = new AnchorPane();
    private Stage primaryStage;

    {
        pauseMenuPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE))
                MainMenuController.getCurrentUser().getSavedGame().start(primaryStage);
        });
        pauseMenuPane.setId("pauseMenuPane");
    }

    @Override
    public void start(Stage primaryStage) {
        gameName();
        buttons();
        gameGuide();
        initStage(primaryStage, pauseMenuPane);
        primaryStage.show();
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(pane);
        App.setSceneStyle(PauseMenu.class, scene);
        primaryStage.setScene(scene);
    }

    private void gameName() {
        Label gameNameLabel = new Label("aa");
        gameNameLabel.setId("gameNameLabel");
        Circle gameNameCircle = new Circle(200, 210, 150);
        gameNameCircle.setId("gameNameCircle");
        gameNameCircle.setFocusTraversable(true);
        pauseMenuPane.getChildren().addAll(gameNameCircle, gameNameLabel);
    }

    private void buttons() {
        Button saveButton = new Button();
        saveButton.setId("saveButton");
        saveButton.textProperty().bind(App.createBiding(SAVE));
        saveButton.setOnMouseClicked(event -> {
            App.getMediaPlayer().stop();
            new MainMenu().start(primaryStage);
        });

        Button restartButton = new Button();
        restartButton.setId("restartButton");
        restartButton.textProperty().bind(App.createBiding(RESTART));
        restartButton.setOnMouseClicked(event -> {
            App.getMediaPlayer().stop();
            MainMenuController.getCurrentUser().setSavedGame(null);
            new Game().start(primaryStage);
        });

        Button exitButton = new Button();
        exitButton.setId("exitButton");
        exitButton.textProperty().bind(App.createBiding(EXIT));
        exitButton.setOnMouseClicked(event -> {
            App.getMediaPlayer().stop();
            MainMenuController.getCurrentUser().setSavedGame(null);
            new MainMenu().start(primaryStage);
        });

        Button[] musicButtons = new Button[3];
        for (int i = 0; i < musicButtons.length; i++) {
            musicButtons[i] = new Button();
            musicButtons[i].setId("music" + (i + 1) + "Button");
            musicButtons[i].textProperty().bind(App.createBiding(valueOf("MUSIC" + (i + 1))));
            int finalI = i;
            musicButtons[i].setOnMouseClicked(event -> {
                App.getMediaPlayer().stop();
                App.playMusic(App.getMediaTracks()[finalI]);
            });
            pauseMenuPane.getChildren().add(musicButtons[i]);
        }
        Button muteButton = new Button();
        muteButton.setId("muteButton");
        muteButton.textProperty().bind(App.createBiding(MUTE));
        muteButton.setOnMouseClicked(event -> App.getMediaPlayer().setVolume(0));


        pauseMenuPane.getChildren().addAll(saveButton, restartButton, exitButton, muteButton);
    }

    private void gameGuide() {
        Label shootKeyLabel = new Label();
        shootKeyLabel.textProperty().bind(App.createBiding(SHOOT_KEY_GUIDE).concat(": " + MainMenuController.getCurrentUser().getShootKey()));
        shootKeyLabel.setId("shootKeyLabel");

        Label freezeKeyLabel = new Label();
        freezeKeyLabel.textProperty().bind(App.createBiding(FREEZE_KEY_GUIDE).concat(": " + MainMenuController.getCurrentUser().getFreezeKey()));
        freezeKeyLabel.setId("freezeKeyLabel");

        pauseMenuPane.getChildren().addAll(shootKeyLabel, freezeKeyLabel);
    }
}
