package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.wulkanowy.api.Client;
import io.github.wulkanowy.api.VulcanException;

public class Login {

    private static final String LOGIN_PAGE_URL = "{schema}://cufs.{host}/{symbol}/Account/LogOn" +
            "?ReturnUrl=%2F{symbol}%2FFS%2FLS%3Fwa%3Dwsignin1.0%26wtrealm%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx%26wctx%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx";

    private Client client;

    public Login(Client client) {
        this.client = client;
    }

    public String login(String email, String password, String symbol) throws VulcanException, IOException {
        Document certificatePage = sendCredentials(email, password);

        return sendCertificate(certificatePage, symbol);
    }

    Document sendCredentials(String email, String password) throws IOException, VulcanException {
        Element form = client.getPageByUrl(LOGIN_PAGE_URL, false).select("#MainDiv form").first();
        Document html = client.postPageByUrl(form.attr("abs:action"), new String[][]{
                {"LoginName", email},
                {"Password", password}
        });

        Element errorMessage = html.select(".ErrorMessage").first();
        if (null != errorMessage) {
            throw new BadCredentialsException(errorMessage.text());
        }

        if (!"Working...".equals(html.select("title").first().text())) {
            throw new LoginErrorException("Could not get valid certificate page");
        }

        return html;
    }

    String sendCertificate(Document certDoc, String defaultSymbol) throws IOException, VulcanException {
        String certificate = certDoc.select("input[name=wresult]").attr("value");
        String symbol = findSymbol(defaultSymbol, certificate);

        client.setSymbol(symbol);

        String url = certDoc.select("form[name=hiddenform]").attr("action");
        String title = client.postPageByUrl(url.replaceFirst("Default", "{symbol}"), new String[][]{
                {"wa", "wsignin1.0"},
                {"wresult", certificate}
        }).select("title").text();

        if ("Logowanie".equals(title)) {
            throw new AccountPermissionException("No account access. Try another symbol");
        }

        if (!"Uonet+".equals(title)) {
            throw new LoginErrorException("Could not log in, unknown error");
        }

        return symbol;
    }

    private String findSymbol(String symbol, String certificate) {
        if ("Default".equals(symbol)) {
            return findSymbolInCertificate(certificate);
        }

        return symbol;
    }

    String findSymbolInCertificate(String certificate) {
        Elements instances = Jsoup
                .parse(certificate.replaceAll(":", ""), "", Parser.xmlParser())
                .select("[AttributeName=\"UserInstance\"] samlAttributeValue");

        if (instances.isEmpty()) {
            return "";
        }

        return instances.get(1).text();
    }
}
