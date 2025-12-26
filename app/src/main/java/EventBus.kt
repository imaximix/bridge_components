package com.imaximix.bridgecomponents

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _refreshEvents = MutableSharedFlow<String>()
    val refreshEvents = _refreshEvents.asSharedFlow()

    suspend fun triggerRefresh(senderId: String) {
        _refreshEvents.emit(senderId)
    }

    private val _notificationsSuccessEvents = MutableSharedFlow<String>()
    val notificationsSuccessEvents = _notificationsSuccessEvents.asSharedFlow()

    suspend fun triggerNotificationsSuccess(deviceToken: String) {
        _notificationsSuccessEvents.emit(deviceToken)
    }
}