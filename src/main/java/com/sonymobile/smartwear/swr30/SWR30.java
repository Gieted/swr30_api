/*
 * Copyright (C) 2014 Sony Mobile Communications Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names
 *    of its contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sonymobile.smartwear.swr30;


import android.text.format.DateUtils;

/**
 * Constants for configuration and registration, see <a href="package-summary.html#ServiceRegistration">Service registration</a>, <a href="package-summary.html#ActivityRegistration">Control activity registration</a>, <a href="package-summary.html#ControlRegistration">Control registration</a>.
 */
public final class SWR30 {

    /**
     * Intent action used to expose an {@link com.sonymobile.smartwear.swr30.ExtensionService} that the host application can bind to, see <a href="package-summary.html#ServiceRegistration">Service registration</a>.
     *
     * @since 1
     */
    public static final String ACTION_BIND = "com.sonymobile.smartwear.swr30.BIND";

    /**
     * Intent action used in the Intent filter inside the service tag in your Android Manifest that declares the {@link com.sonymobile.smartwear.swr30.ExtensionService}.
     * This only needed if callback {@link ExtensionService#onConnectionStatus(int, String)} is desired.
     * @since 1
     */
    public static final String ACTION_ACCESSORY_CONNECTION_STATUS = "com.sonymobile.smartwear.swr30.ACCESSORY_CONNECTION_STATUS";

    /**
     * Intent action used to launch the control activity, see also: <a href="package-summary.html#ActivityRegistration">Control activity registration</a>.
     *
     *
     * <p>
     *     Intent-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_NAME}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     *
     * @since 1
     */
    public static final String ACTION_CONFIGURE_CONTROL = "com.sonymobile.smartwear.swr30.CONFIGURE_CONTROL";

    /**
     * Meta data used to indicate the XML config file in which the control implemented by your application is defined.
     * This is used inside the service tag of the Android Manifest for the service that shall receive messages from the host application, see:
     * <a href="package-summary.html#ServiceRegistration">Service registration</a>.
     *
     * @since 1
     */
    public static final String META_DATA_XML_CONFIGURATION = "com.sonymobile.smartwear.swr30.configuration";

    /**
     * Meta data to connect a {@link android.app.Activity} with a {@link com.sonymobile.smartwear.swr30.Control}, used in the Android Manifest inside the activity tag, see: <a href="package-summary.html#ActivityRegistration">Control activity registration</a>.
     * Note that the value must match a <a href="R.attr.html#swr30ControlName">swr30ControlName</a> in the xml config file, see {@link com.sonymobile.smartwear.swr30.SWR30#META_DATA_XML_CONFIGURATION}.
     *
     * @since 1
     */
    public static final String META_DATA_CONTROL_NAME = "com.sonymobile.smartwear.swr30.control_name";

    /**
     * @see Control#requestStop()
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     *
     * @since 1
     */
    static final int MSG_STOP_REQUEST = 1;

    /**
     * @see ExtensionService#onConnectionStatus(int, String)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_ACCESSORY_CONNECTION_STATUS}</li>
     *     <li>{@link #EXTRA_ACCESSORY_BLUETOOTH_ADDRESS}</li>
     * </ul>
     * </p>
     * @since 1
     */
    static final int MSG_ACCESSORY_CONNECTION_STATUS = 2;

    /**
     * @see Control#onStop()
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     *
     * @since 1
     */
    static final int MSG_STOP = 3;

    /**
     * @see Control#onStart()
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_NAME}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     *
     * @since 1
     */
    static final int MSG_START = 4;

    /**
     * @see Control#setAutoStopMode(int)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_AUTO_STOP_MODE}</li>
     * </ul>
     *
     * @since 1
     */
    static final int MSG_SET_AUTO_STOP_MODE = 5;

    /**
     * @see Control#vibrateStart(int, int, int)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_VIBRATE_ON_DURATION}</li>
     *     <li>{@link #EXTRA_VIBRATE_OFF_DURATION}</li>
     *     <li>{@link #EXTRA_VIBRATE_REPEATS}</li>
     * </ul>
     * </p>
     * @since 1
     */
    static final int MSG_VIBRATE_START = 6;

    /**
     * @see Control#vibrateStop()
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * @since 1
     */
    static final int MSG_VIBRATE_STOP = 7;

