package com.popkter.jetpackcomposegallary.app_panel

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.popkter.jetpackcomposegallary.tool.AppInfo
import com.popkter.jetpackcomposegallary.databinding.AppPanelBinding
import com.popkter.lib_recyclerview_pager.PagerGridLayoutManager

class AppPanel(context: Context, private val mAdapter: AppListAdapter) : BottomSheetDialog(context) {
    private val binding by lazy { AppPanelBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.apply {
            setDimAmount(0F)
        }
        behavior.apply {
            peekHeight = 0
            isHideable = true
            skipCollapsed = true
        }
        binding.recyclerView.apply {
            layoutManager =
                PagerGridLayoutManager(
                    2,
                    7,
                    PagerGridLayoutManager.HORIZONTAL,
                    false
                )
            adapter = mAdapter
        }
        binding.text.setOnClickListener {
            mAdapter.addApp(AppInfo("Test", ColorDrawable(Color.RED),""))
        }
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.maxWidth = MATCH_PARENT
    }

}