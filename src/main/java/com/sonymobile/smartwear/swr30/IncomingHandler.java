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


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

class IncomingHandler extends Handler {

    interface ConnectionStatusListener {
        void onConnectionStatus(int status, String bluetoothAddress);
    }

    IncomingHandler(Context context, Looper looper) {
        super(looper);
        mContext = context.getApplicationContext();
    }

    void registerConnectionStatusListener(ConnectionStatusListener connectionStatusListener) {
        mConnectionStatusListener = connectionStatusListener;
    }

    void unregisterConnectionStatusListener() {
        mConnectionStatusListener = null;
    }

    final Context mContext;
    ConnectionStatusListener mConnectionStatusListener;

    Control mStartedControl;

    Control getStartedControl() {
        return mStartedControl;
    }

    @Override
    public void handleMessage(Message msg) {
        handleIncomingMessage(msg);
    }

    void stopStartedControl() {
        if (mStartedControl != null) {
            mStartedControl.stop();
            mStartedControl = null;
        }
    }

    void handleIncomingMessage(Message msg) {
        if (msg.what == SWR30.MSG_ACCESSORY_CONNECTION_STATUS) {
            Bundle bundle = getBundle(msg);
            if (bundle != null && mConnectionStatusListener != null) {
                mConnectionStatusListener.onConnectionStatus(bundle.getInt(SWR30.EXTRA_ACCESSORY_CONNECTION_STATUS), bundle.getString(SWR30.EXTRA_ACCESSORY_BLUETOOTH_ADDRESS));
            }
            return;
        }
        Control control = getControlInstanceByMessage(msg);
        if (control != null) {
            control.onMessage(msg.what, getBundle(msg));
            if (msg.what == SWR30.MSG_STOP) {
                mStartedControl = null;
            } else if (msg.what == SWR30.MSG_START) {
                mStartedControl = control;
            }
        }
    }

    boolean isInstantiateAllowed(int what) {
        switch (what) {
            // fall through
            case SWR30.MSG_START:
            case SWR30.MSG_ACCESSORY_CONNECTION_STATUS:
            case SWR30.MSG_ADDED:
            case SWR30.MSG_REMOVED:
            case SWR30.MSG_GET_PREVIEW_IMAGE:
            case SWR30.MSG_GET_START_IMAGE:
                return true;
            default:
                return false;
        }
    }

    Bundle getBundle(Message msg) {
        try {
            return (Bundle) msg.obj;
        } catch (ClassCastException e) {
            Dbg.e("ClassCastException: ", e);
            return null;
        }
    }

    boolean isValidControlMessage(Message msg) {
        if (msg == null) {
            Dbg.e("null message");
            return false;
        }
        Bundle bundle = getBundle(msg);
        if (bundle == null) {
            Dbg.e("no bundle from the host application");
            return false;
        }
        String instanceId = bundle.getString(SWR30.EXTRA_CONTROL_INSTANCE_ID);
        if (TextUtils.isEmpty(instanceId)) {
            Dbg.e("no instance ID from the host application");
            return false;
        }
        if (isInstantiateAllowed(msg.what)) {
            String controlName = bundle.getString(SWR30.EXTRA_CONTROL_NAME);
            if (TextUtils.isEmpty(controlName)) {
                Dbg.e("no controlName from the host application");
                return false;
            }
            if (msg.replyTo == null) {
                Dbg.e("no replyTo messenger from the host application");
                return false;
            }
        }
        return true;
    }

    Control getControlInstanceByMessage(Message msg) {
        if (!isValidControlMessage(msg)) {
            return null;
        }
        Bundle bundle = getBundle(msg);
        String instanceId = bundle.getString(SWR30.EXTRA_CONTROL_INSTANCE_ID);
        if (mStartedControl != null && mStartedControl.getControlInstanceId().equals(instanceId)) {
            return mStartedControl;
        }

        if (isInstantiateAllowed(msg.what)) {
            String controlName = bundle.getString(SWR30.EXTRA_CONTROL_NAME);
            return Control.instantiateControlFromClassName(mContext, controlName, instanceId, msg.replyTo);
        } else {
            // existing instance expected for other messages
            Dbg.e("invalid instanceId received: " + instanceId);
            return null;
        }
    }
}