    /**
     * @see Control#showImage(int)
     * @see Control#showImage(android.net.Uri)
     * @see Control#showImage(android.graphics.Bitmap)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_DISPLAY_DATA_URI}</li>
     *     <li>{@link #EXTRA_DISPLAY_DATA_RAW}</li>
     * </ul>
     * @since 1
     */
    static final int MSG_DISPLAY_UPDATE = 8;

    /**
     * @see Control#onTap(long)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_TIMESTAMP}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_TAP_EVENT = 9;

    /**
     * @see Control#requestBatteryStatus()
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     * @since 1
     */
    static final int MSG_REQUEST_BATTERY_STATUS = 10;

    /**
     * @see Control#onBatteryStatus(boolean, int)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_BATTERY_IS_CHARGING}</li>
     *     <li>{@link #EXTRA_BATTERY_LEVEL}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_BATTERY_STATUS = 11;

    /**
     * @see Control#onAdded()
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_NAME}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_ADDED = 12;

    /**
     * @see Control#onRemoved()
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_NAME}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_REMOVED = 13;

    /**
     * @see Control#onError(int, String)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_ERROR_CODE}</li>
     *     <li>{@link #EXTRA_ERROR_DEBUG_MESSAGE}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_ERROR = 15;

    /**
     * @see Control#setSmartAlarm(long)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_ALARM_TIME_LATEST}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_SET_SMART_ALARM = 16;

    /**
     * @see Control#onSetAlarmResult(int)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_SET_ALARM_RESULT}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_SET_SMART_ALARM_RESULT = 17;

    /**
     * @see Control#onKey(int, long)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_KEY_EVENT}</li>
     *     <li>{@link #EXTRA_TIMESTAMP}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_KEY_EVENT = 18;


    /**
     * @see Control#getStartImage() (int, long)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_NAME}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_GET_START_IMAGE = 19;

    /**
     * @see Control#getStartImage() (int, long)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_START_IMAGE_DATA_RAW}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_UPDATE_START_IMAGE = 20;

    /**
     * @see Control#getPreviewImage() (int, long)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_NAME}</li>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_GET_PREVIEW_IMAGE = 21;

    /**
     * @see Control#getPreviewImage() (int, long)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_PREVIEW_IMAGE_DATA_RAW}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_UPDATE_PREVIEW_IMAGE = 22;

    /**
     * @see Control#makeCall(android.net.Uri)
     *
     * <p>
     * Message-extra data:
     * </p>
     * <ul>
     *     <li>{@link #EXTRA_CONTROL_INSTANCE_ID}</li>
     *     <li>{@link #EXTRA_MAKE_CALL_URI}</li>
     * </ul>
     * </p>
     *
     * @since 1
     */
    static final int MSG_MAKE_CALL = 23;

    /**
     * Message-extra that indicates the result of {@link SWR30#MSG_SET_SMART_ALARM} .
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     * <p>
     * ALLOWED VALUES:
     * Any value defined in {@link com.sonymobile.smartwear.swr30.Control.AlarmResult}.
     * </p>
     * @since 1
     */
    static final String EXTRA_SET_ALARM_RESULT = "set_alarm_result";

    /**
     * Message-extra that contains error code. Used in {@link SWR30#MSG_ERROR}.
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     * <p>
     * ALLOWED VALUES:
     * Any value defined in {@link Control.Error}.
     * </p>
     * @since 1
     */
    static final String EXTRA_ERROR_CODE = "error_code";

    /**
     * Message-extra that contains status of the connection between the host application and the accessory.
     * Used in {@link SWR30#MSG_ACCESSORY_CONNECTION_STATUS}.
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     * <p>
     * ALLOWED VALUES:
     * Any value defined in {@link com.sonymobile.smartwear.swr30.ExtensionService.AccessoryConnectionStatus}.
     * </p>
     * @since 1
     */
    static final String EXTRA_ACCESSORY_CONNECTION_STATUS = "accessory_connection_status";

    /**
     * Message-extra that contains Bluetooth address of the accessory.
     * Used in {@link SWR30#MSG_ACCESSORY_CONNECTION_STATUS}.
     * <p>
     * TYPE: TEXT
     * </p>
     * @since 1
     */
    static final String EXTRA_ACCESSORY_BLUETOOTH_ADDRESS = "accessory_bluetooth_address";

