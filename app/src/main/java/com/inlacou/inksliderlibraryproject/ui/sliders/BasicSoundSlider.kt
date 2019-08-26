package com.inlacou.inksliderlibraryproject.ui.sliders

import android.content.Context
import android.util.AttributeSet
import com.inlacou.inkslider.InkSlider
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inksliderlibraryproject.R

class BasicSoundSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: InkSlider(context, attrs, defStyleAttr) {
	
	private var onValueSet: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	private var onValueChange: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	private val colors = context.resources?.getIntArray(R.array.sound_slider)?.toList() ?: listOf()
	private var icons: List<InkSliderMdl.Item> = listOf()
	
	init {
		context.resources?.obtainTypedArray(R.array.sounds)?.let { array ->
			icons = (0 until array.length())
					.reversed()
					.mapIndexed { index, it ->
						InkSliderMdl.Item(value = it, selectable = index != 0, display = InkSliderMdl.Display(icon = array.getResourceId(index, -1), iconTintColor = R.color.white_basic))
					}
			model = InkSliderMdl(
					colors = colors
					, values = icons
					, onValueSet = { item: InkSliderMdl.Item, b: Boolean ->
				onValueSet?.invoke(item, b)
			}, onValueChange = { item: InkSliderMdl.Item, b: Boolean ->
				onValueChange?.invoke(item, b)
			})
			array.recycle()
		}
	}
}