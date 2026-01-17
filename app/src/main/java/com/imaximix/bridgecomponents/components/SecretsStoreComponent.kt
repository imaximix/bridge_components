package com.imaximix.bridgecomponents.components

import android.content.Context
import android.util.Log
import com.imaximix.geovote.SecureStorage
import dev.hotwire.core.bridge.BridgeComponent
import dev.hotwire.core.bridge.BridgeDelegate
import dev.hotwire.core.bridge.Message
import dev.hotwire.navigation.destinations.HotwireDestination
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


class SecretsStoreComponent(name: String,
                            private val delegate: BridgeDelegate<HotwireDestination>
) : BridgeComponent<HotwireDestination>(name, delegate) {

    private val context: Context?
        get() = delegate.destination.fragment.context

    override fun onReceive(message: Message) {

        when (message.event) {
            "connect" -> handleConnectEvent(message)
            "store_secret" -> handleStoreSecretEvent(message)
            else -> Log.w("SecretsStoreComponent", "Unknown event for message: $message")
        }
    }

    private fun handleStoreSecretEvent(message: Message): Unit {
        val messageData = Json.Default.decodeFromString<StoreSecretMessageData>(message.jsonData)

        context?.let {
            val secureStorage = SecureStorage.getInstance(it)
            secureStorage.putString(messageData.key, messageData.secret)
        }
    }

    private fun handleConnectEvent(message: Message): Unit {
        val messageData = Json.Default.decodeFromString<ConnectMessageData>(message.jsonData)

        context?.let {
            val secureStorage = SecureStorage.getInstance(it)
            replyTo("connect", "{ \"secret\": \"${secureStorage.getString(messageData.key)}\" }")
        }

    }


    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class ConnectMessageData(
        val key: String
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class StoreSecretMessageData(
        val key: String,
        val secret: String
    )
}