    /**
     * Message-extra that contains the error message. NOTE: This is not a localized message, and shall NOT be displayed to the user.<br>
     * Example:
     * <pre>
     *     Missing permission: com.sonymobile.smartwear.swr30.PERMISSION_SET_SMART_ALARM
     * </pre>
     * <p>
     *     TYPE: TEXT
     * </p>
     * </p>
     * @since 1
     */
    static final String EXTRA_ERROR_DEBUG_MESSAGE = "error_debug_message";

    /**
     * The name of the Message-extra carrying the latest time at which to wake the user, used in {@link SWR30#MSG_SET_SMART_ALARM} <br>
     * The alarm time is in {@link System#currentTimeMillis()} (wall clock time in UTC). <br>
     * If the user is sleeping lightly the alarm will trigger slightly earlier...
     *
     * <p>
     * TYPE: INTEGER (long)
     * </p>
     * <p>
     * ALLOWED VALUES:
     * Any value in milliseconds larger than {@link System#currentTimeMillis()} + {@link DateUtils#HOUR_IN_MILLIS}, but less than {@link System#currentTimeMillis()} + 24 * {@link DateUtils#HOUR_IN_MILLIS}.
     * </p>
     * @since 1
     */
    static final String EXTRA_ALARM_TIME_LATEST = "alarm_time_latest";


    /** Used in {@link SWR30#MSG_MAKE_CALL}.
     *
     * <p>
     * TYPE: TEXT
     * </p>
     * @since 1
     */
    static final String EXTRA_MAKE_CALL_URI = "make_call_uri";

    /**
     * Extra that contains a control name.
     * Corresponds to the <a href="R.attr.html#swr30ControlName">swr30ControlName</a> attribute of the control.
     *
     *
     * <p>
     *     TYPE: TEXT
     * </p>
     * @since 1
     */
    public static final String EXTRA_CONTROL_NAME = "control_name";

    /**
     * Extra that contains a control instance ID.
     * Multiple instances of a {@link com.sonymobile.smartwear.swr30.Control} can exist, (see <a href="package-summary.html#ControlRegistration">Control registration</a>)
     * each with a unique control instance ID. The ID is assigned by the host application when control is added by the user in the host application UI,
     * see {@link com.sonymobile.smartwear.swr30.SWR30#ACTION_CONFIGURE_CONTROL} and {@link Control#onAdded()}.
     *
     * <p>
     *     TYPE: TEXT
     * </p>
     * @since 1
     */
    public static final String EXTRA_CONTROL_INSTANCE_ID = "control_instance_id";

    /**
     * The name of the Message-extra carrying the requested control stop mode
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     * <p>
     * ALLOWED VALUES:
     * Any value defined in {@link com.sonymobile.smartwear.swr30.Control.AutoStopMode}.
     * </p>
     * @since 1
     */
    static final String EXTRA_AUTO_STOP_MODE = "auto_stop_mode";

    /**
     * The name of the Message-extra carrying the "on" duration in milliseconds
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     * @since 1
     */
    static final String EXTRA_VIBRATE_ON_DURATION = "vibrate_on_duration";

    /**
     * The name of the Message-extra carrying the "off" duration in milliseconds
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     * @since 1
     */
    static final String EXTRA_VIBRATE_OFF_DURATION = "vibrate_off_duration";

    /**
     * The name of the Message-extra carrying the number of repeats of the on/off pattern.
     * Note: the value {@link com.sonymobile.smartwear.swr30.Control.Vibrator#VIBRATE_REPEAT_UNTIL_STOP} means that the on/off pattern is repeated until
     * the {@link #MSG_VIBRATE_STOP} message is received. A value of 0 means that no vibration will be performed.
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     *
     * @since 1
     */
    static final String EXTRA_VIBRATE_REPEATS = "vibrate_repeats";

    /**
     * The name of the Message-extra used to identify the data to be displayed on the accessory
     * display. This Message-extra should be used if the image is in raw data (e.g. an array of bytes).
     * The image should be pure black and white with dimensions of either 296*128 or 128*296, see {@link Control#createBitmap(boolean)} and {@link Control#showImage(android.graphics.Bitmap)}.
     * <p>
     * TYPE: BYTE ARRAY
     * </p>
     * @since 1
     */
    static final String EXTRA_DISPLAY_DATA_RAW = "display_data_raw";

