#
# 即將合併 JoshAutomation 2.0
# 可使用強大的 SAPA 功能 客製化屬於自己的腳本需求
#

# JoshAutomationFX
PC version of JoshAutomation compatible with Nox player and other emulators, currently under developing.

## 安裝環境

接下來我要教您如何使用 JoshAutomationFX，這是一個桌面版本的 Java 應用程式，用來作為 Android 模擬器的自動化小工具。
目前版本: 0.50A (Beta)

### 下載 Java 執行環境
首先先到[這裡下載 Java](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

點一下 Accept License Agreement 後，按一下那個 x64.exe 檔案就可以下載了
然後下載完就把它裝一裝，一直往下按就好


### 安裝 ADB
[下載](https://dl.google.com/android/repository/platform-tools-latest-windows.zip)

完成後，解壓縮
```
adb.exe
AdbWinApi.dll
AdbWinUsbApi.dll
```
到 C:\Windows\ 裡面就可以了


### 調整安卓模擬器打開偵錯功能
* 先將解析度調整成 1440x900 或是 1280x720 (以後會支援更多解析度)
* 然後啟動你的模擬器
* 點設定
* 滑到最下面按 關於(平板電腦/手機)
* 一直按版本號碼直到他說你是開發人員
* 返回後找到開發人員選項
* 將 USB偵錯 打勾

## 下載與執行
[下載](https://github.com/josh-hsu/JoshAutomationFX/blob/master/release/JoshAutomationFX.zip?raw=true) -下載最新版

下載完後，解壓縮到任何一個你記得住的地方，點兩下 JoshAutomationFX.jar ，每次開啟會獲取連接的安卓資訊，請稍等

## 介面說明
* 上面的標籤列，可看到連線中的安卓裝置，最多可連五個建議夜神模擬器
* 標籤列內的有三個大項，其中最下方是螢幕截圖，可看到啟動時，他的畫面，用來判斷是哪個模擬器 (要開更多請到問題回報開request)
* 自動技能與物品就是給您調整設定的地方

## 注意事項
* 要修改已經啟動的條件時，必須先取消啟動，修改後再重新啟動
* 技能一與物品一是最右邊那格，往左列推
* 請先將所有要開的模擬器都打開才打開本程式，避免找不到裝置


## 錯誤回報
麻煩遇到問題，可以將你解壓縮後執行檔案的資料夾下會看到 Log/JAFX_日期.txt

把這個寄給我 joshhsu1002@gmail.com
```
標題: [JAFX] 錯誤回報
內容: 
您的暱稱或是聯絡信箱方式，大約陳述您的問題以及發生的時間
```
如果你看得懂英文，也可以直接在
[這裡](https://github.com/josh-hsu/JoshAutomationFX/issues)
開BUG給我，我會更快收到


## 版權聲明
```
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
