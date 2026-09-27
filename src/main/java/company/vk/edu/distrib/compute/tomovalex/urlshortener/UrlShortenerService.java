package company.vk.edu.distrib.compute.tomovalex.urlshortener;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import company.vk.edu.distrib.compute.Dao;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class UrlShortenerService implements company.vk.edu.distrib.compute.urlshortener.UrlShortenerService {

    private final int port;
    private final HttpServer server;
    private final Dao<String> dao;
    public UrlShortenerService(int port, Dao<String> dao) throws IOException {
        this.port = port;
        this.dao = dao;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/v0/status", exchange -> {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });

        server.createContext("/v0/links", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();


            if ("GET".equals(method)) {
                String prefix = "/v0/links/";
                if (!path.startsWith(prefix)) {
                    exchange.sendResponseHeaders(404, -1);
                    exchange.close();
                    return;
                }

                String id = path.substring(prefix.length());

                if (!id.matches("[A-Za-z0-9]{10}")) {
                    exchange.sendResponseHeaders(422, -1);
                    exchange.close();
                    return;
                }
                try {
                    String url = dao.get(id);

                    byte[] response = url.getBytes(StandardCharsets.UTF_8);

                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");

                    exchange.sendResponseHeaders(200, response.length);

                    try (OutputStream output = exchange.getResponseBody()) {
                        output.write(response);
                    }
                } catch (NoSuchElementException e) {
                    exchange.sendResponseHeaders(404, -1);
                    exchange.close();
                }

            }

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        });

    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop(1);
    }
}
