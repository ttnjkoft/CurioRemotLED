/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.zhy_horizontalscrollview;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String CURRENT_PWM_LEVEL="e50a65c3-4dbf-46a9-8fd2-c7cab8a85302";
    public static String CRIO_LIGHT_DEVICE="47f1de41-c535-414f-a747-1184246636c6";
    public static String PWM_brightness_level="19c4b90e-d021-471a-be05-c48e36a1b99d";
    static {
        // Sample Services.
        //    attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(CRIO_LIGHT_DEVICE, "Curio Lighting Device Service");
        // Sample Characteristics.
        //      attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("f408b6c7-06c0-4b4a-8493-50bc261ea9e7", "GPIO Remote Control");
        attributes.put(PWM_brightness_level, "PWM brightness level");
        attributes.put(CURRENT_PWM_LEVEL, "Current PWM level");
        attributes.put("7952437e-8d8f-4fe7-8ee3-78086eca3837", "Custom Lamp Name");
        attributes.put("5fe31cc3-a56d-41e6-9e3b-bb44228982b0", "Board Type");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

}
