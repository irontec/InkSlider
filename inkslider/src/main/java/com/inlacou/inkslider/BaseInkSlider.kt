package com.inlacou.inkslider

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.inlacou.inkslider.InkSliderMdl.Orientation.*
import com.inlacou.pripple.RippleLinearLayout
import com.inlacou.pripple.RippleRelativeLayout
import com.inlacou.pripple.batchEdit
import kotlin.math.roundToInt

abstract class BaseInkSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: FrameLayout(context, attrs, defStyleAttr) {
	
	abstract val orientation: InkSliderMdl.Orientation
	
	private var anchor: View? = null
	private var currentPosition: Float? = null
	private var linearLayoutColors: LinearLayout? = null
	private var linearLayoutDisplayTopLeft: View? = null
	private var linearLayoutDisplayBottomRight: View? = null
	private var linearLayoutDisplayCenter: View? = null
	private var tvDisplayLeft: TextView? = null
	private var tvDisplayRight: TextView? = null
	private var ivDisplayLeft: ImageView? = null
	private var ivDisplayRight: ImageView? = null
	private var ivDisplayLeftArrow: ImageView? = null
	private var ivDisplayRightArrow: ImageView? = null
	private var linearLayoutDisplayCenterSpecial: View? = null
	private var rippleLayoutPlus: RippleRelativeLayout? = null
	private var rippleLayoutMinus: RippleRelativeLayout? = null
	private var ivPlus: ImageView? = null
	private var ivMinus: ImageView? = null
	
	private fun bindViews() {
		anchor = findViewById(R.id.anchor)
		linearLayoutColors = findViewById(R.id.linearLayout_colors)
		linearLayoutDisplayTopLeft = findViewById(R.id.linearLayout_display_top_left)
		linearLayoutDisplayBottomRight = findViewById(R.id.linearLayout_display_bottom_right)
		linearLayoutDisplayCenter = findViewById(R.id.linearLayout_display_center)
		tvDisplayLeft = findViewById(R.id.tv_display_left)
		tvDisplayRight = findViewById(R.id.tv_display_right)
		ivDisplayLeft = findViewById(R.id.iv_display_left)
		ivDisplayRight = findViewById(R.id.iv_display_right)
		ivDisplayLeftArrow = findViewById(R.id.iv_display_left_arrow)
		ivDisplayRightArrow = findViewById(R.id.iv_display_right_arrow)
		rippleLayoutPlus = findViewById(R.id.ripple_layout_plus)
		rippleLayoutMinus = findViewById(R.id.ripple_layout_minus)
		ivPlus = findViewById(R.id.iv_plus)
		ivMinus = findViewById(R.id.iv_minus)
		linearLayoutDisplayCenterSpecial = findViewById(R.id.linearLayout_display_center_special)
	}
	
	var model: InkSliderMdl = InkSliderMdl(colors = listOf(), values = listOf(InkSliderMdl.Item(value = "Any", display = InkSliderMdl.Display("Any"), selectable = true)))
		set(value) {
			field = value
			controller.model = value
			populate()
		}
	private lateinit var controller: InkSliderCtrl
	val items get() = if(reversed) model.values.asReversed() else model.values
	var currentItem: InkSliderMdl.Item
		get() = model.currentItem
		private set(value) {
			if(model.currentItem!=value){
				model.currentItem = value
				forceUpdate(false)
				controller.onCurrentItemChanged(value, false)
			}
		}
	
	/* Virtual variables for cleaner code */
	
