package com.inlacou.inksliderlibraryproject.ui.fragments.sound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inlacou.inksliderlibraryproject.R
import com.inlacou.inksliderlibraryproject.ui.sliders.BasicSoundSlider

class SoundFragment : Fragment() {
	
	private var soundSlider: BasicSoundSlider? = null
	
	var value
		get() = soundSlider?.currentItem
		set(value) {
			if(value!=null) soundSlider?.setCurrentItem(value, true)
		}
	val values
		get() = soundSlider?.items
	
	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		val rootView = inflater.inflate(R.layout.fragment_sound, container, false)
		
		soundSlider = rootView.findViewById<BasicSoundSlider>(R.id.sound_slider)
		
		return rootView
	}
}