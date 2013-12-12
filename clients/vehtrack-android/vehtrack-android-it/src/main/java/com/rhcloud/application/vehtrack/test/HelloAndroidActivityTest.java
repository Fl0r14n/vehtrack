package com.rhcloud.application.vehtrack.test;

import android.test.ActivityInstrumentationTestCase2;
import com.rhcloud.application.vehtrack.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidActivityTest() {
        super(HelloAndroidActivity.class); 
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}

