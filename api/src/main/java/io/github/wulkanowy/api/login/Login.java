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
        Document certDoc = sendCredentials(email, password);

        return sendCertificate(certDoc, symbol);
    }

    Document sendCredentials(String email, String password) throws IOException, VulcanException {
        String[][] credentials = new String[][]{
                {"LoginName", email},
                {"Password", password}
        };

        String loginFormAction = LOGIN_PAGE_URL;

        Document loginPage = client.getPageByUrl(LOGIN_PAGE_URL, false);
        Element form = loginPage.select("#form1").first();
        if (null != form) {
            Document formPage = client.postPageByUrl(form.attr("abs:action"), new String[][]{
                    {"__VIEWSTATE", loginPage.select("#__VIEWSTATE").val()},
                    {"__VIEWSTATEGENERATOR", loginPage.select("#__VIEWSTATEGENERATOR").val()},
                    {"__EVENTVALIDATION", loginPage.select("#__EVENTVALIDATION").val()},
                    {"__db", loginPage.select("input[name=__db]").val()},
                    {"PassiveSignInButton.x", "0"},
                    {"PassiveSignInButton.y", "0"},
            });
            loginFormAction = formPage.select("#form1").first().attr("abs:action");
            credentials = new String[][]{
                    {"__VIEWSTATE", formPage.select("#__VIEWSTATE").val()},
                    {"__VIEWSTATEGENERATOR", formPage.select("#__VIEWSTATEGENERATOR").val()},
                    {"__EVENTVALIDATION", formPage.select("#__EVENTVALIDATION").val()},
                    {"__db", formPage.select("input[name=__db]").val()},
                    {"UsernameTextBox", email},
                    {"PasswordTextBox", password},
                    {"SubmitButton.x", "0"},
                    {"SubmitButton.y", "0"},
            };
        }

        Document html = client.postPageByUrl(loginFormAction, credentials);

        Element errorMessage = html.select(".ErrorMessage, #ErrorTextLabel").first();
        if (null != errorMessage) {
            throw new BadCredentialsException(errorMessage.text());
        }

        return html;
    }

    String sendCertificate(Document certDoc, String defaultSymbol) throws IOException, VulcanException {
        String certificate = certDoc.select("input[name=wresult]").attr("value");
        String url = certDoc.select("form[name=hiddenform]").attr("action");

        String symbol = findSymbol(defaultSymbol, certificate);
        client.setSymbol(symbol);

        String title = client.postPageByUrl(url.replaceFirst("Default", "{symbol}"), new String[][]{
                {"wa", "wsignin1.0"},
                {"wresult", certificate},
                {"wctx", certDoc.select("input[name=wctx]").attr("value")}
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
