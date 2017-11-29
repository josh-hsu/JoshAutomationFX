package com.mumu.jafx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.mumu.joshautomation.ro.ROAutoRoutineJob;
import com.mumu.joshautomation.ro.ROJobDescription;
import com.mumu.joshautomation.ro.ROJobList;
import com.mumu.joshautomation.script.AutoJob;
import com.mumu.joshautomation.script.AutoJobEventListener;
import com.mumu.joshautomation.script.AutoJobHandler;
import com.mumu.libjoshgame.Cmd;
import com.mumu.libjoshgame.JoshGameLibrary;
import com.mumu.libjoshgame.Log;
import com.mumu.libjoshgame.ScreenPoint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static com.mumu.joshautomation.ro.ROJobDescription.*;

public class Main extends Application implements AutoJobEventListener, JobViewListener {
    private static final String TAG = "Main";
    private final String mCurrentWD = System.getProperty("user.dir");
    private final int mMaxSupportJob = 7;
    private Stage mMainStage;
    private BorderPane mRootView;
    private AnchorPane mJobView;

    private AutoJobHandler mAutoJobHandler;
    private JobViewController mJobViewController;
    private JoshGameLibrary mGL = JoshGameLibrary.getInstance();
    private ArrayList<ROJobList> mROJobListSet;
    private ArrayList<String> mDeviceList;

    private PeriodUpdateThread mUpdateThread;

    /**
     * Constructor
     */
    public Main() {

        Log.d(TAG, " ===== Application Start ===== ");

        // setting up GL before AutoJob initialized
        mGL.setScreenDimension(1440,900);
        mGL.setGameOrientation(ScreenPoint.SO_Landscape);
        mGL.setTouchShift(0);
        mGL.setAmbiguousRange(new int[] {9,9,9});
        mGL.setPlatform(true);
        mGL.setChatty(false);

        initDevices();
        createAutoJob();
        startAutoJob();
    }


    @Override
    public void start(Stage primaryStage) {
        this.mMainStage = primaryStage;
        this.mMainStage.setTitle("RO Nox Tool");

        initRootLayout();

        initJobMainView();

        mJobViewController.updateTabName(mDeviceList);
        mJobViewController.registerListener(this);
        mJobViewController.setMainApp(this);

        //getCurrentScreenshot();
        mUpdateThread = new PeriodUpdateThread();
        mUpdateThread.start();
    }

    @Override
    public void stop(){
        Log.d(TAG, "User close application.");
        for(int i = 0; i < mDeviceList.size(); i++)
            mAutoJobHandler.stopJob(i);

        if (mUpdateThread != null)
            mUpdateThread.interrupt();

        Log.d(TAG, " ===== Application Stop ===== ");
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

    private void initDevices() {
        mDeviceList = Cmd.getInstance().getAdbDevices();
        for (String device: mDeviceList) {
            Log.d(TAG, "adb device found: " + device);
        }
    }

    /*
     * Get current screenshot of all devices
     * This function shouldn't be called directly in UI Thread
     */
    private void getCurrentScreenshot() {
        for (int i = 0; i < mDeviceList.size(); i++) {
            String filename = "/sdcard/screen" + i + ".png";
            String localname = "screen" + i + ".png";
            Cmd.getInstance().runCommand("screencap -p " + filename, mDeviceList.get(i));
            Cmd.getInstance().pullAdbFile(filename, mDeviceList.get(i));
            String path = mCurrentWD + "\\" + localname;
            try {
                URL pathUrl = new File(path).toURI().toURL();
                mJobViewController.updateScreenshot(i, pathUrl.toString());
            } catch (MalformedURLException e) {
                Log.e(TAG, "Screenshot format URL failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createAutoJob() {
        // initial phase
        mAutoJobHandler = AutoJobHandler.getHandler();
        mROJobListSet = new ArrayList<>();

        // create jobs for all connected devices
        for (int i = 0; i < mDeviceList.size(); i++) {
            String device = mDeviceList.get(i);
            ROJobList jobList = new ROJobList();

            // add dummy empty job for initialize
            for(int j = 0; j < mMaxSupportJob; j++) {
                jobList.addJob(j, new ROJobDescription());
            }

            mROJobListSet.add(i, jobList);
            mAutoJobHandler.addJob(new ROAutoRoutineJob(device));
            mAutoJobHandler.setJobEventListener(i, this);
            mAutoJobHandler.setExtra(i, jobList);
        }
    }

    private void startAutoJob() {
        for(int i = 0; i < mDeviceList.size(); i++) {
            mAutoJobHandler.startJob(i);
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

    /*
     * Implementation of JobViewListener
     */
    @Override
    public void onItemEnableChanged(int tab, int index, int enable, int whenIndex, int whenValue, int actionIndex) {
        int when = 0;
        int action = 0;
        int actionValue = 0;

        switch (whenIndex) {
            case 0:
                Log.d(TAG, "When HP < " + whenValue);
                when = OnHPLessThan;
                break;
            case 1:
                Log.d(TAG, "When HP > " + whenValue);
                when = OnHPHigherThan;
                break;
            case 2:
                Log.d(TAG, "When MP < " + whenValue);
                when = OnMPLessThan;
                break;
            case 3:
                Log.d(TAG, "When MP > " + whenValue);
                when = OnMPHigherThan;
                break;
            case 4:
                Log.d(TAG, "When about " + whenValue + " ms later");
                when = OnPeriod;
                break;
            default:
                Log.d(TAG, "When ... not supported");
        }

        if (actionIndex >= 0 && actionIndex < 5) {
            Log.d(TAG, "do press item " + (actionIndex + 1));
            action = ActionPressItem;
            actionValue = actionIndex + 1;
        } else if (actionIndex >= 5 && actionIndex < 11) {
            Log.d(TAG, "do press skill " + (actionIndex - 4));
            action = ActionPressSkill;
            actionValue = actionIndex - 4;
        }

        mROJobListSet.get(tab).setJob(index - 1,
                new ROJobDescription(enable, when, whenValue, action, actionValue));

        AutoJob autoJob = mAutoJobHandler.getJob(tab);
        if (autoJob instanceof ROAutoRoutineJob) {
            ((ROAutoRoutineJob) autoJob).setJob(index - 1, mROJobListSet.get(tab).getJob(index - 1));
        }
    }

    @Override
    public void onExit(JobViewController controller) {

    }

    private class PeriodUpdateThread extends Thread {
        private boolean shouldRunning = false;

        @Override
        public void run() {
            super.run();

            shouldRunning = true;

            while(shouldRunning) {
                getCurrentScreenshot();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    shouldRunning = false;
                    Log.w(TAG, "Update Thread interrupted");
                }
            }
        }
    }
}
