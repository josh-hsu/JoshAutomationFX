/*
 * Copyright (C) 2017 The Josh Tool Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mumu.libjoshgame;

/*
 * Josh Game Library - Version 1.60
 */
/*
   JoshGameLibrary (GL)
   This game control library require the following initial phase

   JoshGameLibrary mGL;
   mGL = new JoshGameLibrary();                       //create your own instance for each device
   mGL.setDeviceName("device_name");                  //Set current device name (serial) for adb -s deviceName command
   mGL.setPlatform(true);                             //setting to PC mode (required)
   mGL.setGameOrientation(ScreenPoint.SO_Landscape);  //setting game orientation for point check
   mGL.setScreenDimension(1080, 1920);                //setting the dimension of screen for point check
   mGL.setTouchShift(6)                               //setting the touch random shift size (for cheating detection)

   Note: with version 1.30 or higher, all the waiting functions are throwing InterruptExceptions
   Note: JoshGameLibrary support minimal SDK version of Android 7.0, if you are using Android 6.0 or below
         you should see Josh-Tool instead.
   Note: with version 1.50 or higher, it only compatibles with PC mode. This means it should only run by PC
         with devices connected through ADB.
   Note: with version 1.60 or higher, JoshGameLibrary is not singleton anymore to fit in multiple devices
         resolution support.
 */
public class JoshGameLibrary {
    private InputService mInputService;
    private CaptureService mCaptureService;
    private static AdbDeviceManager mAdbDeviceManager;
    private int width, height, orientation;
    private String deviceName;

    public JoshGameLibrary() {
        mCaptureService = new CaptureService();
        mInputService = new InputService(mCaptureService);
        mAdbDeviceManager = AdbDeviceManager.getInstance();
    }

    public void setScreenDimension(int w, int h) {
        width = w;
        height = h;
        mCaptureService.setScreenDimension(w, h);
        mInputService.setScreenDimension(w, h);
    }

    public int setScreenDimensionFromDevice() {
        int[] actualSize;

        if (deviceName != null) {
            actualSize = mAdbDeviceManager.getAdbDeviceResolution(deviceName);
            setScreenDimension(actualSize[0], actualSize[1]);
            return 0;
        }

        return -1;
    }

    public void setGameOrientation(int orientation) {
        this.orientation = orientation;
        mInputService.setGameOrientation(orientation);
        mCaptureService.setScreenOrientation(orientation);
    }

    /*
     * setScreenOffset (added in 1.34)
     * screen offset is used for various height screen, especially for
     * the same set of 1920*1080, 2160*1080, 2240*1080
     * Internal service will only treat this value as portrait orientation
     */
    public void setScreenOffset(int xOffset, int yOffset, int offsetOrientation) {
        if (offsetOrientation == ScreenPoint.SO_Landscape) {
            mInputService.setScreenOffset(yOffset, xOffset);
            mCaptureService.setScreenOffset(yOffset, xOffset);
        } else {
            mInputService.setScreenOffset(xOffset, yOffset);
            mCaptureService.setScreenOffset(xOffset, yOffset);
        }
    }

    public void setAmbiguousRange(int[] range) {
        mCaptureService.setAmbiguousRange(range);
    }

    public void setTouchShift(int ran) {
        mInputService.setTouchShift(ran);
    }

    /*
     * setPlatform (added in 1.50)
     * if PC mode, we use ADB command to retrieve screen color
     */
    public void setPlatform(boolean platformPC) {
        mCaptureService.setPlatformPC(platformPC);
        mInputService.setPlatformPC(platformPC);
    }

    /*
     * setDeviceName (added in 1.60)
     * Set current device name (serial) for adb -s deviceName command
     */
    public void setDeviceName(String dev) {
        deviceName = dev;
        mCaptureService.setDeviceName(dev);
        mInputService.setDeviceName(dev);
    }

    public void setChatty(boolean chatty) {
        mCaptureService.setChatty(chatty);
    }

    public CaptureService getCaptureService() {
        return mCaptureService;
    }

    public InputService getInputService() {
        return mInputService;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getScreenWidth () {
        return width;
    }

    public int getScreenHeight() {
        return height;
    }

    public int getScreenOrientation() {
        return orientation;
    }

    static class GLService {
        /*
         * this eases the pain of accessing AdbDeviceManager for GLServices
         */
        String runCommand(String cmd) {
            return mAdbDeviceManager.runCommand(cmd);
        }

        String runCommand(String cmd, String dev) {
            return mAdbDeviceManager.runCommand(cmd, dev);
        }
    }

}
