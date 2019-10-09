package com.inlacou.inkslider

import android.content.Context
import android.util.AttributeSet
import android.view.View

open class HorizontalInkSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: BaseInkSlider(context, attrs, defStyleAttr) {
	
	override val orientation: InkSliderMdl.Orientation
		get() = InkSliderMdl.Orientation.HORIZONTAL
	
	override fun initialize() {
		val rootView = View.inflate(context, R.layout.view_ink_slider_ripple_horizontal, this)
		initialize(rootView)
	}
}

