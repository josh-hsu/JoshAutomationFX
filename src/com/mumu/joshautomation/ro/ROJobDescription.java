package com.mumu.joshautomation.ro;

public class ROJobDescription {
    public final static int Disable = 0;
    public final static int Enable  = 1;

    public final static int OnHPLessThan   = 0;
    public final static int OnHPHigherThan = 1;
    public final static int OnMPLessThan   = 2;
    public final static int OnMPHigherThan = 3;
    public final static int OnPeriod       = 4;
    public final static int OnAutoStart    = 5;
    public final static int OnAutoEnd      = 6;
    public final static int OnFollowStart  = 7;
    public final static int OnFollowEnd    = 8;

    public final static int ActionPressItem  = 0;
    public final static int ActionPressSkill = 1;

    public static int defaultDetectInterval = 1500;

    int sEnabled;
    int sWhen;
    int sWhenValue;
    int sAction;
    int sActionValue;

    public ROJobDescription(int enable, int when, int whenValue, int action, int actionValue) {
        sEnabled = enable;
        sWhen = when;
        sWhenValue = whenValue;
        sAction = action;
        sActionValue = actionValue;
    }

}
