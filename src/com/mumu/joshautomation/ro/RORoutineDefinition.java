package com.mumu.joshautomation.ro;

import com.mumu.libjoshgame.ScreenColor;
import com.mumu.libjoshgame.ScreenCoord;
import com.mumu.libjoshgame.ScreenPoint;

import java.util.ArrayList;

/**
 * RO Routine Definition
 * Sample screen color and coordination for 1080p screen
 */

public class RORoutineDefinition {

    // Supported variant (read only)
    public static final int UNSUPPORT_800x600 = 0;
    public static final int SUPPORTED_1280x720 = 1;
    public static final int SUPPORTED_1440x900 = 2;
    public static final int UNSUPPORT_1920x1080 = 3;
    private int mCurrentPlan = SUPPORTED_1440x900; //default plan

    // Gauge Type
    public static final int GAUGE_TYPE_HP = 0;
    public static final int GAUGE_TYPE_MP = 1;

    // HP supply gauge point info
    public ScreenCoord pointHPStart;
    public ScreenCoord pointHPEnd;
    public ScreenColor pointHPColor;

    // MP supply gauge point info
    public ScreenCoord pointMPStart;
    public ScreenCoord pointMPEnd;
    public ScreenColor pointMPColor;

    /*
     *    Item5   Item4    Item3    Item2    Item1    Auto
     *    Skill6  Skill5   Skill4   Skill3   Skill2   Skill1
     */
    // Item supply points
    private ScreenCoord pointItem1;
    private ScreenCoord pointItem2;
    private ScreenCoord pointItem3;
    private ScreenCoord pointItem4;
    private ScreenCoord pointItem5;
    public ArrayList<ScreenCoord> pointItems;

    // Skill quick points
    private ScreenCoord pointSkill1 = new ScreenCoord(1384, 824, ScreenPoint.SO_Landscape);
    private ScreenCoord pointSkill2 = new ScreenCoord(1274, 824, ScreenPoint.SO_Landscape);
    private ScreenCoord pointSkill3 = new ScreenCoord(1166, 824, ScreenPoint.SO_Landscape);
    private ScreenCoord pointSkill4 = new ScreenCoord(1054, 824, ScreenPoint.SO_Landscape);
    private ScreenCoord pointSkill5 = new ScreenCoord(944,  824, ScreenPoint.SO_Landscape);
    private ScreenCoord pointSkill6 = new ScreenCoord(834,  824, ScreenPoint.SO_Landscape);
    public ArrayList<ScreenCoord> pointSkills;

    // Follow and auto battle
    public ScreenPoint pointFollowed;
    public ScreenPoint pointFollowButton;
    public ScreenPoint pointAutoBattled;
    public ScreenPoint pointAutoBattledYellow;
    public ScreenPoint pointAutoBattledAllMonster;

    //
    // Definition constructor
    //
    public RORoutineDefinition(int selectPlan) {
        setPlan(selectPlan);
        applyPlan();
    }

    public void setPlan(int plan) {
        mCurrentPlan = plan;
    }

    public void applyPlan() {
        switch (mCurrentPlan) {
            case SUPPORTED_1280x720:
                restoreResolution1280x720();
                break;
            case SUPPORTED_1440x900:
            default:
                restoreResolution1440x900();
                break;
        }
    }

