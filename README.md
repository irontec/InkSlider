# InkSliderLibraryProject

[![](https://jitpack.io/v/irontec/InkSlider.svg)](https://jitpack.io/#irontec/InkSlider)

Library to make fancy selector sliders. Indicators can be shown on both sides, one or none. Values and colors are not linked, so you can have a lot more values than colors.

# Parametrization

## By code

* *color*: Color resource id list.
* *values*: Values (**Item** below) list to select.
* *currentItem*: currently selected item.
* *onValueSet*: listener when a values is **selected**. When the user releases the touch.
* *onValueChange*: listener when a value is **displayed**. When the user moves the finger and the value changes.
* *hardSteps*: Defines if the marker can stop in any place when user press released, or only on some hard points (the center of the realm associated with a value)
* *enabled*: if view is enabled or disabled.
* *displayMode*. Values: LEFT, RIGHT, BOTH.
* *Item*
  * *value*: **Any**. Put here what you want the user to be able to select.
  * *display*: **Display** class (below)
  * *selectable*: If this value is selectable. I use it to add some unselectable values on top or bottom if limits should not be selectable.
* *Display*
  * *string*: indicator text value (optional)
  * *textColor*: indicator text color (optional)
  * *arrowTintColor*: indicator arrow tint color (optional)
  * *iconTintColor*: indicator icon tint color (optional)
  * *icon*: indicator icon (optional)

## By XML

### Dimension

```XML
<resources>
	<!-- DISPLAYS -->
	<dimen name="inkslider_display_horizontal_separation">17dp</dimen>
	<dimen name="inkslider_display_arrow_size">18dp</dimen>
	<dimen name="inkslider_display_text_size">33sp</dimen>
	<dimen name="inkslider_side_display_icon_width">60dp</dimen>
	<dimen name="inkslider_side_display_icon_height">60dp</dimen>
	<dimen name="inkslider_display_negative_margin">10dp</dimen>
	<dimen name="inkslider_display_center_special_size">20dp</dimen>
	<dimen name="inkslider_display_center_special_size_selected">50dp</dimen>
	<dimen name="inkslider_display_center_special_stroke_width">1.5dp</dimen>

	<!-- ROW -->
	<dimen name="inkslider_row_vertical_height_horizontal_width">24dp</dimen>
	<dimen name="inkslider_row_vertical_width_horizontal_height">48dp</dimen>

	<!-- BUTTONS -->
	<dimen name="inkslider_button_width">72dp</dimen>
	<dimen name="inkslider_button_height">72dp</dimen>

	<!-- TOP BUTTON CORNERS -->
	<dimen name="inkslider_button_top_corner_top_left">64dp</dimen>
	<dimen name="inkslider_button_top_corner_top_right">64dp</dimen>
	<dimen name="inkslider_button_top_corner_bottom_left">64dp</dimen>
	<dimen name="inkslider_button_top_corner_bottom_right">64dp</dimen>

	<!-- BOTTOM BUTTON CORNERS -->
	<dimen name="inkslider_button_bottom_corner_top_left">64dp</dimen>
	<dimen name="inkslider_button_bottom_corner_top_right">64dp</dimen>
	<dimen name="inkslider_button_bottom_corner_bottom_left">64dp</dimen>
	<dimen name="inkslider_button_bottom_corner_bottom_right">64dp</dimen>

	<!-- BUTTON MARGINS -->
	<dimen name="inkslider_button_top_margin_bottom">16dp</dimen>
	<dimen name="inkslider_button_bottom_margin_top_right">16dp</dimen>
	<dimen name="inkslider_button_bottom_margin_bottom_left">16dp</dimen>
</resources>
```

### Colors

```XML
<resources>
	<color name="inkslider_plus_minus_button_color">@color/colorprimary</color>
	<color name="inkslider_plus_minus_button_color_ripple">@color/colorprimaryripple</color>
	<color name="inkslider_plus_minus_button_icon_color">#FFF</color>
	<color name="inkslider_indicator_arrow_color">#FFF</color>
</resources>
```

# Examples

Temperature slider class:

```kt
import android.content.Context
import android.util.AttributeSet
import com.inlacou.inkslider.BaseInkSlider
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
import com.inlacou.inkslider.BaseInkSlider
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
