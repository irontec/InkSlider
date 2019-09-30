package com.inlacou.inksliderlibraryproject

import android.content.Context
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.inlacou.inksliderlibraryproject.ui.fragments.temperature.TemperatureFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest: BaseInstTest() {
	
	val context: Context get() =  InstrumentationRegistry.getInstrumentation().context
	val device: UiDevice by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }
	
	@Rule
	@JvmField
	var mActivityRule: ActivityTestRule<MainActivity> = object : ActivityTestRule<MainActivity>(MainActivity::class.java){}
	
	@Test
	fun check_slider_temp_is_present() {
		R.id.temp_slider check isDisplayed()
	}
	
	@Test
	fun check_slider_temp_set() {
		R.id.temp_slider check isDisplayed()
		
		val currentFragment = (mActivityRule.activity.currentFragment as TemperatureFragment)
		
		val value = currentFragment.values?.get(1)!!
		mActivityRule.activity.runOnUiThread {
			currentFragment.setValue(value, false)
		}
		
		R.id.tv_display check ViewMatchers.withText(value.display.string)
	}
}