	private val visibleTopLeft get() = model.displayMode==InkSliderMdl.DisplayMode.LEFT_TOP || model.displayMode==InkSliderMdl.DisplayMode.BOTH_SIDES
	private val visibleBottomRight get() = model.displayMode==InkSliderMdl.DisplayMode.RIGHT_BOTTOM || model.displayMode==InkSliderMdl.DisplayMode.BOTH_SIDES
	private val reversed get() = model.reverse xor (orientation== HORIZONTAL)
	/**
	 * Vertical height, horizontal width
	 */
	private val colorRowHeight get() = resources.getDimension(R.dimen.inkslider_row_vertical_height_horizontal_width).toInt()
	/**
	 * Vertical width, horizontal height
	 */
	private val colorRowWidth get() = resources.getDimension(R.dimen.inkslider_row_vertical_width_horizontal_height).toInt()
	private val indicatorCenterSpecialSize get() = resources.getDimension(R.dimen.inkslider_display_center_special_size).toInt()
	private val indicatorCenterSpecialSizeSelected get() = resources.getDimension(R.dimen.inkslider_display_center_special_size_selected).toInt()
	private val indicatorCenterSpecialStrokeWidth get() = resources.getDimension(R.dimen.inkslider_display_center_special_stroke_width).toInt()
	private val totalSize get() = model.colors.size*colorRowHeight
	private val stepSize: Float get() = totalSize/(items.size.toFloat())
	private val topSpacing get() = linearLayoutColors?.getCoordinates()?.top ?: 0
	private val leftSpacing get() = linearLayoutColors?.getCoordinates()?.left ?: 0
	
	init {
		this.initialize()
		setListeners()
		populate()
	}
	
	protected open fun initialize() {
		val rootView = View.inflate(context, R.layout.view_ink_slider_ripple, this)
		initialize(rootView)
	}
	
	protected fun initialize(view: View) {
		controller = InkSliderCtrl(view = this, model = model)
		bindViews()
	}
	
	protected fun populate() {
		clearItems()
		addItems()
		clearDisplays()
	}
	
	/* Public methods */
	/**
	 * Makes view appear with an android.R.anim.fade_in animation
	 */
	fun inAnimation() {
		val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
		linearLayoutColors?.startAnimation(animation)
	}
	
	fun setCurrentItemByIndex(volume: Int, fireListener: Boolean): Boolean {
		if(model.ignoreInputWhileUserInteraction && model.userTouch) return false
		//TODO if reverse
		if(currentItem.display!=items[volume].display) {
			model.currentItem = items[volume]
			forceUpdate(fireListener)
			return true
		}
		return false
	}
	
	fun setCurrentItem(item: InkSliderMdl.Item, fireListener: Boolean): Boolean {
		if(model.ignoreInputWhileUserInteraction && model.userTouch) return false
		if(currentItem.display!=item.display) {
			model.currentItem = item
			forceUpdate(fireListener)
			return true
		}
		return false
	}
	
	fun setCurrentItemByValue(item: Any, fireListener: Boolean): Boolean {
		if(model.ignoreInputWhileUserInteraction && model.userTouch) return false
		model.values.find { it.value==item }?.let { return setCurrentItem(it, fireListener) }
		return false
	}
	
	fun setCurrentItemByDisplay(display: String, fireListener: Boolean): Boolean {
		if(model.ignoreInputWhileUserInteraction && model.userTouch) return false
		model.values.find { it.display.string==display }?.let { return setCurrentItem(it, fireListener) }
		return false
	}
	
	/**
	 * Returns whether the component is enabled or not
	 */
	override fun isEnabled(): Boolean = model.enabled
	
	fun isExpanded(): Boolean = model.expanded
	
	/**
	 * Changes the component enabled state and updates it accordingly
	 */
	override fun setEnabled(enabled: Boolean) {
		val changed = enabled!=model.enabled
		model.enabled = enabled
		if(changed) if(enabled) onEnabled() else onDisabled()
	}
	/**
	 * Changes the component enabled state and updates it accordingly
	 */
	fun setExpanded(expanded: Boolean) {
		val changed = expanded!=model.expanded
		model.expanded = expanded
		if(changed) if(expanded) onExpanded() else onCollapsed()
	}
	/* /Public methods */
	
