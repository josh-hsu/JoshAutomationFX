package com.mumu.joshautomation.ro;

import com.mumu.libjoshgame.Log;

import com.mumu.joshautomation.script.AutoJob;
import com.mumu.joshautomation.script.AutoJobEventListener;
import com.mumu.libjoshgame.JoshGameLibrary;
import com.mumu.libjoshgame.ScreenPoint;

import java.util.ArrayList;

import static com.mumu.joshautomation.ro.ROJobDescription.*;

public class ROAutoRoutineJob extends AutoJob {
    private String TAG = "ROAutoRoutine";
    private ArrayList<Thread> mJobRoutines;
    private JoshGameLibrary mGL;
    private AutoJobEventListener mListener;
    private int mTab;

    private RORoutine mRO;
    private ROAutoRoutineJob mSelf;
    private ROJobList mJobList;
    private String mCurrentDevice;

    public static final String jobName = "RO 自動程序";

    public ROAutoRoutineJob(String device, int tab) {
        super(jobName);

        /* JoshGameLibrary basic initial */
        mGL = new JoshGameLibrary();
        mGL.setDeviceName(device);
        //mGL.setScreenDimension(1440,900);
        mGL.setScreenDimensionFromDevice();
        mGL.setGameOrientation(ScreenPoint.SO_Landscape);
        mGL.setTouchShift(0);
        mGL.setAmbiguousRange(new int[] {0xf,0xf,0xf});
        mGL.setPlatform(true);
        mGL.setChatty(false);

        mRO = new RORoutine(mGL, mListener);
        TAG = TAG + "(" + device + ")"; //make us easy to find out which device
        mSelf = this;
        mTab = tab;
        mCurrentDevice = device;
    }

    @Override
    public void start() {
        super.start();
        Log.d(TAG, "starting job " + getJobName());

        formatNewJobs();
        formatDetectionJobs();
        startAllJobs();
    }

    @Override
    public void stop() {
        super.stop();
        Log.d(TAG, "stopping job " + getJobName());

        stopAllJobs();
    }

    @Override
    public void setExtra(Object object) {
        if (object instanceof ROJobList) {
            mJobList = (ROJobList) object;
            if (mShouldJobRunning)
                onJobListChanged();
        }
    }

    public void setJobEventListener(AutoJobEventListener el) {
        mListener = el;
        mRO = new RORoutine(mGL, mListener);
    }

    private void sendEvent(String msg, Object extra) {
        if (mListener != null) {
            mListener.onEventReceived(msg, extra);
        } else {
            Log.w(TAG, "There is no event listener registered.");
        }
    }

    private void sendMessage(String msg, int index) {
        int[] location = {mTab, index};
        sendEvent(msg, location);
    }

    private void formatNewJobs() {
        if (mJobRoutines != null && !mJobRoutines.isEmpty())
            mJobRoutines.clear();

        mJobRoutines = new ArrayList<>();

        //format job routine
        for(int i = 0; i < mJobList.getJobCount(); i++) {
            mJobRoutines.add(i, new ROJobRoutine(i, mJobList.getJob(i)));
        }
    }

    private void formatDetectionJobs() {
        // Detect of auto battle button
        // index : 7
        class DetectAutoFollow extends RODetectionRoutine {
            private DetectAutoFollow(int index, String name) {
                super(index, name, false);
            }

            @Override
            public boolean onCondition() {
                return !mRO.isFollowingFirst();
            }

            @Override
            public void doAction() throws InterruptedException {
                Log.d(TAG, "try following " + super.currentName);
                mRO.tryFollowingFirst();
            }
        }
        mJobRoutines.add(new DetectAutoFollow(7, "detectAutoBattle"));

        // Detect of auto battle button
        // index : 8
        class DetectAutoBattle extends RODetectionRoutine {
            private DetectAutoBattle(int index, String name) {
                super(index, name, false);
            }

            @Override
            public boolean onCondition() {
                return !mRO.isAutoBattleEnabled();
            }

            @Override
            public void doAction() throws InterruptedException {
                Log.d(TAG, "try auto battle " + super.currentName);
                mRO.tryAutoBattle();
            }
        }
        mJobRoutines.add(new DetectAutoBattle(8, "detectAutoBattle"));

    }

