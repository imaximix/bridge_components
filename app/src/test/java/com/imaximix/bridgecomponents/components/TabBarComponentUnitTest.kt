package com.imaximix.bridgecomponents.components

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class TabBarComponentUnitTest {
    @Test
    fun can_parse_message_data() {
        // Arrange - Create JSON string that represents MessageData
        val jsonData = """
            {
                "tabs": [
                    {
                        "name": "Home",
                        "path": "home",
                        "android_image": "ic_home"
                    },
                    {
                        "name": "Profile",
                        "path": "profile",
                        "android_image": "ic_profile"
                    }
                ],
                "refreshUnselectedTabs": false,
                "tabBarHidden": false
            }
        """.trimIndent()

        // Act - Deserialize the JSON into MessageData
        val messageData = Json.Default.decodeFromString<TabBarComponent.MessageData>(jsonData)

        // Assert - Verify the deserialization was successful
        assertEquals(2, messageData.tabs.size)
        assertEquals("Home", messageData.tabs[0].name)
        assertEquals("home", messageData.tabs[0].path)
        assertEquals("ic_home", messageData.tabs[0].imageName)
        assertEquals("Profile", messageData.tabs[1].name)
        assertEquals("profile", messageData.tabs[1].path)
        assertEquals("ic_profile", messageData.tabs[1].imageName)
        assertEquals(false, messageData.refreshUnselectedTabs)
        assertEquals(false, messageData.tabBarHidden)
    }
}