	/**
	 * Clears both displays resetting position and making them View.INVISIBLE
	 */
	private fun clearDisplays() {
		when(orientation) {
			VERTICAL -> {
				linearLayoutDisplayTopLeft?.setMargins(top = 0)
				linearLayoutDisplayBottomRight?.setMargins(top = 0)
				linearLayoutDisplayCenter?.setMargins(top = 0)
			}
			HORIZONTAL -> {
				linearLayoutDisplayTopLeft?.setMargins(left = 0)
				linearLayoutDisplayBottomRight?.setMargins(left = 0)
				linearLayoutDisplayCenter?.setMargins(left = 0)
			}
		}
		linearLayoutDisplayTopLeft?.setVisible(visible = false, holdSpaceOnDissapear = visibleTopLeft)
		linearLayoutDisplayBottomRight?.setVisible(visible = false, holdSpaceOnDissapear = visibleBottomRight)
		linearLayoutDisplayCenter?.setVisible(visible = false, holdSpaceOnDissapear = false)
		linearLayoutDisplayCenterSpecial?.setVisible(visible = false, holdSpaceOnDissapear = false)
		
		ivDisplayRight?.setVisible(visible = false, holdSpaceOnDissapear = false)
		ivDisplayLeft?.setVisible(visible = false, holdSpaceOnDissapear = false)
		tvDisplayRight?.setVisible(visible = false, holdSpaceOnDissapear = false)
		tvDisplayLeft?.setVisible(visible = false, holdSpaceOnDissapear = false)
		model.values.first().display.string?.let {
			tvDisplayRight?.setVisible(visible = true)
			tvDisplayLeft?.setVisible(visible = true)
			tvDisplayRight?.text = it
			tvDisplayLeft?.text = it
		}
		model.values.first().display.icon?.let {
			ivDisplayRight?.setVisible(visible = true)
			ivDisplayLeft?.setVisible(visible = true)
			ivDisplayRight?.setDrawableRes(it)
			ivDisplayLeft?.setDrawableRes(it)
		}
		
		anchor?.let {
			val negativeMargin = resources.getDimension(R.dimen.inkslider_display_negative_margin).toInt()
			val buttonHeight = resources.getDimension(R.dimen.inkslider_button_height).toInt()
			val buttonWidth = resources.getDimension(R.dimen.inkslider_button_width).toInt()
			if(orientation == HORIZONTAL) {
				var newHeight = colorRowWidth - (if (visibleTopLeft) negativeMargin else 0) - (if (visibleBottomRight) negativeMargin else 0)
				if (colorRowWidth < buttonHeight) {
					val correction = (buttonHeight - colorRowWidth) / 2
					if(visibleBottomRight && !visibleTopLeft) newHeight += correction else if(!visibleBottomRight && visibleTopLeft) newHeight -= correction/2
				}else{
					val correction = (colorRowWidth - buttonHeight) / 2
					if(!visibleBottomRight && visibleTopLeft) newHeight -= correction/2
				}
				it.layoutParams = it.layoutParams.apply { height = newHeight }
			}else{
				var newWidth = colorRowWidth - (if (visibleTopLeft) negativeMargin else 0) - (if (visibleBottomRight) negativeMargin else 0)
				if (colorRowWidth < buttonWidth) {
					val correction = (buttonWidth - colorRowWidth) / 2
					if(visibleBottomRight && !visibleTopLeft) newWidth += 0 else if(!visibleBottomRight && visibleTopLeft) newWidth -= correction/2
				}else{
					val correction = (colorRowWidth - buttonWidth) / 2
					if(!visibleBottomRight && visibleTopLeft) newWidth -= correction/2
				}
				it.layoutParams = it.layoutParams.apply { width = newWidth }
			}
		}
	}
	
	/**
	 * Removes all views from linearLayout
	 */
	private fun clearItems() {
		linearLayoutColors?.removeAllViews()
	}
	
	/**
	 * Adds an item to the linearLayout for each color in model colors array
	 */
	private fun addItems() {
		val colors = if(reversed) model.colors.asReversed() else model.colors
		val disabledMode = model.disableMode
		when  {
			model.disabled && disabledMode is InkSliderMdl.DisableModes.Tint -> colors.forEachIndexed { index: Int, item: Int -> addRow(resources.getColorCompat(disabledMode.tintColor), index, colors.size) }
			model.colorMode==InkSliderMdl.ColorMode.NORMAL -> colors.forEachIndexed { index: Int, item: Int -> addRow(item, index, colors.size) }
			model.colorMode==InkSliderMdl.ColorMode.GRADIENT -> colors.forEachIndexed { index: Int, item: Int -> addRow(if(index > 0) colors[index - 1] else item, item, index, colors.size) }
			model.colorMode==InkSliderMdl.ColorMode.GRADIENT_CONTINUOUS -> addGradient(colors)
		}
	}
	
