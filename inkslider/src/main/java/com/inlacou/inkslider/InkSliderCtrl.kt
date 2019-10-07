package com.inlacou.inkslider

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
	
}