package net.techcable.sonarpet.maven;

import net.techcable.sonarpet.utils.SSLUtils;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

public class HttpUtils {
    private HttpUtils() {}

    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36";
    public static <E extends Exception, R> R download(
            URL url,
            IOConsumer<InputStream, R, E> handler
    ) throws IOException, E {
        // NOTE: Use SSLUtils in order to trust letsencrypt
        HttpURLConnection connection = (HttpURLConnection) SSLUtils.openConnection(url);
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new FileNotFoundException("HTTP not found: " + url);
        }
        String encoding = connection.getContentEncoding();
        try (InputStream rawInput = connection.getInputStream()) {
            final InputStream in;
            if (encoding != null) {
                switch (encoding) {
                    case "gzip":
                        in = new GZIPInputStream(rawInput);
                        break;
                    case "deflate":
                        in = new DeflaterInputStream(rawInput);
                        break;
                    default:
                        throw new IOException("Can't handle encoding: " + encoding);
                }
            } else {
                in = rawInput;
            }
            return handler.accept(in);
        }
    }
    public static boolean checkExistence(URL url) throws IOException {
        // NOTE: Use SSLUtils in order to trust letsencrypt
        HttpURLConnection connection = (HttpURLConnection) SSLUtils.openConnection(url);
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestMethod("HEAD");
        connection.connect();
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            case HttpURLConnection.HTTP_OK:
                return true;
            default:
                throw new IOException("Unexpected response code: " + responseCode);
        }
    }
    public interface IOConsumer<T, R, E extends Exception> {
        @Nullable
        R accept(T value) throws IOException, E;
    }

}
