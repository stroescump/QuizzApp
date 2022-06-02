package com.irinamihaila.quizzapp.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.DashboardItemBinding


class DashboardItem(private val ctx: Context, private val attributeSet: AttributeSet?) :
    ConstraintLayout(ctx, attributeSet) {
    private lateinit var binding: DashboardItemBinding

    init {
        inflateLayout()
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.DashboardItem)
        setItemTitle(ta)
        setBackgroundShadow(ta)
        setIcon(ta)
        ta.recycle()
    }

    private fun inflateLayout() {
        binding = DashboardItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun setItemTitle(ta: TypedArray) {
        binding.tvQuizTopic.text = ta.getText(R.styleable.DashboardItem_dashboardItem_text)
    }

    private fun setIcon(ta: TypedArray) {
        binding.ivQuizTopic.setImageResource(
            ta.getResourceId(
                R.styleable.DashboardItem_dashboardItem_icon,
                0
            )
        )
    }

    private fun setBackgroundShadow(ta: TypedArray) {
        binding.cvQuizTopic.setBackgroundResource(
            ta.getResourceId(
                R.styleable.DashboardItem_dashboardItem_background,
                0
            )
        )
    }
}