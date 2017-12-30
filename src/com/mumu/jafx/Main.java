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
import com.mumu.libjoshgame.AdbDeviceManager;
import com.mumu.libjoshgame.Log;

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

    private JobViewController mJobViewController;
    private RootViewController mRootViewController;

    private AutoJobHandler mAutoJobHandler;
    private ArrayList<ROJobList> mROJobListSet;
    private ArrayList<String> mDeviceList;

    private PeriodUpdateThread mUpdateThread;
    private boolean mDeviceInitialized = false;

    /**
     * Constructor
     */
    public Main() {
        Log.d(TAG, " ===== Application Start ===== ");
    }


    @Override
    public void start(Stage primaryStage) {
        this.mMainStage = primaryStage;
        this.mMainStage.setTitle("RO Nox Tool");

        initRootLayout();
        initJobMainView();

        mRootViewController.setMainApp(this);

        mJobViewController.registerListener(this);
        mJobViewController.setMainApp(this);

        new DeviceInitialThread().start();

        createScreenshotDirectory();
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
            mRootViewController = loader.getController();

            // Show the scene containing the root layout.
            Scene scene = new Scene(mRootView);
            mMainStage.setScene(scene);
            mMainStage.show();
        } catch (IOException e) {
            Log.f(TAG, "WTF: init root layout cause an exception: " + e.getMessage(), e);
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
            Log.f(TAG, "WTF: init job main view cause an exception: " + e.getMessage(), e);
        }
    }

    private void checkConnectedDevices() {
        mDeviceList = AdbDeviceManager.getInstance().getAdbDevices();
        for (String device: mDeviceList) {
            Log.d(TAG, "adb device found: " + device);
        }
    }

    private void createScreenshotDirectory() {
        File theDir = new File(mCurrentWD + "\\Screenshot");

        if (!theDir.exists()) {
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                Log.f(TAG, "WTF: mkdir cause an exception: " + se.getMessage(), se);
            }

            if(result) {
                Log.d(TAG, "DIR created");
            }
        }
    }

    /*
     * Get current screenshot of all devices
     * This function shouldn't be called directly in UI Thread
     */
    private boolean getCurrentScreenshot() {
        if (mJobViewController == null || mDeviceList == null || !mDeviceInitialized) {
            Log.d(TAG, "Waiting for view created.");
            return false;
        }

        for (int i = 0; i < mDeviceList.size(); i++) {
            String filename = "/sdcard/screen" + i + ".png";
            String localName = "screen" + i + ".png";
            String path = mCurrentWD + "\\Screenshot\\";
            AdbDeviceManager.getInstance().runCommand("screencap -p " + filename, mDeviceList.get(i));
            AdbDeviceManager.getInstance().pullAdbFile(filename, path, mDeviceList.get(i));

            try {
                URL pathUrl = new File(path + localName).toURI().toURL();
                mJobViewController.updateScreenshot(i, pathUrl.toString());
            } catch (MalformedURLException e) {
                Log.f(TAG, "Screenshot format URL failed: " + e.getMessage(), e);
            }
        }

        return true;
    }

    private void createAutoJob() {
        // initial phase
        mAutoJobHandler = AutoJobHandler.getHandler();
        mROJobListSet = new ArrayList<>();

        // create jobs for all connected devices
        for (int tab = 0; tab < mDeviceList.size(); tab++) {
            String device = mDeviceList.get(tab);
            ROJobList jobList = new ROJobList();

            // add dummy empty job for initialize
            for(int j = 0; j < mMaxSupportJob; j++) {
                jobList.addJob(j, new ROJobDescription());
            }

            mROJobListSet.add(tab, jobList);
            mAutoJobHandler.addJob(new ROAutoRoutineJob(device, tab));
            mAutoJobHandler.setJobEventListener(tab, this);
            mAutoJobHandler.setExtra(tab, jobList);
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
        if (mJobViewController != null) {
            if (extra instanceof int[]) {
                int[] location = (int[]) extra;
                mJobViewController.updateInfo(location[0], location[1], msg);
            } else {
                Log.w(TAG, "Message from auto job contain illegal extra");
            }
        }
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

        // this is redundant, just put when = whenIndex in the future
        switch (whenIndex) {
            case OnHPLessThan:
                Log.d(TAG, "When HP < " + whenValue);
                when = OnHPLessThan;
                break;
            case OnHPHigherThan:
                Log.d(TAG, "When HP > " + whenValue);
                when = OnHPHigherThan;
                break;
            case OnMPLessThan:
                Log.d(TAG, "When MP < " + whenValue);
                when = OnMPLessThan;
                break;
            case OnMPHigherThan:
                Log.d(TAG, "When MP > " + whenValue);
                when = OnMPHigherThan;
                break;
            case OnPeriod:
                Log.d(TAG, "When about " + whenValue + " seconds later");
                when = OnPeriod;
                break;
            case OnPretendDeath:
            case OnPretendDeathLong:
                Log.d(TAG, "Pretend death after " + whenValue + " seconds and use action to bring life back.");
                when = whenIndex;
            default:
                Log.d(TAG, "When ... not supported, maybe future");
        }

        if (actionIndex >= 0 && actionIndex < 5) {
            Log.d(TAG, "do press item " + (actionIndex + 1));
            action = ActionPressItem;
            actionValue = actionIndex + 1;
        } else if (actionIndex >= 5 && actionIndex < 11) {
            Log.d(TAG, "do press skill " + (actionIndex - 4));
            action = ActionPressSkill;
            actionValue = actionIndex - 4;
        } else if (actionIndex == 11) { /* action step */
            Log.d(TAG, "do moving a step");
            action = ActionMoveAStep;
            actionValue = -1;
        }

        if (mROJobListSet.size() > tab) {
            mROJobListSet.get(tab).setJob(index - 1,
                    new ROJobDescription(enable, when, whenValue, action, actionValue));
        } else {
            Log.w(TAG, "No device in this tab");
            return;
        }

        AutoJob autoJob = mAutoJobHandler.getJob(tab);
        if (autoJob instanceof ROAutoRoutineJob) {
            ((ROAutoRoutineJob) autoJob).setAutoJob(index - 1, mROJobListSet.get(tab).getJob(index - 1));
        }
    }

    @Override
    public void onExit(JobViewController controller) {
        //do nothing
    }

    @Override
    public void onDetailFeatureChanged(int tab, int index, boolean enable) {
        if (!mDeviceInitialized)
            return;

        ROAutoRoutineJob autoJob = (ROAutoRoutineJob) mAutoJobHandler.getJob(tab);
        autoJob.setDetailJobEnable(index, enable);
    }

    private class DeviceInitialThread extends Thread {

        @Override
        public void run() {
            super.run();

            checkConnectedDevices();
            createAutoJob();
            startAutoJob();

            mJobViewController.updateTabName(mDeviceList);
            mDeviceInitialized = true;
        }
    }

    private class PeriodUpdateThread extends Thread {
        private boolean shouldRunning = false;

        @Override
        public void run() {
            super.run();

            shouldRunning = true;

            while(shouldRunning) {
                boolean updateDone = getCurrentScreenshot();

                try {
                    if (updateDone)
                        Thread.sleep(100);
                    else
                        Thread.sleep(2500);
                } catch (InterruptedException e) {
                    shouldRunning = false;
                    Log.w(TAG, "Update Thread interrupted");
                    break;
                } catch (Exception e) {
                    Log.f(TAG, "Update Thread cause an exception: ", e);
                }
            }
        }
    }
}
