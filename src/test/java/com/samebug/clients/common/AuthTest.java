package com.samebug.clients.common;

import com.intellij.ide.BrowserUtil;
import com.samebug.clients.common.api.client.OAuthServer;

import java.io.IOException;
import java.text.MessageFormat;

public class AuthTest {
    public static void main(String[] args) throws IOException {
        OAuthServer s = new OAuthServer(new OAuthServer.Listener() {
            @Override
            public void success(String code) {
                System.out.println("Got the code: " + code);
            }

            @Override
            public void fail() {
                System.out.println("Authentication failed!");
            }
        });

        String scope = "email profile";
        String redirectUri = "http://127.0.0.1:9990";
        String clientId = "1086819285889-h3u7vm9vletsprtbv2uref3t6jjiq4vv.apps.googleusercontent.com";
        String url = MessageFormat.format("https://accounts.google.com/o/oauth2/v2/auth?" +
                "scope={0}&" +
                "response_type=code&" +
                "state=security_token%3D138r5719ru3e1%26url%3Dhttps://oauth2.example.com/token&" +
                "redirect_uri={1}&" +
                "client_id={2}", scope, redirectUri, clientId);
        BrowserUtil.browse(url);
    }
}
