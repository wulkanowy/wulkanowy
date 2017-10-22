package io.github.wulkanowy.activity.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import org.greenrobot.greendao.database.Database;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.DaoMaster;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.Safety;

public class UserFirstLoginTest {
    private static DaoSession daoSession;

    private Context context;

    private Context targetContext;

    @BeforeClass
    public static void setUpClass() {

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(
                InstrumentationRegistry.getTargetContext(), "wulkanowyTest-database");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();
    }

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();

        daoSession.getAccountDao().deleteAll();
        daoSession.clear();

        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", 0);
        editor.apply();
    }

    @Test
    public void connectTest() throws Exception {
        String certificate = "<xml>Certificate</xml>";
        Login login = Mockito.mock(Login.class);
        Mockito.when(login.sendCredentials(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(certificate);
        UserFirstLogin userFirstLogin = new UserFirstLogin(context, login, "TEST@TEST", "TEST_PASS", "TEST_SYMBOL");

        Assert.assertEquals(certificate, userFirstLogin.connect());
    }

    @Test
    public void sendCertificateTest() throws Exception {
        String symbol = "DISCOVERED_SYMBOL";

        Login login = Mockito.mock(Login.class);
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenReturn(symbol);
        UserFirstLogin userFirstLogin = new UserFirstLogin(context, login, "TEST@TEST", "TEST_PASS", "TEST_SYMBOL");

        Assert.assertEquals(symbol, userFirstLogin.sendCertificate("<xml></xml>"));
    }

    @Test
    public void loginTest() throws Exception {
        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getId()).thenReturn("0123");
        Mockito.when(snp.getSymbol()).thenReturn("symbol123");

        PersonalData personalData = Mockito.mock(PersonalData.class);
        Mockito.when(personalData.getFirstAndLastName()).thenReturn("NAME-TEST");

        Login login = Mockito.mock(Login.class);
        Mockito.when(login.login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("symbol123");

        UserFirstLogin userFirstLogin = new UserFirstLogin(targetContext, login, "TEST@TEST", "TEST_PASS", "TEST_SYMBOL");
        userFirstLogin.login(daoSession, snp, personalData);

        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        Long userId = sharedPreferences.getLong("userId", 0);

        Account account = daoSession.getAccountDao().load(userId);

        Safety safety = new Safety();
        Assert.assertNotNull(account);
        Assert.assertEquals("TEST@TEST", account.getEmail());
        Assert.assertEquals("NAME-TEST", account.getName());
        Assert.assertEquals("TEST_PASS", safety.decrypt("TEST@TEST", account.getPassword()));
    }

    @AfterClass
    public static void cleanUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.clear();
    }
}
