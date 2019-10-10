package com.inlacou.inksliderlibraryproject.ui.fragments.temperature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inksliderlibraryproject.R
import com.inlacou.inksliderlibraryproject.ui.sliders.BasicTemperatureSlider

class TemperatureFragment : Fragment() {
	
	private var tempSlider: BasicTemperatureSlider? = null
	private var tvDisplay: TextView? = null
	private var tvDisplayRealtime: TextView? = null
	
	val value
		get() = tempSlider?.currentItem
	val values
		get() = tempSlider?.items
	
	fun setValue(value: InkSliderMdl.Item?, fireListener: Boolean){
		if(value!=null) tempSlider?.setCurrentItem(value, fireListener)
	}
	
	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		val rootView = inflater.inflate(R.layout.fragment_temperature, container, false)
		
		tempSlider = rootView.findViewById(R.id.temp_slider)
		tvDisplay = rootView.findViewById(R.id.tv_display)
		tvDisplayRealtime = rootView.findViewById(R.id.tv_display_realtime)
		
		tempSlider?.onValueSet = { item: InkSliderMdl.Item, fromUser: Boolean ->
			tvDisplay?.text = item.display.string
		}
		
		tempSlider?.onValueChange = { item: InkSliderMdl.Item, fromUser: Boolean ->
			tvDisplayRealtime?.text = item.display.string
		}
		
		return rootView
	}
}