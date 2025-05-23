package org.jingtao8a.util;

public class ServiceUtil {
    public static String makeServiceKey(String interfaceName, String version) {
        String serviceKey = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);
        if (version != null && version.trim().length() > 0) {
            serviceKey += "#".concat(version);
        }
        return serviceKey;
    }
}
