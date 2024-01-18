package org.jingtao8a.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ExtensionLoader<T> { // 扩展类加载器
    /**
     * 扩展类存放地址
     */
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    /**
     *扩展类加载器实例缓存
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADER_MAP = new ConcurrentHashMap<>();//key为接口类

    /**
     * 扩展类单例缓存
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCE_MAP = new ConcurrentHashMap<>();//key为实例类

    /**
     *扩展类实例缓存
     */
    private static Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 扩展类配置列表缓存
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private final Class<T> type;
    private final String defaultNameCache;
    private ExtensionLoader(Class<T> type) {
        this.type = type;
        SPI annotation = type.getAnnotation(SPI.class);
        this.defaultNameCache = annotation.value();
    }

    /**
     * 获取对应类型的扩展加载器实例
     *
     * @param type 扩展类加载器的类型
     * @return 扩展类加载器实例
     */
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        // 扩展类型不能为空
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        // 扩展类型必须为接口
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        // 扩展类型必须被@SPI注解
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>)EXTENSION_LOADER_MAP.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADER_MAP.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>)EXTENSION_LOADER_MAP.get(type);
        }
        return extensionLoader;
    }

    public T getDefaultExtension() {
        return getExtension(defaultNameCache);
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            log.warn("Extension name is null or empty");
            return getDefaultExtension();
        }
        //从缓存中获取实例，没有命中
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        // 双重锁检查
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T)instance;
    }

    private T createExtension(String name) {
        Class<?> clazz = getAllExtensionClass().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name" + name);
        }
        T instance = (T) EXTENSION_INSTANCE_MAP.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCE_MAP.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCE_MAP.get(clazz);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getAllExtensionClass() {
        Map<String, Class<?>> classes = cachedClasses.get();
        //双重锁检查
        if (classes == null) {
            synchronized (cachedClasses) {
                if (classes == null) {
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> classes) {
        //扩展配置文件名
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(classes, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {//忽略注释
                    continue;
                }
                String[] kv = line.split("=");
                Class<?> clazz = classLoader.loadClass(kv[1]);
                extensionClasses.put(kv[0], clazz);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
