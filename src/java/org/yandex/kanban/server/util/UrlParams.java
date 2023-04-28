package org.yandex.kanban.server.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

public class UrlParams {
    private final Map<String, List<String>> params;

    private UrlParams(Map<String, List<String>> params) {
        this.params = params;
    }

    public static UrlParams getParams(URI uri) throws MalformedURLException {
        if(uri.getRawQuery() == null){
            Map<String, List<String>> map = new HashMap<>();
            return new UrlParams(map);
        }
        String[] pairs = uri.getRawQuery().split("&");
        Map<String, List<String>> map = new HashMap<>();
        for(String param: pairs){
            String[] pair = param.split("=");
            String key = pair[0];
            if(pair.length != 2)
                throw new MalformedURLException("Param \"" + key + "\" has no value.");
            String[] values = pair[1].split(";");
            map.put(key, Arrays.asList(values));
        }
        return new UrlParams(map);
    }

    public boolean contains(String key){
        return params.containsKey(key);
    }

    public List<String> get(String key){
        return params.get(key);
    }

    public Optional<String> getFirst(String key){
        List<String> vals = params.get(key);

        if(vals == null || vals.size() == 0){
            return Optional.empty();
        }
        return Optional.of(vals.get(vals.size()-1));
    }
}