    private void startAllJobs() {
        if (mJobRoutines != null) {
            for(int i = 0; i < mJobList.getJobCount(); i++) {
                if (!mJobRoutines.get(i).isAlive())
                    mJobRoutines.get(i).start();
            }
        }

        mJobRoutines.get(7).start();
        mJobRoutines.get(8).start();
    }

    private void stopAllJobs() {
        if (mJobRoutines != null) {
            for(int i = 0; i < mJobList.getJobCount(); i++) {
                mJobRoutines.get(i).interrupt();
            }
        }
        mJobRoutines.get(7).interrupt();
        mJobRoutines.get(8).interrupt();
    }

    public void setAutoJob(int index, ROJobDescription job) {
        Thread t = mJobRoutines.get(index);
        if (t instanceof ROJobRoutine)
            ((ROJobRoutine) t).setJob(job);
        else
            Log.e(TAG, "index " + index + " is not an ROJobRoutine");
    }

    public void setDetailJobEnable(int index, boolean enable) {
        int DETAIL_INDEX_START = 7;
        RODetectionRoutine routine = (RODetectionRoutine) mJobRoutines.get(DETAIL_INDEX_START + index);
        if (routine != null) {
            routine.setEnable(enable);
        }
    }

    /*
     * onJobListChanged
     * Currently we will stop all jobs to accomplish new jobs
     */
    private void onJobListChanged() {
        stopAllJobs();
        formatNewJobs();
        startAllJobs();
    }

    private class ROJobRoutine extends Thread {
        private ROJobDescription currentJob;
        private int currentIndex;
        private boolean deferAction = false;

        ROJobRoutine(int index, ROJobDescription job) {
            currentJob = job;
            currentIndex = index;
        }

        public void setJob(ROJobDescription job) {
            currentJob = job;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        private int getNextSleepTime(int originalSleepTime) {
            if (deferAction) {
                deferAction = false;
                return 1000;
            } else {
                return originalSleepTime;
            }
        }

        private void sleepIn(int expectSleepTime) throws InterruptedException {
            int leftSleepTime = expectSleepTime;

            while(leftSleepTime > 0) {
                Thread.sleep(1000);
                leftSleepTime -= 1000;
                sendMessage("倒數計時: " + leftSleepTime/1000 + " / " + expectSleepTime/1000, currentIndex);
            }
        }

        /*
         * main function should handle job parsing and running
         */
        private void main() throws Exception {
            while (mShouldJobRunning) {
                //do jobs
                if (currentJob.sEnabled == 1) {
                    switch (currentJob.sWhen) {
                        case OnMPLessThan:
                        case OnHPLessThan:
                        case OnHPHigherThan:
                        case OnMPHigherThan:
                            sendMessage("倒數計時中...", currentIndex);
                            sleepIn(getNextSleepTime(defaultDetectInterval));
                            Log.d(TAG, "sleep end checking " + currentIndex + " for values");

                            if (!mRO.isInBattleMode()) {
                                Log.d(TAG, "Not in battle mode, defer it.");
                                sendMessage("非戰鬥中，延遲動作", currentIndex);
                                deferAction = true;
                                continue;
                            }

                            if (currentJob.sEnabled == 0)
                                continue;

                            sendMessage("執行檢查", currentIndex);
                            if (mRO.checkBattleSupply(currentJob.sWhen, currentJob.sWhenValue)) {
                                mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            } else {
                                sendMessage("未達到設定條件", currentIndex);
                            }
                            break;
                        case OnPeriod:
                            sendMessage("倒數計時中...", currentIndex);
                            sleepIn(getNextSleepTime(currentJob.sWhenValue * 1000));
                            Log.d(TAG, "sleep end, checking " + currentIndex + " for period job");
                            sendMessage("檢查中", currentIndex);

                            if (!mRO.isInBattleMode()) {
                                Log.d(TAG, "Not in battle mode, defer it.");
                                sendMessage("非戰鬥中，延遲動作", currentIndex);
                                deferAction = true;
                                continue;
                            }

                            if (currentJob.sEnabled == 0)
                                continue;

                            sendMessage("執行動作", currentIndex);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            break;
                        case OnPretendDeath:
                            sendMessage("倒數計時中...", currentIndex);
                            sleepIn(getNextSleepTime(currentJob.sWhenValue * 1000));
                            Log.d(TAG, "sleep end, press pretending death.");
                            sendMessage("檢查中", currentIndex);

                            if (!mRO.isInBattleMode()) {
                                Log.d(TAG, "Not in battle mode, defer it.");
                                sendMessage("非戰鬥中，延遲動作", currentIndex);
                                deferAction = true;
                                continue;
                            }

                            if (currentJob.sEnabled == 0)
                                continue;

                            sendMessage("點位於技能一的裝死", currentIndex);
                            mRO.executeAction(ActionPressSkill, 1);
                            sleepIn(1000);
                            sendMessage("點四次裝死後要執行的動作", currentIndex);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            break;
                        case OnPretendDeathLong:
                            sendMessage("倒數計時中...", currentIndex);
                            sleepIn(getNextSleepTime(currentJob.sWhenValue * 1000));
                            Log.d(TAG, "sleep end, press pretending death.");
                            sendMessage("檢查中", currentIndex);

                            if (!mRO.isInBattleMode()) {
                                Log.d(TAG, "Not in battle mode, defer it.");
                                sendMessage("非戰鬥中，延遲動作", currentIndex);
                                deferAction = true;
                                continue;
                            }

                            if (currentJob.sEnabled == 0)
                                continue;

                            sendMessage("點位於技能一的裝死", currentIndex);
                            mRO.executeAction(ActionPressSkill, 1);
                            sleepIn(5000);
                            sendMessage("點四次裝死後要執行的動作", currentIndex);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            break;
                        default:
                            sendMessage("尚未支援的功能", currentIndex);
                            Log.w(TAG, "When " + currentJob.sWhen + " is not supported");
                            Thread.sleep(500);
                    }
                } else {
                    sendMessage("尚未啟用", currentIndex);
                    Thread.sleep(3000);
                }
            }
        }

        public void run() {
            try {
                main();
            } catch (InterruptedException e) {
                sendMessage("被其他程序終止", currentIndex);
                Log.w(TAG, "ROJobRoutine [" + currentIndex + "] caught an exception or been interrupted: " + e.getMessage());
            } catch (Exception e) {
                sendMessage("嚴重錯誤，已終止", currentIndex);
                Log.f(TAG, "WTF: serious thing happened: " + e.getMessage(), e);
            }
        }
    }