    /*
     * Resolution: 1440 x 900
     */
    private void restoreResolution1440x900() {
        // HP supply gauge point info
        pointHPStart = new ScreenCoord(49,138,ScreenPoint.SO_Landscape);
        pointHPEnd   = new ScreenCoord(157,138,ScreenPoint.SO_Landscape);
        pointHPColor = new ScreenColor(137,214,37,0xff);

        // MP supply gauge point info
        pointMPStart = new ScreenCoord(49,149,ScreenPoint.SO_Landscape);
        pointMPEnd   = new ScreenCoord(157,149,ScreenPoint.SO_Landscape);
        pointMPColor = new ScreenColor(113,145,232,0xff);

        // Item supply points
        pointItem1 = new ScreenCoord(1274, 714, ScreenPoint.SO_Landscape);
        pointItem2 = new ScreenCoord(1166, 714, ScreenPoint.SO_Landscape);
        pointItem3 = new ScreenCoord(1054, 714, ScreenPoint.SO_Landscape);
        pointItem4 = new ScreenCoord(944 , 714, ScreenPoint.SO_Landscape);
        pointItem5 = new ScreenCoord(834 , 714, ScreenPoint.SO_Landscape);
        pointItems = new ArrayList<ScreenCoord>() {{
            add(pointItem1);add(pointItem2);add(pointItem3);add(pointItem4);add(pointItem5);
        }};

        // Skill quick points
        pointSkill1 = new ScreenCoord(1384, 824, ScreenPoint.SO_Landscape);
        pointSkill2 = new ScreenCoord(1274, 824, ScreenPoint.SO_Landscape);
        pointSkill3 = new ScreenCoord(1166, 824, ScreenPoint.SO_Landscape);
        pointSkill4 = new ScreenCoord(1054, 824, ScreenPoint.SO_Landscape);
        pointSkill5 = new ScreenCoord(944,  824, ScreenPoint.SO_Landscape);
        pointSkill6 = new ScreenCoord(834,  824, ScreenPoint.SO_Landscape);
        pointSkills = new ArrayList<ScreenCoord>() {{
            add(pointSkill1);add(pointSkill2);add(pointSkill3);add(pointSkill4);add(pointSkill5);add(pointSkill6);
        }};

        // Follow and auto battle
        pointFollowed = new ScreenPoint(255,255,255,255,276,32, ScreenPoint.SO_Landscape);
        pointFollowButton = new ScreenPoint(62,87,174,255,191,390, ScreenPoint.SO_Landscape);
        pointAutoBattled = new ScreenPoint(100,141,225,255,1351,703, ScreenPoint.SO_Landscape);
        pointAutoBattledYellow = new ScreenPoint(255,189,33,255,1351,703, ScreenPoint.SO_Landscape);
        pointAutoBattledAllMonster = new ScreenPoint(226,227,226,255,1058,404, ScreenPoint.SO_Landscape);
    }

    /*
     * Resolution 1280 x 720
     */
    private void restoreResolution1280x720() {
        // HP supply gauge point info
        pointHPStart = new ScreenCoord(49,138,ScreenPoint.SO_Landscape);
        pointHPEnd   = new ScreenCoord(157,138,ScreenPoint.SO_Landscape);
        pointHPColor = new ScreenColor(137,214,37,0xff);

        // MP supply gauge point info
        pointMPStart = new ScreenCoord(49,149,ScreenPoint.SO_Landscape);
        pointMPEnd   = new ScreenCoord(157,149,ScreenPoint.SO_Landscape);
        pointMPColor = new ScreenColor(113,145,232,0xff);

        // Item supply points
        pointItem1 = new ScreenCoord(1274, 714, ScreenPoint.SO_Landscape);
        pointItem2 = new ScreenCoord(1166, 714, ScreenPoint.SO_Landscape);
        pointItem3 = new ScreenCoord(1054, 714, ScreenPoint.SO_Landscape);
        pointItem4 = new ScreenCoord(944 , 714, ScreenPoint.SO_Landscape);
        pointItem5 = new ScreenCoord(834 , 714, ScreenPoint.SO_Landscape);
        pointItems = new ArrayList<ScreenCoord>() {{
            add(pointItem1);add(pointItem2);add(pointItem3);add(pointItem4);add(pointItem5);
        }};

        // Skill quick points
        pointSkill1 = new ScreenCoord(1384, 824, ScreenPoint.SO_Landscape);
        pointSkill2 = new ScreenCoord(1274, 824, ScreenPoint.SO_Landscape);
        pointSkill3 = new ScreenCoord(1166, 824, ScreenPoint.SO_Landscape);
        pointSkill4 = new ScreenCoord(1054, 824, ScreenPoint.SO_Landscape);
        pointSkill5 = new ScreenCoord(944,  824, ScreenPoint.SO_Landscape);
        pointSkill6 = new ScreenCoord(834,  824, ScreenPoint.SO_Landscape);
        pointSkills = new ArrayList<ScreenCoord>() {{
            add(pointSkill1);add(pointSkill2);add(pointSkill3);add(pointSkill4);add(pointSkill5);add(pointSkill6);
        }};

        // Follow and auto battle
        pointFollowed = new ScreenPoint(255,255,255,255,276,32, ScreenPoint.SO_Landscape);
        pointFollowButton = new ScreenPoint(62,87,174,255,191,390, ScreenPoint.SO_Landscape);
        pointAutoBattled = new ScreenPoint(100,141,225,255,1351,703, ScreenPoint.SO_Landscape);
        pointAutoBattledYellow = new ScreenPoint(255,189,33,255,1351,703, ScreenPoint.SO_Landscape);
        pointAutoBattledAllMonster = new ScreenPoint(226,227,226,255,1058,404, ScreenPoint.SO_Landscape);
    }
}

