package com.inlacou.inksliderlibraryproject.ui.fragments.temperature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inlacou.inksliderlibraryproject.R
import com.inlacou.inksliderlibraryproject.ui.sliders.BasicSoundSlider
import com.inlacou.inksliderlibraryproject.ui.sliders.BasicTemperatureSlider

class TemperatureFragment : Fragment() {
	
	private var tempSlider: BasicTemperatureSlider? = null
	
	var value
		get() = tempSlider?.currentItem
		set(value) {
			if(value!=null) tempSlider?.setCurrentItem(value, true)
		}
	val values
		get() = tempSlider?.items
	
	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		val rootView = inflater.inflate(R.layout.fragment_temperature, container, false)
		
		tempSlider = rootView.findViewById<BasicTemperatureSlider>(R.id.temp_slider)
		
		return rootView
	}
}