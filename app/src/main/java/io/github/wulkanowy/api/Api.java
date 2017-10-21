package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public abstract class Api {

    protected Cookies cookies = new Cookies();

    public Cookies getCookiesObject() {
        return cookies;
    }

    public Map<String, String> getCookies() {
        return cookies.getItems();
    }

    public Cookies addCookies(Map<String, String> cookies) {
        this.cookies.addItems(cookies);
        return this.cookies;
    }

    public Cookies setCookies(Map<String, String> cookies) {
        this.cookies.setItems(cookies);
        return this.cookies;
    }

    public Document getPageByUrl(String url) throws IOException {
        return Jsoup.connect(url)
                .followRedirects(true)
                .cookies(getCookies())
                .get();
    }

    public Document postPageByUrl(String url, String[][] params) throws IOException {
        Connection connection = Jsoup.connect(url);

        for (String[] data : params) {
            connection.data(data[0], data[1]);
        }

        Connection.Response response = connection.cookies(getCookies())
                .followRedirects(true)
                .method(Connection.Method.POST)
                .execute();

        addCookies(response.cookies());

        return response.parse();
    }
}
