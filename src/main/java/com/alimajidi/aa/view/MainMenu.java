package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.LoginMenuController;
import com.alimajidi.aa.controller.MainMenuController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class MainMenu extends Application {
    private final AnchorPane mainMenuPane = new AnchorPane();
    private Stage primaryStage;

    {
        mainMenuPane.setId("mainMenuPane");
    }

    @Override
    public void start(Stage primaryStage) {
        App.getMediaPlayer().stop();
        System.gc();
        gameName();
        buttons();
        initStage(primaryStage, mainMenuPane);
        primaryStage.show();
    }

    private void buttons() {
        Button exitButton = new Button();
        exitButton.setId("exitButton");
        exitButton.textProperty().bind(App.createBiding(EXIT));
        exitButton.setOnMouseClicked(event -> {
            LoginMenuController.updateDatabase();
            System.exit(130);
        });

        Button settingButton = new Button();
        settingButton.setId("settingButton");
        settingButton.textProperty().bind(App.createBiding(SETTING));
        settingButton.setOnMouseClicked(event -> new SettingMenu().start(primaryStage));

        Button leaderBoardButton = new Button();
        leaderBoardButton.setId("leaderBoardButton");
        leaderBoardButton.textProperty().bind(App.createBiding(LEADER_BOARD));
        leaderBoardButton.setOnMouseClicked(event -> new LeaderBoardMenu().start(primaryStage));

        Button profileButton = new Button();
        profileButton.setId("profileButton");
        profileButton.textProperty().bind(App.createBiding(PROFILE));
        profileButton.setOnMouseClicked(event -> new ProfileMenu().start(primaryStage));

        Button continueButton = new Button();
        continueButton.setId("continueButton");
        continueButton.textProperty().bind(App.createBiding(CONTINUE));
        continueButton.setOnMouseClicked(event -> {
            if (MainMenuController.getCurrentUser().getSavedGame() == null) return;
            MainMenuController.getCurrentUser().getSavedGame().start(primaryStage);
        });

        Button newGameButton = new Button();
        newGameButton.setId("newGameButton");
        newGameButton.textProperty().bind(App.createBiding(NEW_GAME));
        newGameButton.setOnMouseClicked(event -> {
            MainMenuController.getCurrentUser().setSavedGame(null);
            new Game().start(primaryStage);
        });

        mainMenuPane.getChildren().addAll(exitButton, settingButton, leaderBoardButton, profileButton, continueButton, newGameButton);
    }

    private void gameName() {
        Label gameNameLabel = new Label("aa");
        gameNameLabel.setId("gameNameLabel");
        Circle gameNameCircle = new Circle(200, 250, 150);
        gameNameCircle.setId("gameNameCircle");
        gameNameCircle.setFocusTraversable(true);
        mainMenuPane.getChildren().addAll(gameNameCircle, gameNameLabel);
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(pane);
        App.setSceneStyle(MainMenu.class, scene);
        primaryStage.setScene(scene);
    }

}
