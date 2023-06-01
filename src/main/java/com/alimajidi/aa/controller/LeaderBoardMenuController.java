package com.alimajidi.aa.controller;

import com.alimajidi.aa.model.User;
import com.alimajidi.aa.view.App;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.util.concurrent.atomic.AtomicInteger;

import static com.alimajidi.aa.view.MultiLanguage.*;

public class LeaderBoardMenuController {
    private final TableView<User> tableView = new TableView<>();
    private final TableColumn<User, String> userColumn = new TableColumn<>();
    private final TableColumn<User, Integer> difficultyColumn = new TableColumn<>();
    private final TableColumn<User, Integer> scoreColumn = new TableColumn<>();
    private final TableColumn<User, Integer> timeColumn = new TableColumn<>();

    {
        tableView.setId("leaderBoardTableView");
        tableView.getColumns().add(userColumn);
        userColumn.textProperty().bind(App.createBiding(USERNAME));
        userColumn.setId("userColumn");
        userColumn.setResizable(false);

        tableView.getColumns().add(difficultyColumn);
        difficultyColumn.textProperty().bind(App.createBiding(DIFFICULTY));
        difficultyColumn.setId("difficultyColumn");
        difficultyColumn.setResizable(false);

        tableView.getColumns().add(scoreColumn);
        scoreColumn.textProperty().bind(App.createBiding(SCORE));
        scoreColumn.setId("scoreColumn");
        scoreColumn.setResizable(false);

        tableView.getColumns().add(timeColumn);
        timeColumn.textProperty().bind(App.createBiding(TIME));
        timeColumn.setId("timeColumn");
        timeColumn.setResizable(false);

        tableView.setSortPolicy(param -> false);
    }

    private void setRowsId() {
        tableView.refresh();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        tableView.setRowFactory(param -> {
            TableRow<User> row = new TableRow<>();
            row.setId("tableViewRow" + atomicInteger.getAndIncrement());
            return row;
        });
    }

    public void showTableView(int difficulty) {
        tableView.getItems().clear();
        setRowsId();
        userColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getUsername()));

        difficultyColumn.setCellValueFactory(
                param -> new SimpleIntegerProperty(difficulty).asObject());

        scoreColumn.setCellValueFactory(
                param -> new SimpleIntegerProperty(param.getValue().getHighScoreByDifficulty(difficulty)).asObject());

        timeColumn.setCellValueFactory(
                param -> new SimpleIntegerProperty(param.getValue().getBestTimeByDifficulty(difficulty)).asObject());


        tableView.getItems().addAll(User.getUsers().stream().sorted((o1, o2) -> {
            if (o1.getHighScoreByDifficulty(difficulty) < o2.getHighScoreByDifficulty(difficulty)) return 1;
            else if (o1.getHighScoreByDifficulty(difficulty).equals(o2.getHighScoreByDifficulty(difficulty)))
                return Integer.compare(o1.getBestTimeByDifficulty(difficulty), o2.getBestTimeByDifficulty(difficulty));
            return -1;
        }).limit(10).toList());
    }

    public void showTableView() {
        tableView.getItems().clear();
        setRowsId();
        userColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getUsername()));

        difficultyColumn.setCellValueFactory(
                param -> new SimpleIntegerProperty(param.getValue().getHighScoreDifficulty()).asObject());

        scoreColumn.setCellValueFactory(
                param -> new SimpleIntegerProperty(param.getValue().getHighScore()).asObject());

        timeColumn.setCellValueFactory(
                param -> new SimpleIntegerProperty(param.getValue().getHighScoreTime()).asObject());


        tableView.getItems().addAll(User.getUsers().stream().sorted((o1, o2) -> {
            if (o1.getHighScore() < o2.getHighScore()) return 1;
            else if (o1.getHighScore().equals(o2.getHighScore()))
                return Integer.compare(o1.getHighScoreTime(), o2.getHighScoreTime());
            return -1;
        }).limit(10).toList());
    }

    public TableView<User> getTableView() {
        return tableView;
    }
}
