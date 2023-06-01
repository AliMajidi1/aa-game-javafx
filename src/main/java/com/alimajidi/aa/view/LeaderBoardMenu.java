package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.LeaderBoardMenuController;
import com.alimajidi.aa.controller.LoginMenuController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static com.alimajidi.aa.view.MultiLanguage.ALL_DIFFICULTY;
import static com.alimajidi.aa.view.MultiLanguage.BACK;

public class LeaderBoardMenu extends Application {
    private final AnchorPane leaderBoardMenuPane = new AnchorPane();
    private Stage primaryStage;
    private LeaderBoardMenuController leaderBoardMenuController;

    {
        leaderBoardMenuPane.setId("leaderBoardMenuPane");
    }

    @Override
    public void start(Stage primaryStage) {
        leaderBoardMenuController = new LeaderBoardMenuController();
        buttons();
        initStage(primaryStage, leaderBoardMenuPane);
        primaryStage.show();
    }

    private void buttons() {
        Button allDifficultyButton = new Button();
        allDifficultyButton.setId("allDifficultyButton");
        allDifficultyButton.textProperty().bind(App.createBiding(ALL_DIFFICULTY));
        allDifficultyButton.setOnMouseClicked(event -> leaderBoardMenuController.showTableView());
        allDifficultyButton.setFocusTraversable(true);

        Button[] difficultyButtons = new Button[3];

        for (int i = 0; i < difficultyButtons.length; i++) {
            difficultyButtons[i] = new Button();
            difficultyButtons[i].setId("difficulty" + (i + 1) + "Button");
            difficultyButtons[i].textProperty().bind(App.createBiding(MultiLanguage.valueOf("BY_DIFFICULTY" + (i + 1))));
            int finalI = i;
            difficultyButtons[i].setOnMouseClicked(event -> leaderBoardMenuController.showTableView(finalI + 1));
            leaderBoardMenuPane.getChildren().add(difficultyButtons[i]);
        }

        Button backButton = new Button();
        backButton.setId("backButton");
        backButton.textProperty().bind(App.createBiding(BACK));
        backButton.setOnMouseClicked(event -> {
            LoginMenuController.updateDatabase();
            new MainMenu().start(primaryStage);
        });

        leaderBoardMenuPane.getChildren().addAll(leaderBoardMenuController.getTableView(), allDifficultyButton, backButton);
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(pane);
        App.setSceneStyle(LeaderBoardMenu.class, scene);
        primaryStage.setScene(scene);
    }

}
