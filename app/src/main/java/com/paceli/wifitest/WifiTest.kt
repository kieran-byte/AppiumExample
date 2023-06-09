package com.paceli.wifitest

import android.content.Context
import android.net.wifi.WifiManager
import android.widget.RelativeLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WifiTest {

    private val device: UiDevice

    init {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    @Before
    fun setUp() {
        // Press Home key before running the test
        device.pressHome()
    }

    @After
    fun tearDown() {
        // Press Home key after running the test
        device.pressHome()
    }

    @Test
    fun validateWifi() {
        // Open apps list by scrolling on home screen
        val workspace = device.findObject(
            By.res("com.google.android.apps.nexuslauncher:id/workspace")
        )
        workspace.scroll(Direction.DOWN, 1.0f)

        // Click on Settings icon to launch the app
        val settings = device.findObject(
            By.res("com.google.android.apps.nexuslauncher:id/icon").text("Settings")
        )
        settings.click()

        // Wait up to 2 seconds for the element be displayed on screen
        val networkAndInternet = device.wait(Until.findObject(By.text("Network & internet")), 2000)
        networkAndInternet.click()
        // Click on element with text "Wi‑Fi"
        val wifi = device.wait(Until.findObject(By.text("Wi‑Fi")), 2000)
        wifi.click()
        // Click on element with text "Add network"
        val addNetwork = device.wait(Until.findObject(By.text("Add network")), 2000)
        addNetwork.click()

        // Obtain an instance of UiObject2 of the text field
        val ssidField = device.wait(Until.findObject(By.res("com.android.settings:id/ssid")), 2000)
        // Call the setText method using  Kotlin's property access syntax
        val ssid = "AndroidWifi"
        ssidField.text = ssid
        //Click on Save button
        device.findObject(By.res("android:id/button1").text("Save")).click()

        // BySelector matching the just added Wi-Fi
        val ssidSelector = By.text(ssid).res("android:id/title")
        // BySelector matching the connected status
        val status = By.text("Connected").res("android:id/summary")
        // BySelector matching on entry of Wi-Fi list with the desired SSID and status
        val networkEntrySelector = By.clazz(RelativeLayout::class.qualifiedName)
            .hasChild(ssidSelector)
            .hasChild(status)

        // Perform the validation using hasObject
        // Wait up to 5 seconds to find the element we're looking for
        val isConnected = device.wait(Until.hasObject(networkEntrySelector), 5000)
        Assert.assertTrue("Verify if device is connected to added Wi-Fi", isConnected)

        // Perform the validation using Android APIs
        val connectedWifi = getCurrentWifiSsid()
        Assert.assertEquals("Verify if is connected to the Wifi", ssid, connectedWifi)
    }

    private fun getCurrentWifiSsid(): String? {
        val context = InstrumentationRegistry.getInstrumentation().context
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        // The SSID is quoted, then we need to remove quotes
        return wifiInfo.ssid?.removeSurrounding("\"")
    }
}
