package com.inlacou.inksliderlibraryproject

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import org.hamcrest.Matcher

abstract class BaseInstTest {
	
	fun getStrings(resId: Int): Array<String> = InstrumentationRegistry.getInstrumentation().targetContext.resources.getStringArray(resId)
	fun getString(resId: Int) = InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(resId)
	infix fun Int.check(matcher: Matcher<View>): ViewInteraction = Espresso.onView(ViewMatchers.withId(this)).check(ViewAssertions.matches(matcher))
	
	fun UiDevice.makeClick(id: Int) {
		Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
	}
	fun UiDevice.makeClick(text: String): Boolean {
		return findObject(UiSelector().text(text)).click()
	}
	fun UiDevice.makeClick(texts: Array<String>): Boolean = makeClick(texts.toList())
	fun UiDevice.makeClick(texts: List<String>): Boolean {
		var clicked = false
		texts.forEach { text ->
			try{
				if(findObject(UiSelector().text(text)).click()) clicked = true
			} catch (uonfoe: UiObjectNotFoundException) {}
		}
		if(!clicked) throw UiObjectNotFoundException("No Ui object found with text any of $texts")
		return clicked
	}
	
	fun UiDevice.clickCheckable(text: String): Boolean {
		return findObject(UiSelector().text(text)).let {
			if(it?.isCheckable==true) it.click()
			else false
		}
	}
}