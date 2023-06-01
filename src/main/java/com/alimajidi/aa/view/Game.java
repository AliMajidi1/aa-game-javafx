package com.alimajidi.aa.view;

import com.alimajidi.aa.controller.LoginMenuController;
import com.alimajidi.aa.controller.MainMenuController;
import com.alimajidi.aa.model.User;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.Random;


public class Game extends Application {
    private final AnchorPane gamePane = new AnchorPane();
    private final SimpleStringProperty scoreProperty = new SimpleStringProperty(String.format("%04d", 0));
    private final User currentUser = MainMenuController.getCurrentUser();
    private final Label[] smallCirclesLabel = new Label[currentUser.getCurrentBallCount()];
    private final Circle[] smallCircles = new Circle[currentUser.getCurrentBallCount() + currentUser.getCurrentMapNumber() + 4];
    private final Label scoreLabel = new Label();
    private final Label timerLabel = new Label();
    private final Circle mainCircle = new Circle(200, 270, 60);
    private final Circle auxiliaryCircle = new Circle(200, 270, 1000);
    private final SimpleIntegerProperty remainingBallsProperty = new SimpleIntegerProperty(currentUser.getCurrentBallCount());
    private final Group mainGroup = new Group();
    private final Label remainingBallsLabel = new Label();
    private final Label degreeLabel = new Label("0");
    private final ProgressBar freezeProgressBar = new ProgressBar();
    private final RotateTransition mainGroupRotateTransition = new RotateTransition();
    private final TranslateTransition ballTransition = new TranslateTransition(Duration.millis(100));
    private final TranslateTransition reloadBallsTransition = new TranslateTransition(Duration.millis(40));
    private final Group allRemainingBallsGroup = new Group();
    private final SimpleIntegerProperty currentPhase = new SimpleIntegerProperty(1);
    private final SimpleDoubleProperty currentDegree = new SimpleDoubleProperty(0);
    private final int maxTime;
    private final MediaPlayer beepSoundMediaPlayer = new MediaPlayer(
            new Media(Objects.requireNonNull(Game.class.getResource("/media/beep.wav")).toExternalForm()));
    private final ImageView freezeImageView = new ImageView(Objects.requireNonNull(Game.class
            .getResource("/images/frozen.png")).toExternalForm());
    private int time;
    private Timeline timerTimeline;
    private Timeline changeDegreeTimeline;
    private Timeline changeBallsVisibilityTimeline;
    private Timeline changeRotationTimeline;
    private Timeline changeBallsRadiusTimeline;
    private Timeline freezeTimeline;
    private Stage primaryStage;
    private Scene scene;
    private Timeline freezeAnimationTimeline;

