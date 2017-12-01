package com.mumu.jafx;


import com.mumu.libjoshgame.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class JobViewController {
    private static final String TAG = "ViewController";
    private Main mMainApp;
    private int mMaxSupportJob = 3;

    @FXML private Label mStatusLabel;
    @FXML private Tab mTab1;
    @FXML private Tab mTab2;
    @FXML private Tab mTab3;
    @FXML private GridPane mGridPane1;
    @FXML private GridPane mGridPane2;
    @FXML private GridPane mGridPane3;
    @FXML private ImageView mDeviceImage1;
    @FXML private ImageView mDeviceImage2;
    @FXML private ImageView mDeviceImage3;

    private ArrayList<Tab> mTabSet;
    private ArrayList<GridPane> mGridPaneSet;
    private ArrayList<AutoJobPane> mAutoJobPanes;
    private ArrayList<ImageView> mScreenViewSet;

    private JobViewListener mListener;

    private EventHandler<ActionEvent> mNodeEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Node node = (Node) event.getSource();

            //if user check the checkbox, we send a message to Main handler
            AutoJobPaneNodeInfo info = getNodeLocationInfo(node);

            if (node instanceof CheckBox) {
                sendJobRequest(info.pane, info.row);
            } else {
                CheckBox checkBox = getCheckboxInIndex(info.pane, info.row);
                checkBox.setSelected(false);
            }

            Log.d(TAG, "Node info => " + getNodeLocationInfo(node).toString());

        }
    };


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

        mScreenViewSet = new ArrayList<>();
        mScreenViewSet.add(mDeviceImage1);
        mScreenViewSet.add(mDeviceImage2);
        mScreenViewSet.add(mDeviceImage3);

    }

    public void registerListener(JobViewListener listener) {
        if (listener != null)
            mListener = listener;
        else
            Log.d(TAG, "WTF, null listener?");
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
            CheckBox checkBox = (CheckBox) getNodeByRowColumnIndex(i, 0, gridPane);
            if (checkBox != null) {
                checkBox.setOnAction(mNodeEventHandler);
                checkBoxes.add(checkBox);
            }
        }

        //format when choice box
        ArrayList<ChoiceBox> choiceBoxes = new ArrayList<>();
        for(int i = 1; i < rowCount; i++) {
            ChoiceBox choiceBox = (ChoiceBox) getNodeByRowColumnIndex(i, 1, gridPane);
            if (choiceBox != null) {
                choiceBox.setOnAction(mNodeEventHandler);
                choiceBoxes.add(choiceBox);
            }
        }

        ArrayList<TextField> textFields = new ArrayList<>();
        for(int i = 1; i < rowCount; i++) {
            TextField textField = (TextField) getNodeByRowColumnIndex(i, 2, gridPane);
            if (textField != null) {
                textField.setOnAction(mNodeEventHandler);
                textField.setPromptText("%數(0~100)，或秒數(1是1秒)");
                textFields.add(textField);
            }
        }

        ArrayList<ChoiceBox> actionChoiceBoxes = new ArrayList<>();
        for(int i = 1; i < rowCount; i++) {
            ChoiceBox choiceBox = (ChoiceBox) getNodeByRowColumnIndex(i, 3, gridPane);
            if (choiceBox != null) {
                choiceBox.setOnAction(mNodeEventHandler);
                actionChoiceBoxes.add(choiceBox);
            }
        }

        jobPane.enableCheckBoxes = checkBoxes;
        jobPane.whenChoiceBoxes = choiceBoxes;
        jobPane.whenValueTextFields = textFields;
        jobPane.actionChoiceBoxes = actionChoiceBoxes;

        return jobPane;
    }

    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        ObservableList<Node> children = gridPane.getChildren();

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

    private CheckBox getCheckboxInIndex(final int tab, final int row) {
        GridPane gridPane = mGridPaneSet.get(tab);
        Node node = getNodeByRowColumnIndex(row, 0, gridPane);
        if (node != null) {
            if (node instanceof CheckBox) {
                return (CheckBox) node;
            }
        }

        return null;
    }

    private AutoJobPaneNodeInfo getNodeLocationInfo(Node node) {
        int tabIndex = -1;
        Node parent = node.getParent();

        if (parent instanceof GridPane) {
            for(int i = 0; i < mGridPaneSet.size(); i++) {
                if (parent == mGridPaneSet.get(i))
                    tabIndex = i;
            }
        }

        if (tabIndex == -1) {
            Log.w(TAG, "Cannot find this node in all panes");
            return null;
        }

        Integer row = GridPane.getRowIndex(node);
        Integer column = GridPane.getColumnIndex(node);

        if (row != null && column != null) {
            return new AutoJobPaneNodeInfo(tabIndex, row, column);
        } else {
            Log.w(TAG, "this node might not have current row or column index");
            return null;
        }
    }

    public void setMainApp(Main app) {
        mMainApp = app;
    }

    public void updateTabName(ArrayList<String> devices) {
        Platform.runLater(() -> {
                for(int i = 0; i < mMaxSupportJob; i++) {
                    if (i < devices.size())
                        mTabSet.get(i).setText(devices.get(i));
                    else
                        mTabSet.get(i).setText("無裝置");
                }
            }
        );
    }

    public void updateStatus(String msg) {
        Platform.runLater(() -> mStatusLabel.setText("" + msg));
    }

    public void updateScreenshot(int index, String path) {
        Platform.runLater(() -> {
                if (index >= mScreenViewSet.size()) {
                    Log.w(TAG, "Invalid index to update screenshot");
                } else {
                    ImageView imgView = mScreenViewSet.get(index);
                    imgView.setImage(new Image(path));
                }
            }
        );
    }

    private void sendJobRequest(int tabIndex, int row) {
        AutoJobPane jobPane = mAutoJobPanes.get(tabIndex);
        int enable = jobPane.enableCheckBoxes.get(row - 1).selectedProperty().getValue() ? 1 : 0;
        int whenIndex = jobPane.whenChoiceBoxes.get(row - 1).getSelectionModel().getSelectedIndex();
        String whenString = jobPane.whenValueTextFields.get(row - 1).getText();
        int actionIndex = jobPane.actionChoiceBoxes.get(row - 1).getSelectionModel().getSelectedIndex();
        int whenValue = -1;

        if (whenString.equals("")) {
            alertFieldValueInvalid(row, "您沒有設定條件數值。");
            jobPane.enableCheckBoxes.get(row - 1).setSelected(false);
            return;
        } else {
            try {
                whenValue = Integer.parseInt(whenString);
            } catch (NumberFormatException e) {
                alertFieldValueInvalid(row, "您輸入的數值有問題。\n" + " > " + whenString + " 應該不是數字吧？");
                jobPane.enableCheckBoxes.get(row - 1).setSelected(false);
                return;
            }

            if (whenValue <= 0) {
                alertFieldValueInvalid(row, "您輸入的數值必須大於0。");
                jobPane.enableCheckBoxes.get(row - 1).setSelected(false);
                return;
            }
        }

        Log.d(TAG, "Checked info: enable: " + enable + ", when: " + whenIndex + ", whenValue: " + whenString + ", actionIndex: " + actionIndex);
        mListener.onItemEnableChanged(tabIndex, row, enable, whenIndex, whenValue, actionIndex);
    }

    private void alertFieldValueInvalid(int row, String msg) {
        Log.w(TAG, "User input invalid value: " + msg);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(mMainApp.getMainStage());
        alert.setTitle("錯誤");
        alert.setHeaderText("請檢查第 " + row + " 行的參數設定");
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private class AutoJobPane {
        int columnCount = 4;
        int rowCount = 8; //if we want to dynamically generate job table, this should be editable

        ArrayList<CheckBox> enableCheckBoxes;
        ArrayList<ChoiceBox> whenChoiceBoxes;
        ArrayList<TextField> whenValueTextFields;
        ArrayList<ChoiceBox> actionChoiceBoxes;
    }

    private class AutoJobPaneNodeInfo {
        int pane;
        int column;
        int row;

        AutoJobPaneNodeInfo(int p, int r, int c) {
            pane = p;
            column = c;
            row = r;
        }

        public String toString() {
            return "Node: (tab, row, column) = (" + pane + ", " + row + " ," + column + ")";
        }
    }
}
