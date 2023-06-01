package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.LoginMenuController;
import com.alimajidi.aa.controller.MainMenuController;
import com.alimajidi.aa.controller.ProfileMenuController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.Objects;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class ProfileMenu extends Application {

    private final AnchorPane profileMenuPane = new AnchorPane();
    private Stage primaryStage;
    private TextField usernameField;
    private PasswordField passwordField;
    private ImageView mainAvatarImageView;

    {
        profileMenuPane.setId("profileMenuPane");
    }


    @Override
    public void start(Stage primaryStage) {
        avatar();
        buttons();
        initStage(primaryStage, profileMenuPane);
        primaryStage.show();
    }

    private void avatar() {
        Circle avatarCircle = new Circle(200, 250, 150);
        avatarCircle.setId("avatarCircle");
        profileMenuPane.getChildren().add(avatarCircle);

        mainAvatar();
        defaultAvatar(1, 60, 220);
        defaultAvatar(2, 280, 220);
        defaultAvatar(3, 170, 110);
        defaultAvatar(4, 170, 330);
    }

    private void mainAvatar() {
        mainAvatarImageView = new ImageView(new Image(MainMenuController.getCurrentUser().getAvatarPath()));
        Circle mainAvatarClip = new Circle(70, 70, 70);
        mainAvatarImageView.setPreserveRatio(true);
        mainAvatarImageView.setFitWidth(140);
        mainAvatarImageView.setTranslateX(130);
        mainAvatarImageView.setTranslateY(180);
        mainAvatarImageView.setClip(mainAvatarClip);
        mainAvatarImageView.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                String mimetype = new MimetypesFileTypeMap().getContentType(file);
                String type = mimetype.split("/")[0];
                if (type.equals("image")) {
                    MainMenuController.getCurrentUser().setAvatarPath(file.toURI().toString());
                    mainAvatarImageView.setImage(new Image(MainMenuController.getCurrentUser().getAvatarPath()));
                }
            }
        });
        profileMenuPane.getChildren().add(mainAvatarImageView);
    }

    private void defaultAvatar(int imageNumber, int translateX, int translateY) {
        Image image = new Image(Objects.requireNonNull(ProfileMenu.class
                .getResource("/images/avatars/default/" + imageNumber + ".png")).toExternalForm());
        Circle clip = new Circle(30, 30, 30);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(60);
        imageView.setTranslateX(translateX);
        imageView.setTranslateY(translateY);
        imageView.setClip(clip);
        imageView.setOnMouseClicked(event -> {
            mainAvatarImageView.setImage(image);
            MainMenuController.getCurrentUser().setAvatarPath(image.getUrl());
        });
        profileMenuPane.getChildren().add(imageView);
    }

    private void buttons() {
        Button logoutButton = new Button();
        logoutButton.setId("logoutButton");
        logoutButton.textProperty().bind(App.createBiding(LOGOUT));
        logoutButton.setOnMouseClicked(event -> {
            ProfileMenuController.logout();
            new LoginMenu().start(primaryStage);
        });

        Button deleteAccButton = new Button();
        deleteAccButton.setId("deleteAccButton");
        deleteAccButton.textProperty().bind(App.createBiding(DELETE_ACC));
        deleteAccButton.setOnMouseClicked(event -> {
            ProfileMenuController.deleteAcc();
            new LoginMenu().start(primaryStage);
        });

        Button backButton = new Button();
        backButton.setId("backButton");
        backButton.textProperty().bind(App.createBiding(BACK));
        backButton.setOnMouseClicked(event -> {
            LoginMenuController.updateDatabase();
            new MainMenu().start(primaryStage);
        });

        AnchorPane changeUserPassPane = new AnchorPane();
        changeUserPassPane.setId("changeUserPassPane");

        usernameField = new TextField();
        usernameField.setId("usernameField");
        usernameField.promptTextProperty().bind(App.createBiding(NEW_USERNAME));
        passwordField = new PasswordField();
        passwordField.setId("passwordField");
        passwordField.promptTextProperty().bind(App.createBiding(NEW_PASSWORD));
        changeUserPassPane.setVisible(false);

        Label changeUserPassResponseMessageLabel = new Label();
        changeUserPassResponseMessageLabel.setId("changeUserPassResponseMessageLabel");

        Button applyChangeUserPassButton = new Button();
        applyChangeUserPassButton.setId("applyChangeUserPassButton");
        applyChangeUserPassButton.textProperty().bind(App.createBiding(APPLY));
        applyChangeUserPassButton.setOnMouseClicked(event -> {
            ProfileMenuController
                    .changeUserPass(usernameField.getText(), passwordField.getText(), changeUserPassResponseMessageLabel);
            if (changeUserPassResponseMessageLabel.getText().equals(CHANGE_USER_PASS_SUCCESS.getText()))
                changeUserPassPane.setVisible(false);
        });

        changeUserPassPane.getChildren()
                .addAll(usernameField, passwordField, applyChangeUserPassButton, changeUserPassResponseMessageLabel);

        Button changeUserPassButton = new Button();
        changeUserPassButton.setId("changeUserPassButton");
        changeUserPassButton.textProperty().bind(App.createBiding(CHANGE_USER_PASS));
        changeUserPassButton.setOnMouseClicked(event -> changeUserPassPane.setVisible(!changeUserPassPane.isVisible()));


        profileMenuPane.getChildren().addAll(logoutButton, deleteAccButton, backButton, changeUserPassButton, changeUserPassPane);
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(pane);
        App.setSceneStyle(ProfileMenu.class, scene);
        primaryStage.setScene(scene);
    }
}
