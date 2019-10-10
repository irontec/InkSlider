package com.inlacou.inksliderlibraryproject.ui.sliders

import android.content.Context
import android.util.AttributeSet
import com.inlacou.inkslider.BaseInkSlider
import com.inlacou.inkslider.HorizontalInkSlider
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inkslider.VerticalInkSlider
import com.inlacou.inksliderlibraryproject.R

class BasicSoundSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: HorizontalInkSlider(context, attrs, defStyleAttr) {
	
	var onValueSet: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	var onValueChange: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	private val colors = context.resources?.getIntArray(R.array.sound_slider)?.toList() ?: listOf()
	private var sounds: List<InkSliderMdl.Item> = listOf()
	
	init {
		context.resources?.obtainTypedArray(R.array.sounds)?.let { array ->
			sounds = (0 until array.length())
					.reversed()
					.mapIndexed { index, it ->
						InkSliderMdl.Item(value = it, selectable = index != 0, display = InkSliderMdl.Display(icon = array.getResourceId(index, -1), iconTintColor = R.color.white_basic))
					}
			model = InkSliderMdl(
					colors = colors
					, values = sounds
					, displayMode = InkSliderMdl.DisplayMode.CENTER
					, onValueSet = { item: InkSliderMdl.Item, b: Boolean ->
				onValueSet?.invoke(item, b)
			}, onValueChange = { item: InkSliderMdl.Item, b: Boolean ->
				onValueChange?.invoke(item, b)
			})
			array.recycle()
		}
	}
}