    /**
     * The name of the Message-extra used to identify the URI of the image to be displayed on the accessory display.
     * If the URI points out a drawable resource, the resource should be placed in res/drawable-nodpi folder,
     * to avoid scaling (which would be based on the Android device screen dpi if in e.g. res/drawable-xhdpi folder).
     * The image should be pure black and white with dimensions of either 296*128 or 128*296.
     * If the image is in raw data (e.g. an array of bytes) use
     * {@link #EXTRA_DISPLAY_DATA_RAW} instead.
     * <p>
     * TYPE: TEXT
     * </p>
     * @since 1
     */
    static final String EXTRA_DISPLAY_DATA_URI = "display_data_uri";


    /**
     * The name of the Message-extra used to identify the start image.
     * <p>
     * TYPE: BYTE ARRAY
     * </p>
     * @since 1
     */
    static final String EXTRA_START_IMAGE_DATA_RAW = "start_image_data_raw";

    /**
     * The name of the Message-extra used to identify the preview image.
     * <p>
     * TYPE: BYTE ARRAY
     * </p>
     * @since 1
     */
    static final String EXTRA_PREVIEW_IMAGE_DATA_RAW = "preview_image_data_raw";

    /**
     * The name of the Message-extra used to carry the time stamp of the key or tap event
     * <p>
     * TYPE: INTEGER (long)
     * </p>
     * @since 1
     */
    static final String EXTRA_TIMESTAMP = "timestamp";

    /**
     * The name of the Message-extra used to identify the type of key event.
     * <p>
     * TYPE: INTEGER (int)
     * </p>
     * <p>
     * ALLOWED VALUES:
     * Any key event defined in {@link com.sonymobile.smartwear.swr30.Control.KeyEvent}.
     * </p>
     *
     * @since 1
     */
    static final String EXTRA_KEY_EVENT = "key_event";

    /**
     * Current battery level as a percentage.
     * <p>
     * This Message-extra is used with {@link #MSG_BATTERY_STATUS}.
     * TYPE: Integer
     * </p>
     *
     * @since 1
     */
    static final String EXTRA_BATTERY_LEVEL = "battery_Level";

    /**
     * Indicates if the battery is being charged or not.
     * <p>
     * This Message-extra is used with {@link #MSG_BATTERY_STATUS}.
     * </p>
     * <p>
     * VALID VALUES: The value can be true (charging) or false.
     * </p>
     * <p>
     * TYPE: BOOLEAN
     * </p>
     *
     * @since 1
     */
    static final String EXTRA_BATTERY_IS_CHARGING = "battery_is_charging";

    /**
     * Constants that define the various permissions used to protect the API.
     */
    public interface Permission {
        /**
         * This permission is used and defined by the host application.
         * You can add this to service and activity elements in your manifest that is related to SWR30, see <a href="package-summary.html#ServiceRegistration">Service registration</a> and <a href="package-summary.html#ActivityRegistration">Control activity registration</a>.
         * @since 1
         */
        String PERMISSION_HOSTAPP = "com.sonymobile.smartwear.swr30.PERMISSION_HOSTAPP";

        /**
         * This permission is required by all applications using this API, and grants access to draw to the display, listen to taps etc.
         * <p>
         * Example: &lt;uses-permission android:name={@value com.sonymobile.smartwear.swr30.SWR30.Permission#PERMISSION_CONTROL_EXTENSION}/&gt;
         * </p>
         *
         * @since 1
         */
        String PERMISSION_CONTROL_EXTENSION = "com.sonymobile.smartwear.swr30.PERMISSION_CONTROL_EXTENSION";

        /**
         * Allows an application to set a smart alarm {@link com.sonymobile.smartwear.swr30.Control#setSmartAlarm(long)}.
         * <p>
         * Example: &lt;uses-permission android:name={@value com.sonymobile.smartwear.swr30.SWR30.Permission#PERMISSION_SET_SMART_ALARM}/&gt;
         * </p>
         *
         * @since 1
         */
        String PERMISSION_SET_SMART_ALARM = "com.sonymobile.smartwear.swr30.PERMISSION_SET_SMART_ALARM";
    }

