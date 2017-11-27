package com.mumu.joshautomation.ro;

import java.util.ArrayList;

public class ROJobList {
    private ArrayList<ROJobDescription> mJobList;
    private int mJobCount;

    public ROJobList() {
        mJobList = new ArrayList<>();
        mJobCount = 0;
    }

    public ROJobList(ArrayList<ROJobDescription> list) {
        if (list == null) {
            mJobList = new ArrayList<>();
            mJobCount = 0;
        } else {
            mJobList = list;
            mJobCount = mJobList.size();
        }
    }

    public void addJob(int idx, ROJobDescription job) {
        mJobList.add(idx, job);
        mJobCount = mJobList.size();
    }

    public void setEnable(int idx, int enable) {
        mJobList.get(idx).sEnabled = enable;
    }


    public void setJob(int idx, ROJobDescription job) {
        mJobList.set(idx, job);
    }

    public int getJobCount() {
        return mJobCount;
    }

    public ROJobDescription getJob(int idx) {
        return mJobList.get(idx);
    }
}
