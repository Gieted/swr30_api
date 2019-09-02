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

import android.util.Log;
/**
 * Log class that encapsulates all calls to {@link android.util.Log}, used by {@link com.sonymobile.smartwear.swr30.ExtensionService} and {@link com.sonymobile.smartwear.swr30.Control}.
 * <br>By default all logging are disabled, but can be enabled by setting {@link Dbg#DEBUG} to true and execute: adb shell setprop log.tag.&lt;YOUR_LOG_TAG&gt; DEBUG<br>
 * The log tag can be changed by {@link com.sonymobile.smartwear.swr30.Dbg#setLogTag(String)}.<br>
 * */
public final class Dbg {

    public static final boolean DEBUG = false;

    private static String LOG_TAG = "SWR30_Extension";

    private Dbg() {
    }

    private static boolean isLogEnabled() {
        if (DEBUG) {
            return Log.isLoggable(LOG_TAG, Log.DEBUG);
        } else {
            return false;
        }
    }

    /**
     * Calls {@link android.util.Log#v(String, String)} if logs are enabled.
     * */
    public static void v(String s) {
        if (isLogEnabled()) {
            android.util.Log.v(LOG_TAG, s);
        }
    }

    /**
     * Calls {@link android.util.Log#e(String, String)} if logs are enabled.
     * */
    public static void e(String s) {
        if (isLogEnabled()) {
            android.util.Log.e(LOG_TAG, s);
        }
    }

    /**
     * Calls {@link android.util.Log#e(String, String, Throwable)} if logs are enabled.
     * */
    public static void e(String s, Throwable t) {
        if (isLogEnabled()) {
            android.util.Log.e(LOG_TAG, s, t);
        }
    }

    /**
     * Calls {@link android.util.Log#w(String, String)} if logs are enabled.
     * */
    public static void w(String s) {
        if (isLogEnabled()) {
            android.util.Log.w(LOG_TAG, s);
        }
    }

    /**
     * Calls {@link android.util.Log#w(String, String, Throwable)} if logs are enabled.
     * */
    public static void w(String s, Throwable t) {
        if (isLogEnabled()) {
            android.util.Log.w(LOG_TAG, s, t);
        }
    }

    /**
     * Calls {@link android.util.Log#d(String, String)} if logs are enabled.
     * */
    public static void d(String s) {
        if (isLogEnabled()) {
            android.util.Log.d(LOG_TAG, s);
        }
    }

    /**
     * Change the log tag, to differentiate from other SWR30 applications installed.
     * @param tag The log tag that shall be used.
     * */
    public static void setLogTag(final String tag) {
        LOG_TAG = tag;
    }
}
