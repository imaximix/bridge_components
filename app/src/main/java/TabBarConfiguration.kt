package com.imaximix.bridgecomponents


import android.view.ViewGroup
import dev.hotwire.navigation.tabs.HotwireBottomNavigationController
import dev.hotwire.navigation.tabs.HotwireBottomTab

object TabBarConfiguration {
    var fetchContainer: (() -> ViewGroup)? = null
    private var onTabsChanged: ((List<HotwireBottomTab>) -> Unit)? = null
    private var onTabsLoad: ((List<HotwireBottomTab>) -> Unit)? = null
    private var onChangeVisibility: ((HotwireBottomNavigationController.Visibility) -> Unit)? = null
    var fetchIconResId: ((String) -> Int)? = null
    private var fetchRootUrl: (() -> String)? = null

    fun configure(
        onTabsChanged: (List<HotwireBottomTab>) -> Unit,
        onTabsLoad: (List<HotwireBottomTab>) -> Unit,
        onChangeVisibility: (HotwireBottomNavigationController.Visibility) -> Unit,
        fetchContainer: () -> ViewGroup,
        fetchIconResId: (String) -> Int,
        fetchRootUrl: () -> String
    ) {
        this.onTabsChanged = onTabsChanged
        this.onTabsLoad = onTabsLoad
        this.onChangeVisibility = onChangeVisibility
        this.fetchContainer = fetchContainer
        this.fetchIconResId = fetchIconResId
        this.fetchRootUrl = fetchRootUrl
    }

    fun setTabs(tabs: List<HotwireBottomTab>) {
        onTabsChanged?.invoke(tabs)
    }

    fun loadTabs(tabs: List<HotwireBottomTab>) {
        onTabsLoad?.invoke(tabs)
    }

    fun setVisibility(visibility: HotwireBottomNavigationController.Visibility) {
        onChangeVisibility?.invoke(visibility)
    }

    fun getRootUrl(): String {
        return fetchRootUrl?.invoke() ?: ""
    }
}