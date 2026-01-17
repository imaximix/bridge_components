package com.imaximix.bridgecomponents.components

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.imaximix.bridgecomponents.EventBus
import com.imaximix.bridgecomponents.TabBarConfiguration
import dev.hotwire.core.bridge.BridgeComponent
import dev.hotwire.core.bridge.BridgeDelegate
import dev.hotwire.core.bridge.Message
import dev.hotwire.navigation.destinations.HotwireDestination
import dev.hotwire.navigation.navigator.NavigatorConfiguration
import dev.hotwire.navigation.navigator.NavigatorHost
import dev.hotwire.navigation.tabs.HotwireBottomNavigationController
import dev.hotwire.navigation.tabs.HotwireBottomTab
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.util.UUID

class TabBarComponent(
    name: String,
    private val delegate: BridgeDelegate<HotwireDestination>
) : BridgeComponent<HotwireDestination>(name, delegate) {

    private val instanceId = UUID.randomUUID().toString()

    init {
        delegate.destination.fragment.viewLifecycleOwner.lifecycleScope.launch {
            delegate.destination.fragment.viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                EventBus.refreshEvents.collect { senderId ->
                    if (senderId != instanceId) {
                        Log.d("TabBarComponent", "Refresh event received!")
                        replyTo("connect")
                    }
                }
            }
        }
    }

    override fun onReceive(message: Message) {
        // Handle incoming messages based on the message `event`.
        when (message.event) {
            "connect" -> handleConnectEvent(message)
            else -> Log.w("TabBarComponent", "Unknown event for message: $message")
        }
    }

    private fun handleConnectEvent(message: Message) {
        val activity = delegate.destination.fragment.activity
        val messageData = Json.Default.decodeFromString<MessageData>(message.jsonData)
        val container = TabBarConfiguration.fetchContainer?.invoke()

        if (container == null || activity == null) {
            return
        }


            if (container.childCount != messageData.tabs.count()) {

                container.removeAllViews()
                val hotwireTabs = messageData.tabs.map { tab ->

                    val fragmentContainer = FragmentContainerView(activity).apply {
                        id = View.generateViewId()
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    container.addView(fragmentContainer)

                    activity.supportFragmentManager
                        .beginTransaction()
                        .add(fragmentContainer.id, NavigatorHost())
                        .commit()

                    val iconResId = TabBarConfiguration.fetchIconResId?.invoke(tab.imageName) ?: 0
                    HotwireBottomTab(
                        title = tab.name,
                        iconResId = iconResId,
                        configuration = NavigatorConfiguration(
                            name = tab.path,
                            navigatorHostId = fragmentContainer.id,
                            startLocation = "${TabBarConfiguration.getRootUrl()}/${tab.path}"
                        )
                    )
                }


                TabBarConfiguration.setTabs(hotwireTabs)

                activity.supportFragmentManager.executePendingTransactions()

                TabBarConfiguration.loadTabs(hotwireTabs)


                if (messageData.tabBarHidden) {
                    TabBarConfiguration.setVisibility(HotwireBottomNavigationController.Visibility.HIDDEN)

                } else {
                    TabBarConfiguration.setVisibility(HotwireBottomNavigationController.Visibility.DEFAULT)

                }
            }
            else if (messageData.refreshUnselectedTabs) {
                delegate.destination.fragment.lifecycleScope.launch {
                    EventBus.triggerRefresh(instanceId)
                }
            }

//        }

    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class MessageData(
        val tabs: List<Tab>,
        val refreshUnselectedTabs: Boolean,
        val tabBarHidden: Boolean
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class Tab(
        val name: String,
        val path: String,
        @SerialName("android_image") val imageName: String)


}