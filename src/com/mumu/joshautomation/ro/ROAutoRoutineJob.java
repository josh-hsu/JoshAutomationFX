package com.mumu.joshautomation.ro;

import com.mumu.libjoshgame.Log;

import com.mumu.joshautomation.script.AutoJob;
import com.mumu.joshautomation.script.AutoJobEventListener;
import com.mumu.libjoshgame.JoshGameLibrary;
import com.mumu.libjoshgame.ScreenPoint;

import java.util.ArrayList;

import static com.mumu.joshautomation.ro.ROJobDescription.*;

public class ROAutoRoutineJob extends AutoJob {
    private static final String TAG = "ROAutoRoutine";
    private ArrayList<ROJobRoutine> mJobRoutines;
    private JoshGameLibrary mGL;
    private AutoJobEventListener mListener;

    private RORoutine mRO;
    private ROAutoRoutineJob mSelf;
    private ROJobList mJobList;
    private String mCurrentDevice;

    public static final String jobName = "RO 自動程序";

    public ROAutoRoutineJob(String device) {
        super(jobName);

        /* JoshGameLibrary basic initial */
        mGL = JoshGameLibrary.getInstance();
        mGL.setGameOrientation(ScreenPoint.SO_Landscape);

        mRO = new RORoutine(mGL, mListener, device);
        mSelf = this;
        mCurrentDevice = device;
    }

    public ROAutoRoutineJob() {
        super(jobName);

        /* JoshGameLibrary basic initial */
        mGL = JoshGameLibrary.getInstance();
        mGL.setGameOrientation(ScreenPoint.SO_Landscape);

        mRO = new RORoutine(mGL, mListener, null);
        mSelf = this;
    }

    @Override
    public void start() {
        super.start();
        Log.d(TAG, "starting job " + getJobName());

        formatNewJobs();
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
        mRO = new RORoutine(mGL, mListener, mCurrentDevice);
    }

    private void sendEvent(String msg, Object extra) {
        if (mListener != null) {
            mListener.onEventReceived(msg, extra);
        } else {
            Log.w(TAG, "There is no event listener registered.");
        }
    }

    private void sendMessage(String msg) {
        sendEvent(msg, mSelf);
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

    private void startAllJobs() {
        if (mJobRoutines != null) {
            for(int i = 0; i < mJobList.getJobCount(); i++) {
                if (!mJobRoutines.get(i).isRunning())
                    mJobRoutines.get(i).start();
            }
        }
    }

    private void stopAllJobs() {
        if (mJobRoutines != null) {
            for(int i = 0; i < mJobList.getJobCount(); i++) {
                mJobRoutines.get(i).interrupt();
            }
        }
    }

    public void setJob(int index, ROJobDescription job) {
        mJobRoutines.get(index).setJob(job);
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
        private boolean currentRunning = false;

        ROJobRoutine(int index, ROJobDescription job) {
            currentJob = job;
            currentIndex = index;
        }

        public boolean isRunning() {
            return currentRunning;
        }

        public void setJob(ROJobDescription job) {
            currentJob = job;
        }

        /*
         * main function should handle job parsing and running
         */
        private void main() throws Exception {
            currentRunning = true;

            while (mShouldJobRunning) {
                //do jobs
                if (currentJob.sEnabled == 1) {
                    switch (currentJob.sWhen) {
                        case OnMPLessThan:
                        case OnHPLessThan:
                            Thread.sleep(defaultDetectInterval);
                            Log.d(TAG, "checking " + currentIndex + " for values");
                            if (mRO.checkBattleSupply(currentJob.sWhen, currentJob.sWhenValue))
                                mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            break;
                        case OnPeriod:
                            Thread.sleep(currentJob.sWhenValue);
                            Log.d(TAG, "checking " + currentIndex + " for period job");
                            mRO.executeAction(currentJob.sAction, currentJob.sActionValue);
                            break;
                        default:
                            Log.w(TAG, "When " + currentJob.sWhen + " is not supported");
                            Thread.sleep(500);
                    }
                } else {
                    Log.d(TAG, "Device: " + mCurrentDevice + " Job " + currentIndex + " is not enabled, sleep for 5 seconds");
                    Log.d(TAG, "Job description: " + currentJob.toString());
                    Thread.sleep(3000);
                }
            }
        }

        public void run() {
            try {
                main();
            } catch (Exception e) {
                Log.e(TAG, "ROJobRoutine [" + currentIndex + "] caught an exception or been interrupted: " + e.getMessage());
            }
        }
    }
}
