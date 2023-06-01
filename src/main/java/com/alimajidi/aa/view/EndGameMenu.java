package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.LoginMenuController;
import com.alimajidi.aa.controller.MainMenuController;
import com.alimajidi.aa.model.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class EndGameMenu extends Application {
    private final int score, time;
    private final boolean isWinner;
    private final AnchorPane endGamePane = new AnchorPane();
    private Stage primaryStage;

    {
        endGamePane.setId("endGamePane");
    }

    public EndGameMenu(boolean isWinner, int score, int time) {
        this.isWinner = isWinner;
        this.score = score;
        this.time = time;
    }

    @Override
    public void start(Stage primaryStage) {
        endGamePane.setStyle(isWinner ? "-fx-background-color: #7eff7e" : "-fx-background-color: #ff0000");
        setHighScore();
        buttons();
        gameName();
        showDetails();
        initStage(primaryStage, endGamePane);
        primaryStage.show();
    }

    private void showDetails() {
        Label scoreLabel = new Label();
        scoreLabel.setId("scoreLabel");
        scoreLabel.textProperty().bind(App.createBiding(SCORE).concat(": " + score));

        Label timeLabel = new Label();
        timeLabel.setId("timeLabel");
        timeLabel.textProperty().bind(App.createBiding(TIME).concat(": " + time));

        endGamePane.getChildren().addAll(timeLabel, scoreLabel);
    }

    private void setHighScore() {
        User currentUser = MainMenuController.getCurrentUser();
        currentUser.addHighScore(score, time);
        LoginMenuController.updateDatabase();
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(pane);
        App.setSceneStyle(EndGameMenu.class, scene);
        primaryStage.setScene(scene);
    }

    private void gameName() {
        Label gameNameLabel = new Label("aa");
        gameNameLabel.setId("gameNameLabel");
        Circle gameNameCircle = new Circle(200, 250, 150);
        gameNameCircle.setId("gameNameCircle");
        gameNameCircle.setFocusTraversable(true);
        endGamePane.getChildren().addAll(gameNameCircle, gameNameLabel);
    }

    private void buttons() {
        Button restartButton = new Button();
        restartButton.setId("restartButton");
        restartButton.textProperty().bind(App.createBiding(RESTART));
        restartButton.setOnMouseClicked(event -> {
            System.gc();
            new Game().start(primaryStage);
        });

        Button leaderBoardButton = new Button();
        leaderBoardButton.setId("leaderBoardButton");
        leaderBoardButton.textProperty().bind(App.createBiding(LEADER_BOARD));
        leaderBoardButton.setOnMouseClicked(event -> new LeaderBoardMenu().start(primaryStage));

        Button exitButton = new Button();
        exitButton.setId("exitButton");
        exitButton.textProperty().bind(App.createBiding(EXIT));
        exitButton.setOnMouseClicked(event -> new MainMenu().start(primaryStage));

        endGamePane.getChildren().addAll(exitButton, restartButton, leaderBoardButton);
    }

}
