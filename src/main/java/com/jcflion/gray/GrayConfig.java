package com.jcflion.gray;

import java.io.Serializable;
import java.util.Set;

/**
 * 灰度策略
 *
 * @author kanner
 */
public class GrayConfig implements Serializable {

    /**
     * 配置项名：configFilename.key
     */
    private String configName;
    /**
     * 规则类型
     */
    private RuleType type;
    /**
     * 名单列表
     */
    private Set<String> nameSet;
    /**
     * 取模规则列表
     */
    private Set<Long> percentSet;

    public GrayConfig() {
    }

    public GrayConfig(String configName, RuleType type, Set<String> nameSet, Set<Long> percentSet) {
        this.configName = configName;
        this.type = type;
        this.nameSet = nameSet;
        this.percentSet = percentSet;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    public Set<String> getNameSet() {
        return nameSet;
    }

    public void setNameSet(Set<String> nameSet) {
        this.nameSet = nameSet;
    }

    public Set<Long> getPercentSet() {
        return percentSet;
    }

    public void setPercentSet(Set<Long> percentSet) {
        this.percentSet = percentSet;
    }

    @Override
    public String toString() {
        return "GrayConfig [configName=" + configName + ", type=" + type + ", nameSet=" + nameSet + ", percentSet=" + percentSet
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configName == null) ? 0 : configName.hashCode());
        result = prime * result + ((nameSet == null) ? 0 : nameSet.hashCode());
        result = prime * result + ((percentSet == null) ? 0 : percentSet.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GrayConfig other = (GrayConfig) obj;
        if (configName == null) {
            if (other.configName != null)
                return false;
        } else if (!configName.equals(other.configName))
            return false;
        if (nameSet == null) {
            if (other.nameSet != null)
                return false;
        } else if (!nameSet.equals(other.nameSet))
            return false;
        if (percentSet == null) {
            if (other.percentSet != null)
                return false;
        } else if (!percentSet.equals(other.percentSet))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}
