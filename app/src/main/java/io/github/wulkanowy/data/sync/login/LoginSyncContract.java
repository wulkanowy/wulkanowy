package io.github.wulkanowy.data.sync.login;

import java.io.IOException;

import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.utils.security.CryptoException;

public interface LoginSyncContract {

    void loginUser(String email, String password, String symbol)
            throws VulcanException, IOException,
            CryptoException;

    void loginCurrentUser() throws VulcanException, IOException,
            CryptoException;
}
