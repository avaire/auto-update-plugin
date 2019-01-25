package com.avairebot.autoupdate;

import com.avairebot.utilities.NumberUtil;

public class SemanticVersion {

    private int major;
    private int minor;
    private int patch;

    SemanticVersion(String version) {
        String[] split = version.split("\\.");

        if (split.length > 0) {
            major = NumberUtil.parseInt(split[0]);
        }
        if (split.length > 1) {
            minor = NumberUtil.parseInt(split[1]);
        }
        if (split.length > 2) {
            patch = NumberUtil.parseInt(split[2]);
        }
    }

    int getMajor() {
        return major;
    }

    int getMinor() {
        return minor;
    }

    int getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
