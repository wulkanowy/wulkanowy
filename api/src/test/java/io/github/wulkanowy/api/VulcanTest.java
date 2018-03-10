package io.github.wulkanowy.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import io.github.wulkanowy.api.login.Login;

public class VulcanTest {

    private Vulcan vulcan;

    @Before
    public void setUp() throws Exception {
        vulcan = new Vulcan();
        vulcan.setClient(Mockito.mock(Client.class));
        vulcan.setLogin(Mockito.mock(Login.class));
    }

    @Test
    public void setFullEndpointInfoTest() throws Exception {
        vulcan.login("http://fakelog.net\\\\admin", "pass", "Default", "123");

        Assert.assertEquals("http", vulcan.getProtocolSchema());
        Assert.assertEquals("fakelog.net", vulcan.getLogHost());
        Assert.assertEquals("admin", vulcan.getEmail());
    }

    @Test
    public void getClientTwiceTest() throws Exception {
        Vulcan vulcan = new Vulcan();
        Login login = Mockito.mock(Login.class);
        vulcan.setLogin(login);
        Assert.assertTrue(vulcan.getClient().equals(vulcan.getClient()));
    }

    @Test
    public void getLoginTwiceTest() throws Exception {
        Vulcan vulcan = new Vulcan();
        Assert.assertTrue(vulcan.getLogin().equals(vulcan.getLogin()));
    }
}
