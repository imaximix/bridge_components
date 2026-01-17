package com.imaximix.bridgecomponents.components

import android.content.Context
import com.imaximix.geovote.SecureStorage
import dev.hotwire.core.bridge.BridgeComponent
import dev.hotwire.core.bridge.BridgeDelegate
import dev.hotwire.core.bridge.Message
import dev.hotwire.navigation.destinations.HotwireDestination


class SecretsStoreComponent(name: String,
                            private val delegate: BridgeDelegate<HotwireDestination>
) : BridgeComponent<HotwireDestination>(name, delegate) {

    private val context: Context?
        get() = delegate.destination.fragment.context

    override fun onReceive(message: Message) {

        context?.let {
            val storage = SecureStorage(it)
            storage.putString("", "")
        }
    }
}

