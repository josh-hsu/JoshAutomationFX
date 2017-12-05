package com.mumu.joshautomation.ro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

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
    public final static int OnServerDisconnected = 9;
    private final static int OnDefaultState      = 99;

    private final static String StringOnHPLessThan   = "當 HP 小於";
    private final static String StringOnHPHigherThan = "當 HP 大於";
    private final static String StringOnMPLessThan   = "當 SP 小於";
    private final static String StringOnMPHigherThan = "當 SP 大於";
    private final static String StringOnPeriod       = "定時";
    private final static String StringOnAutoStart    = "當開始自動戰鬥(尚未開放)";
    private final static String StringOnAutoEnd      = "當自動戰鬥結束(尚未開放)";
    private final static String StringOnFollowStart  = "當開始跟隨(尚未開放)";
    private final static String StringOnFollowEnd    = "當結束跟隨(尚未開放)";
    private final static String StringOnServerDisconnected = "當伺服器斷線(尚未開放)";
    private static ArrayList<String> stringOnWhenList = new ArrayList<String>() {{
        add(StringOnHPLessThan);add(StringOnHPHigherThan);add(StringOnMPLessThan);add(StringOnMPHigherThan);add(StringOnPeriod);
        add(StringOnAutoStart);add(StringOnAutoEnd);add(StringOnFollowStart);add(StringOnFollowEnd);add(StringOnServerDisconnected);
    }};

    private static final int NumberOfItem  = 5;
    private static final int NumberOfSkill = 6;

    public final static int ActionPressItem   = 0;
    public final static int ActionPressSkill  = 1;
    public final static int ActionMoveAStep   = 2;
    public final static int ActionMakeSureFollow   = 2;
    private final static int ActionDefault    = 99;

    private final static String StringActionPressItem  = "使用物品";
    private final static String StringActionPressSkill = "使用技能";
    private final static String StringActionMoveAStep  = "移動一步";
    private static ArrayList<String> stringActionList = new ArrayList<String>() {{
        for (int i = 0; i < NumberOfItem; i ++) {
            add(StringActionPressItem + " " + (i+1));
        }
        for (int i = 0; i < NumberOfSkill; i++) {
            add(StringActionPressSkill + " " + (i+1));
        }
        add(StringActionMoveAStep);
    }};

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

    public ROJobDescription() {
        sEnabled = Disable;
        sWhen = OnDefaultState;
        sWhenValue = 0;
        sAction = ActionDefault;
        sActionValue = -1;
    }

    public String toString() {
        return "Enable: " + sEnabled + " when: " + sWhen + " whenValue: " + sWhenValue +
                " action: " + sAction + " actionValue: " + sActionValue;
    }

    public static ObservableList<String> getWhenList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(stringOnWhenList);
        return list;
    }

    public static ObservableList<String> getActionList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(stringActionList);
        return list;
    }

    public static String getWhenString(int index) {
        return stringOnWhenList.get(index);
    }

}
