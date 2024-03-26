package io.github.wulkanowy.utils

import androidx.recyclerview.widget.DiffUtil

fun <T : Any> DiffUtil.ItemCallback<T>.toCallback(old: List<T>, new: List<T>) =
    object : DiffUtil.Callback() {
        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            this@toCallback.areItemsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            this@toCallback.areContentsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) =
            this@toCallback.getChangePayload(old[oldItemPosition], new[newItemPosition])
    }
