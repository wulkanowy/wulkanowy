package io.github.wulkanowy.api;

public class NotLoggedInErrorException extends VulcanException {

    public NotLoggedInErrorException() {
        super("Prawdopodobnie nie jesteś zalogowany - zaloguj się");
    }

    protected NotLoggedInErrorException(String message) {
        super(message);
    }
}
