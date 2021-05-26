package com.inlacou.inksliderlibraryproject.ui.fragments.temperature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inksliderlibraryproject.R
import com.inlacou.inksliderlibraryproject.ui.sliders.BasicTemperatureSlider

class TemperatureFragment : Fragment() {
	
	private var tempSlider: BasicTemperatureSlider? = null
	private var tvDisplay: TextView? = null
	private var tvDisplayRealtime: TextView? = null
	private var btEnableDisable: Button? = null
	private var btExpandCollapse: Button? = null

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
		btEnableDisable = rootView.findViewById(R.id.bt_enable_disable)
		btExpandCollapse = rootView.findViewById(R.id.bt_expand_collapse)

		btEnableDisable?.setOnClickListener {
			tempSlider.let {
				if(it!=null) {
					it.isEnabled = !it.isEnabled
					btEnableDisable?.text = if(it.isEnabled) "disable" else "enable"
				}
			}
		}

		btExpandCollapse?.setOnClickListener {
			tempSlider.let {
				if(it!=null) {
					it.setExpanded(!it.isExpanded())
					btExpandCollapse?.text = if(it.isExpanded()) "collapse" else "expand"
				}
			}
		}

		tempSlider?.onValueSet = { item: InkSliderMdl.Item, fromUser: Boolean ->
			tvDisplay?.text = item.display.string
		}
		
		tempSlider?.onValueChange = { item: InkSliderMdl.Item, fromUser: Boolean ->
			tvDisplayRealtime?.text = item.display.string
		}
		
		return rootView
	}
}