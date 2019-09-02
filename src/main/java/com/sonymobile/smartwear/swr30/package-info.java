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


/**
 * The SWR30 API enables your Android application to take total control of the SWR30 accessory. <br>
 * <br>
 * Your application does <b>NOT</b> execute directly on the accessory. Instead the SWR30 host application interacts with the applications supporting this API, to allow them to control the accessory.<br>
 *
 * You need to define a control that inherits from {@link com.sonymobile.smartwear.swr30.Control}. A control can be granted control over the display, vibrator, listen to tap and key events, etc. <br><br>
 * See also see <a href="package-summary.html#ServiceRegistration">Service registration</a>, <a href="package-summary.html#ActivityRegistration">Control activity registration</a> and <a href="package-summary.html#ControlRegistration">Control registration</a>, for details about the how to register the components needed.
 *
 * <a name="Registration"></a>
 * <h3>Registration</h3>
 * <a name="ServiceRegistration"></a>
 * <h4>Service registration:</h4>
 * The host application finds applications by calling {@link android.content.pm.PackageManager#queryIntentServices} with {@link com.sonymobile.smartwear.swr30.SWR30#ACTION_BIND} as intent action.
 * For this to work you need to declare a service in your AndroidManifest.xml in following way:
 * <ul>
 * <li>android:name must point out {@link com.sonymobile.smartwear.swr30.ExtensionService}, or a class that inherits from {@link com.sonymobile.smartwear.swr30.ExtensionService}.
 * <li>android:exported must be true.</li>
 * <li>Declare an Intent filter for action {@value com.sonymobile.smartwear.swr30.SWR30#ACTION_BIND}.</li>
 * <li>Point out xml configuration file using meta-data {@value com.sonymobile.smartwear.swr30.SWR30#META_DATA_XML_CONFIGURATION}.</li>
 * </ul>
 * Example (AndroidManifest.xml):
 * <pre>
 * &lt;service
 *     android:name="com.sonymobile.smartwear.swr30.ExtensionService"
 *     android:exported="true"
 *     android:permission="com.sonymobile.smartwear.swr30.PERMISSION_HOSTAPP"&gt;
 *     &lt;intent-filter&gt;
 *         &lt;action android:name="com.sonymobile.smartwear.swr30.BIND"/&gt;
 *     &lt;/intent-filter&gt;
 *     &lt;meta-data android:name="com.sonymobile.smartwear.swr30.configuration" android:resource="@xml/swr30_sample_configuration"/&gt;
 * &lt;/service&gt;
 * </pre>
 *
 * <a name="ControlRegistration"></a>
 * <h4>Control registration:</h4>
 *  The control is registered via a XML resource pointed out by {@link com.sonymobile.smartwear.swr30.SWR30#META_DATA_XML_CONFIGURATION}, see example above.
 *  This XML file must have an extension element, with a control element as child. Future versions of the API may support multiple controls, but currently only one is supported.<br>
 * <p>
 *     The extension tag has attribute <a href="R.attr.html#swr30ExtensionMinApiVersion">swr30ExtensionMinApiVersion</a>.<br>
 * </p>
 * <p>
 *     The control tag has attributes as below.<br>
 *     <a href="R.attr.html#swr30ControlName">swr30ControlName</a>,
 *     <a href="R.attr.html#swr30ControlLabel">swr30ControlLabel</a>,
 *     <a href="R.attr.html#swr30ControlSingleInstance">swr30ControlSingleInstance</a>.<br>
 *     <a href="R.attr.html#swr30ControlPreviewImage">swr30ControlPreviewImage</a> and
 *     <a href="R.attr.html#swr30ControlStartImage">swr30ControlStartImage</a>.<br>
 *
 * </p>
 * Example: (res/xml/swr30_sample_configuration.xml)
 * <pre>
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
 * &lt;extension
 *     xmlns:custom="http://schemas.android.com/apk/res-auto"
 *     custom:swr30ExtensionMinApiVersion="1"&gt;
 *     &lt;control
 *         custom:swr30ControlName="@string/control_name"
 *         custom:swr30ControlLabel="@string/control_label"
 *         custom:swr30ControlSingleInstance="false"
 *         custom:swr30ControlPreviewImage="@drawable/control_preview"
 *         custom:swr30ControlStartImage="@drawable/control_start"&gt;
 *     &lt;/control&gt;
 * &lt;/extension&gt;
 * </pre>
 * <a name="ActivityRegistration"></a>
 * <h4>Control activity registration:</h4>
 * Each control should have a {@link android.app.Activity}. This activity is launched by the host application when:
 * <ul>
 * <li>The user adds a new instance of the control in the host application UI, similar as Android widgets.</li>
 * <li>The user edits an existing instance in the host application UI.</li>
 * </ul>
 * The activity can be used to let the user configure settings for a control instance, or (if no user settings) give the user some introduction to the control and how to interact with it.<br><br>
 * <br>
 * The activity is started with {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}. The intent has action {@link com.sonymobile.smartwear.swr30.SWR30#ACTION_CONFIGURE_CONTROL} and {@link com.sonymobile.smartwear.swr30.SWR30#EXTRA_CONTROL_INSTANCE_ID} as intent extra, to allow different settings to be displayed for different instances of the control.<br>
 * <b>NOTE: </b>The result needs to be set to {@link android.app.Activity#RESULT_OK} to let the host application know that the activity was not cancelled, see: {@link android.app.Activity#setResult(int)}. <br>
 * See also <a href="Control.html#ControlLifecycle">Control Lifecycle</a>.<br><br>
 *
 * To configure an activity as a control activity, declare it in following way in AndroidManifest.xml:<br>
 *
 * <ul>
 * <li>Declare an Intent filter for action {@value com.sonymobile.smartwear.swr30.SWR30#ACTION_CONFIGURE_CONTROL}.</li>
 * <li>Associate the activity with a control by using meta-data {@value com.sonymobile.smartwear.swr30.SWR30#META_DATA_CONTROL_NAME}.</li>
 * </ul>
 * Example (AndroidManifest.xml):
 * <pre>
 * &lt;activity
 *     android:name=".SampleActivity"
 *     android:permission="com.sonymobile.smartwear.swr30.PERMISSION_HOSTAPP"&gt;
 *     &lt;intent-filter&gt;
 *         &lt;action android:name="com.sonymobile.smartwear.swr30.CONFIGURE_CONTROL"/&gt;
 *     &lt;/intent-filter&gt;
 *     &lt;meta-data android:name="com.sonymobile.smartwear.swr30.control_name" android:value="@string/control_name"/&gt;
 * &lt;/activity&gt;
 * </pre>
 *
 * See <a href="R.attr.html#swr30ControlName">swr30ControlName</a> for details and example of control name.<br>
 *
 * A control without an associated activity will still work, but it is less user friendly, since the user might not know how to interact with it on the accessory.<br>
 *
 * <h3>Permission</h3>
 * At a minimum, applications supporting this API need to be granted the permission {@value com.sonymobile.smartwear.swr30.SWR30.Permission#PERMISSION_CONTROL_EXTENSION}. Some features require additional permissions, see {@link com.sonymobile.smartwear.swr30.SWR30.Permission} <br><br>
 * Example (AndroidManifest.xml):
 * <pre>
 *     &lt;uses-permission android:name="com.sonymobile.smartwear.swr30.PERMISSION_CONTROL_EXTENSION"/&gt;
 * </pre>
 */

package com.sonymobile.smartwear.swr30;
