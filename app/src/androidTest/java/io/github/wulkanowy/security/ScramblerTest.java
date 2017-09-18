package io.github.wulkanowy.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScramblerTest {

    private Scrambler scrambler = new Scrambler();

    private Context targetContext;

    @Before
    public void setUp() throws CryptoException {
        targetContext = InstrumentationRegistry.getTargetContext();
        scrambler.loadKeyStore();
        scrambler.generateNewKey("TEST", targetContext);
    }

    @Test
    public void decryptEncryptTest() throws CryptoException {
        Assert.assertEquals("pass", scrambler.decryptString("TEST", scrambler.encryptString("TEST", "pass")));
    }

    @Test(expected = CryptoException.class)
    public void generateNewKeyEmptyTest() throws CryptoException {
        scrambler.generateNewKey("", targetContext);
    }
}
