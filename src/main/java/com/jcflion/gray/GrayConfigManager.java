package com.jcflion.gray;

import com.jcflion.ConfigManager;
import com.jcflion.util.CollectionUtil;
import com.jcflion.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kanner
 */
public final class GrayConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrayConfigManager.class);

    private static final ConcurrentHashMap<String, GrayConfig> GRAY_CONFIG_MAP = new ConcurrentHashMap<>();

    private GrayConfigManager() {}

    /**
     * 灰度判断id是否允许
     *
     * @param configName 配置项格式：configFilename.key
     * @param id 要判断是否允许灰度的id
     * @return 是否允许
     */
    public static boolean isAllowed(String configName, String id) {
        final GrayConfig grayConfig = parseConfig(configName);
        return isAllowed(grayConfig, id);
    }

    /**
     * 灰度判断id是否允许
     *
     * @param configFilename 配置文件名
     * @param key 配置名
     * @param id 要判断是否允许灰度的id
     * @return 是否允许
     */
    public static boolean isAllowed(String configFilename, String key, String id) {
        final GrayConfig grayConfig = parseConfig(configFilename, key);
        return isAllowed(grayConfig, id);
    }

    /**
     * 灰度判断id是否允许
     *
     * @param configName 配置项格式：configFilename.key
     * @param id 要判断是否允许灰度的id
     * @return 是否允许
     */
    public static boolean isAllowed(String configName, long id) {
        final GrayConfig grayConfig = parseConfig(configName);
        return isAllowed(grayConfig, id);
    }

    /**
     * 灰度判断id是否允许
     *
     * @param configFilename 配置文件名
     * @param key 配置名
     * @param id 要判断是否允许灰度的id
     * @return 是否允许
     */
    public static boolean isAllowed(String configFilename, String key, long id) {
        final GrayConfig grayConfig = parseConfig(configFilename, key);
        return isAllowed(grayConfig, id);
    }

    /**
     * 灰度判断id是否允许
     *
     * @param grayConfig 灰度策略
     * @param id 要判断是否允许灰度的id
     * @return 是否允许
     */
    private static boolean isAllowed(GrayConfig grayConfig, String id) {
        if (null == grayConfig || null == grayConfig.getType()) {
            return false;
        }
        if (RuleType.ALLOW.equals(grayConfig.getType())) {
            // allow
            // check black list
            if (CollectionUtil.isNotEmpty(grayConfig.getNameSet())) {
                if (grayConfig.getNameSet().contains(id)) {
                    return false;
                }
            }
            // check percent list
            if (CollectionUtil.isNotEmpty(grayConfig.getPercentSet())) {
                final long hashId = StringUtil.hash(id);
                for (final Long percent : grayConfig.getPercentSet()) {
                    if (null != percent && 0L == hashId % percent) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            // deny
            // check white list
            if (CollectionUtil.isNotEmpty(grayConfig.getNameSet())) {
                if (grayConfig.getNameSet().contains(id)) {
                    return true;
                }
            }
            // check percent list
            if (CollectionUtil.isNotEmpty(grayConfig.getPercentSet())) {
                final long hashId = StringUtil.hash(id);
                for (final Long percent : grayConfig.getPercentSet()) {
                    if (null != percent && 0L == hashId % percent) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 灰度判断id是否允许
     *
     * @param grayConfig 灰度策略
     * @param id 要判断是否允许灰度的id
     * @return 是否允许
     */
    private static boolean isAllowed(GrayConfig grayConfig, long id) {
        if (null == grayConfig || null == grayConfig.getType()) {
            return false;
        }
        if (RuleType.ALLOW.equals(grayConfig.getType())) {
            // allow
            // check black list
            if (CollectionUtil.isNotEmpty(grayConfig.getNameSet())) {
                final String name = String.valueOf(id);
                if (grayConfig.getNameSet().contains(name)) {
                    return false;
                }
            }
            // check percent list
            if (CollectionUtil.isNotEmpty(grayConfig.getPercentSet())) {
                for (final Long percent : grayConfig.getPercentSet()) {
                    if (null != percent && 0L == id % percent) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            // deny
            // check white list
            if (CollectionUtil.isNotEmpty(grayConfig.getNameSet())) {
                final String name = String.valueOf(id);
                if (grayConfig.getNameSet().contains(name)) {
                    return true;
                }
            }
            // check percent list
            if (CollectionUtil.isNotEmpty(grayConfig.getPercentSet())) {
                for (final Long percent : grayConfig.getPercentSet()) {
                    if (null != percent && 0L == id % percent) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 根据配置文件名和配置名查询并解析灰度规则
     *
     * @param configFilename 配置文件名
     * @param key 配置名
     * @return 灰度规则
     */
    private static GrayConfig parseConfig(String configFilename, String key) {
        if (StringUtil.isEmpty(configFilename) || StringUtil.isEmpty(key)) {
            return null;
        }
        final String configName = StringUtil.concat(configFilename, ".", key);
        GrayConfig grayConfig = GRAY_CONFIG_MAP.get(configName);
        if (null != grayConfig) { // hit cache
            return grayConfig;
        }
        // miss cache
        synchronized (GrayConfigManager.class) {
            final String config = ConfigManager.getConfig(configFilename, key);
            if (StringUtil.isEmpty(config)) {
                return null;
            }
            final String[] arr = StringUtil.splitFirst(config.trim(), ";");
            if (CollectionUtil.isEmpty(arr)) {
                return null;
            }
            final RuleType ruleType = RuleType.parse(arr[0].trim());
            if (null == ruleType) {
                return null;
            }
            Set<Long> percentSet = null;
            Set<String> nameSet = null;
            if (arr.length > 1) {
                final String grayContent = arr[1].trim();
                if (StringUtil.isNotEmpty(grayContent)) {
                    final String[] grayItems = StringUtil.split(grayContent, ";");
                    if (CollectionUtil.isNotEmpty(grayItems)) {
                        percentSet = new HashSet<>(grayItems.length);
                        nameSet = new HashSet<>(grayItems.length);
                        for (final String grayItem : grayItems) {
                            if (StringUtil.isEmpty(grayItem)) {
                                continue;
                            }
                            if (grayItem.startsWith("%")) {
                                // percent
                                final String percent = StringUtil.removeFirst(grayItem, "%");
                                if (StringUtil.isEmpty(percent)) {
                                    continue;
                                }
                                try {
                                    percentSet.add(Long.parseLong(percent));
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            } else {
                                // name
                                nameSet.add(grayItem);
                            }
                        }
                        if (percentSet.isEmpty()) {
                            percentSet = null;
                        }
                        if (nameSet.isEmpty()) {
                            nameSet = null;
                        }
                    }
                }
            }
            grayConfig = new GrayConfig(configName, ruleType, nameSet, percentSet);
            GRAY_CONFIG_MAP.put(configName, grayConfig);
        }
        return grayConfig;
    }

    /**
     * 根据配置项查询并解析灰度规则
     *
     * @param configName 配置项格式：configFilename.key
     * @return 灰度规则
     */
    private static GrayConfig parseConfig(String configName) {
        if (StringUtil.isEmpty(configName)) {
            return null;
        }
        final String[] arr = StringUtil.splitFirst(configName, ".");
        if (null == arr || 2 != arr.length) {
            return null;
        }
        final String drawerName = arr[0];
        final String key = arr[1];
        return parseConfig(drawerName, key);
    }

    public static void resetGrayConfigCache() {
        GRAY_CONFIG_MAP.clear();
        LOGGER.info("resetGrayConfigCache!!!");
    }
}