	/**
	 * Adds a row to the linearLayout with provided {@param colorResId} solid color background
	 */
	private fun addRow(colorResId: Int, index: Int, maxItems: Int) {
		linearLayoutColors?.let { linearLayout ->
			when(orientation) {
				HORIZONTAL -> if(linearLayoutColors?.height!=colorRowWidth) { linearLayout.layoutParams = linearLayout.layoutParams.apply { height = colorRowWidth } }
				VERTICAL -> if(linearLayoutColors?.width!=colorRowWidth) { linearLayout.layoutParams = linearLayout.layoutParams.apply { width = colorRowWidth } }
			}
			val view = View(context)
			linearLayout.addView(view)
			view.apply {
				background = GradientDrawable().apply {
					setColor(colorResId)
					cornerRadii = getCorners(index, maxItems)
				}
				layoutParams = layoutParams.apply {
					when(orientation) {
						HORIZONTAL -> { width = colorRowHeight; height = colorRowWidth }
						VERTICAL -> { height = colorRowHeight; width = colorRowWidth }
					}
				}
			}
		}
	}
	
	/**
	 * Adds a row to the linearLayout with provided {@param colorResId} to {@param secondColorResId} gradient background
	 */
	private fun addRow(colorResId: Int, secondColorResId: Int, index: Int, maxItems: Int) {
		linearLayoutColors?.let { linearLayout ->
			when(orientation){
				HORIZONTAL -> if(linearLayoutColors?.height!=colorRowWidth) { linearLayout.layoutParams = linearLayout.layoutParams.apply { height = colorRowWidth } }
				VERTICAL -> if(linearLayoutColors?.width!=colorRowWidth) { linearLayout.layoutParams = linearLayout.layoutParams.apply { width = colorRowWidth } }
			}
			val view = View(context)
			linearLayout.addView(view)
			view.apply {
				background = GradientDrawable(when(orientation) {
					VERTICAL -> if(model.reverse) GradientDrawable.Orientation.BOTTOM_TOP else GradientDrawable.Orientation.TOP_BOTTOM
					HORIZONTAL -> if(model.reverse) GradientDrawable.Orientation.RIGHT_LEFT else GradientDrawable.Orientation.LEFT_RIGHT
				}, intArrayOf(colorResId, secondColorResId)).apply {
					cornerRadii = getCorners(index, maxItems)
				}
				layoutParams = (layoutParams).apply {
					when(orientation) {
						VERTICAL -> { height = colorRowHeight; width = colorRowWidth }
						HORIZONTAL -> { width = colorRowHeight; height = colorRowWidth }
					}
				}
			}
		}
	}
	
	private fun addGradient(colors: List<Int>) {
		linearLayoutColors?.let { linearLayout ->
			when(orientation){
				HORIZONTAL -> if(linearLayoutColors?.height!=colorRowWidth) { linearLayout.layoutParams = linearLayout.layoutParams.apply { height = colorRowWidth } }
				VERTICAL -> if(linearLayoutColors?.width!=colorRowWidth) { linearLayout.layoutParams = linearLayout.layoutParams.apply { width = colorRowWidth } }
			}
			val view = View(context)
			linearLayout.addView(view)
			view.apply {
				background = GradientDrawable(when(orientation) {
					VERTICAL -> if(model.reverse) GradientDrawable.Orientation.BOTTOM_TOP else GradientDrawable.Orientation.TOP_BOTTOM
					HORIZONTAL -> if(model.reverse) GradientDrawable.Orientation.RIGHT_LEFT else GradientDrawable.Orientation.LEFT_RIGHT
				}, colors.toIntArray()).apply {
					cornerRadii = floatArrayOf(
							model.cornerRadius, model.cornerRadius, model.cornerRadius, model.cornerRadius,
							model.cornerRadius, model.cornerRadius, model.cornerRadius, model.cornerRadius)
				}
				layoutParams = (layoutParams).apply {
					when(orientation) {
						VERTICAL -> { height = colorRowHeight*colors.size; width = colorRowWidth }
						HORIZONTAL -> { width = colorRowHeight*colors.size; height = colorRowWidth }
					}
				}
			}
		}
	}
	
