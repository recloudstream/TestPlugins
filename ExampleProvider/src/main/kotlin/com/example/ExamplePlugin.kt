package com.example

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import com.lagradost.cloudstream3.APIHolder
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

@CloudstreamPlugin
class TestPlugin: Plugin() {
    var activity: AppCompatActivity? = null

    override fun load(context: Context) {
        activity = context as AppCompatActivity
        // All providers should be added in this manner
        registerMainAPI(ExampleProvider(this))

        openSettings = { ctx ->
            val frag = BlankFragment(this)
            frag.show(activity!!.supportFragmentManager, "Frag")
        }
    }
}