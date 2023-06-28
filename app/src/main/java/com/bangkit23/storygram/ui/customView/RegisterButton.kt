package com.bangkit23.storygram.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.bangkit23.storygram.R

class RegisterButton : AppCompatButton {

    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        setTextColor(txtColor)
        background = null
        textSize = 12f
        isAllCaps = false
        text = "Register"
    }


    private fun init() {
        txtColor = ContextCompat.getColor(context, R.color.purple_700)
    }


}