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

    public static final String jobName = "RO 自動程序";

    public ROAutoRoutineJob() {
        super(jobName);

        /* JoshGameLibrary basic initial */
        mGL = JoshGameLibrary.getInstance();
        mGL.setGameOrientation(ScreenPoint.SO_Landscape);

        mRO = new RORoutine(mGL, mListener);
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

    private void sendMessage(String msg) {
        sendEvent(msg, this);
    }

    private void formatNewJobs() {
        mJobRoutines = new ArrayList<>();

        //format job routine
        for(int i = 0; i < mJobList.getJobCount(); i++) {
            mJobRoutines.add(i, new ROJobRoutine(i, mJobList.getJob(i)));
        }
    }

    private void startAllJobs() {
        if (mJobRoutines != null) {
            for(int i = 0; i < mJobList.getJobCount(); i++) {
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

    private void onJobListChanged() {
        stopAllJobs();
        formatNewJobs();
        startAllJobs();
    }

    private class ROJobRoutine extends Thread {
        private ROJobDescription currentJob;
        private int currentIndex;

        ROJobRoutine(int index, ROJobDescription job) {
            currentJob = job;
            currentIndex = index;
        }

        /*
         * main function should handle job parsing and running
         */
        private void main() throws Exception {
            while (mShouldJobRunning) {
                //do jobs
                if (currentJob.sEnabled == 1) {
                    switch (currentJob.sWhenValue) {
                        case OnMPLessThan:
                            break;
                        case OnHPLessThan:
                            break;
                        case OnPeriod:
                            break;
                    }
                } else {
                    Log.d(TAG, "Job " + currentIndex + " is not enabled, sleep for 5 seconds");
                    Thread.sleep(5000);
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
