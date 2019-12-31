package com.inlacou.inkslider

class InkSliderCtrl(val view: BaseInkSlider, var model: InkSliderMdl) {
	
	/* Action forwarding */
	fun onValueSet(fromUser: Boolean) {
		model.currentItem.let { model.onValueSet?.invoke(it, fromUser) }
	}
	
	fun onTouchStart() {
		model.userTouch = true
	}
	
	fun onPlusRelease() {
		model.userTouch = false
	}
	
	fun onMinusRelease() {
		model.userTouch = false
	}
	
	fun onTouchRelease() {
		model.userTouch = false
		onValueSet(true)
	}
	
	fun onCurrentItemChanged(newItem: InkSliderMdl.Item, fromUser: Boolean) {
		model.onValueChange?.invoke(newItem, fromUser)
	}
	/* /Action forwarding */
	
	/* Button clicks */
	fun onPlusClick(){
		if(model.disabled) return
		model.userTouch = true
		val currentItemPos = model.values.indexOf(model.currentItem)
		if (currentItemPos > 0) model.values[currentItemPos - 1].let {
			if (it.selectable) model.currentItem = it
		}
		view.forceUpdate(true)
	}
	
	fun onMinusClick(){
		if(model.disabled) return
		model.userTouch = true
		val currentItemPos = model.values.indexOf(model.currentItem)
		if (currentItemPos < model.values.size - 1) model.values[currentItemPos + 1].let {
			if (it.selectable) model.currentItem = it
		}
		view.forceUpdate(true)
	}
	/* /Button clicks */
	
}