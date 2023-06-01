package com.alimajidi.aa.view.multiplayer;

import com.alimajidi.aa.view.App;
import com.alimajidi.aa.view.LoginMenu;
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
    private final int score;
    private final int loser;
    private final AnchorPane endGamePane = new AnchorPane();
    private Stage primaryStage;

    {
        endGamePane.setId("endGamePane");
    }

    public EndGameMenu(int loser, int score) {
        this.loser = loser;
        this.score = score;
    }

    @Override
    public void start(Stage primaryStage) {
        buttons();
        gameName();
        showDetails();
        initStage(primaryStage, endGamePane);
        primaryStage.show();
    }

    private void showDetails() {
        Label winnerLabel = new Label();
        winnerLabel.setId("winnerLabel");
        winnerLabel.textProperty().bind(App.createBiding(PLAYER).concat(loser == 1 ? 2 : 1).concat(App.createBiding(WINS)));

        Label scoreLabel = new Label();
        scoreLabel.setId("scoreLabel");
        scoreLabel.textProperty().bind(App.createBiding(SCORE).concat(": " + score));
        endGamePane.getChildren().addAll(winnerLabel, scoreLabel);
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

        Button exitButton = new Button();
        exitButton.setId("exitButton");
        exitButton.textProperty().bind(App.createBiding(EXIT));
        exitButton.setOnMouseClicked(event -> new LoginMenu().start(primaryStage));

        endGamePane.getChildren().addAll(exitButton, restartButton);
    }

}
