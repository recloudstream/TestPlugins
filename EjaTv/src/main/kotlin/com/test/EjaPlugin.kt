package com.test

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import com.lagradost.cloudstream3.APIHolder
import android.content.Context

@CloudstreamPlugin
class EjaPlugin: Plugin() {
    override fun load(context: Context) {
        // All providers should be added in this manner
        APIHolder.allProviders.add(EjaTv())
    }
}