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

    // Gauge Type
    public static final int GAUGE_TYPE_HP = 0;
    public static final int GAUGE_TYPE_MP = 1;

    // HP supply gauge point info
    static ScreenCoord pointHPStart = new ScreenCoord(49,138,ScreenPoint.SO_Landscape);
    static ScreenCoord pointHPEnd   = new ScreenCoord(157,138,ScreenPoint.SO_Landscape);
    static ScreenColor pointHPColor = new ScreenColor(137,214,37,0xff);
    static ScreenColor pointHPEmptyColor = new ScreenColor(97,99,101,0xff);

    // MP supply gauge point info
    static ScreenCoord pointMPStart = new ScreenCoord(49,149,ScreenPoint.SO_Landscape);
    static ScreenCoord pointMPEnd   = new ScreenCoord(157,149,ScreenPoint.SO_Landscape);
    static ScreenColor pointMPColor = new ScreenColor(113,145,232,0xff);
    static ScreenColor pointMPEmptyColor = new ScreenColor(97,99,101,0xff);

    /*
     *    Item5   Item4    Item3    Item2    Item1    Auto
     *    Skill6  Skill5   Skill4   Skill3   Skill2   Skill1
     */
    // Item supply points
    static private ScreenCoord pointItem1 = new ScreenCoord(1274, 714, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointItem2 = new ScreenCoord(1166, 714, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointItem3 = new ScreenCoord(1054, 714, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointItem4 = new ScreenCoord(944 , 714, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointItem5 = new ScreenCoord(834 , 714, ScreenPoint.SO_Landscape);
    static ArrayList<ScreenCoord> pointItems = new ArrayList<ScreenCoord>() {{
        add(pointItem1);add(pointItem2);add(pointItem3);add(pointItem4);add(pointItem5);
    }};

    // Skill quick points
    static private ScreenCoord pointSkill1 = new ScreenCoord(1384, 824, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointSkill2 = new ScreenCoord(1274, 824, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointSkill3 = new ScreenCoord(1166, 824, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointSkill4 = new ScreenCoord(1054, 824, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointSkill5 = new ScreenCoord(944,  824, ScreenPoint.SO_Landscape);
    static private ScreenCoord pointSkill6 = new ScreenCoord(834,  824, ScreenPoint.SO_Landscape);
    static ArrayList<ScreenCoord> pointSkills = new ArrayList<ScreenCoord>() {{
        add(pointSkill1);add(pointSkill2);add(pointSkill3);add(pointSkill4);add(pointSkill5);add(pointSkill6);
    }};

    // Follow and auto battle
    static ScreenPoint pointFollowed = new ScreenPoint(255,255,255,255,276,32, ScreenPoint.SO_Landscape);
    static ScreenPoint pointFollowButton = new ScreenPoint(62,87,174,255,191,390, ScreenPoint.SO_Landscape);
    static ScreenPoint pointAutoBattled = new ScreenPoint(100,141,225,255,1351,703, ScreenPoint.SO_Landscape);
    static ScreenPoint pointAutoBattledYellow = new ScreenPoint(255,189,33,255,1351,703, ScreenPoint.SO_Landscape);
    static ScreenPoint pointAutoBattledAllMonster = new ScreenPoint(226,227,226,255,1058,404, ScreenPoint.SO_Landscape);
}

