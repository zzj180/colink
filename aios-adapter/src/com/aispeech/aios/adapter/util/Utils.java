package com.aispeech.aios.adapter.util;

/*
 * Copyright (C) 2011 The Android Open Source Project
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


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.util.Log;
import android.bluetooth.BluetoothDevice;
import android.app.KeyguardManager;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.os.ParcelUuid;

/**
 * Utils is a helper class that contains constants for various Android resource
 * IDs, debug logging flags, and static methods for creating dialogs.
 */
public class Utils {
    public static final boolean V = true; // verbose logging
    public static final boolean D = true; // regular logging

	static final int BD_ADDR_LEN = 6; // bytes
	static final int BD_UUID_LEN = 16; // bytes	

    private static final String KEY_INPUT_VIEW_X = "KEY_INPUT_VIEW_X";
    private static final String KEY_INPUT_VIEW_Y = "KEY_INPUT_VIEW_Y";
     private static final String SP_NAME = "float_window_settings";

    private Utils() {
    }






}
