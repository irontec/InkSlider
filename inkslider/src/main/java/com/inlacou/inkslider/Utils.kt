package com.inlacou.inkslider

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import kotlin.math.roundToInt


/* List manipulation */
/**
 * Gets first selectable item.
 * @throws NoSuchElementException when there is no item to select (no selectable items).
 */
internal fun List<InkSliderMdl.Item>.getFirstSelectable(): InkSliderMdl.Item {
	return first { it.selectable }
}

/**
 * Gets last selectable item.
 * @throws NoSuchElementException when there is no item to select (no selectable items).
 */
internal fun List<InkSliderMdl.Item>.getLastSelectable(): InkSliderMdl.Item {
	return last { it.selectable }
}

/**
 * Gets nearest selectable item. Ignores non selectable items and current item.
 * @throws NoSuchElementException when there is no item to select (no selectable items).
 */
internal fun List<InkSliderMdl.Item>.getNearestSelectable(roughIndex: Float): InkSliderMdl.Item {
	return getNearestSelectable(roughIndex = roughIndex, selectable = { it.selectable })
}
/* /List manipulation */

/* List extensions */
/**
 * Gets nearest selectable item. Ignores non selectable items.
 * @param roughIndex approximate index to start search. Rounded up. On equal distance, higher index has priority.
 * @param selectable method to filter only to selectable items
 * @return T
 * @throws NoSuchElementException when there is no item to select (no selectable items).
 */
private fun <T> List<T>.getNearestSelectable(roughIndex: Float, selectable: (T) -> Boolean): T {
	val index = roughIndex.roundToInt()
	val roundUp = index>=roughIndex
	return getNextNearestSelectable(lastIndex = index, goUp = !roundUp, selectable = selectable)
}

/**
 * Gets next selectable item on list. Ignores non selectable items.
 * @param lastIndex to know where we are
 * @param stepSize to know step size (it increases, as we go bouncing top/bottom because we check for the nearest one on any direction.
 * @param goUp to know current direction
 * @param topReached if top is reached, we no longer need to go in that direction
 * @param bottomReached if bottom reached, we no longer need to go in that direction
 * @param selectable method to filter only to selectable items
 * @return T
 * @throws NoSuchElementException when there is no item to select (no selectable items in any direction, or no items at all, for example).
 */
private fun <T> List<T>.getNextNearestSelectable(lastIndex: Int, stepSize: Int = 0, goUp: Boolean, topReached: Boolean = false, bottomReached: Boolean = false, selectable: (T) -> Boolean): T {
	var auxTopReached = topReached
	var auxBottomReached = bottomReached
	val newIndex = lastIndex+
			if(goUp) stepSize
			else -stepSize
	val currentItem = try {
		get(newIndex)
	}catch (ioobe: IndexOutOfBoundsException){
		if(goUp) auxTopReached = true
		if(!goUp) auxBottomReached = true
		null
	}
	return if(currentItem!=null && selectable.invoke(currentItem)){
		currentItem
	}else if(topReached && bottomReached) {
		throw NoSuchElementException()
	}else {
		getNextNearestSelectable(lastIndex = newIndex, stepSize = when {
			topReached -> 1
			bottomReached -> 1
			else -> stepSize+1
		}, goUp = when {
			topReached -> false
			bottomReached -> true
			else -> !goUp
		}, topReached = auxTopReached, bottomReached = auxBottomReached, selectable = selectable)
	}
}
/* /List extensions */

/* View extensions */
internal fun View.getCoordinates(): Rect {
	val offsetViewBounds = Rect()
	//returns the visible bounds
	getDrawingRect(offsetViewBounds)
	// calculates the relative coordinates to the parent
	parent?.let {
		if(it is ViewGroup) it.offsetDescendantRectToMyCoords(this, offsetViewBounds)
	}
	return offsetViewBounds
}

internal fun View?.setVisible(visible: Boolean, holdSpaceOnDissapear: Boolean = false) {
	if (this == null) return
	if(visible){
		this.visibility = View.VISIBLE
	}else{
		if(holdSpaceOnDissapear){
			this.visibility = View.INVISIBLE
		}else{
			this.visibility = View.GONE
		}
	}
}

internal fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
	if (layoutParams is ViewGroup.MarginLayoutParams) {
		val p = layoutParams as ViewGroup.MarginLayoutParams
		p.setMargins(left ?: p.leftMargin, top ?: p.topMargin, right ?: p.rightMargin, bottom ?: p.bottomMargin)
		requestLayout()
	}
}

internal fun ImageView.setDrawableRes(resId: Int){
	this.setImageDrawable(resources.getDrawableCompat(resId))
}

internal fun Resources.getDrawableCompat(resId: Int): Drawable {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		getDrawable(resId, null)
	}else{
		getDrawable(resId)
	}
}

/**
 * Called on onGlobalLayout called from ViewTreeObserver.OnGlobalLayoutListener
 */
internal fun View.onDrawn(callback: () -> Unit){
	val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
		override fun onGlobalLayout() {
			viewTreeObserver?.removeOnGlobalLayoutListener(this)
			callback.invoke()
		}
	}
	viewTreeObserver?.addOnGlobalLayoutListener(listener)
}

internal fun ImageView.tint(colorResId: Int){
	ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(this.context.getColorCompat(colorResId)))
}

internal fun Context.getColorCompat(resId: Int): Int {
	return resources.getColorCompat(resId)
}

internal fun Resources.getColorCompat(resId: Int): Int {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		getColor(resId, null)
	}else{
		getColor(resId)
	}
}
/* /View extensions */