package threadlocal;

import java.util.HashMap;

public class MyClass {
    ThreadLocal<HashMap<String, String>> threadLocalMap = new ThreadLocal<>();

    public void put(String key, String value) {
        HashMap<String, String> map = threadLocalMap.get();
        if (map == null) {
            HashMap<String, String> newMap = new HashMap<>();
            newMap.put(key, value);
            threadLocalMap.set(newMap);
            return;
        }
        map.put(key, value);
    }

    public String get(String key) {
        HashMap<String, String> map = threadLocalMap.get();
        if (map == null) {
            threadLocalMap.set(new HashMap<>());
            return null;
        }
        return map.get(key);
    }

    public void clear() {
        threadLocalMap.remove();
    }
}
