package com.inlacou.inksliderlibraryproject.ui.fragments.sound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inlacou.inksliderlibraryproject.R

class SoundFragment : Fragment() {
	
	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_sound, container, false)
	}
}