	private fun getCorners(index: Int, maxItems: Int): FloatArray {
		return when(orientation){
			VERTICAL -> when (index) {
				0 -> floatArrayOf(
						model.cornerRadius, model.cornerRadius, model.cornerRadius, model.cornerRadius,
						0f, 0f, 0f, 0f)
				maxItems-1 -> floatArrayOf(
						0f, 0f, 0f, 0f,
						model.cornerRadius, model.cornerRadius, model.cornerRadius, model.cornerRadius)
				else -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
			}
			HORIZONTAL -> when (index) {
				0 -> floatArrayOf(
						model.cornerRadius, model.cornerRadius, 0f, 0f,
						0f, 0f, model.cornerRadius, model.cornerRadius)
				maxItems-1 -> floatArrayOf(
						0f, 0f, model.cornerRadius, model.cornerRadius,
						model.cornerRadius, model.cornerRadius, 0f, 0f)
				else -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
			}
		}
	}
	
	private var longClickLastTrigger: Long = 0
	private var longClickSpeedMax = 500
	private var longClickSpeedMin = 50
	private var longClickSpeedStep = 150
	private var longClickSpeed = longClickSpeedMax
	
	@SuppressLint("ClickableViewAccessibility")
	private fun setListeners() {
		//Touch listeners
		rippleLayoutPlus?.isClickable = true
		rippleLayoutPlus?.setOnTouchListener { view, motionEvent ->
			if(model.disabled && !model.disableMode.reactToUserInput) return@setOnTouchListener false
			when(motionEvent?.action){
				MotionEvent.ACTION_DOWN -> {
					//Start
					attemptClaimDrag()
					controller.onPlusClick()
					longClickLastTrigger = System.currentTimeMillis()
					longClickSpeed = longClickSpeedMax
				}
				MotionEvent.ACTION_MOVE -> {
					rippleLayoutPlus?.onTouchEvent(motionEvent)
					val now = System.currentTimeMillis()
					if(now-longClickLastTrigger>longClickSpeed) {
						longClickLastTrigger = now
						if(longClickSpeed>longClickSpeedMin) longClickSpeed -= longClickSpeedStep
						controller.onPlusClick()
					}
				}
				MotionEvent.ACTION_UP -> { controller.onPlusRelease() }
			}
			false
		}
		
		rippleLayoutMinus?.isClickable = true
		rippleLayoutMinus?.setOnTouchListener { view, motionEvent ->
			if(model.disabled && !model.disableMode.reactToUserInput) return@setOnTouchListener false
			when(motionEvent?.action){
				MotionEvent.ACTION_DOWN -> {
					//Start
					attemptClaimDrag()
					controller.onMinusClick()
					longClickLastTrigger = System.currentTimeMillis()
					longClickSpeed = longClickSpeedMax
				}
				MotionEvent.ACTION_MOVE -> {
					val now = System.currentTimeMillis()
					if(now-longClickLastTrigger>longClickSpeed) {
						longClickLastTrigger = now
						if(longClickSpeed>longClickSpeedMin) longClickSpeed -= longClickSpeedStep
						controller.onMinusClick()
					}
				}
				MotionEvent.ACTION_UP -> { controller.onMinusRelease() }
			}
			false
		}
		
		//Touch listener
		linearLayoutColors?.setOnTouchListener { _, event ->
			if(model.disabled && !model.disableMode.reactToUserInput) return@setOnTouchListener false
			val relativePosition = when(orientation){
				VERTICAL -> event.y
				HORIZONTAL -> event.x
			} //reaches 0 at top linearLayoutColors and goes on the minus realm if you keep going up
			currentPosition = relativePosition
			val roughStep = if(reversed) (relativePosition/stepSize)-1 else (relativePosition/stepSize)
			val step = (relativePosition/stepSize).roundToInt()
			val newItem = when {
				step<=0 -> items.getFirstSelectable()
				step>=items.size -> items.getLastSelectable()
				else -> items.getNearestSelectable(roughIndex = roughStep)
			}
			if(currentItem!=newItem) {
				controller.onCurrentItemChanged(newItem, true)
			}
			model.currentItem = newItem
			updateDisplays(event.action==MotionEvent.ACTION_MOVE || event.action==MotionEvent.ACTION_DOWN)
			
			when(event.action) {
				MotionEvent.ACTION_DOWN -> {
					attemptClaimDrag()
					controller.onTouchStart()
					true
				}
				MotionEvent.ACTION_CANCEL -> false
				MotionEvent.ACTION_UP -> {
					if(model.hardSteps) forceUpdate(true) else controller.onTouchRelease()
					false
				}
				MotionEvent.ACTION_MOVE -> true
				else -> false
			}
		}
	}
	
