package com.mumu.jafx;

import java.io.IOException;

import com.mumu.joshautomation.ro.ROAutoDrinkJob;
import com.mumu.joshautomation.script.AutoJobEventListener;
import com.mumu.joshautomation.script.AutoJobHandler;
import com.mumu.libjoshgame.Cmd;
import com.mumu.libjoshgame.JoshGameLibrary;
import com.mumu.libjoshgame.ScreenPoint;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application implements AutoJobEventListener {

    private Stage mMainStage;
    private BorderPane mRootView;
    private AnchorPane mJobView;

    private AutoJobHandler mAutoJobHandler;
    private JobViewController mJobViewController;
    private JoshGameLibrary mGL = JoshGameLibrary.getInstance();


    /**
     * Constructor
     */
    public Main() {
        // setting up GL before AutoJob initialized
        mGL.setScreenDimension(1440,900);
        mGL.setGameOrientation(ScreenPoint.SO_Landscape);
        mGL.setTouchShift(0);
        mGL.setAmbiguousRange(new int[] {9,9,9});
        mGL.setPlatform(true);
        mGL.setChatty(true);

        mAutoJobHandler = AutoJobHandler.getHandler();
        mAutoJobHandler.addJob(new ROAutoDrinkJob());
        mAutoJobHandler.setJobEventListener(0, this);

        Cmd cmd = new Cmd();
        cmd.getAdbDevices();
        cmd.runCommand("ls -l /sdcard", 0);

        // shouldn't be started here, but we just want to test it out
        mAutoJobHandler.startJob(0);

    }


    @Override
    public void start(Stage primaryStage) {
        this.mMainStage = primaryStage;
        this.mMainStage.setTitle("RO Nox Tool");

        initRootLayout();

        initJobMainView();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            mRootView = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(mRootView);
            mMainStage.setScene(scene);
            mMainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    private void initJobMainView() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/JobMainView.fxml"));
            mJobView = loader.load();
            mJobViewController = loader.getController();

            // Set person overview into the center of root layout.
            mRootView.setCenter(mJobView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getMainStage() {
        return mMainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    /*
     * Implementation of AutoJobEventListener
     */
    @Override
    public void onEventReceived(String msg, Object extra) {
        if (mJobViewController != null)
            mJobViewController.updateStatus(msg);
    }

    @Override
    public void onJobDone(String jobName) {

    }
}
