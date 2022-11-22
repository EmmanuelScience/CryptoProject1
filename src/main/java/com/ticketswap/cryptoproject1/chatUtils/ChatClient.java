package com.ticketswap.cryptoproject1.chatUtils;

import com.ticketswap.cryptoproject1.config.RSA;
import com.ticketswap.cryptoproject1.entities.UserType;

public interface ChatClient {
    void execute();
    void setUserName(String userName);
    String getUserName();
    UserType getUserType();
    RSA getRSA();
}
