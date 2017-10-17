package io.github.wulkanowy.api.login;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.wulkanowy.api.Api;
import io.github.wulkanowy.api.Cookies;

public class Login extends Api {

    private String loginPageUrl = "https://cufs.vulcan.net.pl/Default/Account/LogOn";

    private String certificatePageUrl = "https://cufs.vulcan.net.pl/Default"
            + "/FS/LS?wa=wsignin1.0&wtrealm=https://uonetplus.vulcan.net.pl/Default"
            + "/LoginEndpoint.aspx&wctx=https://uonetplus.vulcan.net.pl/Default"
            + "/LoginEndpoint.aspx";

    private String loginEndpointPageUrl =
            "https://uonetplus.vulcan.net.pl/{symbol}/LoginEndpoint.aspx";

    public Login(Cookies cookies) {
        this.cookies = cookies;
    }

    public String login(String email, String password, String symbol)
            throws BadCredentialsException, LoginErrorException, AccountPermissionException {
        String symbolWhoa;

        try {
            sendCredentials(email, password);
            String[] certificate = getCertificateData();
            symbolWhoa = sendCertificate(certificate[0], certificate[1]);
        } catch (IOException e) {
            throw new LoginErrorException();
        }

        return symbolWhoa;
    }

    private void sendCredentials(String email, String password)
            throws IOException, BadCredentialsException {

        Connection.Response response = Jsoup.connect(loginPageUrl)
                .data("LoginName", email)
                .data("Password", password)
                .method(Connection.Method.POST)
                .execute();

        setCookies(response.cookies());
        Document document = response.parse();

        if (null != document.select(".ErrorMessage").first()) {
            throw new BadCredentialsException();
        }
    }

    private String[] getCertificateData() throws IOException {
        Document certificatePage = getPageByUrl(certificatePageUrl);

        return new String[]{
                certificatePage.select("input[name=wa]").attr("value"),
                certificatePage.select("input[name=wresult]").attr("value")
        };
    }

    private String sendCertificate(String protocolVersion, String certificate)
            throws IOException, LoginErrorException, AccountPermissionException {
        Elements els = Jsoup.parse(certificate.replaceAll(":",""), "",
                Parser.xmlParser()).select("[AttributeName=\"UserInstance\"] samlAttributeValue");
        String symbol = els.get(1).text();

        Connection.Response response = Jsoup.connect(loginEndpointPageUrl
                .replace("{symbol}", symbol))
                .data("wa", protocolVersion)
                .data("wresult", certificate)
                .cookies(getCookies())
                .followRedirects(true)
                .method(Connection.Method.POST)
                .execute();

        addCookies(response.cookies());
        Document html = response.parse();

        if (html.getElementsByTag("title").text().equals("Logowanie")) {
            throw new AccountPermissionException();
        }

        if (!html.select("title").text().equals("Uonet+")) {
            throw new LoginErrorException();
        }

        return symbol;
    }
}
