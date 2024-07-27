package com.popkter.jetpackcomposegallary.app_panel

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.popkter.jetpackcomposegallary.tool.AppInfo
import com.popkter.jetpackcomposegallary.R
import com.popkter.jetpackcomposegallary.tool.launchApp


class AppListAdapter(appList: List<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    private val mutableList: MutableList<AppInfo> = appList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view) {
            val appInfo = mutableList[it]
            launchApp(parent.context, appInfo.packageName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = mutableList[position]
        holder.appName.text = appInfo.name
        holder.appIcon.setImageDrawable(appInfo.icon)
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    fun addApp(appInfo: AppInfo) {
        if (mutableList.any { it.packageName == appInfo.packageName }) return
        mutableList.add(appInfo)
        notifyItemInserted(mutableList.size - 1)
    }


    class AppViewHolder(view: View, listener: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        var appName: TextView = itemView.findViewById(R.id.app_name)
        var appIcon: ImageView = itemView.findViewById(R.id.app_icon)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener(position)
                }
            }
        }
    }

}