	fun forceUpdate(fromUser: Boolean) {
		val currentItemPos = model.values.indexOf(model.currentItem)
		currentPosition = if(reversed) {
			totalSize-(stepSize*currentItemPos)
		}else{
			stepSize*currentItemPos
		}.toFloat()
		updateDisplays()
		controller.onValueSet(fromUser)
	}
	
	private fun updateDisplays(touching: Boolean = false) {
		//Shows/hides displays depending on current DisplayMode
		linearLayoutDisplayTopLeft?.setVisible(visibleTopLeft && !(model.disabled && !model.disableMode.showIndicator), false)
		linearLayoutDisplayBottomRight?.setVisible(visibleBottomRight && !(model.disabled && !model.disableMode.showIndicator), false)
		linearLayoutDisplayCenter?.setVisible(model.displayMode==InkSliderMdl.DisplayMode.CENTER && !(model.disabled && !model.disableMode.showIndicator), false)
		linearLayoutDisplayCenterSpecial?.setVisible(model.displayMode==InkSliderMdl.DisplayMode.CENTER_SPECIAL && !(model.disabled && !model.disableMode.showIndicator), false)
		
		//Style display
		model.currentItem.display.let { display ->
			var color: Int? = null
			var colorAccent: Int = Color.WHITE
			display.textColor?.let {
				color = it
			}
			val disableMode = model.disableMode
			if(model.disabled && disableMode is InkSliderMdl.DisableModes.Tint) {
				color = resources.getColorCompat(disableMode.tintColor)
				colorAccent = resources.getColorCompat(disableMode.tintColorAccent)
			}
			
			//Set display text
			display.string.let {
				if (it != null) {
					tvDisplayRight?.text = it
					tvDisplayLeft?.text = it
				}
				tvDisplayRight?.setVisible(it!=null, false)
				tvDisplayLeft?.setVisible(it!=null, false)
			}
			//Set display text color
			display.textColor?.let {
				tvDisplayRight?.setTextColor(it)
				tvDisplayLeft?.setTextColor(it)
			}
			//Set display icon
			display.icon.let {
				if (it != null) {
					ivDisplayLeft?.setDrawableRes(it)
					ivDisplayRight?.setDrawableRes(it)
				}
				ivDisplayRight?.setVisible(it!=null, false)
				ivDisplayLeft?.setVisible(it!=null, false)
			}
			//Set display icon color
			display.iconTintColor?.let {
				ivDisplayLeft?.tint(it)
				ivDisplayRight?.tint(it)
			}
			//Set display icon color
			display.arrowTintColor?.let {
				ivDisplayLeftArrow?.tint(it)
				ivDisplayRightArrow?.tint(it)
			}
			linearLayoutDisplayCenterSpecial?.let { view ->
				color?.let {
					view.layoutParams = view.layoutParams?.apply {
						width = if (touching) indicatorCenterSpecialSizeSelected else indicatorCenterSpecialSize
						height = if (touching) indicatorCenterSpecialSizeSelected else indicatorCenterSpecialSize
					}
					view.background = GradientDrawable().apply {
						cornerRadius = 300f
						setStroke(indicatorCenterSpecialStrokeWidth, colorAccent)
						setColor(it)
					}
				}
			}
		}
		
		//Set display position
		when(orientation){
			HORIZONTAL -> {
				Log.d("InkSlider", "horizontal position: $currentPosition")
				currentPosition?.toInt()?.let {
					if (it in 1 until (linearLayoutColors?.width ?: width)) {
						linearLayoutDisplayTopLeft?.setMargins(left = it-((linearLayoutDisplayTopLeft?.width?:0)/2)+leftSpacing)
						linearLayoutDisplayBottomRight?.setMargins(left = it-((linearLayoutDisplayBottomRight?.width?:0)/2)+leftSpacing)
						linearLayoutDisplayCenter?.setMargins(left = it-((linearLayoutDisplayCenter?.width?:0)/2)+leftSpacing)
						linearLayoutDisplayCenterSpecial?.setMargins(left = it-((linearLayoutDisplayCenterSpecial?.width?:0)/2)+leftSpacing)
					}else{
						Log.d("InkSlider", "horizontal else ${(linearLayoutColors?.width ?: width)}")
					}
				}
			}
			VERTICAL -> {
				Log.d("InkSlider", "vertical position: $currentPosition")
				currentPosition?.toInt()?.let {
					if (it in 1 until (linearLayoutColors?.height ?: height)) {
						linearLayoutDisplayTopLeft?.setMargins(top = it - ((linearLayoutDisplayTopLeft?.height ?: 0) / 2) + topSpacing)
						linearLayoutDisplayBottomRight?.setMargins(top = it - ((linearLayoutDisplayBottomRight?.height ?: 0) / 2) + topSpacing)
						linearLayoutDisplayCenter?.setMargins(top = it - ((linearLayoutDisplayCenter?.height ?: 0) / 2) + topSpacing)
						linearLayoutDisplayCenterSpecial?.setMargins(top = it - (resources.getDimension(R.dimen.inkslider_display_center_special_size).toInt() / 2) + topSpacing)
					} else {
						Log.d("InkSlider", "vertical else ${(linearLayoutColors?.height ?: height)}")
					}
				}
			}
		}
	}
	
	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private fun attemptClaimDrag() {
		parent?.requestDisallowInterceptTouchEvent(true)
	}
	
