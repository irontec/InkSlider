package com.inlacou.inkslider

import kotlin.math.roundToInt

class InkSliderCtrl(val view: InkSlider, var model: InkSliderMdl) {
	
	/* Action forwarding */
	fun onValueSet(fromUser: Boolean) {
		model.currentItem.let { model.onValueSet?.invoke(it, fromUser) }
	}
	
	fun onTouchRelease() {
		onValueSet(true)
	}
	
	fun onCurrentItemChanged(newItem: InkSliderMdl.Item, fromUser: Boolean) {
		model.onValueChange?.invoke(newItem, fromUser)
	}
	/* /Action forwarding */
	
	/* Button clicks */
	fun onPlusClick(){
		if(model.disabled) return
		val currentItemPos = model.values.indexOf(model.currentItem)
		if (currentItemPos > 0) model.values[currentItemPos - 1].let {
			if (it.selectable) model.currentItem = it
		}
		view.forceUpdate()
	}
	
	fun onMinusClick(){
		if(model.disabled) return
		val currentItemPos = model.values.indexOf(model.currentItem)
		if (currentItemPos < model.values.size - 1) model.values[currentItemPos + 1].let {
			if (it.selectable) model.currentItem = it
		}
		view.forceUpdate()
	}
	/* /Button clicks */
	
	/* List manipulation */
	/**
	 * Gets first selectable item.
	 * @throws NoSuchElementException when there is no item to select (no selectable items).
	 */
	internal fun getFirstSelectable(items: List<InkSliderMdl.Item>): InkSliderMdl.Item {
		return items.first { it.selectable }
	}
	
	/**
	 * Gets last selectable item.
	 * @throws NoSuchElementException when there is no item to select (no selectable items).
	 */
	internal fun getLastSelectable(items: List<InkSliderMdl.Item>): InkSliderMdl.Item {
		return items.last { it.selectable }
	}
	
	/**
	 * Gets nearest selectable item. Ignores non selectable items and current item.
	 * @throws NoSuchElementException when there is no item to select (no selectable items).
	 */
	internal fun getNearestSelectable(roughIndex: Float, items: List<InkSliderMdl.Item>): InkSliderMdl.Item {
		return items.getNearestSelectable(roughIndex = roughIndex, selectable = { it.selectable })
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
	
}