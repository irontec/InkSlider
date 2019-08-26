package com.inlacou.inksliderlibraryproject.ui.fragments.temperature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inlacou.inksliderlibraryproject.R

class TemperatureFragment : Fragment() {
	
	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_temperature, container, false)
	}
}