package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.LoginMenuController;
import com.alimajidi.aa.view.multiplayer.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class LoginMenu extends Application {
    private final AnchorPane loginMenuPane = new AnchorPane();
    private final Label responseMessageLabel = new Label();
    private Stage primaryStage;
    private TextField usernameField;
    private PasswordField passwordField;

    {
        loginMenuPane.setId("loginMenuPane");
        loginMenuPane.getChildren().add(responseMessageLabel);
        responseMessageLabel.setId("responseMessageLabel");
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(pane);
        App.setSceneStyle(LoginMenu.class, scene);
        primaryStage.getIcons().add(new
                Image(Objects.requireNonNull(LoginMenu.class.getResource("/images/icon.png")).toExternalForm()));
        primaryStage.setResizable(false);
        primaryStage.setTitle("   aa Game");
        primaryStage.setScene(scene);
    }


    @Override
    public void start(Stage primaryStage) {
        gameName();
        loginFields();
        buttons();
        initStage(primaryStage, loginMenuPane);
        primaryStage.show();
    }

    private void gameName() {
        Label gameNameLabel = new Label("aa");
        gameNameLabel.setId("gameNameLabel");
        Circle gameNameCircle = new Circle(200, 250, 150);
        gameNameCircle.setId("gameNameCircle");
        gameNameCircle.setFocusTraversable(true);
        loginMenuPane.getChildren().addAll(gameNameCircle, gameNameLabel);
    }

    private void loginFields() {
        usernameField = new TextField();
        usernameField.setId("usernameField");
        usernameField.promptTextProperty().bind(App.createBiding(USERNAME));
        passwordField = new PasswordField();
        passwordField.setId("passwordField");
        passwordField.promptTextProperty().bind(App.createBiding(PASSWORD));

        TextField passwordTextField = new TextField();
        passwordTextField.setId("passwordTextField");
        passwordTextField.promptTextProperty().bind(App.createBiding(PASSWORD));
        passwordTextField.setVisible(false);

        showHidePass(passwordField, passwordTextField);

        loginMenuPane.getChildren().addAll(usernameField, passwordField, passwordTextField);
    }

    private void showHidePass(PasswordField passwordField, TextField passwordTextField) {
        CheckBox showPassCheckBox = new CheckBox();
        showPassCheckBox.setId("showPassCheckBox");
        showPassCheckBox.setText("");
        showPassCheckBox.setOnMouseClicked(event -> {
            passwordField.setVisible(!passwordField.isVisible());
            passwordTextField.setVisible(!passwordTextField.isVisible());
        });
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());

        loginMenuPane.getChildren().add(showPassCheckBox);
    }

    private void buttons() {
        Button loginButton = new Button();
        loginButton.textProperty().bind(App.createBiding(LOGIN));
        loginButton.setId("loginButton");
        loginButton.setOnMouseClicked(event -> login());

        Button signUpButton = new Button();
        signUpButton.textProperty().bind(App.createBiding(SIGNUP));
        signUpButton.setId("signUpButton");
        signUpButton.setOnMouseClicked(event -> register());

        Button guestButton = new Button();
        guestButton.textProperty().bind(App.createBiding(GUEST));
        guestButton.setId("guestButton");
        guestButton.setOnMouseClicked(event -> guest());

        Button game1v1Button = new Button();
        game1v1Button.textProperty().bind(App.createBiding(GAME_1v1));
        game1v1Button.setId("game1v1Button");
        game1v1Button.setOnMouseClicked(event -> new Game().start(primaryStage));

        loginMenuPane.getChildren().addAll(loginButton, signUpButton, guestButton, game1v1Button);
    }


    private void guest() {
        LoginMenuController.guest();
        MainMenu mainMenu = new MainMenu();
        mainMenu.start(primaryStage);
    }

    private void login() {
        String username = usernameField.getText(), password = passwordField.getText();
        LoginMenuController.login(responseMessageLabel, username, password);

        if (responseMessageLabel.getText().equals(LOGIN_SUCCESS.getText())) {
            MainMenu mainMenu = new MainMenu();
            mainMenu.start(primaryStage);
        }
    }

    private void register() {
        String username = usernameField.getText(), password = passwordField.getText();
        LoginMenuController.register(responseMessageLabel, username, password);

        if (responseMessageLabel.getText().equals(REGISTER_SUCCESS.getText())) {
            LoginMenuController.setUserPreference();
            MainMenu mainMenu = new MainMenu();
            mainMenu.start(primaryStage);
        }
    }


}
