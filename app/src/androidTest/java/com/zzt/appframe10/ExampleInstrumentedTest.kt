package com.zzt.appframe10

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import junit.framework.TestCase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.zzt.appframe10", appContext.packageName)
    }

    private var device: UiDevice? = null

    /**
     * 打开Gmail
     */
    @Test
    fun startGmail() {
        println(">>>> startGmail")

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device?.pressHome()

        val gmail: UiObject2? = device?.findObject(By.text("Gmail"))
        // Perform a click and wait until the app is opened.
        val opened: Boolean? = gmail?.clickAndWait(Until.newWindow(), 3000)
        TestCase.assertTrue(opened ?: false)
    }
}