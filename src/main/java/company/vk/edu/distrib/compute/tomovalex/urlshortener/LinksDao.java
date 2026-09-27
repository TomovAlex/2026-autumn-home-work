package company.vk.edu.distrib.compute.tomovalex.urlshortener;

import company.vk.edu.distrib.compute.Dao;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class LinksDao implements Dao<String> {
    private final Map<String, String> links = new ConcurrentHashMap<>();

    private void isValidKey(String key) {
        if(key.length() != 10) {
            throw new IllegalArgumentException("invalid key: " + key);
        }
    }
    @Override
    public String get(String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        String value = links.get(key);
        if (value == null) {
            throw new NoSuchElementException();
        }

        return value;
    }

    @Override
    public void upsert(String key, String value) throws IllegalArgumentException, IOException {
        isValidKey(key);
        links.put(key, value);
    }

    @Override
    public void delete(String key) throws IllegalArgumentException, IOException {
        links.remove(key);
    }

    @Override
    public void close() throws IOException {

    }
}
