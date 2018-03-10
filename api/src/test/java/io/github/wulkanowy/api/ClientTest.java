package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class ClientTest {

    private String getFixtureAsString(String fixtureFileName) {
        return FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
    }

    @Test(expected = VulcanOfflineException.class)
    public void checkForErrorsOffline() throws Exception {
        Client client = new Client("", "", "");

        Document doc = Jsoup.parse(getFixtureAsString("login/PrzerwaTechniczna.html"));

        client.checkForErrors(doc);
    }

    @Test(expected = NotLoggedInErrorException.class)
    public void checkForErrors() throws Exception {
        Client client = new Client("", "", "");

        Document doc = Jsoup.parse(getFixtureAsString("login/Logowanie-notLoggedIn.html"));

        client.checkForErrors(doc);
    }
}
