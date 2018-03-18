package io.github.wulkanowy.api.login;

import io.github.wulkanowy.api.VulcanException;

public class AccountPermissionException extends VulcanException {

    AccountPermissionException() {
        super("Brak dostępu do konta. Spróbuj innego symbolu");
    }
}
