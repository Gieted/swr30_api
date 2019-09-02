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

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Process;

/**
 * This service is used to route messages between the host application and {@link com.sonymobile.smartwear.swr30.Control}s.
 * The service defines a {@link android.os.Messenger} for incoming messages which is returned to its binder in {@link android.app.Service#onBind(android.content.Intent)}.onBind.<br>
 * Messages to from host application to a control, ends up in callbacks such as {@link Control#onStart()}. <br>
 * Messages to from a control to the host application is sent via functions in the control class, such as {@link Control#requestStop()}<br>
 * NOTE: Registration is needed, read more at: <a href="package-summary.html#ServiceRegistration">Service registration</a>.
 *
 */
public class ExtensionService extends Service implements IncomingHandler.ConnectionStatusListener {

    private Messenger mReceivingMessenger;
    private IncomingHandler mIncomingHandler;

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        Dbg.d(ExtensionService.class.getSimpleName() + " onBind");
        IBinder binder = null;
        try {
            binder = mReceivingMessenger.getBinder();
        } catch (Exception e) {
            Dbg.e("onBind Exception: " + e);
        }
        return binder;
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Dbg.d(ExtensionService.class.getSimpleName() + " onCreate");
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ExtensionServiceBgThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                Dbg.e("uncaughtException", e);
            }
        });

        mIncomingHandler = new IncomingHandler(this, thread.getLooper());
        mIncomingHandler.registerConnectionStatusListener(this);
        mReceivingMessenger = new Messenger(mIncomingHandler);
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Dbg.d(ExtensionService.class.getSimpleName() + " onDestroy");
        mIncomingHandler.unregisterConnectionStatusListener();

        try {
            mIncomingHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIncomingHandler.stopStartedControl();
                    mIncomingHandler.getLooper().quitSafely();
                }
            });
        } catch (Exception e) {
            Dbg.e("onDestroy Exception: " + e);
        }
    }

    /**
     * Called to inform about the status of the connection between host application and the accessory.
     * NOTE: This callback is ONLY called if the service has {@value com.sonymobile.smartwear.swr30.SWR30#ACTION_ACCESSORY_CONNECTION_STATUS} in the Intent filter in the Android Manifest.
     * Also note that when no control instance is added this callback is NOT called.
     *
     * @param status Any value defined in {@link AccessoryConnectionStatus}.
     * @param bluetoothAddress The Bluetooth address of the accessory, e.g. "00:11:22:AA:BB:CC".
     */
    public void onConnectionStatus(int status, String bluetoothAddress) {
        Dbg.d(ExtensionService.class.getSimpleName() + " onConnectionStatus" + status + " " + bluetoothAddress);
    }

    /**
     * Accessory connection status constants. Used in {@link #onConnectionStatus(int, String)}.
     */
    public interface AccessoryConnectionStatus {
        /**
         * The accessory is disconnected from the host application.
         */
        static final int STATUS_DISCONNECTED = SWR30.AccessoryConnectionStatus.STATUS_DISCONNECTED;

        /**
         * The accessory is connected to the host application.
         */
        static final int STATUS_CONNECTED = SWR30.AccessoryConnectionStatus.STATUS_CONNECTED;
    }

}
