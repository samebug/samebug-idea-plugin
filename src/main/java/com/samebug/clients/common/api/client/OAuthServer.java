package com.samebug.clients.common.api.client;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OAuthServer {
    public static final int port = 9990;
    public static final int timeoutInMs = 60000;

    HttpServer server;
    Listener listener;
    Timer timer;

    public OAuthServer(Listener listener) {
        this.listener = listener;
        timer = new Timer("oauth-server");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fail();
            }
        }, timeoutInMs);

        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new MyHandler());
            server.start();
        } catch (IOException e) {
            listener.fail();
        }
    }

    private synchronized void success(String code) {
        if (listener != null) {
            listener.success(code);
            shutdown();
        }
    }

    private synchronized void fail() {
        if (listener != null) {
            listener.fail();
            shutdown();
        }
    }

    private void shutdown() {
        listener = null;
        timer.cancel();
        server.stop(0);
    }

    final class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                String code = null;
                List<NameValuePair> params = URLEncodedUtils.parse(t.getRequestURI(), "UTF-8");
                for (NameValuePair param : params) {
                    if ("code".equals(param.getName())) code = param.getValue();
                }

                Headers headers = t.getResponseHeaders();
                headers.add("Location", "https://samebug.io/");
                headers.add("Content-Type", "text/html");
                String response = "Found";
                t.sendResponseHeaders(302, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();

                if (code == null) fail();
                else success(code);
            } catch (IOException e) {
                listener.fail();
                throw e;
            }
        }
    }

    public interface Listener {
        void success(String code);

        void fail();
    }
}
