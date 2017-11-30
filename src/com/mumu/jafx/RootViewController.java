package com.mumu.jafx;


import com.mumu.libjoshgame.Log;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RootViewController {
    private static final String TAG = "ViewController";
    private Main mMainApp;

    @FXML private MenuItem mMenuExit;
    @FXML private MenuItem mMenuAbout;
    @FXML private MenuItem mMenuRORefresh;
    @FXML private MenuItem mMenuReportIssue;

    private JobViewListener mListener;

    private EventHandler<ActionEvent> mNodeEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Object eventSource = event.getSource();
            if (eventSource == mMenuExit) {
                System.exit(0);
            } else if (eventSource == mMenuAbout) {
                showAboutPage();
            } else if (eventSource == mMenuReportIssue) {
                doIssueReport();
            } else if (eventSource == mMenuRORefresh) {
                doRORefresh();
            } else {
                Log.w(TAG, "Unknown type of event source.");
            }
        }
    };

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Add event listener
        mMenuAbout.setOnAction(mNodeEventHandler);
        mMenuExit.setOnAction(mNodeEventHandler);
        mMenuReportIssue.setOnAction(mNodeEventHandler);
        mMenuRORefresh.setOnAction(mNodeEventHandler);

    }

    public void setMainApp(Main app) {
        mMainApp = app;
    }

    private void showAboutPage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(mMainApp.getMainStage());
        alert.setTitle("關於");
        alert.setHeaderText("JoshAutomationFX for RO 守護什麼東西");
        alert.setContentText("哈囉，本軟體是利用 JavaFX 撰寫的程式，其架構用於 RO 的手機板，因為我也是個懶惰又愛玩遊戲的人。\n" +
                "這個 App 不用錢，如果你不幸花錢買了這個 App ... 請你聯絡我，我會給你免費連結\n\n" +
                "聯絡我: alenbos0517@gmail.com\n" +
                "釋出日期: 2017-11-29\n" +
                "版本: 0.19C (Beta)");

        alert.showAndWait();
    }

    private void doRORefresh() {

    }

    private void doIssueReport() {

    }
}
