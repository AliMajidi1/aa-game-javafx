package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.LoginMenuController;
import com.alimajidi.aa.controller.MainMenuController;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class SettingMenu extends Application {
    private final AnchorPane settingMenuPane = new AnchorPane();
    private Stage primaryStage;

    {
        settingMenuPane.setId("settingMenuPane");
    }

    @Override
    public void start(Stage primaryStage) {
        chooseVolume();
        backButton();
        chooseBallCount();
        chooseMap();
        chooseDifficulty();
        chooseTheme();
        chooseLanguage();
        chooseCustomButtons();
        initStage(primaryStage, settingMenuPane);
        primaryStage.show();
    }

    private void chooseDifficulty() {
        ToggleButton[] difficultyToggleButton = new ToggleButton[3];
        Label selectedDifficultyLabel = new Label();
        selectedDifficultyLabel.setId("selectedDifficultyLabel");
        selectedDifficultyLabel.textProperty().bind(App.createBiding(valueOf("DIFFICULTY" + MainMenuController.getCurrentUser().getCurrentDifficulty())));

        for (int i = 0; i < difficultyToggleButton.length; i++) {
            difficultyToggleButton[i] = new ToggleButton();
            difficultyToggleButton[i].setId("difficulty" + (i + 1) + "ToggleButton");
            difficultyToggleButton[i].textProperty().bind(App.createBiding(valueOf("DIFFICULTY" + (i + 1))));
            difficultyToggleButton[i].setSelected(MainMenuController.getCurrentUser().getCurrentDifficulty() == i + 1);
            int finalI = i;
            difficultyToggleButton[i].selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    MainMenuController.getCurrentUser().setCurrentDifficulty(finalI + 1);
                    difficultyToggleButton[Math.abs(finalI - 1)].setSelected(false);
                    difficultyToggleButton[Math.abs(finalI - 2)].setSelected(false);
                    selectedDifficultyLabel.textProperty().unbind();
                    selectedDifficultyLabel.textProperty().bind(App.createBiding(valueOf("DIFFICULTY" + MainMenuController.getCurrentUser().getCurrentDifficulty())));
                }
            });
            settingMenuPane.getChildren().add(difficultyToggleButton[i]);
        }

        settingMenuPane.getChildren().add(selectedDifficultyLabel);
    }

    private void chooseCustomButtons() {
        ToggleButton shootKeyToggleButton = new ToggleButton();
        shootKeyToggleButton.setId("shootKeyToggleButton");
        shootKeyToggleButton.textProperty().bind(App.createBiding(SHOOT_KEY));
        shootKeyToggleButton.setSelected(false);

        ToggleButton freezeKeyToggleButton = new ToggleButton();
        freezeKeyToggleButton.setId("freezeKeyToggleButton");
        freezeKeyToggleButton.textProperty().bind(App.createBiding(FREEZE_KEY));
        freezeKeyToggleButton.setSelected(false);

        Label shootKeyLabel = new Label(MainMenuController.getCurrentUser().getShootKey().toString());
        shootKeyLabel.setId("shootKeyLabel");

        Label freezeKeyLabel = new Label(MainMenuController.getCurrentUser().getFreezeKey().toString());
        freezeKeyLabel.setId("freezeKeyLabel");

        shootKeyToggleButton.setOnKeyPressed(event -> {
            if (shootKeyToggleButton.isSelected()) {
                freezeKeyToggleButton.setSelected(false);
                MainMenuController.getCurrentUser().setShootKey(event.getCode());
                shootKeyLabel.setText(event.getCode().toString());
                shootKeyToggleButton.setSelected(false);
            }
        });

        freezeKeyToggleButton.setOnKeyPressed(event -> {
            if (freezeKeyToggleButton.isSelected()) {
                shootKeyToggleButton.setSelected(false);
                MainMenuController.getCurrentUser().setFreezeKey(event.getCode());
                freezeKeyLabel.setText(event.getCode().toString());
                freezeKeyToggleButton.setSelected(false);
            }
        });

        settingMenuPane.getChildren().addAll(shootKeyToggleButton, freezeKeyToggleButton, shootKeyLabel, freezeKeyLabel);
    }

    private void chooseLanguage() {
        ToggleButton englishToggleButton = new ToggleButton();
        englishToggleButton.setId("englishToggleButton");
        englishToggleButton.textProperty().bind(App.createBiding(ENGLISH));
        englishToggleButton.setSelected(!MainMenuController.getCurrentUser().isPersian());

        ToggleButton persianToggleButton = new ToggleButton();
        persianToggleButton.setId("persianToggleButton");
        persianToggleButton.textProperty().bind(App.createBiding(PERSIAN));
        persianToggleButton.setSelected(MainMenuController.getCurrentUser().isPersian());

        englishToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                MainMenuController.getCurrentUser().setPersian(false);
                persianToggleButton.setSelected(false);
            }
        });

        persianToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                MainMenuController.getCurrentUser().setPersian(true);
                englishToggleButton.setSelected(false);
            }
        });

        settingMenuPane.getChildren().addAll(englishToggleButton, persianToggleButton);
    }

    private void chooseTheme() {
        ToggleButton lightModeToggleButton = new ToggleButton();
        lightModeToggleButton.setId("lightModeToggleButton");
        lightModeToggleButton.textProperty().bind(App.createBiding(LIGHT_MODE));
        lightModeToggleButton.setSelected(!MainMenuController.getCurrentUser().isDarkMode());

        ToggleButton darkModeToggleButton = new ToggleButton();
        darkModeToggleButton.setId("darkModeToggleButton");
        darkModeToggleButton.textProperty().bind(App.createBiding(DARK_MODE));
        darkModeToggleButton.setSelected(MainMenuController.getCurrentUser().isDarkMode());

        lightModeToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                MainMenuController.getCurrentUser().setDarkMode(false);
                darkModeToggleButton.setSelected(false);
                App.setSceneStyle(SettingMenu.class, primaryStage.getScene());
            }
        });

        darkModeToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                MainMenuController.getCurrentUser().setDarkMode(true);
                lightModeToggleButton.setSelected(false);
                App.setSceneStyle(SettingMenu.class, primaryStage.getScene());
            }
        });

        settingMenuPane.getChildren().addAll(lightModeToggleButton, darkModeToggleButton);
    }

    private void chooseVolume() {
        Slider volumeSlider = new Slider(0, 100, MainMenuController.getCurrentUser().getVolume() * 100);
        volumeSlider.setId("volumeSlider");
        volumeSlider.setOrientation(Orientation.VERTICAL);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MainMenuController.getCurrentUser().setVolume((Double) newValue / 100);
            App.getMediaPlayer().setVolume((Double) newValue / 100);
        });
        settingMenuPane.getChildren().addAll(volumeSlider);
    }

    private void chooseMap() {
        ToggleButton[] mapToggleButton = new ToggleButton[3];
        Label selectedMapLabel = new Label();
        selectedMapLabel.setId("selectedMapLabel");
        selectedMapLabel.textProperty().bind(App.createBiding(valueOf("MAP" + MainMenuController.getCurrentUser().getCurrentMapNumber())));

        for (int i = 0; i < mapToggleButton.length; i++) {
            mapToggleButton[i] = new ToggleButton();
            mapToggleButton[i].setId("map" + (i + 1) + "ToggleButton");
            mapToggleButton[i].textProperty().bind(App.createBiding(valueOf("MAP" + (i + 1))));
            mapToggleButton[i].setSelected(MainMenuController.getCurrentUser().getCurrentMapNumber() == i + 1);
            int finalI = i;
            mapToggleButton[i].selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    MainMenuController.getCurrentUser().setCurrentMapNumber(finalI + 1);
                    mapToggleButton[Math.abs(finalI - 1)].setSelected(false);
                    mapToggleButton[Math.abs(finalI - 2)].setSelected(false);
                    selectedMapLabel.textProperty().unbind();
                    selectedMapLabel.textProperty().bind(App.createBiding(valueOf("MAP" + MainMenuController.getCurrentUser().getCurrentMapNumber())));
                }
            });
            settingMenuPane.getChildren().add(mapToggleButton[i]);
        }

        settingMenuPane.getChildren().add(selectedMapLabel);

    }

    private void backButton() {
        Button backButton = new Button();
        backButton.setId("backButton");
        backButton.textProperty().bind(App.createBiding(BACK));
        backButton.setOnMouseClicked(event -> {
            LoginMenuController.updateDatabase();
            new MainMenu().start(primaryStage);
        });
        settingMenuPane.getChildren().add(backButton);
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(pane);
        App.setSceneStyle(SettingMenu.class, scene);
        primaryStage.setScene(scene);
    }

    private void chooseBallCount() {
        Slider ballCountSlider = new Slider(5, 30, MainMenuController.getCurrentUser().getCurrentBallCount());
        ballCountSlider.setId("ballCountSlider");
        ballCountSlider.setBlockIncrement(1);

        Label ballCountTitleLabel = new Label();
        ballCountTitleLabel.setId("ballCountTitleLabel");
        ballCountTitleLabel.textProperty().bind(App.createBiding(BALLS_COUNT));

        Label ballCountLabel = new Label();
        ballCountLabel.setId("ballCountLabel");
        ballCountLabel.textProperty().bind(ballCountSlider.valueProperty().asString("%.0f"));

        ballCountLabel.textProperty().addListener((observable, oldValue, newValue) ->
                MainMenuController.getCurrentUser().setCurrentBallCount(Integer.parseInt(newValue)));

        settingMenuPane.getChildren().addAll(ballCountSlider, ballCountTitleLabel, ballCountLabel);
    }


}