    /**
     * Constants definitions for {@link com.sonymobile.smartwear.swr30.Control.KeyEvent}
     */
    interface KeyEvent {
        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.KeyEvent#KEY_VOLUME_UP}
         *
         * @since 1
         */
        int KEY_VOLUME_UP = 0;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.KeyEvent#KEY_VOLUME_DOWN}
         *
         * @since 1
         */
        int KEY_VOLUME_DOWN = 1;

    }

    /**
     * Constants definitions for {@link com.sonymobile.smartwear.swr30.Control.AutoStopMode}
     */
    interface AutoStopMode {

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.AutoStopMode#AUTO_STOP_OFF}
         *
         * @since 1
         */
        int AUTO_STOP_OFF = 0;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.AutoStopMode#AUTO_STOP_ON}
         *
         * @since 1
         */
        int AUTO_STOP_ON = 1;
    }

    /**
     * Constants definitions for {@link com.sonymobile.smartwear.swr30.Control.Vibrator}
     */
    interface Vibrator {

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.Vibrator#VIBRATE_REPEAT_UNTIL_STOP}
         *
         * @since 1
         */
        int VIBRATE_REPEAT_UNTIL_STOP = -1;
    }

    /**
     * Constants definitions for {@link com.sonymobile.smartwear.swr30.Control.Error}
     */
    interface Error {

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.Error#ERROR_MISSING_PERMISSION}
         *
         * @since 1
         */
        int ERROR_MISSING_PERMISSION = 1;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.Error#ERROR_NOT_STARTED}
         *
         * @since 1
         */
        int ERROR_NOT_STARTED = 2;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.Error#ERROR_MAKE_CALL_EMERGENCY_NUMBER_NOT_ALLOWED}
         *
         * @since 1
         */
        int ERROR_MAKE_CALL_EMERGENCY_NUMBER_NOT_ALLOWED = 3;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.Error#ERROR_MAKE_CALL_INVALID_URI}
         *
         * @since 1
         */
        int ERROR_MAKE_CALL_INVALID_URI = 4;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.Error#ERROR_MAKE_CALL_UNKNOWN_ERROR}
         *
         * @since 1
         */
        int ERROR_MAKE_CALL_UNKNOWN_ERROR = 5;
    }

    /**
     * Constants definitions for {@link com.sonymobile.smartwear.swr30.Control.AlarmResult}
     */
    interface AlarmResult {

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.AlarmResult#SET_ALARM_SUCCESS}
         *
         * @since 1
         */
        int SET_ALARM_SUCCESS = 1;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.AlarmResult#SET_ALARM_FAILED_TOO_EARLY}
         *
         * @since 1
         */
        int SET_ALARM_FAILED_TOO_EARLY = 2;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.AlarmResult#SET_ALARM_FAILED_TOO_LATE}
         *
         * @since 1
         */
        int SET_ALARM_FAILED_TOO_LATE = 3;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.AlarmResult#SET_ALARM_FAILED_CONFLICT}
         *
         * @since 1
         */
        int SET_ALARM_FAILED_CONFLICT = 4;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.AlarmResult#SET_ALARM_FAILED_FULL}
         *
         * @since 1
         */
        int SET_ALARM_FAILED_FULL = 5;

    }

    /**
     * Constants definitions for {@link com.sonymobile.smartwear.swr30.Control.DisplaySize}
     */
    interface DisplaySize {

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}
         *
         * @since 1
         */
        int DISPLAY_SIZE_LONGEST = 296;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST}
         *
         * @since 1
         */
        int DISPLAY_SIZE_SHORTEST = 128;
    }

    /**
     * Constants definitions for {@link com.sonymobile.smartwear.swr30.ExtensionService.AccessoryConnectionStatus}
     */
    interface AccessoryConnectionStatus {
        /**
         * Defines {@link com.sonymobile.smartwear.swr30.ExtensionService.AccessoryConnectionStatus#STATUS_DISCONNECTED}
         */
        static final int STATUS_DISCONNECTED = 0;

        /**
         * Defines {@link com.sonymobile.smartwear.swr30.ExtensionService.AccessoryConnectionStatus#STATUS_CONNECTED}
         */
        static final int STATUS_CONNECTED = 1;
    }


    /**
     * @hide
     * This class is only intended as a utility class containing declared constants
     * that will be used by application developers.
     */
    private SWR30() {
    }
}
