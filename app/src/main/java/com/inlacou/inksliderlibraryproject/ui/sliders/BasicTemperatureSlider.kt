package com.inlacou.inksliderlibraryproject.ui.sliders

import android.content.Context
import android.util.AttributeSet
import com.inlacou.inkslider.InkSlider
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inksliderlibraryproject.R

class BasicTemperatureSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: InkSlider(context, attrs, defStyleAttr){
	
	var onValueSet: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	var onValueChange: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	private val colors = context.resources?.getIntArray(R.array.temperature_slider)?.toList() ?: listOf()
	private val temperatures: List<InkSliderMdl.Item>
	
	init {
		temperatures = (170 .. 270)
				.filter { it.toString().last()=='0' || it.toString().last()=='5' }
				.map { it.toDouble() }.reversed()
				.mapIndexed { index, it -> InkSliderMdl.Item(value = it / 10, display = InkSliderMdl.Display("${it / 10}º", textColor = colors[(index + 1) / 2])) }
				.toMutableList()
		temperatures.add(0, InkSliderMdl.Item(value = 27, display = InkSliderMdl.Display(string = "27.0º", textColor = colors[0]), selectable = false))
		model = InkSliderMdl(
				colors = colors
				, values = temperatures
				, onValueSet = { item: InkSliderMdl.Item, b: Boolean ->
			onValueSet?.invoke(item, b)
		}, onValueChange = { item: InkSliderMdl.Item, b: Boolean ->
			onValueChange?.invoke(item, b)
		})
	}
	
}