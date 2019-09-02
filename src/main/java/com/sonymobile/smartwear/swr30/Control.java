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

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Controls are used to take control over the accessory, for example the display, vibrator, listen to tap and key events, setting smart wake up alarms, and checking battery status.<br>
 * <br>
 * The user can add multiple instances of a control, but only one of them can be started at a time.<br>
 * NOTE: Registration needed for the host application to find this class (see <a href="package-summary.html#ControlRegistration">Control registration</a>).<br>
 * Each control should have a {@link android.app.Activity}, read more at: <a href="package-summary.html#ActivityRegistration">Control activity registration</a>.<br>
 * The host application does not interact directly with a control. Instead it binds to a {@link ExtensionService}, sends messages which trigger callbacks such as
 * {@link Control#onStart()}.
 *
 * <a name="ControlLifecycle"></a>
 * <h3>Control Lifecycle</h3>
 * <p>
 * A control has essentially the following states:
 * <ul>
 * <li>After the user has added an instance of the control in the host application UI it is in the <b>added</b> state. Note that the user can add several instances of the same control.</li>
 * <li>When a control is in the <b>started</b> state it can control the vibrator, update the display and react to input such as taps and key presses. Only one instance is started at a time.</li>
 * </ul>
 * <img src="../../../../../images/control_lifecycle.png"
 * alt="Control lifecycle"/> <br>
 * <a name="ControlLifecycleCallbacks"></a>
 * <h4>Control Lifecycle callbacks</h4>
 * <ul>
 * <li>{@link Control#onAdded()}: called when an instance is added by the user in the host application UI.</li>
 * <li>{@link Control#onStart()}: called when this instance is granted control of the accessory (update the display, vibrate, listen to taps, keys etc.).</li>
 * <li>{@link Control#onStop()}: called when this instance is no longer visible on the display.</li>
 * <li>{@link Control#onRemoved()}: called when this instance is removed by the user in the host application UI. You should remove any persistent data related to {@link Control#getControlInstanceId()} at this point.</li>
 * </ul>
 * </p>
 */
public class Control {

    final Context mContext;
    final String mControlInstanceId;
    final Messenger mMessengerToHostApp;

    static final int STATE_ADDED = 0;

    static final int STATE_STARTED = 1;

    int mState;

    /**
     * Create a control. Note that no initialisation of variables nor listeners such as content observers or broadcast receivers should be done here, but in {@link #onStart()} instead.
     * The reason for this is that this constructor is called one time in {@link #onAdded()} and again in {@link #onStart()} however with the same controlInstanceId.
     *
     * @param context The context.
     * @param controlInstanceId Multiple instances of a {@link com.sonymobile.smartwear.swr30.Control} can exist,
     * each with unique control instance ID, see <a href="package-summary.html#ControlRegistration">Control registration</a>. The ID is assigned by the host application when control is added by the user in the host application UI,
     * see {@link com.sonymobile.smartwear.swr30.SWR30#ACTION_CONFIGURE_CONTROL} and {@link Control#onAdded()}.
     * @param messenger Messenger used to send reply messages to the host application.
     *
     * @since 1
     */
    public Control(final Context context, final String controlInstanceId, final Messenger messenger) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        mState = STATE_ADDED;
        mContext = context;
        mControlInstanceId = controlInstanceId;
        mMessengerToHostApp = messenger;
    }

    /**
     * Creates a full screen bitmap with optimal configuration to be displayed on the accessory.<br>
     *
     * @param isPortrait True for portrait bitmap, false for landscape.<br>
     * A portrait image will have width {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST} and height {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}.<br>
     * A landscape image will have width {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST} and height {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST}.<br>
     * Landscape images are rotated on the accessory automatically depending on the left/right hand setting in the host application.
     *
     * @return A {@link android.graphics.Bitmap} with {@link android.graphics.Bitmap.Config#RGB_565} for optimal size and {@link DisplayMetrics#DENSITY_DEFAULT} to avoid scaling.
     *
     * @since 1
     */
    public static Bitmap createBitmap(final boolean isPortrait) {
        int width;
        int height;

        if (isPortrait) {
            width = DisplaySize.DISPLAY_SIZE_SHORTEST;
            height = DisplaySize.DISPLAY_SIZE_LONGEST;
        } else {
            width = DisplaySize.DISPLAY_SIZE_LONGEST;
            height = DisplaySize.DISPLAY_SIZE_SHORTEST;
        }

        // RGB_565 is most suitable for monochrome display
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // Set the density to default to avoid scaling.
        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        // Set white background
        bitmap.eraseColor(Color.WHITE);
        return bitmap;
    }

    /**
     * Render the enclosed {@link android.view.View} to a {@link android.graphics.Bitmap}. The bitmap will have optimal configuration to be displayed on the accessory.<br>
     *
     * @param view An inflated or dynamic instance of a {@link android.view.View} or {@link android.view.ViewGroup}, to be rendered to fit within the bounds of the accessory screen.
     * Please note that any declared width or height of your outer layout element will have no function when rendering the layout to a bitmap. The layout will be sized according to the isPortrait parameter.
     * Still, declaring width/height to {@link com.sonymobile.smartwear.swr30.R.dimen#swr30_display_size_shortest} and {@link com.sonymobile.smartwear.swr30.R.dimen#swr30_display_size_longest} is recommended so the preview in the IDE is showing proper proportions for your layout.<br>
     * We strongly encourage use of only pixel dimensions when specifying sizes in layouts, in order to prevent unwanted scaling.
     *
     * @param isPortrait True for portrait bitmap, false for landscape.<br>
     * A portrait image will have width {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST} and height {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}.<br>
     * A landscape image will have width {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST} and height {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST}.<br>
     * Landscape images are rotated on the accessory automatically depending on the left/right hand setting in the host application.
     *
     * @return A {@link android.graphics.Bitmap} displaying the rendered view.
     *
     * @since 1
     * */
    public static Bitmap renderView(View view, boolean isPortrait) {
        Bitmap screenBitmap = createBitmap(isPortrait);
        Canvas canvas = new Canvas(screenBitmap);

        view.setLayoutParams(new ViewGroup.LayoutParams(screenBitmap.getWidth(), screenBitmap.getHeight()));
        view.measure(screenBitmap.getWidth(), screenBitmap.getHeight());
        view.layout(0, 0, screenBitmap.getWidth(), screenBitmap.getHeight());
        view.draw(canvas);

        return screenBitmap;
    }


    /**
     * Unique instance ID, assigned by the host application when an instance is added, see {@link Control#onAdded()}.
     * @return instance ID.
     *
     * @since 1
     */
    public String getControlInstanceId() {
        return mControlInstanceId;
    }

    /**
     * Returns {@link android.content.Context#getApplicationContext()} from the {@link com.sonymobile.smartwear.swr30.ExtensionService}.
     * @return the context.
     *
     * @since 1
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Called just after the control activity (see <a href="package-summary.html#ActivityRegistration">Control activity registration</a>) has finished with {@link android.app.Activity#RESULT_OK} for a new instance.
     * If no control activity is set, this is called right after the instance has been added.
     * See also <a href="Control.html#ControlLifecycle">Control Lifecycle</a>. There is no need to call the super class version of the method.
     *
     * @since 1
     */
    public void onAdded() {
        Dbg.d("onAdded not implemented");
    }

    /**
     * Called when a control instance has been removed by the user in the host application UI, see <a href="package-summary.html#ActivityRegistration">Control activity registration</a>. You should remove any persistent data related to {@link Control#getControlInstanceId()} at this point.
     * The control will no longer be shown on the accessory. See also <a href="Control.html#ControlLifecycle">Control Lifecycle</a>. There is no need to call the super class version of the method.
     *
     * @since 1
     */
    public void onRemoved() {
        Dbg.d("onRemoved not implemented");
    }

    /**
     * Called to indicate result of  {@link com.sonymobile.smartwear.swr30.Control#setSmartAlarm(long)}. There is no need to call the super class version of the method.
     *
     * @param result Any value defined in {@link Control.AlarmResult}.
     *
     * @since 1
     */
    public void onSetAlarmResult(final int result) {
        Dbg.d("onSetAlarmResult not implemented");
    }

    /**
     * Called after {@link com.sonymobile.smartwear.swr30.Control#requestBatteryStatus()} has been requested. There is no need to call the super class version of the method.
     *
     * @param isCharging Indicates if the accessory battery is being charged or not. The value can be true (charging) or false.
     * @param level Current battery level as a percentage.
     *
     * @since 1
     */
    public void onBatteryStatus(final boolean isCharging, final int level) {
        Dbg.d("onBatteryStatus not implemented");
    }

    final void start() {
        mState = STATE_STARTED;
        Dbg.d("Control onStart");
        onStart();
    }

    final void stop() {
        if (mState == STATE_STARTED) {
            Dbg.d("Control onStop");
            onStop();
        }
        mState = STATE_ADDED;
    }

    final void add() {
        mState = STATE_ADDED;
        Dbg.d("Control onAdded");
        onAdded();
    }

    final void remove() {
        stop();

        // no state for removed
        Dbg.d("Control onRemoved");
        onRemoved();
    }

    /**
     * Called when the control is no longer visible on the display. This can be triggered by the host application or by the control itself, see {@link Control#requestStop()}. See also <a href="Control.html#ControlLifecycle">Control Lifecycle</a>. There is no need to call the super class version of the method.
     *
     * @since 1
     */
    public void onStop() {
        Dbg.d("onStop not implemented");
    }

    /**
     * Called when a control is started when the user navigates to the control on the accessory. The control is granted control of the accessory (update the display, vibrate, listen to taps, keys etc) and is expected to update the display immediately. See also <a href="Control.html#ControlLifecycle">Control Lifecycle</a>. There is no need to call the super class version of the method.
     *
     * @since 1
     */
    public void onStart() {
    }

    /**
     * Override this method to provide a instance specific start image. If the same image shall be used for all instances, use {@link com.sonymobile.smartwear.swr30.R.attr#swr30ControlStartImage} instead.<br>
     * If no start image is defined by your application, the preview image will be used as start image instead.<br>
     * The start image is stored on the accessory, and shown on the accessory display when the user starts the control, and {@link #onStart()} is called as soon as the host application is aware of this.
     * The start image is also shown on the accessory when the user navigates to the application while the accessory has lost connection with the host application.<br>
     * The method is called in following situations to refresh the image:
     * <ul>
     *   <li>when the instance has been added (see {@link #onAdded()}) or edited by the user in the host application UI.</li>
     *   <li>when host application detects that your application APK has been updated.</li>
     *   <li>when the control has been started, see {@link #onStart()}.</li>
     * </ul>
     * This means it can be called both in the <b>added</b> and in the <b>started</b> state.<br>
     * NOTE: Use helper functions {@link #createBitmap(boolean)} or {@link #renderView(android.view.View, boolean)} to make sure that the bitmap has correct size and configuration.<br>
     * There is no need to call the super class version of the method.
     *
     * @return A start image.
     *
     * @since 1
     */
    public Bitmap getStartImage() {
        Dbg.d("getStartImage not implemented");
        return null;
    }

    /**
     * Override this method to provide a instance specific preview image. If the same image shall be used for all instances, use {@link com.sonymobile.smartwear.swr30.R.attr#swr30ControlPreviewImage} instead.<br>
     * The preview image is shown in the host application in the list of configured controls.<br>
     * If no start image is defined by your application, the preview image is also used as start image on the accessory.<br>
     * The method is called in following situations to refresh the image:
     * <ul>
     *   <li>when the instance has been added (see {@link #onAdded()}) or edited by the user in the host application UI.</li>
     *   <li>when host application detects that your application APK has been updated.</li>
     *   <li>when the control has been started, see {@link #onStart()}.</li>
     * </ul>
     * This means it can be called both in the <b>added</b> and in the <b>started</b> state.<br>
     * NOTE: Use helper functions {@link #createBitmap(boolean)} or {@link #renderView(android.view.View, boolean)} to make sure that the bitmap has correct size and configuration.<br>
     * There is no need to call the super class version of the method.
     *
     * @return A preview image.
     *
     * @since 1
     */
    public Bitmap getPreviewImage() {
        Dbg.d("getPreviewImage not implemented");
        return null;
    }

    /**
     * Called when tap is detected. There is no need to call the super class version of the method.
     *
     * @param timeStamp The time when the tap occurred.
     *
     * @since 1
     */
    public void onTap(final long timeStamp) {
        Dbg.d("onTap not implemented");
    }

    /**
     * Called when the user presses any of the supported keys on the accessory. There is no need to call the super class version of the method.
     *
     * @param event Can be any key event defined in {@link KeyEvent}.
     * @param timeStamp The time when the event occurred.
     *
     * @since 1
     */
    public void onKey(final int event, final long timeStamp) {
        Dbg.d("onKey not implemented");
    }

    /**
     * Called when an error occurs. There is no need to call the super class version of the method.
     *
     * @param errorCode Can be any value defined in {@link Error}.
     * @param debugMessage Error message. NOTE: This is not a localized message, and shall NOT be displayed to the user. Example:
     * <pre>
     *     Missing permission: com.sonymobile.smartwear.swr30.PERMISSION_SET_SMART_ALARM
     * </pre>
     *
     * @since 1
     */
    public void onError(final int errorCode, final String debugMessage) {
        Dbg.e("onError code: " + errorCode + " " + debugMessage);
    }

    /**
     * Called when a message for the control instance is received. This function can be overridden to handle custom messages or if the message should be rerouted.
     *
     * @param what indicates which kind of message received from the host application.
     * @param extras message extras.
     */
    void onMessage(final int what, final Bundle extras) {
        Dbg.d("onMessage: " + what);
        Bundle responseExtras = createExtrasWithInstanceId();

        switch (what) {
            case SWR30.MSG_ADDED:
                add();
                break;
            case SWR30.MSG_REMOVED:
                remove();
                break;
            case SWR30.MSG_STOP:
                stop();
                break;
            case SWR30.MSG_START:
                start();
                break;
            case SWR30.MSG_ERROR:
                onError(extras.getInt(SWR30.EXTRA_ERROR_CODE), extras.getString(SWR30.EXTRA_ERROR_DEBUG_MESSAGE));
                break;
            case SWR30.MSG_TAP_EVENT:
                onTap(extras.getLong(SWR30.EXTRA_TIMESTAMP));
                break;
            case SWR30.MSG_KEY_EVENT:
                onKey(extras.getInt(SWR30.EXTRA_KEY_EVENT), extras.getLong(SWR30.EXTRA_TIMESTAMP));
                break;
            case SWR30.MSG_SET_SMART_ALARM_RESULT:
                onSetAlarmResult(extras.getInt(SWR30.EXTRA_SET_ALARM_RESULT));
                break;
            case SWR30.MSG_BATTERY_STATUS:
                onBatteryStatus(extras.getBoolean(SWR30.EXTRA_BATTERY_IS_CHARGING), extras.getInt(SWR30.EXTRA_BATTERY_LEVEL));
                break;
            case SWR30.MSG_GET_PREVIEW_IMAGE:
                responseExtras.putByteArray(SWR30.EXTRA_PREVIEW_IMAGE_DATA_RAW, bitmapToByteArray(getPreviewImage()));
                sendMessageToHostapp(SWR30.MSG_UPDATE_PREVIEW_IMAGE, responseExtras);
                break;
            case SWR30.MSG_GET_START_IMAGE:
                responseExtras.putByteArray(SWR30.EXTRA_START_IMAGE_DATA_RAW, bitmapToByteArray(getStartImage()));
                sendMessageToHostapp(SWR30.MSG_UPDATE_START_IMAGE, responseExtras);
                break;
            default:
                Dbg.e("Unhandled message type: " + what);
                break;
        }
    }

    /**
     * Called to show an image on the accessory display. In order to minimize battery usage updates should not be done too often. Updates of the display may take up to 0.5 seconds.
     *
     * @param resourceId The resource should be placed in res/drawable-nodpi folder, to avoid scaling (which would be based on the Android device screen dpi if in e.g. res/drawable-xhdpi folder).
     * The image should be pure black and white with dimensions of either
     * {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}*{@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST} or
     * {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST}*{@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}.<br>
     * Landscape images are rotated on the accessory automatically depending on the left/right hand setting in the host application.
     *
     * @since 1
     */
    protected void showImage(final int resourceId) {
        Uri resourceUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(mContext.getPackageName()).appendPath(Integer.toString(resourceId))
                .build();
        showImage(resourceUri);
    }

    /**
     * Called to show an image on the accessory display. In order to minimize battery usage updates should not be done too often. Updates of the display may take up to 0.5 seconds.
     *
     * @param uri a URI to an image. The image should be pure black and white with dimensions of either
     * {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}*{@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST} or
     * {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST}*{@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}.<br>
     * Landscape images are rotated on the accessory automatically depending on the left/right hand setting in the host application.
     * If the URI points out a drawable resource, the resource should be placed in res/drawable-nodpi folder,
     * to avoid scaling (which would be based on the Android device screen dpi if in e.g. res/drawable-xhdpi folder).
     *
     * @since 1
     */
    protected void showImage(final Uri uri) {
        Bundle extras = createExtrasWithInstanceId();
        extras.putString(SWR30.EXTRA_DISPLAY_DATA_URI, uri.toString());
        sendMessageToHostapp(SWR30.MSG_DISPLAY_UPDATE, extras);
    }

    /**
     * Called to show an image on the accessory display. In order to minimize battery usage updates should not be done too often. Updates of the display may take up to 0.5 seconds.
     *
     * @param bitmap The image should be pure black and white with dimensions of either
     * {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}*{@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST} or
     * {@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_SHORTEST}*{@value com.sonymobile.smartwear.swr30.Control.DisplaySize#DISPLAY_SIZE_LONGEST}.<br>
     * Landscape images are rotated on the accessory automatically depending on the left/right hand setting in the host application.
     *
     * @since 1
     */
    protected void showImage(final Bitmap bitmap) {
        Bundle extras = createExtrasWithInstanceId();
        extras.putByteArray(SWR30.EXTRA_DISPLAY_DATA_RAW, bitmapToByteArray(bitmap));
        sendMessageToHostapp(SWR30.MSG_DISPLAY_UPDATE, extras);
    }

    /**
     * Called to start a vibration pattern on the accessory. Note that taps can not be detected when the accessory is vibrating.
     *
     * @param onDuration on duration in milli seconds.
     * @param offDuration off duration in milli seconds.
     * @param repeats number of repeats of the on/off pattern.
     * Note: the value {@link Control.Vibrator#VIBRATE_REPEAT_UNTIL_STOP} means that the on/off pattern is repeated until
     * the {@link #vibrateStop()} is called by the control. A value of 0 means that no vibration will be performed.
     *
     * @since 1
     */
    protected void vibrateStart(final int onDuration, final int offDuration, final int repeats) {
        Bundle extras = createExtrasWithInstanceId();
        extras.putInt(SWR30.EXTRA_VIBRATE_ON_DURATION, onDuration);
        extras.putInt(SWR30.EXTRA_VIBRATE_OFF_DURATION, offDuration);
        extras.putInt(SWR30.EXTRA_VIBRATE_REPEATS, repeats);
        sendMessageToHostapp(SWR30.MSG_VIBRATE_START, extras);
    }

    /**
     * Called to stop the vibration pattern on the accessory.
     * If no vibration is ongoing, this message will be ignored by the host application.
     *
     * @since 1
     */
    protected void vibrateStop() {
        Bundle extras = createExtrasWithInstanceId();
        sendMessageToHostapp(SWR30.MSG_VIBRATE_STOP, extras);
    }

    /**
     * Called to stop this instance. See also <a href="Control.html#ControlLifecycle">Control Lifecycle</a>.
     *
     * @since 1
     */
    protected void requestStop() {
        Bundle extras = createExtrasWithInstanceId();
        sendMessageToHostapp(SWR30.MSG_STOP_REQUEST, extras);
    }

    /**
     * Called to set auto stop mode.
     * By default the host application stops a started control instance after a 30 seconds of user inactivity.
     *
     * @param mode Any value defined in {@link Control.AutoStopMode}.
     *
     * @since 1
     */
    protected void setAutoStopMode(final int mode) {
        Bundle extras = createExtrasWithInstanceId();
        extras.putInt(SWR30.EXTRA_AUTO_STOP_MODE, mode);
        sendMessageToHostapp(SWR30.MSG_SET_AUTO_STOP_MODE, extras);
    }

    /**
     * Call this to set a smart alarm. Requires the permission {@link SWR30.Permission#PERMISSION_SET_SMART_ALARM}. Result is indicated in {@link Control#onSetAlarmResult(int)}.
     *
     * @param alarmTimeLatest The latest time at which to wake the user. The alarm time is in {@link System#currentTimeMillis()} (wall clock time in UTC). If the user is sleeping lightly the alarm will trigger slightly earlier... Any value in milliseconds larger than {@link System#currentTimeMillis()} + {@link DateUtils#HOUR_IN_MILLIS} is allowed, but less than {@link System#currentTimeMillis()} + 24 * {@link DateUtils#HOUR_IN_MILLIS}.
     *
     * @since 1
     */
    protected void setSmartAlarm(final long alarmTimeLatest) {
        Bundle extras = createExtrasWithInstanceId();
        extras.putLong(SWR30.EXTRA_ALARM_TIME_LATEST, alarmTimeLatest);
        sendMessageToHostapp(SWR30.MSG_SET_SMART_ALARM, extras);
    }

    /**
     * This will stop the control and initiate an outgoing call from the accessory using
     * the accessory call handling UI. The accessory microphone and speaker will be used.
     *
     * You need to add the following to you Android manifest for this function to work:<br>
      <p>
     * Example: &lt;uses-permission android:name="android.permission.CALL_PHONE"/&gt;
     * </p>
     *
     * @param phoneNumberUri Phone number uri, example: Uri.parse("tel:123456")
     *
     * @since 1
     */
    protected void makeCall(final Uri phoneNumberUri) {
        Bundle extras = createExtrasWithInstanceId();
        extras.putString(SWR30.EXTRA_MAKE_CALL_URI, phoneNumberUri.toString());
        sendMessageToHostapp(SWR30.MSG_MAKE_CALL, extras);
    }

    /**
     * Call this to request battery status, see {@link com.sonymobile.smartwear.swr30.Control#onBatteryStatus(boolean, int)}.
     *
     * @since 1
     */
    protected void requestBatteryStatus() {
        Bundle extras = createExtrasWithInstanceId();
        sendMessageToHostapp(SWR30.MSG_REQUEST_BATTERY_STATUS, extras);
    }

    /**
     * Send message to the host application.
     *
     * @param what the message type.
     * @param extras the message argument
     */
    void sendMessageToHostapp(final int what, final Bundle extras) {
        Message respMsg = Message.obtain(null, what, extras);
        try {
            Dbg.d("sendMessageToHostapp " + what);
            mMessengerToHostApp.send(respMsg);
        } catch (RemoteException e) {
            Dbg.e("RemoteException: ", e);
        }
    }

    /**
     * Create a Bundle with control instance ID.
     *
     * @return Bundle with control instance ID.
     */
    Bundle createExtrasWithInstanceId() {
        Bundle extras = new Bundle();
        extras.putString(SWR30.EXTRA_CONTROL_INSTANCE_ID, mControlInstanceId);
        return extras;
    }

    /**
     * Instantiate control.
     *
     * This function is used from {@link com.sonymobile.smartwear.swr30.ExtensionService} to instantiate a control from message, e.g. {@link com.sonymobile.smartwear.swr30.SWR30#MSG_START}.
     * @param context The context.
     * @param controlName Control name from message, see {@link SWR30#EXTRA_CONTROL_NAME}.
     * @param instanceId instance ID from message, see {@link SWR30#EXTRA_CONTROL_INSTANCE_ID}.
     * @param messenger Messenger used to send reply messages to the host application.
     * @return control instance.
     */
    static Control instantiateControlFromClassName(Context context, String controlName, String instanceId, Messenger messenger) throws IllegalArgumentException{
        String className;
        final String MISSING_CONSTRUCTOR_MESSAGE = " must have the public constructor Control(Context context, String instanceId, Messenger messenger) ";
        final String FAILED_INSTANTIATE_CONTROL_MESSAGE = "Could not instantiate Control ";
        if (controlName.startsWith(".")) {
            // Shorthand: if the first character of the name is a period (for example, ".MyControl"), it is appended to the package name
            className = context.getPackageName() + controlName;
        } else {
            // Fully qualified class name
            className = controlName;
        }
        try {
            Class<?> controlClass = Class.forName(className);
            if (controlClass == null
                    || !Control.class.isAssignableFrom(controlClass)) {
                throw new IllegalArgumentException(
                        "Control class " + controlClass + " must extend " + Control.class.getCanonicalName());
            }
            Constructor<?> constructor = controlClass.getConstructor(Context.class, String.class, Messenger.class);
            constructor.setAccessible(true);
            return (Control) constructor.newInstance(context, instanceId, messenger);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found " + className, e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(FAILED_INSTANTIATE_CONTROL_MESSAGE + className, e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(FAILED_INSTANTIATE_CONTROL_MESSAGE + className, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(className
                    + MISSING_CONSTRUCTOR_MESSAGE, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(className
                    + MISSING_CONSTRUCTOR_MESSAGE, e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(className
                    + MISSING_CONSTRUCTOR_MESSAGE, e);
        }
    }

    /**
     * This function is used internally to pack a bitmap into a byte array before sending them to host application.
     *
     * @param bitmap The bitmap.
     * @return byte array.
     */
    static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Key event constants. Used in {@link #onKey(int, long)}.
     */
    public interface KeyEvent {
        /**
         * Volume up key.
         *
         * @since 1
         */
        int KEY_VOLUME_UP = SWR30.KeyEvent.KEY_VOLUME_UP;

        /**
         * Volume down key.
         *
         * @since 1
         */
        int KEY_VOLUME_DOWN = SWR30.KeyEvent.KEY_VOLUME_DOWN;

    }

    /**
     * Constants for auto stop modes of the control. Used in {@link #setAutoStopMode(int)}.
     */
    public interface AutoStopMode {

        /**
         * The control remains running after also after 30 seconds of user inactivity.
         *
         * @since 1
         */
        int AUTO_STOP_OFF = SWR30.AutoStopMode.AUTO_STOP_OFF;

        /**
         * The control is stopped automatically after about 30 seconds of user inactivity.
         * <p>
         * This is default behavior.
         * </p>
         *
         * @since 1
         */
        int AUTO_STOP_ON = SWR30.AutoStopMode.AUTO_STOP_ON;
    }

    /**
     * Vibrator constants.
     */
    public interface Vibrator {

        /**
         * Value used in the repeat parameter of the {@link #vibrateStart(int, int, int)} to set that the on/off pattern shall be repeated until explicitly stopped by {@link #vibrateStop()}.
         *
         * @since 1
         */
        int VIBRATE_REPEAT_UNTIL_STOP = SWR30.Vibrator.VIBRATE_REPEAT_UNTIL_STOP;
    }

    /**
     * Error constants. Used in {@link com.sonymobile.smartwear.swr30.Control#onError(int, String)}.
     */
    public interface Error {

        /**
         * Used when the application is missing a permission.
         *
         * @since 1
         */
        int ERROR_MISSING_PERMISSION = SWR30.Error.ERROR_MISSING_PERMISSION;

        /**
         * Used when a {@link com.sonymobile.smartwear.swr30.Control} attempts to send messages while not in the <b>started</b> state.
         * See also <a href="Control.html#ControlLifecycle">Control Lifecycle</a>.
         *
         * @since 1
         */
        int ERROR_NOT_STARTED = SWR30.Error.ERROR_NOT_STARTED;

        /**
         * Used when {@link #makeCall(android.net.Uri)} is called with emergency number.
         *
         * @since 1
         */
        int ERROR_MAKE_CALL_EMERGENCY_NUMBER_NOT_ALLOWED = SWR30.Error.ERROR_MAKE_CALL_EMERGENCY_NUMBER_NOT_ALLOWED;

        /**
         * Used when {@link #makeCall(android.net.Uri)} is called with invalid number or wrong scheme.
         *
         * @since 1
         */
        int ERROR_MAKE_CALL_INVALID_URI = SWR30.Error.ERROR_MAKE_CALL_INVALID_URI;

        /**
         * Used when a unknown error occurs on the accessory after {@link #makeCall(android.net.Uri)} has been called.
         *
         * @since 1
         */
        int ERROR_MAKE_CALL_UNKNOWN_ERROR = SWR30.Error.ERROR_MAKE_CALL_UNKNOWN_ERROR;
    }

    /**
     * Set alarm result constants. See {@link #setSmartAlarm(long)} and {@link #onSetAlarmResult(int)}.
     */
    public interface AlarmResult {

        /**
         * The alarm was set successfully.
         *
         * @since 1
         */
        int SET_ALARM_SUCCESS = SWR30.AlarmResult.SET_ALARM_SUCCESS;

        /**
         * The alarm time was too early.
         *
         * @since 1
         */
        int SET_ALARM_FAILED_TOO_EARLY = SWR30.AlarmResult.SET_ALARM_FAILED_TOO_EARLY;

        /**
         * The alarm time was too late.
         *
         * @since 1
         */
        int SET_ALARM_FAILED_TOO_LATE = SWR30.AlarmResult.SET_ALARM_FAILED_TOO_LATE;

        /**
         * The alarm time conflicts with existing alarm.
         *
         * @since 1
         */
        int SET_ALARM_FAILED_CONFLICT = SWR30.AlarmResult.SET_ALARM_FAILED_CONFLICT;

        /**
         * The maximum number of alarms have already been set.
         *
         * @since 1
         */
        int SET_ALARM_FAILED_FULL = SWR30.AlarmResult.SET_ALARM_FAILED_FULL;

    }

    /**
     * Display size constants.
     */
    public interface DisplaySize {

        /**
         * The longest display size in pixels. For use in XML layouts, use {@link com.sonymobile.smartwear.swr30.R.dimen#swr30_display_size_longest}.
         *
         * @since 1
         */
        int DISPLAY_SIZE_LONGEST = SWR30.DisplaySize.DISPLAY_SIZE_LONGEST;

        /**
         * The shortest display size in pixels. For use in XML layouts, use {@link com.sonymobile.smartwear.swr30.R.dimen#swr30_display_size_shortest}.
         *
         * @since 1
         */
        int DISPLAY_SIZE_SHORTEST = SWR30.DisplaySize.DISPLAY_SIZE_SHORTEST;
    }

    /**
     * Text size constants.
     */
    public interface TextSize {

        /**
         * The smallest text size in pixels. For use in XML layouts, use {@link com.sonymobile.smartwear.swr30.R.dimen#swr30_text_size_small}.
         *
         * @since 1
         */
        public static final int TEXT_SIZE_SMALL = 20;

        /**
         * The medium text size in pixels. For use in XML layouts, use {@link com.sonymobile.smartwear.swr30.R.dimen#swr30_text_size_medium}.
         *
         * @since 1
         */
        public static final int TEXT_SIZE_MEDIUM = 24;

        /**
         * The large text size in pixels. For use in XML layouts, use {@link com.sonymobile.smartwear.swr30.R.dimen#swr30_text_size_large}.
         *
         * @since 1
         */
        public static final int TEXT_SIZE_LARGE = 32;
    }

}
