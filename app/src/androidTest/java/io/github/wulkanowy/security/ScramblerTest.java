package io.github.wulkanowy.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ScramblerTest {

    private Context targetContext;

    private Scrambler scramblerLoad = new Scrambler();

    private Scrambler scramblerNoLoad = new Scrambler();

    @Before
    public void setUp() throws CryptoException {
        targetContext = InstrumentationRegistry.getTargetContext();
        scramblerLoad.loadKeyStore();
    }

    @Test
    public void decryptEncryptStringTest() throws CryptoException {
        scramblerLoad.generateNewKey("TEST", targetContext);
        Assert.assertEquals("pass",
                scramblerLoad.decryptString("TEST", scramblerLoad.encryptString("TEST", "pass")));
    }

    @Test(expected = CryptoException.class)
    public void decryptEmptyTest() throws CryptoException {
        scramblerLoad.decryptString("", "");
    }

    @Test(expected = CryptoException.class)
    public void decryptNoLoadKeyStoreTest() throws CryptoException {
        scramblerNoLoad.decryptString("TEST", "TEST");
    }

    @Test(expected = CryptoException.class)
    public void encryptEmptyTest() throws CryptoException {
        scramblerLoad.encryptString("", "");
    }

    @Test(expected = CryptoException.class)
    public void encryptNoLoadKeyStoreTest() throws CryptoException {
        scramblerNoLoad.encryptString("TEST", "TEST");
    }

    @Test(expected = CryptoException.class)
    public void generateNewKeyEmptyTest() throws CryptoException {
        scramblerLoad.generateNewKey("", targetContext);
    }

    @Test(expected = CryptoException.class)
    public void generateNewKeyNoLoadKeyStoreTest() throws CryptoException {
        scramblerNoLoad.generateNewKey("TEST", targetContext);
    }


}