    {
        mainGroupRotateTransition.setInterpolator(Interpolator.LINEAR);
        ballTransition.setInterpolator(Interpolator.LINEAR);
        maxTime = time = remainingBallsProperty.get() * 5
                + currentUser.getCurrentDifficulty() * 30 + currentUser.getCurrentMapNumber() * 20;
        gamePane.setId("gamePane");
        gamePane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                pauseGame();
                new PauseMenu().start(primaryStage);
            }
        });

        freezeImageView.setOpacity(0);
        freezeImageView.setPreserveRatio(true);
        freezeImageView.setFitHeight(120);
        freezeImageView.setTranslateX(140);
        freezeImageView.setTranslateY(210);
    }

    @Override
    public void start(Stage primaryStage) {
        if (mainCircle.getId() != null) {
            resumeStage();
            resumeGame();
        } else {
            App.playMusic(App.getMediaTracks()[0]);
            mainCircle();
            initSmallBalls();
            status();
            createMap();
            initStage(primaryStage, gamePane);

            gamePane.getChildren().add(freezeImageView);
        }

        primaryStage.show();

        if (scoreLabel.getTranslateX() == 0) setPositions();
    }

    private void freeze() {
        int freezeTime = 9 - currentUser.getCurrentDifficulty() * 2;

        freezeImageView.setOpacity(1);
        freezeAnimationTimeline = new Timeline(new KeyFrame(Duration.millis(freezeTime),
                event -> freezeImageView.setOpacity(freezeImageView.getOpacity() - 0.001)));

        freezeAnimationTimeline.setCycleCount(1000);
        freezeAnimationTimeline.play();
        freezeAnimationTimeline.setOnFinished(event -> freezeImageView.setOpacity(0));

        mainGroupRotateTransition.stop();
        Duration duration = mainGroupRotateTransition.getDuration();
        mainGroupRotateTransition.setDuration(duration.add(Duration.millis(10000)));
        mainGroupRotateTransition.play();

        freezeTimeline = new Timeline(new KeyFrame(Duration.seconds(freezeTime), event -> {
            mainGroupRotateTransition.stop();
            mainGroupRotateTransition.setDuration(duration);
            mainGroupRotateTransition.play();
        }));

        freezeTimeline.play();
    }

    private void shoot() {
        beepSoundMediaPlayer.setVolume(App.getMediaPlayer().getVolume());
        beepSoundMediaPlayer.seek(Duration.ZERO);
        beepSoundMediaPlayer.play();
        Circle targetCircle = (Circle) allRemainingBallsGroup.getChildren().get(0);
        Label targetLabel = (Label) allRemainingBallsGroup.getChildren().get(1);
        allRemainingBallsGroup.getChildren().removeAll(targetCircle, targetLabel);
        gamePane.getChildren().addAll(targetCircle, targetLabel);
        double[] translateXY = calculateTranslate(targetCircle, currentDegree.get());
        ballTransition.setNode(targetCircle);
        if (translateXY == null) {
            ballTransition.setByX(currentDegree.get() > 0
                    ? 400 - targetCircle.getCenterX() - targetCircle.getRadius()
                    : -targetCircle.getCenterX() + targetCircle.getRadius());
            ballTransition.setByY(-(1 / Math.tan(currentDegree.get() * Math.PI / 180))
                    * ballTransition.getByX());
            ballTransition.setDuration(Duration.millis(Math.sqrt(Math.pow(ballTransition.getByX(), 2) +
                    Math.pow(ballTransition.getByY(), 2)) / 1.1));
            ballTransition.setCycleCount(1);
            ballTransition.setOnFinished(event -> endGameScreen());
            ballTransition.play();
            return;
        }
        ballTransition.setDuration(Duration.millis(Math.sqrt(Math.pow(translateXY[0] - targetCircle.getCenterX(), 2)
                + Math.pow(translateXY[1] - targetCircle.getCenterY(), 2)) / 1.1));
        ballTransition.setByX(translateXY[0] - targetCircle.getCenterX());
        ballTransition.setByY(translateXY[1] - targetCircle.getCenterY());
        ballTransition.setCycleCount(1);
        ballTransition.play();
        Rotate rotate = new Rotate();
        ballTransition.setOnFinished(event2 -> {
            gamePane.getChildren().removeAll(targetCircle, targetLabel);
            targetLabel.translateXProperty().unbind();
            targetLabel.translateYProperty().unbind();

            rotate.setAngle(-mainGroup.getRotate());
            rotate.setPivotX(200);
            rotate.setPivotY(270);

            Line line = new Line(200, 270, translateXY[0], translateXY[1]);
            line.getTransforms().add(rotate);
            line.setStyle(currentUser.isDarkMode() ? "-fx-stroke: white" : "-fx-stroke: black");

            targetCircle.setTranslateX(0);
            targetCircle.setTranslateY(0);
            targetCircle.setCenterX(translateXY[0]);
            targetCircle.setCenterY(translateXY[1]);
            targetCircle.getTransforms().add(rotate);

            Rotate labelRotate = new Rotate(-mainGroup.getRotate());
            labelRotate.setPivotX(200 - targetLabel.getTranslateX());
            labelRotate.setPivotY(270 - targetLabel.getTranslateY());
            targetLabel.getTransforms().add(labelRotate);

            mainGroup.getChildren().addAll(line, targetCircle, targetLabel);
            mainGroup.getChildren().remove(mainCircle);
            mainGroup.getChildren().add(mainCircle);

            if (checkLose()) {
                endGameScreen();
                return;
            }

            remainingBallsProperty.set(remainingBallsProperty.get() - 1);
            if (remainingBallsProperty.get() == 0) endGameScreen();
            remainingBallsProperty.addListener((observable, oldValue, newValue) -> {
                if (((int) newValue) <= currentUser.getCurrentBallCount() * 3 / 4 && ((int) newValue) > currentUser.getCurrentBallCount() / 2 && currentPhase.get() != 2) {
                    currentPhase.set(2);
                    startPhase2();
                }
                if (((int) newValue) <= currentUser.getCurrentBallCount() / 2 && ((int) newValue) > currentUser.getCurrentBallCount() / 4 && currentPhase.get() != 3) {
                    currentPhase.set(3);
                    startPhase3();
                }
                if (((int) newValue) <= currentUser.getCurrentBallCount() / 4 && currentPhase.get() != 4) {
                    currentPhase.set(4);
                    startPhase4();
                }
            });

        });

        reloadBallsTransition.setNode(allRemainingBallsGroup);
        reloadBallsTransition.setByY(-47);
        reloadBallsTransition.setCycleCount(1);
        reloadBallsTransition.play();
    }

    private void endGameScreen() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {
            App.getMediaPlayer().stop();
            pauseGame();
            EndGameMenu endGameMenu = new EndGameMenu(remainingBallsProperty.get() == 0,
                    Integer.parseInt(scoreProperty.get()), maxTime - time);
            endGameMenu.start(primaryStage);
        }));
        timeline.play();
    }

    private double[] calculateTranslate(Circle targetCircle, double degree) {
        double x, y, distance;
        degree = (90 - degree) * Math.PI / 180;
        for (double i = 0; i < 500; i += 0.001) {
            x = targetCircle.getCenterX() + targetCircle.getTranslateX() + i * Math.cos(degree);
            y = targetCircle.getCenterY() + targetCircle.getTranslateY() - i * Math.sin(degree);
            distance = Math.sqrt(Math.pow(x - 200, 2) + Math.pow(y - 270, 2));
            if (Math.abs(distance - 155) < 0.001)
                return new double[]{x, y};
        }
        return null;
    }

    private boolean checkLose() {
        List<Circle> balls = mainGroup.getChildren().stream().filter(node -> node instanceof Circle)
                .filter(node -> ((Circle) node).getRadius() <= 15).map(node -> (Circle) node).toList();

        for (Circle ball1 : balls)
            for (Circle ball2 : balls)
                if (!ball1.equals(ball2))
                    if (Math.sqrt(Math.pow(ball1.getBoundsInParent().getCenterX() - ball2.getBoundsInParent().getCenterX(), 2)
                            + Math.pow(ball1.getBoundsInParent().getCenterY() - ball2.getBoundsInParent().getCenterY(), 2)) < ball1.getRadius() * 2)
                        return true;
        return false;
    }

    private void startPhase2() {
        changeRotationTimeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            mainGroupRotateTransition.stop();
            mainGroupRotateTransition.setByAngle(-1 * mainGroupRotateTransition.getByAngle());
            mainGroupRotateTransition.play();
        }), new KeyFrame(Duration.millis(new Random().nextInt(3000, 6000))));

        changeRotationTimeline.play();
        changeRotationTimeline.setOnFinished(event -> {
            changeRotationTimeline.getKeyFrames().remove(1);
            changeRotationTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(new Random().nextInt(3000, 6000))));
            changeRotationTimeline.play();
        });

        changeBallsRadiusTimeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            for (Circle circle : smallCircles) {
                if (circle.getRadius() == 12) circle.setRadius(15);
                else if (circle.getRadius() == 15) circle.setRadius(12);
                if (checkLose())
                    endGameScreen();
            }
        }), new KeyFrame(Duration.millis(1000)));

        changeBallsRadiusTimeline.setCycleCount(-1);
        changeBallsRadiusTimeline.play();
    }

    private void startPhase3() {
        final boolean[] isHidden = {true};
        changeBallsVisibilityTimeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            for (Node child : mainGroup.getChildren())
                if (!child.equals(mainCircle) && !child.equals(auxiliaryCircle))
                    child.setVisible(!isHidden[0]);
            isHidden[0] = !isHidden[0];
        }), new KeyFrame(Duration.millis(1500)));

        changeBallsVisibilityTimeline.setCycleCount(-1);
        changeBallsVisibilityTimeline.play();
    }

    private void startPhase4() {
        gamePane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                for (Node child : allRemainingBallsGroup.getChildren())
                    if (child instanceof Circle smallCircle)
                        smallCircle.setCenterX(smallCircle.getCenterX() > 50 ? smallCircle.getCenterX() - 10 : smallCircle.getCenterX());
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                for (Node child : allRemainingBallsGroup.getChildren())
                    if (child instanceof Circle smallCircle)
                        smallCircle.setCenterX(smallCircle.getCenterX() < 350 ? smallCircle.getCenterX() + 10 : smallCircle.getCenterX());
            }
        });
        changeDegreeTimeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            double randomDegree = new Random().nextDouble(3 * currentUser.getCurrentDifficulty(), 5 * currentUser.getCurrentDifficulty());
            currentDegree.set(new Random().nextBoolean() ? randomDegree : -randomDegree);
        }), new KeyFrame(Duration.millis(1000)));
        changeDegreeTimeline.setCycleCount(-1);
        changeDegreeTimeline.play();
    }


    private void createMap() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(
                mainGroupRotateTransition.getDuration().toMillis() / (currentUser.getCurrentMapNumber() + 4)), event -> {
            Circle targetCircle = (Circle) allRemainingBallsGroup.getChildren().get(0);
            allRemainingBallsGroup.getChildren().remove(targetCircle);
            gamePane.getChildren().add(targetCircle);
            ballTransition.setNode(targetCircle);
            ballTransition.setByY(425 - 540);
            ballTransition.setCycleCount(1);
            ballTransition.play();
            Rotate rotate = new Rotate();
            ballTransition.setOnFinished(event2 -> {
                gamePane.getChildren().remove(targetCircle);
                rotate.setAngle(-mainGroup.getRotate());
                rotate.setPivotX(200);
                rotate.setPivotY(270);

                targetCircle.setTranslateY(0);
                targetCircle.setCenterY(425);

                Line line = new Line(200, 270, 200, 425);
                line.getTransforms().add(rotate);
                line.setStyle(currentUser.isDarkMode() ? "-fx-stroke: white" : "-fx-stroke: black");

                targetCircle.getTransforms().add(rotate);

                mainGroup.getChildren().addAll(line, targetCircle);
                mainGroup.getChildren().remove(mainCircle);
                mainGroup.getChildren().add(mainCircle);
            });

            reloadBallsTransition.setNode(allRemainingBallsGroup);
            reloadBallsTransition.setByY(-47);
            reloadBallsTransition.setCycleCount(1);
            reloadBallsTransition.play();
            reloadBallsTransition.setOnFinished(event1 -> {
                allRemainingBallsGroup.setTranslateY(0);
                for (Node child : allRemainingBallsGroup.getChildren()) {
                    if (child instanceof Circle circle) circle.setCenterY(circle.getCenterY() - 47);
                }
            });

        }));
        timeline.setCycleCount(currentUser.getCurrentMapNumber() + 4);
        timeline.play();

        timeline.setOnFinished(event -> {
            timerTimeline.play();
            gamePane.requestFocus();
            gamePane.addEventFilter(KeyEvent.KEY_PRESSED, event1 -> {
                if (event1.getCode().equals(currentUser.getFreezeKey()) && freezeProgressBar.getProgress() > 0.95) {
                    freeze();
                    freezeProgressBar.setProgress(0);
                }
                if (event1.getCode().equals(currentUser.getShootKey())) shoot();
            });
        });

    }

    private void initSmallBalls() {
        for (int i = 0; i < smallCircles.length; i++) {
            smallCircles[i] = new Circle(200, 540 + 47 * i, 12);
            allRemainingBallsGroup.getChildren().add(smallCircles[i]);
            if (i - currentUser.getCurrentMapNumber() - 4 >= 0) {
                smallCirclesLabel[i - currentUser.getCurrentMapNumber() - 4] =
                        new Label("" + (smallCirclesLabel.length - (i - currentUser.getCurrentMapNumber() - 4)));
                allRemainingBallsGroup.getChildren().add(smallCirclesLabel[i - currentUser.getCurrentMapNumber() - 4]);
                smallCirclesLabel[i - currentUser.getCurrentMapNumber() - 4]
                        .setStyle((currentUser.isDarkMode() ? "-fx-text-fill: black;" : "-fx-text-fill: white;")
                                + " -fx-font: 20px 'Lateef';");
            }
        }
        gamePane.getChildren().add(allRemainingBallsGroup);

    }

    private void setPositions() {
        scoreLabel.setTranslateX(200 - scoreLabel.getWidth() / 2);
        scoreLabel.setTranslateY(270 - scoreLabel.getHeight() / 2);

        remainingBallsLabel.setTranslateX(200 - remainingBallsLabel.getWidth() / 2);

        freezeProgressBar.setTranslateX(380 - freezeProgressBar.getWidth());
        freezeProgressBar.setTranslateY(remainingBallsLabel.getHeight() / 2 - freezeProgressBar.getHeight() / 2);

        for (int i = 0; i < smallCircles.length; i++)
            if (i - currentUser.getCurrentMapNumber() - 4 >= 0) {
                smallCirclesLabel[i - currentUser.getCurrentMapNumber() - 4].translateXProperty().bind(smallCircles[i]
                        .centerXProperty().add(-smallCirclesLabel[i - currentUser.getCurrentMapNumber() - 4].getWidth() / 2)
                        .add(smallCircles[i].translateXProperty()));

                smallCirclesLabel[i - currentUser.getCurrentMapNumber() - 4].translateYProperty().bind(smallCircles[i]
                        .centerYProperty().add(-smallCirclesLabel[i - currentUser.getCurrentMapNumber() - 4].getHeight() / 2)
                        .add(smallCircles[i].translateYProperty()));

            }

        Circle remainingBallsClip = new
                Circle(remainingBallsLabel.getWidth() / 2, remainingBallsLabel.getHeight() / 2, remainingBallsLabel.getWidth() / 2);
        remainingBallsLabel.setClip(remainingBallsClip);
    }

    private void status() {
        degreeLabel.setId("degreeLabel");
        degreeLabel.textProperty().bind(currentDegree.asString("%.2f"));

        SimpleStringProperty timerStringProperty =
                new SimpleStringProperty(String.format("%02d:%02d", time / 60, time % 60));

        scoreLabel.setId("scoreLabel");
        scoreLabel.textProperty().bind(scoreProperty);

        timerLabel.setId("timerLabel");
        timerLabel.textProperty().bind(timerStringProperty);

        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int newScore = ((currentUser.getCurrentBallCount() - remainingBallsProperty.get())
                    * currentUser.getCurrentMapNumber() * currentUser.getCurrentDifficulty() - (maxTime - time)) * 10;
            if (newScore < 0) newScore = 0;
            scoreProperty.set(String.format("%04d", newScore));
            time--;
            timerStringProperty.set(String.format("%02d:%02d", time / 60, time % 60));
            if (time == 0)
                endGameScreen();
        }));
        timerTimeline.setCycleCount(time);

        remainingBallsProperty.addListener((observable, oldValue, newValue) -> {
            freezeProgressBar.setProgress(freezeProgressBar.getProgress() == 1 ? 1 : freezeProgressBar.getProgress() + 0.2);
            int balls = currentUser.getCurrentBallCount();
            if (newValue.intValue() <= balls / 3) remainingBallsLabel.setStyle("-fx-background-color: #62ff62");
            if (newValue.intValue() > balls / 3 && newValue.intValue() <= (2 * balls) / 3)
                remainingBallsLabel.setStyle("-fx-background-color: #fff262");
        });

        remainingBallsLabel.setId("remainingBallsLabel");
        remainingBallsLabel.textProperty().bind(remainingBallsProperty.asString().map(s -> String.format("%03d", Integer.parseInt(s))));

        freezeProgressBar.setId("freezeProgressBar");
        freezeProgressBar.setProgress(0);

        gamePane.getChildren().addAll(degreeLabel, scoreLabel, timerLabel, remainingBallsLabel, freezeProgressBar);
    }

    private void mainCircle() {
        mainCircle.setId("mainCircle");
        auxiliaryCircle.setId("auxiliaryCircle");
        mainGroup.getChildren().addAll(auxiliaryCircle, mainCircle);
        gamePane.getChildren().add(mainGroup);

        mainGroupRotateTransition.setByAngle(360);
        mainGroupRotateTransition.setDuration(Duration.millis(5400 - 1000 * currentUser.getCurrentDifficulty()));
        mainGroupRotateTransition.setNode(mainGroup);
        mainGroupRotateTransition.setDelay(Duration.ZERO);
        mainGroupRotateTransition.setCycleCount(-1);
        mainGroupRotateTransition.play();
    }

    private void initStage(Stage primaryStage, Pane pane) {
        this.primaryStage = primaryStage;
        scene = new Scene(pane);
        App.setSceneStyle(Game.class, scene);
        primaryStage.setScene(scene);
    }

    private void resumeStage() {
        App.setSceneStyle(Game.class, scene);
        primaryStage.setScene(scene);
    }

    private void pauseGame() {
        currentUser.setSavedGame(this);
        LoginMenuController.updateDatabase();
        mainGroupRotateTransition.pause();
        timerTimeline.pause();
        if (changeDegreeTimeline != null) changeDegreeTimeline.pause();
        if (changeBallsVisibilityTimeline != null) changeBallsVisibilityTimeline.pause();
        if (changeRotationTimeline != null) changeRotationTimeline.pause();
        if (changeBallsRadiusTimeline != null) changeBallsRadiusTimeline.pause();
        if (freezeTimeline != null) freezeTimeline.pause();
        if (freezeAnimationTimeline != null) freezeAnimationTimeline.pause();
    }

    private void resumeGame() {
        mainGroupRotateTransition.play();
        timerTimeline.play();
        if (changeDegreeTimeline != null) changeDegreeTimeline.play();
        if (changeBallsVisibilityTimeline != null) changeBallsVisibilityTimeline.play();
        if (changeRotationTimeline != null) changeRotationTimeline.play();
        if (changeBallsRadiusTimeline != null) changeBallsRadiusTimeline.play();
        if (freezeTimeline != null) freezeTimeline.play();
        if (freezeAnimationTimeline != null) freezeAnimationTimeline.play();
    }
}
