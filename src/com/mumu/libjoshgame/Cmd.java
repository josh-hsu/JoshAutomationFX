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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Cmd {
    private final String TAG = "Cmd";
    private static Cmd mThis;
    private static String mAdbDefaultIP = "127.0.0.1";
    private String[] mAdbDevicePort = {"62001", "62025", "62026", "62027", "62028", "62029"};

    private Cmd() {
        tryConnect();
    }

    public static Cmd getInstance() {
        if (mThis == null)
            mThis = new Cmd();
        return mThis;
    }

    public String runCommand(String cmd) {
        return runCommand(cmd, null);
    }

    public String runCommand(String cmd, String device) {
        String fullCmd;
        String result = "";

        if (device != null)
            fullCmd = "-s " + device + " shell " + cmd;
        else
            fullCmd = " shell " + cmd;

        try {
            result = runAdbCommandInternal(fullCmd);
        } catch (Exception e) {
            Log.e(TAG, "Run adb command failed.");
        }
        return result;
    }

    private void tryConnect() {
        Log.d(TAG, "try connect");
        for (String port : mAdbDevicePort) {
            String cmd = "connect " + mAdbDefaultIP + ":" + port;
            try {
                runAdbCommandInternal(cmd);
            } catch (Exception e) {
                Log.w(TAG, "Adb connect failed: " + cmd);
            }
        }
    }

    public ArrayList<String> getAdbDevices() {
        ArrayList<String> resultList = new ArrayList<>();

        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec("adb devices");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            // read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null) {
                String[] parsedDevice = s.split("\t");
                if (parsedDevice.length == 2 && parsedDevice[1].equals("device"))
                    resultList.add(parsedDevice[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "adb devices: " + resultList.size());
        return resultList;

    }

    public void pullAdbFile(String remote, String destination, String device) {
        try {
            runAdbCommandInternal("-s " + device + " pull " + remote + " " + destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String runSystemCommand(String cmd) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec(cmd);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            // read the output from the command
            String s, result = "";
            while ((s = stdInput.readLine()) != null) {
                result += s;
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                Log.w(TAG, "Fail to execute command " + cmd + ": " + s);
            }

            return result;

        } catch (Exception e) {
            Log.e(TAG, "command error");
        }

        return "";
    }

    private String runAdbCommandInternal(String cmd) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec("adb " + cmd);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));

        // read the output from the command
        String s, result = "";
        while ((s = stdInput.readLine()) != null) {
            result += s;
        }

        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            Log.w(TAG, "Fail to execute command " + cmd + ": " + s);
        }

        return result;
    }
}
