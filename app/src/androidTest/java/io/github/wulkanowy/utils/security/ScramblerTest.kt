package io.github.wulkanowy.utils.security

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SdkSuppress
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class ScramblerTest {

    @Test
    @SdkSuppress(minSdkVersion = 18)
    fun encryptDecryptTest() {
        val targetContext = InstrumentationRegistry.getTargetContext()
        assertEquals("TEST", Scrambler.decrypt(Scrambler.encrypt("TEST", targetContext)))
    }
}
