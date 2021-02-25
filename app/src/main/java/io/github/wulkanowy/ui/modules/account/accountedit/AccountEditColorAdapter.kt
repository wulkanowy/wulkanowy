package io.github.wulkanowy.ui.modules.account.accountedit

import android.R
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemAccountEditColorBinding
import javax.inject.Inject

class AccountEditColorAdapter @Inject constructor() :
    RecyclerView.Adapter<AccountEditColorAdapter.ViewHolder>() {

    var items = listOf<Int>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemAccountEditColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("RestrictedApi", "NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            accountEditItemColor.setImageDrawable(GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                color = ColorStateList.valueOf(item)
            })

            val mask = GradientDrawable()
            mask.shape = GradientDrawable.OVAL
            mask.setColor(Color.BLACK)

            accountEditItemColorContainer.foreground = createForegroundDrawable(item)
        }
    }

    class ViewHolder(val binding: ItemAccountEditColorBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun getRippleColor(@ColorInt color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] * 0.5f
        return Color.HSVToColor(hsv)
    }

    private fun createForegroundDrawable(color: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mask = GradientDrawable()
            mask.shape = GradientDrawable.OVAL
            mask.setColor(Color.BLACK)
            RippleDrawable(ColorStateList.valueOf(getRippleColor(color)), null, mask)
        } else {
            val foreground = StateListDrawable()
            foreground.alpha = 80
            foreground.setEnterFadeDuration(250)
            foreground.setExitFadeDuration(250)

            val mask = GradientDrawable()
            mask.shape = GradientDrawable.OVAL
            mask.setColor(getRippleColor(color))
            foreground.addState(intArrayOf(R.attr.state_pressed), mask)
            foreground.addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
            foreground
        }
    }
}
