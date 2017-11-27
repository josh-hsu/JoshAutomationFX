package com.mumu.jafx;


import com.mumu.libjoshgame.Log;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;

public class JobViewController {
    private static final String TAG = "ViewController";
    @FXML private Label mStatusLabel;
    @FXML private Tab mTab1;
    @FXML private Tab mTab2;
    @FXML private Tab mTab3;
    @FXML private GridPane mGridPane1;
    @FXML private GridPane mGridPane2;
    @FXML private GridPane mGridPane3;

    private ArrayList<Tab> mTabSet;
    private ArrayList<GridPane> mGridPaneSet;
    private ArrayList<AutoJobPane> mAutoJobPanes;


    private Stage dialogStage;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        mTabSet = new ArrayList<>();
        mTabSet.add(mTab1);
        mTabSet.add(mTab2);
        mTabSet.add(mTab3);

        mGridPaneSet = new ArrayList<>();
        mGridPaneSet.add(mGridPane1);
        mGridPaneSet.add(mGridPane2);
        mGridPaneSet.add(mGridPane3);

        mAutoJobPanes = new ArrayList<>();
        mAutoJobPanes.add(formatAutoJobPane(mGridPane1));
        mAutoJobPanes.add(formatAutoJobPane(mGridPane2));
        mAutoJobPanes.add(formatAutoJobPane(mGridPane3));

        mAutoJobPanes.get(0).enableCheckBoxes.get(0).setSelected(true);
    }

    private AutoJobPane formatAutoJobPane(GridPane gridPane) {
        AutoJobPane jobPane = new AutoJobPane();
        int rowCount = jobPane.rowCount;
        int columnCount = jobPane.columnCount;

        if (columnCount != 4) {
            Log.d(TAG, "column count mismatched.");
        }

        //format enable box
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for(int i = 1; i < rowCount; i++) {
            Log.d(TAG, "i = " + i);
            checkBoxes.add((CheckBox) getNodeByRowColumnIndex(i, 0, gridPane));
        }

        //format when choice box
        ArrayList<ChoiceBox> choiceBoxes = new ArrayList<>();
        for(int i = 1; i < rowCount; i++) {
            choiceBoxes.add((ChoiceBox) getNodeByRowColumnIndex(i, 1, gridPane));
        }

        ArrayList<TextField> textFields = new ArrayList<>();
        for(int i = 1; i < rowCount; i++) {
            textFields.add((TextField) getNodeByRowColumnIndex(i, 2, gridPane));
        }

        ArrayList<ChoiceBox> actionChoiceBoxes = new ArrayList<>();
        for(int i = 1; i < rowCount; i++) {
            actionChoiceBoxes.add((ChoiceBox) getNodeByRowColumnIndex(i, 3, gridPane));
        }

        jobPane.enableCheckBoxes = checkBoxes;
        jobPane.whenChoiceBoxes = choiceBoxes;
        jobPane.whenValueTextFields = textFields;
        jobPane.actionChoiceBoxes = actionChoiceBoxes;

        return jobPane;
    }

    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        ObservableList<Node> children = gridPane.getChildren();

        Log.d(TAG, "children count " + children.size());

        for (Node node : children) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer columnIndex = GridPane.getColumnIndex(node);
            if (rowIndex != null && rowIndex == row) {
                if (columnIndex != null && columnIndex == column) {
                    return node;
                }
            }
        }

        Log.d(TAG, "cannot find node at (" + row + ", " + column + ")");
        return null;
    }

    public void updateTabName(ArrayList<String> devices) {
        for(int i = 0; i < devices.size(); i++) {
            mTabSet.get(i).setText(devices.get(i));
        }
    }

    public void updateStatus(String msg) {
        Platform.runLater(() -> mStatusLabel.setText("" + msg));
    }

    private class AutoJobPane {
        int columnCount = 4;
        int rowCount = 6;

        ArrayList<CheckBox> enableCheckBoxes;
        ArrayList<ChoiceBox> whenChoiceBoxes;
        ArrayList<TextField> whenValueTextFields;
        ArrayList<ChoiceBox> actionChoiceBoxes;
    }
}
