package com.lockboxth.lockboxkiosk.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class SpacesItemDecoration(space: Int) : ItemDecoration() {
    private val halfSpace: Int
    fun getItemOffsets(outRect: Rect, parent: RecyclerView, view: View, state: RecyclerView.State?) {
        if (parent.paddingLeft != halfSpace) {
            parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace)
            parent.clipToPadding = false
        }
        outRect.top = halfSpace
        outRect.bottom = halfSpace
        outRect.left = halfSpace
        outRect.right = halfSpace
    }

    init {
        halfSpace = space / 2
    }
}