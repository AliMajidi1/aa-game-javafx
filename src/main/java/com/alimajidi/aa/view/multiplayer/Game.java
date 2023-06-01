package com.alimajidi.aa.view.multiplayer;

import com.alimajidi.aa.view.App;
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
    private final int eachUserBallCount = 15;
    private final int difficulty = 2;
    private final SimpleStringProperty scoreProperty1 = new SimpleStringProperty(String.format("%04d", 0));
    private final SimpleStringProperty scoreProperty2 = new SimpleStringProperty(String.format("%04d", 0));
    private final Label[] smallCirclesLabel1 = new Label[eachUserBallCount];
    private final Label[] smallCirclesLabel2 = new Label[eachUserBallCount];
    private final Circle[] smallCircles1 = new Circle[eachUserBallCount];
    private final Circle[] smallCircles2 = new Circle[eachUserBallCount];
    private final Label scoreLabel1 = new Label();
    private final Label scoreLabel2 = new Label();
    private final Circle mainCircle = new Circle(200, 400, 60);
    private final Circle auxiliaryCircle = new Circle(200, 400, 1000);
    private final SimpleIntegerProperty remainingBallsProperty1 = new SimpleIntegerProperty(eachUserBallCount);
    private final SimpleIntegerProperty remainingBallsProperty2 = new SimpleIntegerProperty(eachUserBallCount);
    private final Group mainGroup = new Group();
    private final Label remainingBallsLabel1 = new Label();
    private final Label remainingBallsLabel2 = new Label();
    private final Label degreeLabel = new Label("0");
    private final ProgressBar freezeProgressBar = new ProgressBar();
    private final RotateTransition mainGroupRotateTransition = new RotateTransition();
    private final TranslateTransition ballTransition = new TranslateTransition(Duration.millis(100));
    private final TranslateTransition reloadBallsTransition = new TranslateTransition(Duration.millis(30));
    private final Group allRemainingBallsGroup1 = new Group();
    private final Group allRemainingBallsGroup2 = new Group();
    private final SimpleIntegerProperty currentPhase = new SimpleIntegerProperty(1);
    private final SimpleDoubleProperty currentDegree = new SimpleDoubleProperty(0);
    private final MediaPlayer beepSoundMediaPlayer = new MediaPlayer(
            new Media(Objects.requireNonNull(Game.class.getResource("/media/beep.wav")).toExternalForm()));
    private final ImageView freezeImageView = new ImageView(Objects.requireNonNull(Game.class
            .getResource("/images/frozen.png")).toExternalForm());
    private Timeline changeDegreeTimeline;
    private Timeline changeBallsVisibilityTimeline;
    private Timeline changeRotationTimeline;
    private Timeline changeBallsRadiusTimeline;
    private Timeline freezeTimeline;
    private Stage primaryStage;
    private Scene scene;
    private Timeline freezeAnimationTimeline;
    private Timeline timerTimeline;

    {
        mainGroupRotateTransition.setInterpolator(Interpolator.LINEAR);
        ballTransition.setInterpolator(Interpolator.LINEAR);
        gamePane.setId("gamePane");
        gamePane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                pauseGame();
                new PauseMenu(this).start(primaryStage);
            }
            if (event.getCode().equals(KeyCode.TAB) && freezeProgressBar.getProgress() > 0.95) {
                freeze();
                freezeProgressBar.setProgress(0);
            }
            if (event.getCode().equals(KeyCode.SPACE)) shoot(allRemainingBallsGroup1);
            if (event.getCode().equals(KeyCode.ENTER)) shoot(allRemainingBallsGroup2);
        });

        freezeImageView.setOpacity(0);
        freezeImageView.setPreserveRatio(true);
        freezeImageView.setFitHeight(120);
        freezeImageView.setTranslateX(140);
        freezeImageView.setTranslateY(340);
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
            initStage(primaryStage, gamePane);
            gamePane.getChildren().add(freezeImageView);
            gamePane.requestFocus();
        }

        primaryStage.show();

        if (scoreLabel1.getTranslateX() == 0) setPositions();
    }

    private void freeze() {
        int freezeTime = 9 - difficulty * 2;

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

    private void shoot(Group allRemainingBallsGroup) {
        beepSoundMediaPlayer.setVolume(App.getMediaPlayer().getVolume());
        beepSoundMediaPlayer.seek(Duration.ZERO);
        beepSoundMediaPlayer.play();
        Circle targetCircle = (Circle) allRemainingBallsGroup.getChildren().get(0);
        Label targetLabel = (Label) allRemainingBallsGroup.getChildren().get(1);
        allRemainingBallsGroup.getChildren().removeAll(targetCircle, targetLabel);
        gamePane.getChildren().addAll(targetCircle, targetLabel);
        double[] translateXY = calculateTranslate(targetCircle, currentDegree.get()
                , allRemainingBallsGroup.equals(allRemainingBallsGroup2) ? 1 : -1);
        ballTransition.setNode(targetCircle);
        if (translateXY == null) {
            ballTransition.setByX(currentDegree.get() > 0
                    ? 400 - targetCircle.getCenterX() - targetCircle.getRadius()
                    : -targetCircle.getCenterX() + targetCircle.getRadius());
            int sign = allRemainingBallsGroup.equals(allRemainingBallsGroup2) ? 1 : -1;
            ballTransition.setByY(sign * (1 / Math.tan(currentDegree.get() * Math.PI / 180))
                    * ballTransition.getByX());
            ballTransition.setDuration(Duration.millis(Math.sqrt(Math.pow(ballTransition.getByX(), 2) +
                    Math.pow(ballTransition.getByY(), 2)) / 1.1));
            ballTransition.setCycleCount(1);
            ballTransition.setOnFinished(event -> endGameScreen(allRemainingBallsGroup.equals(allRemainingBallsGroup1) ? 1 : 2));
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
            rotate.setPivotY(400);

            Line line = new Line(200, 400, translateXY[0], translateXY[1]);
            line.getTransforms().add(rotate);
            line.setStyle(App.isDarkMode() ? "-fx-stroke: white" : "-fx-stroke: black");

            targetCircle.setTranslateX(0);
            targetCircle.setTranslateY(0);
            targetCircle.setCenterX(translateXY[0]);
            targetCircle.setCenterY(translateXY[1]);
            targetCircle.getTransforms().add(rotate);

            Rotate labelRotate = new Rotate(-mainGroup.getRotate());
            labelRotate.setPivotX(200 - targetLabel.getTranslateX());
            labelRotate.setPivotY(400 - targetLabel.getTranslateY());
            targetLabel.getTransforms().add(labelRotate);

            mainGroup.getChildren().addAll(line, targetCircle, targetLabel);
            mainGroup.getChildren().remove(mainCircle);
            mainGroup.getChildren().add(mainCircle);

            int loser = checkLose();
            if (loser != 0) {
                endGameScreen(loser);
                return;
            }

            if (allRemainingBallsGroup.equals(allRemainingBallsGroup1)) {
                remainingBallsProperty1.set(remainingBallsProperty1.get() - 1);
                if (remainingBallsProperty1.get() == 0) endGameScreen(2);
            } else {
                remainingBallsProperty2.set(remainingBallsProperty2.get() - 1);
                if (remainingBallsProperty2.get() == 0) endGameScreen(1);
            }

            int allRemaining = remainingBallsProperty1.get() + remainingBallsProperty2.get();

            if (allRemaining <= 2 * eachUserBallCount * 3 / 4 && (allRemaining > 2 * eachUserBallCount / 2 && currentPhase.get() != 2)) {
                currentPhase.set(2);
                startPhase2();
            }
            if (allRemaining <= 2 * eachUserBallCount / 2 && (allRemaining > 2 * eachUserBallCount / 4 && currentPhase.get() != 3)) {
                currentPhase.set(3);
                startPhase3();
            }
            if (allRemaining <= 2 * eachUserBallCount / 4 && currentPhase.get() != 4) {
                currentPhase.set(4);
                startPhase4();
            }
        });

        reloadBallsTransition.setNode(allRemainingBallsGroup);
        reloadBallsTransition.setByY(allRemainingBallsGroup.equals(allRemainingBallsGroup1) ? -47 : 47);
        reloadBallsTransition.setCycleCount(1);
        reloadBallsTransition.play();
        reloadBallsTransition.setOnFinished(event1 -> {
            allRemainingBallsGroup.setTranslateY(0);
            for (Node child : allRemainingBallsGroup.getChildren()) {
                if (child instanceof Circle circle)
                    circle.setCenterY(allRemainingBallsGroup.equals(allRemainingBallsGroup1)
                            ? circle.getCenterY() - 47 : circle.getCenterY() + 47);
            }
        });

    }

    private void endGameScreen(int loser) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {
            App.getMediaPlayer().stop();
            pauseGame();
            EndGameMenu endGameMenu = new EndGameMenu(loser, loser == 2
                    ? Integer.parseInt(scoreProperty1.get()) : Integer.parseInt(scoreProperty2.get()));
            endGameMenu.start(primaryStage);
        }));
        timeline.play();
    }

    private double[] calculateTranslate(Circle targetCircle, double degree, int sign) {
        double x, y, distance;
        degree = (90 - degree) * Math.PI / 180;
        for (double i = 0; i < 500; i += 0.001) {
            x = targetCircle.getCenterX() + targetCircle.getTranslateX() + i * Math.cos(degree);
            y = targetCircle.getCenterY() + targetCircle.getTranslateY() + sign * i * Math.sin(degree);
            distance = Math.sqrt(Math.pow(x - 200, 2) + Math.pow(y - 400, 2));
            if (Math.abs(distance - 155) < 0.001)
                return new double[]{x, y};
        }
        return null;
    }

    private int checkLose() {
        List<Circle> balls = mainGroup.getChildren().stream().filter(node -> node instanceof Circle)
                .filter(node -> ((Circle) node).getRadius() <= 15).map(node -> (Circle) node).toList();

        for (Circle ball1 : balls)
            for (Circle ball2 : balls)
                if (!ball1.equals(ball2))
                    if (Math.sqrt(Math.pow(ball1.getBoundsInParent().getCenterX() - ball2.getBoundsInParent().getCenterX(), 2)
                            + Math.pow(ball1.getBoundsInParent().getCenterY() - ball2.getBoundsInParent().getCenterY(), 2)) < ball1.getRadius() * 2) {
                        if (ball1.getStyle().equals(ball2.getStyle()))
                            return ball1.getStyle().endsWith("gary;") ? 2 : 1;

                        if (mainGroup.getChildren().indexOf(ball1) > mainGroup.getChildren().indexOf(ball2))
                            return ball1.getStyle().endsWith("gray;") ? 2 : 1;
                        else return ball2.getStyle().endsWith("gray;") ? 2 : 1;
                    }
        return 0;
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
            changeRadius(smallCircles1);
            changeRadius(smallCircles2);
        }), new KeyFrame(Duration.millis(1000)));


        changeBallsRadiusTimeline.setCycleCount(-1);
        changeBallsRadiusTimeline.play();
    }

    private void changeRadius(Circle[] circles) {
        for (Circle circle : circles) {
            if (circle.getRadius() == 12) circle.setRadius(15);
            else if (circle.getRadius() == 15) circle.setRadius(12);
            int loser = checkLose();
            if (loser != 0) {
                endGameScreen(loser);
                return;
            }
        }
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
                for (Node child : allRemainingBallsGroup1.getChildren())
                    if (child instanceof Circle smallCircle)
                        smallCircle.setCenterX(smallCircle.getCenterX() > 50 ? smallCircle.getCenterX() - 10 : smallCircle.getCenterX());
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                for (Node child : allRemainingBallsGroup1.getChildren())
                    if (child instanceof Circle smallCircle)
                        smallCircle.setCenterX(smallCircle.getCenterX() < 350 ? smallCircle.getCenterX() + 10 : smallCircle.getCenterX());
            }
            if (event.getCode().equals(KeyCode.A)) {
                for (Node child : allRemainingBallsGroup2.getChildren())
                    if (child instanceof Circle smallCircle)
                        smallCircle.setCenterX(smallCircle.getCenterX() > 50 ? smallCircle.getCenterX() - 10 : smallCircle.getCenterX());
            }
            if (event.getCode().equals(KeyCode.D)) {
                for (Node child : allRemainingBallsGroup2.getChildren())
                    if (child instanceof Circle smallCircle)
                        smallCircle.setCenterX(smallCircle.getCenterX() < 350 ? smallCircle.getCenterX() + 10 : smallCircle.getCenterX());
            }
        });

        changeDegreeTimeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            double randomDegree = new Random().nextDouble(3 * difficulty, 5 * difficulty);
            currentDegree.set(new Random().nextBoolean() ? randomDegree : -randomDegree);
        }), new KeyFrame(Duration.millis(1000)));
        changeDegreeTimeline.setCycleCount(-1);
        changeDegreeTimeline.play();
    }

    private void initPlayerSmallBalls(Group group, Circle[] circles, Label[] labels, int centerY, int sign) {
        for (int i = 0; i < circles.length; i++) {
            circles[i] = new Circle(200, centerY + (sign * 47 * i), 12);
            group.getChildren().add(circles[i]);
            labels[i] = new Label("" + (labels.length - i));
            group.getChildren().add(labels[i]);
            labels[i].setStyle((App.isDarkMode() ? "-fx-text-fill: black;" : "-fx-text-fill: white;")
                    + " -fx-font: 20px 'Lateef';");
        }
    }

    private void initSmallBalls() {
        initPlayerSmallBalls(allRemainingBallsGroup1, smallCircles1, smallCirclesLabel1, 681, 1);
        initPlayerSmallBalls(allRemainingBallsGroup2, smallCircles2, smallCirclesLabel2, 119, -1);

        gamePane.getChildren().addAll(allRemainingBallsGroup1, allRemainingBallsGroup2);
    }

    private void setPositions() {
        scoreLabel1.setTranslateX(200 - scoreLabel1.getWidth() / 2);
        scoreLabel1.setTranslateY(400 - scoreLabel1.getHeight() / 2 + 25);

        scoreLabel2.setTranslateX(200 - scoreLabel2.getWidth() / 2);
        scoreLabel2.setTranslateY(400 - scoreLabel2.getHeight() / 2 - 25);

        remainingBallsLabel1.setTranslateX(200 - remainingBallsLabel1.getWidth() / 2);
        remainingBallsLabel1.setTranslateY(800 - remainingBallsLabel1.getHeight());

        remainingBallsLabel2.setTranslateX(200 - remainingBallsLabel1.getWidth() / 2);

        freezeProgressBar.setTranslateX(-35);
        freezeProgressBar.setTranslateY(400 - freezeProgressBar.getHeight() / 2);


        setCircleLabelsPosition(smallCircles1, smallCirclesLabel1);
        setCircleLabelsPosition(smallCircles2, smallCirclesLabel2);

        Circle remainingBallsClip1 = new
                Circle(remainingBallsLabel1.getWidth() / 2, remainingBallsLabel2.getHeight() / 2, remainingBallsLabel1.getWidth() / 2);
        remainingBallsLabel1.setClip(remainingBallsClip1);

        Circle remainingBallsClip2 = new
                Circle(remainingBallsLabel2.getWidth() / 2, remainingBallsLabel2.getHeight() / 2, remainingBallsLabel2.getWidth() / 2);
        remainingBallsLabel2.setClip(remainingBallsClip2);
    }

    private void setCircleLabelsPosition(Circle[] circles, Label[] labels) {
        for (int i = 0; i < circles.length; i++) {
            if (circles[i].equals(smallCircles2[i])) circles[i].setStyle("-fx-fill: gray;");
            else circles[i].setStyle("-fx-fill: black;");
            labels[i].translateXProperty().bind(circles[i]
                    .centerXProperty().add(-labels[i].getWidth() / 2)
                    .add(circles[i].translateXProperty()));
            labels[i].translateYProperty().bind(circles[i]
                    .centerYProperty().add(-labels[i].getHeight() / 2)
                    .add(circles[i].translateYProperty()));
        }
    }

    private void status() {
        degreeLabel.setId("degreeLabel");
        degreeLabel.textProperty().bind(currentDegree.asString("%.2f"));

        scoreLabel1.setId("scoreLabel1");
        scoreLabel1.textProperty().bind(scoreProperty1);

        scoreLabel2.setId("scoreLabel2");
        scoreLabel2.textProperty().bind(scoreProperty2);


        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int newScore1 = (eachUserBallCount - remainingBallsProperty1.get()) * difficulty * 15;
            int newScore2 = (eachUserBallCount - remainingBallsProperty2.get()) * difficulty * 15;
            scoreProperty1.set(String.format("%04d", newScore1));
            scoreProperty2.set(String.format("%04d", newScore2));
        }));
        timerTimeline.setCycleCount(-1);
        timerTimeline.play();

        remainingBallsListener(remainingBallsProperty1, remainingBallsLabel1);
        remainingBallsListener(remainingBallsProperty2, remainingBallsLabel2);

        remainingBallsLabel1.setId("remainingBallsLabel1");
        remainingBallsLabel1.textProperty().bind(remainingBallsProperty1.asString().map(s -> String.format("%03d", Integer.parseInt(s))));

        remainingBallsLabel2.setId("remainingBallsLabel2");
        remainingBallsLabel2.textProperty().bind(remainingBallsProperty2.asString().map(s -> String.format("%03d", Integer.parseInt(s))));

        freezeProgressBar.setId("freezeProgressBar");
        freezeProgressBar.setRotate(90);
        freezeProgressBar.setProgress(0);

        gamePane.getChildren().addAll(degreeLabel, scoreLabel1, scoreLabel2, remainingBallsLabel1, remainingBallsLabel2, freezeProgressBar);
    }

    private void remainingBallsListener(SimpleIntegerProperty property, Label label) {
        property.addListener((observable, oldValue, newValue) -> {
            freezeProgressBar.setProgress(freezeProgressBar.getProgress() == 1 ? 1 : freezeProgressBar.getProgress() + 0.1);
            if (newValue.intValue() <= eachUserBallCount / 3) label.setStyle("-fx-background-color: #62ff62");
            if (newValue.intValue() > eachUserBallCount / 3 && newValue.intValue() <= (2 * eachUserBallCount) / 3)
                label.setStyle("-fx-background-color: #fff262");
        });
    }

    private void mainCircle() {
        mainCircle.setId("mainCircle");
        auxiliaryCircle.setId("auxiliaryCircle");
        mainGroup.getChildren().addAll(auxiliaryCircle, mainCircle);
        gamePane.getChildren().add(mainGroup);

        mainGroupRotateTransition.setByAngle(360);
        mainGroupRotateTransition.setDuration(Duration.millis(5400 - 1000 * difficulty));
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
        mainGroupRotateTransition.pause();
        if (changeDegreeTimeline != null) changeDegreeTimeline.pause();
        if (changeBallsVisibilityTimeline != null) changeBallsVisibilityTimeline.pause();
        if (changeRotationTimeline != null) changeRotationTimeline.pause();
        if (changeBallsRadiusTimeline != null) changeBallsRadiusTimeline.pause();
        if (freezeTimeline != null) freezeTimeline.pause();
        if (freezeAnimationTimeline != null) freezeAnimationTimeline.pause();
    }

    private void resumeGame() {
        mainGroupRotateTransition.play();
        if (changeDegreeTimeline != null) changeDegreeTimeline.play();
        if (changeBallsVisibilityTimeline != null) changeBallsVisibilityTimeline.play();
        if (changeRotationTimeline != null) changeRotationTimeline.play();
        if (changeBallsRadiusTimeline != null) changeBallsRadiusTimeline.play();
        if (freezeTimeline != null) freezeTimeline.play();
        if (freezeAnimationTimeline != null) freezeAnimationTimeline.play();
    }
}
