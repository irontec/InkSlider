package com.inlacou.inksliderlibraryproject.ui.fragments.sound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inksliderlibraryproject.R
import com.inlacou.inksliderlibraryproject.ui.sliders.BasicSoundSlider

class SoundFragment : Fragment() {
	
	private var soundSlider: BasicSoundSlider? = null
	private var tvDisplay: TextView? = null
	private var tvDisplayRealtime: TextView? = null
	private var btEnableDisable: Button? = null
	private var btExpandCollapse: Button? = null
	
	val value
		get() = soundSlider?.currentItem
	val values
		get() = soundSlider?.items
	
	fun setValue(value: InkSliderMdl.Item?, fireListener: Boolean){
		if(value!=null) soundSlider?.setCurrentItem(value, fireListener)
	}
	
	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		val rootView = inflater.inflate(R.layout.fragment_sound, container, false)
		
		soundSlider = rootView.findViewById(R.id.sound_slider)
		tvDisplay = rootView.findViewById(R.id.tv_display)
		tvDisplayRealtime = rootView.findViewById(R.id.tv_display_realtime)
		btEnableDisable = rootView.findViewById(R.id.bt_enable_disable)
		btExpandCollapse = rootView.findViewById(R.id.bt_expand_collapse)
		
		btEnableDisable?.setOnClickListener {
			soundSlider.let {
				if(it!=null) {
					it.isEnabled = !it.isEnabled
					btEnableDisable?.text = if(it.isEnabled) "disable" else "enable"
				}
			}
		}
		
		btExpandCollapse?.setOnClickListener {
			soundSlider.let {
				if(it!=null) {
					it.setExpanded(!it.isExpanded())
					btExpandCollapse?.text = if(it.isExpanded()) "collapse" else "expand"
				}
			}
		}
		
		soundSlider?.onValueSet = { item: InkSliderMdl.Item, fromUser: Boolean ->
			tvDisplay?.text = item.display.string
		}
		
		soundSlider?.onValueChange = { item: InkSliderMdl.Item, fromUser: Boolean ->
			tvDisplayRealtime?.text = item.display.string
		}
		
		return rootView
	}
}