	/**
	 * Changes component to it's expanded state
	 */
	private fun onExpanded() {
		clearItems()
		linearLayoutColors?.onDrawn { forceUpdate(true) } //TODO only when coming from empty
		addItems()
	}
	
	/**
	 * Changes component to it's collapsed state
	 */
	private fun onCollapsed() {
		clearItems()
		clearDisplays()
	}
	
	/**
	 * Changes component to it's enabled state
	 */
	private fun onEnabled() {
		updateButtonThemes()
		if(isExpanded()) {
			clearItems()
			linearLayoutColors?.onDrawn { forceUpdate(true) } //TODO only when coming from empty
			addItems()
		}
	}
	
	/**
	 * Changes component to it's disabled state
	 */
	private fun onDisabled() {
		updateButtonThemes()
		if(isExpanded()) {
			clearItems()
			linearLayoutColors?.onDrawn { forceUpdate(true) } //TODO only when coming from empty
			addItems()
		}
	}
	
	private fun updateButtonThemes() {
		val disabledMode = model.disableMode
		rippleLayoutPlus?.batchEdit {
			this.normalBackgroundColor = resources.getColorCompat(if(!model.enabled && disabledMode is InkSliderMdl.DisableModes.Tint) disabledMode.tintColor else R.color.inkslider_plus_minus_button_color)
			this.rippleBackgroundColor = resources.getColorCompat(if(!model.enabled && disabledMode is InkSliderMdl.DisableModes.Tint) disabledMode.tintColorAccent else R.color.inkslider_plus_minus_button_color_ripple)
		}
		rippleLayoutMinus?.batchEdit {
			this.normalBackgroundColor = resources.getColorCompat(if(!model.enabled && disabledMode is InkSliderMdl.DisableModes.Tint) disabledMode.tintColor else R.color.inkslider_plus_minus_button_color)
			this.rippleBackgroundColor = resources.getColorCompat(if(!model.enabled && disabledMode is InkSliderMdl.DisableModes.Tint) disabledMode.tintColorAccent else R.color.inkslider_plus_minus_button_color_ripple)
		}
		rippleLayoutPlus?.isEnabled = model.enabled || model.disableMode.reactToUserInput
		rippleLayoutMinus?.isEnabled = model.enabled || model.disableMode.reactToUserInput
		ivPlus?.tint(if(!model.enabled && disabledMode is InkSliderMdl.DisableModes.Tint) disabledMode.tintColorAccent else R.color.inkslider_plus_minus_button_icon_color)
		ivMinus?.tint(if(!model.enabled && disabledMode is InkSliderMdl.DisableModes.Tint) disabledMode.tintColorAccent else R.color.inkslider_plus_minus_button_icon_color)
	}
	
}