    public class RODetectionRoutine extends Thread {
        private int currentIndex;
        private String currentName;
        private boolean deferAction = false;
        private boolean enabled = false;
        private boolean shouldBeInBattle = false;

        RODetectionRoutine(int index, String name, boolean inBattle) {
            currentIndex = index;
            currentName = name;
            shouldBeInBattle = inBattle;
        }

        /*
         * Override onCondition and doAction to do custom job
         */
        public boolean onCondition() {
            Log.w(TAG, "onCondition is not override in job" + currentName);
            return false;
        }

        public void doAction() throws InterruptedException {
            Log.w(TAG, "doAction is not override in job" + currentName);
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setEnable(boolean enable) {
            enabled = enable;
        }

        /*
         * internal helper function
         */
        private int getNextSleepTime(int originalSleepTime) {
            if (deferAction) {
                deferAction = false;
                return 1000;
            } else {
                return originalSleepTime;
            }
        }

        /*
         * main function should handle job parsing and running
         */
        private void main() throws Exception {
            while (mShouldJobRunning) {
                //do jobs
                if (enabled) {
                    if (shouldBeInBattle) {
                        if (!mRO.isInBattleMode()) {
                            if (onCondition()) {
                                doAction();
                            }
                        }
                    } else {
                        if (onCondition()) {
                            doAction();
                        }
                    }
                }

                Thread.sleep(3000);
            }
        }

        public void run() {
            try {
                main();
            } catch (InterruptedException e) {
                Log.w(TAG, "RODetectionRoutine [" + currentIndex + "] " + currentName +
                        " caught an exception or been interrupted: " + e.getMessage());
            } catch (Exception e) {
                Log.f(TAG, "WTF: serious thing happened: " + e.getMessage(), e);
            }
        }
    }

}
