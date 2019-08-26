# InkSliderLibraryProject

[![](https://jitpack.io/v/irontec/InkSlider.svg)](https://jitpack.io/#irontec/InkSlider)

Library to make fancy selector sliders. Indicators can be shown on both sides, one or none. Values and colors are not linked, so you can have a lot more values than colors.

![temp gif](https://github.com/irontec/InkSliderLibrary/blob/master/temp.gif)
![soung gif](https://github.com/irontec/InkSliderLibrary/blob/master/sound.gif)

# Parametrization

* *color*: Color resource id list.
* *values*: Values (**Item** below) list to select.
* *currentItem*: currently selected item.
* *onValueSet*: listener when a values is **selected**. When the user releases the touch.
* *onValueChange*: listener when a value is **displayed**. When the user moves the finger and the value changes.
* *hardSteps*: Defines if the marker can stop in any place when user press released, or only on some hard points (the center of the realm associated with a value)
* *enabled*: if view is enabled or disabled.
* *displayMode*. Values: LEFT, RIGHT, BOTH.
* *Item*
* * *value*: **Any**. Put here what you want the user to be able to select.
* * *display*: **Display** class (below)
* * *selectable*: If this value is selectable. I use it to add some unselectable values on top or bottom if limits should not be selectable.
* *Display*
* * *string*: indicator text value (optional)
* * *textColor*: indicator text color (optional)
* * *arrowTintColor*: indicator arrow tint color (optional)
* * *iconTintColor*: indicator icon tint color (optional)
* * *icon*: indicator icon (optional)

# Examples

Temperature slider class:

```kt
import android.content.Context
import android.util.AttributeSet
import com.inlacou.inkslider.InkSlider
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inksliderlibraryproject.R

class BasicTemperatureSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: InkSlider(context, attrs, defStyleAttr){
	
	var onValueSet: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	var onValueChange: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	val colors = context.resources?.getIntArray(R.array.temperature_slider)?.toList() ?: listOf()
	val temperatures: List<InkSliderMdl.Item>
	
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
```

Sound slider class:

```kt
import android.content.Context
import android.util.AttributeSet
import com.inlacou.inkslider.InkSlider
import com.inlacou.inkslider.InkSliderMdl
import com.inlacou.inksliderlibraryproject.R

class BasicSoundSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: InkSlider(context, attrs, defStyleAttr) {
	
	var onValueSet: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	var onValueChange: ((InkSliderMdl.Item, fromUser: Boolean) -> Unit)? = null
	val colors = context.resources?.getIntArray(R.array.sound_slider)?.toList() ?: listOf()
	var icons: List<InkSliderMdl.Item> = listOf()
	
	init {
		context.resources?.obtainTypedArray(R.array.sounds)?.let { array ->
			icons = (0 until array.length())
					.reversed()
					.mapIndexed { index, it ->
						InkSliderMdl.Item(value = it, selectable = index != 0, display = InkSliderMdl.Display(icon = array.getResourceId(index, -1)))
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
```
