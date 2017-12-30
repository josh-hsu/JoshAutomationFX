package com.mumu.joshautomation.ro;

import com.mumu.libjoshgame.Log;
import com.mumu.joshautomation.script.AutoJobEventListener;
import com.mumu.libjoshgame.JoshGameLibrary;
import com.mumu.libjoshgame.ScreenCoord;
import com.mumu.libjoshgame.ScreenPoint;

import static com.mumu.joshautomation.ro.ROJobDescription.*;
import static com.mumu.joshautomation.ro.RORoutineDefinition.*;

/**
 * RO Routine
 * RO game helper functions
 */

class RORoutine {
    private static final String TAG = "RORoutine";
    private JoshGameLibrary mGL;
    private AutoJobEventListener mCallbacks;
    private RORoutineDefinition mROD;

    public RORoutine(JoshGameLibrary gl, AutoJobEventListener el) {
        mGL = gl;
        mCallbacks = el;
        mROD = new RORoutineDefinition(RORoutineDefinition.SUPPORTED_1440x900);
    }

    private void sendMessage(String msg) {
        if (mCallbacks != null)
            mCallbacks.onEventReceived(msg, this);
    }

    private void sendMessageVerbose(String msg) {
        boolean verboseMode = true;
        if (verboseMode)
            sendMessage(msg);
    }

    private void sleep(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    /* --------------
     * Supply routine
     * -------------- */
    private ScreenCoord getTargetGaugeCoord(float percent, int gauge) {
        if (gauge == GAUGE_TYPE_HP) {
            int length = (int)((mROD.pointHPEnd.x - mROD.pointHPStart.x) * percent) / 100;
            return new ScreenCoord(mROD.pointHPStart.x + length, mROD.pointHPStart.y, mROD.pointHPStart.orientation);
        } else if (gauge == GAUGE_TYPE_MP) {
            int length = (int)((mROD.pointMPEnd.x - mROD.pointMPStart.x) * percent) / 100;
            return new ScreenCoord(mROD.pointMPStart.x + length, mROD.pointMPStart.y, mROD.pointMPStart.orientation);
        } else {
            Log.d(TAG, "Unknown gauge type " + gauge);
        }

        return null;
    }

    private boolean isMPLowerThan(float percent) {
        return !isMPHigherThan(percent);
    }

    private boolean isHPLowerThan(float percent) {
        return !isHPHigherThan(percent);
    }

    private boolean isMPHigherThan(float percent) {
        ScreenPoint point = new ScreenPoint(getTargetGaugeCoord(percent, GAUGE_TYPE_MP), mROD.pointMPColor);
        return mGL.getCaptureService().colorIs(point);
    }

    private boolean isHPHigherThan(float percent) {
        ScreenPoint point = new ScreenPoint(getTargetGaugeCoord(percent, GAUGE_TYPE_HP), mROD.pointHPColor);
        return mGL.getCaptureService().colorIs(point);
    }

    public float getCurrentHPValue() {
        return 0.0f;
    }

    public float getCurrentMPValue() {
        return 0.0f;
    }

    public boolean isInBattleMode() {
        return isHPHigherThan(50) || isHPLowerThan(50);
    }

    private void tapOnQuickItem(int index) {
        if (index < 1)
            return;

        ScreenCoord itemCoord = mROD.pointItems.get(index - 1);
        mGL.getInputService().tapOnScreen(itemCoord);
    }

    private void tapOnSkill(int index) {
        if (index < 1)
            return;

        ScreenCoord skillCoord = mROD.pointSkills.get(index - 1);
        mGL.getInputService().tapOnScreen(skillCoord);
    }

    private void tapOnRandomSpot(int radius) {
        int center_x = mGL.getScreenWidth() / 2;
        int center_y = mGL.getScreenHeight() / 2;
        int x_shift = (int) (Math.random() * radius) - radius/2;
        int y_shift = (int) (Math.random() * radius) - radius/2;
        ScreenCoord tapPoint = new ScreenCoord(center_x + x_shift, center_y + y_shift, mGL.getScreenOrientation());
        mGL.getInputService().tapOnScreen(tapPoint);
    }

    boolean checkBattleSupply(int type, int typeValue) {
        // check on character HP and MP
        switch (type) {
            case OnHPLessThan:
                if (isHPLowerThan((float)typeValue))
                    return true;
                break;
            case OnHPHigherThan:
                if (isHPHigherThan((float)typeValue))
                    return true;
                break;
            case OnMPLessThan:
                if (isMPLowerThan((float)typeValue))
                    return true;
                break;
            case OnMPHigherThan:
                if (isMPHigherThan((float)typeValue))
                    return true;
                break;
            default:
                Log.d(TAG, "Unknown supply type: " + type);
        }

        return false;
    }

    void executeAction(int action, int actionValue) {
        switch (action) {
            case ActionPressItem:
                tapOnQuickItem(actionValue);
                break;
            case ActionPressSkill:
                tapOnSkill(actionValue);
                break;
            case ActionMoveAStep:
                tapOnRandomSpot(80);
                break;
            default:
                Log.d(TAG, "Unknown supply action: " + action);
        }
    }

    boolean isAutoBattleEnabled() {
        return mGL.getCaptureService().colorIs(mROD.pointAutoBattled) ||
                mGL.getCaptureService().colorIs(mROD.pointAutoBattledYellow);
    }

    boolean isFollowingFirst() {
        return mGL.getCaptureService().colorIs(mROD.pointFollowed);
    }

    void tryAutoBattle() throws InterruptedException {
        if (!isAutoBattleEnabled()) {
            mGL.getInputService().tapOnScreen(mROD.pointAutoBattled.coord);
            sleep(1000);
            if (isAutoBattleEnabled()) {
                Log.d(TAG, "This char is following someone, so tap auto battle end.");
            } else {
                if (mGL.getCaptureService().colorIs(mROD.pointAutoBattledAllMonster)) {
                    mGL.getInputService().tapOnScreen(mROD.pointAutoBattledAllMonster.coord);
                }
            }
        }
    }

    void tryFollowingFirst() throws InterruptedException {
        if (!isFollowingFirst()) {
            mGL.getInputService().tapOnScreen(mROD.pointFollowed.coord);
            sleep(1000);
            if (!isFollowingFirst() && mGL.getCaptureService().colorIs(mROD.pointFollowButton)) {
                mGL.getInputService().tapOnScreen(mROD.pointFollowButton.coord);
            }
        }
    }
}
