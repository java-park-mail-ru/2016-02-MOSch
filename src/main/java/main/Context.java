package main;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KOPTE3 on 30.03.2016.
 */
@SuppressWarnings("unchecked")
public class Context {
    @NotNull
    final Map<Class, Object> contextMap = new HashMap<>();

    public void put(@NotNull Class classClass, @NotNull Object object) {
        contextMap.put(classClass, object);
    }

    @NotNull
    public <T> T get(@NotNull Class<T> clazz) {
        return (T) contextMap.get(clazz);
    }
}
