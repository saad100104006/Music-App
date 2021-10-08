package com.example.speechify.widget.mytab

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.speechify.R
import com.example.speechify.ktx.gone
import com.example.speechify.ktx.visible
import kotlinx.android.synthetic.main.custom_tab.view.*

class MyTab : ConstraintLayout {

    private var onTabClick: ((Int) -> (Unit))? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initControl(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initControl(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initControl(context, attrs)
    }

    private fun initControl(context: Context, attrs: AttributeSet) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_tab, this)

        onTabClick?.invoke(0)


        firstTv.setOnClickListener {
            setSelectedTab(0)
            onTabClick?.invoke(0)
        }
        secondTv.setOnClickListener {
            setSelectedTab(1)
            onTabClick?.invoke(1)
        }
        thirdTv.setOnClickListener {
            setSelectedTab(2)
            onTabClick?.invoke(2)
        }
    }

    public fun setOnTabClickListener(onTabClick: ((Int) -> (Unit))) {
        this.onTabClick = onTabClick
    }

    public fun setSelectedTab(@IntRange(from = 0, to = 2) position: Int) {
        val buttonList = listOf(firstSelectedTv, secondSelectedTv, thirdSelectedTv)
        buttonList.gone()

        when (position) {
            0 -> firstSelectedTv.visible()
            1 -> secondSelectedTv.visible()
            else -> thirdSelectedTv.visible()
        }
    }
}