import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.imaximix.geovote.SecureStorage
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecureStorageTest {

    private lateinit var secureStorage: SecureStorage

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using the singleton to match how the app uses it
        secureStorage = SecureStorage.getInstance(context)
        // Clear before each test to ensure isolation
        secureStorage.clearAll()
    }

    @Test
    fun testStringStorage() {
        val key = "auth_token"
        val value = "eyasdf1234567890"

        secureStorage.putString(key, value)
        val result = secureStorage.getString(key)

        assertEquals("The stored secret should match the retrieved secret", value, result)
    }

    @Test
    fun testIntStorage() {
        val key = "user_id"
        val value = 98765

        secureStorage.putInt(key, value)
        val result = secureStorage.getInt(key)

        assertEquals(value, result)
    }

    @Test
    fun testBooleanStorage() {
        val key = "is_logged_in"

        secureStorage.putBoolean(key, true)
        assertTrue(secureStorage.getBoolean(key))

        secureStorage.putBoolean(key, false)
        assertFalse(secureStorage.getBoolean(key))
    }

    @Test
    fun testRemove() {
        secureStorage.putString("temp_key", "delete_me")
        secureStorage.remove("temp_key")

        assertNull(secureStorage.getString("temp_key"))
    }

    @Test
    fun testDefaultValues() {
        val result = secureStorage.getString("non_existent", "default_val")
        assertEquals("default_val", result)

        val intResult = secureStorage.getInt("non_existent_int", 42)
        assertEquals(42, intResult)
    }

    @Test
    fun testClearAll() {
        secureStorage.putString("key1", "val1")
        secureStorage.putInt("key2", 2)

        secureStorage.clearAll()

        assertNull(secureStorage.getString("key1"))
        assertEquals(0, secureStorage.getInt("key2")) // 0 is your default for getInt
    }
}