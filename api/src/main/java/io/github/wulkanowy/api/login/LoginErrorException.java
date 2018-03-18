package io.github.wulkanowy.api.login;

import io.github.wulkanowy.api.NotLoggedInErrorException;

class LoginErrorException extends NotLoggedInErrorException {

    LoginErrorException() {
        super("Nie udało się zalogować, nieznany błąd");
    }
}
