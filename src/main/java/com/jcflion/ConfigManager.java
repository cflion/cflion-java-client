package com.jcflion;

import com.jcflion.util.CollectionUtil;
import com.jcflion.util.StringUtil;
import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.data.ByteSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kanner
 */
public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> CONFIG_LOCAL_MAP = new ConcurrentHashMap<>();

    private ConfigManager() {}

    /**
     * 根据配置文件名和配置名查询配置
     *
     * @param configFilename 配置文件名
     * @param key 配置项
     * @return 配置值
     */
    public static String getConfig(String configFilename, String key) {
        if (StringUtil.isEmpty(configFilename) || StringUtil.isEmpty(key) || CollectionUtil.isEmpty(CONFIG_LOCAL_MAP)) {
            return null;
        }
        final ConcurrentHashMap<String, String> configMap = CONFIG_LOCAL_MAP.get(configFilename);
        if (CollectionUtil.isEmpty(configMap)) {
            return null;
        }
        return configMap.get(key);
    }

    /**
     * 根据配置文件名和配置名查询配置，若配置项不存在，则使用默认值作为返回结果
     *
     * @param configFilename 配置文件名
     * @param key 配置名
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static String getConfigWithDefault(String configFilename, String key, String defaultValue) {
        final String value = getConfig(configFilename, key);
        return StringUtil.isEmpty(value) ? defaultValue : value;
    }

    /**
     * 查询配置
     *
     * @param configName 配置项格式：configFilename.key
     * @return 配置值
     */
    public static String getConfig(String configName) {
        if (StringUtil.isEmpty(configName)) {
            return null;
        }
        final String[] arr = StringUtil.splitFirst(configName, ".");
        if (null == arr || 2 != arr.length) {
            return null;
        }
        final String drawerName = arr[0];
        final String key = arr[1];
        return getConfig(drawerName, key);
    }

    /**
     * 查询配置，若不存在配置项，则使用默认值作为返回结果
     *
     * @param configName 配置项格式：configFilename.key
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static String getConfigWithDefault(String configName, String defaultValue) {
        final String value = getConfig(configName);
        return StringUtil.isEmpty(value) ? defaultValue : value;
    }

    /**
     * 查询boolean配置，若不存在配置项，则使用默认值作为返回结果
     *
     * @param configName 配置项格式：configFilename.key
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static boolean getBooleanConfig(String configName, boolean defaultValue) {
        final String value = getConfig(configName);
        return StringUtil.isEmpty(value) ? defaultValue : StringUtil.convertBoolean(value, defaultValue);
    }

    /**
     * 查询double配置，若不存在配置项，则使用默认值作为返回结果
     *
     * @param configName 配置项格式：configFilename.key
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static double getDoubleConfig(String configName, double defaultValue) {
        final String value = getConfig(configName);
        return StringUtil.isEmpty(value) ? defaultValue : StringUtil.convertDouble(value, defaultValue);
    }

    /**
     * 查询float配置，若不存在配置项，则使用默认值作为返回结果
     *
     * @param configName 配置项格式：configFilename.key
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static double getFloatConfig(String configName, float defaultValue) {
        final String value = getConfig(configName);
        return StringUtil.isEmpty(value) ? defaultValue : StringUtil.convertFloat(value, defaultValue);
    }

    /**
     * 查询short配置，若不存在配置项，则使用默认值作为返回结果
     *
     * @param configName 配置项格式：configFilename.key
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static short getShortConfig(String configName, short defaultValue) {
        final String value = getConfig(configName);
        return StringUtil.isEmpty(value) ? defaultValue : StringUtil.convertShort(value, defaultValue);
    }

    /**
     * 查询int配置，若不存在配置项，则使用默认值作为返回结果
     *
     * @param configName 配置项格式：configFilename.key
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static int getIntConfig(String configName, int defaultValue) {
        final String value = getConfig(configName);
        return StringUtil.isEmpty(value) ? defaultValue : StringUtil.convertInt(value, defaultValue);
    }

    /**
     * 查询long配置，若不存在配置项，则使用默认值作为返回结果
     *
     * @param configName 配置项格式：configFilename.key
     * @param defaultValue 默认配置值
     * @return 配置值
     */
    public static long getLongConfig(String configName, long defaultValue) {
        final String value = getConfig(configName);
        return StringUtil.isEmpty(value) ? defaultValue : StringUtil.convertLong(value, defaultValue);
    }

    /**
     * 加载配置文本在内存中
     *
     * @param configContent 应用的配置文本
     */
    public static void reloadConfigContent(String configContent) {
        if (StringUtil.isEmpty(configContent)) {
            return;
        }
        final String[] lines = StringUtil.split(configContent, "\n");
        if (CollectionUtil.isEmpty(lines)) {
            return;
        }
        String configFilename = null;
        for (final String line : lines) {
            if (StringUtil.isEmpty(line)) {
                continue;
            }
            if (line.startsWith("#")) { // comment
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) { // config file name
                configFilename = line.substring(1, line.length() - 1);
            } else if (line.contains("=")) {
                parseConfigMap(configFilename, line);
            }
        }
    }

    /**
     * 解析配置文件的一个配置行
     *
     * @param configFilename 配置文件名
     * @param line 一个配置行
     */
    private static void parseConfigMap(String configFilename, String line) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("parse config, configFilename={}, line={}", configFilename, line);
        }
        if (StringUtil.isEmpty(configFilename) || StringUtil.isEmpty(line) || line.startsWith("#")) {
            return;
        }
        final String[] arr = StringUtil.splitFirst(line, "=");
        if (null == arr || 2 != arr.length || StringUtil.isEmpty(arr[0]) || StringUtil.isEmpty(arr[1])) {
            return;
        }
        updateConfigMap(configFilename.trim(), arr[0].trim(), arr[1].trim());
    }

    /**
     * 更新内存中的配置
     *
     * @param drawerName 配置文件名
     * @param key 配置名
     * @param value 配置值
     */
    private static void updateConfigMap(String drawerName, String key, String value) {
        if (StringUtil.isEmpty(drawerName) || StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
            return;
        }
        CONFIG_LOCAL_MAP.putIfAbsent(drawerName, new ConcurrentHashMap<>());
        CONFIG_LOCAL_MAP.get(drawerName).put(key, value);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("update config, filename={}, key={}, value={}", drawerName, key, value);
        }
    }

    public static void main(String[] args) throws Exception {
        final Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();
        final KV kvClient = client.getKVClient();
        System.out.println(kvClient.get(ByteSequence.fromString("foo")).get().getKvs().get(0).getValue().toStringUtf8());
    }
}
