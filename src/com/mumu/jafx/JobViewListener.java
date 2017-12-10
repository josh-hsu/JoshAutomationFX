package com.mumu.jafx;

public interface JobViewListener {
    void onItemEnableChanged(int tab, int index, int enable, int whenIndex, int whenValue, int actionIndex);
    void onDetailFeatureChanged(int tab, int index, boolean enable);
    void onExit(JobViewController controller);
}
