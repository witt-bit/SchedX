package org.schedx.util;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * <p>语义化版本对象</p>
 * <p>创建于 2025-05-03 21:06 21:06 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SemanticVersion implements Serializable, Comparable<SemanticVersion> {

    private static final long serialVersionUID = -4973529688559993054L;

    /**
     * 初始版本
     */
    public static final SemanticVersion INIT_VERSION = new SemanticVersion(0, 0, 0, null);

    /**
     * 主要版本
     */
    private long major;
    /**
     * 次要版本
     */
    private long minor;
    /**
     * 修订版本
     */
    private long patch;

    /**
     * 预发行版
     */
    private String preRelease;

    public SemanticVersion(long major, long minor, long patch, String preRelease) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
    }

    public SemanticVersion(long major, long minor, long patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final SemanticVersion that = (SemanticVersion) o;
        return major == that.major && minor == that.minor && patch == that.patch && Objects.equals(preRelease, that.preRelease);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(major);
        result = 31 * result + Long.hashCode(minor);
        result = 31 * result + Long.hashCode(patch);
        result = 31 * result + Objects.hashCode(preRelease);
        return result;
    }

    @Override
    public String toString() {
        final StringJoiner versionString = new StringJoiner(".");
        versionString.add(String.valueOf(this.major));
        versionString.add(String.valueOf(this.minor));
        versionString.add(String.valueOf(this.patch));
        if (this.preRelease != null) {
            versionString.add(this.preRelease);
        }
        return versionString.toString();
    }

    @Override
    public int compareTo(SemanticVersion o) {
        if (this.major > o.major) {
            return 1;
        }

        if (this.major < o.major) {
            return -1;
        }
        if (this.minor > o.minor) {
            return 1;
        }

        if (this.minor < o.minor) {
            return -1;
        }

        if (this.patch > o.patch) {
            return 1;
        }


        if (this.patch < o.patch) {
            return -1;
        }

        if (this.preRelease == null && o.preRelease == null) {
            return 0;
        }

        if (this.preRelease == null || o.preRelease == null) {
            return this.preRelease == null ? -1 : 1;
        }

        return this.preRelease.compareTo(o.preRelease);
    }

    /**
     * 大于
     *
     * @param o o
     * @return boolean
     */
    public boolean isGreaterThan(SemanticVersion o) {
        return this.compareTo(o) > 0;
    }

    /**
     * 大于
     *
     * @param o o
     * @return boolean
     */
    public boolean isGreaterThan(String o) {
        return this.isGreaterThan(parse(o));
    }

    /**
     * 少于
     *
     * @param o o
     * @return boolean
     */
    public boolean isLessThan(SemanticVersion o) {
        return this.compareTo(o) < 0;
    }

    /**
     * 少于
     *
     * @param o o
     * @return boolean
     */
    public boolean isLessThanOrEqual(SemanticVersion o) {
        return this.compareTo(o) <= 0;
    }

    /**
     * 少于
     *
     * @param o o
     * @return boolean
     */
    public boolean isLessThan(String o) {
        return this.isLessThan(parse(o));
    }

    /**
     * 少于
     *
     * @param o o
     * @return boolean
     */
    public boolean isLessThanOrEqual(String o) {
        return this.isLessThanOrEqual(parse(o));
    }

    /**
     * 等于
     *
     * @param o o
     * @return boolean
     */
    public boolean isEqualTo(SemanticVersion o) {
        return this.compareTo(o) == 0;
    }

    /**
     * 等于
     *
     * @param o o
     * @return boolean
     */
    public boolean isEqualTo(String o) {
        return this.isEqualTo(parse(o));
    }

    /**
     * 解析字符串版本号
     *
     * @param versionNumber 版本号
     * @return {@link SemanticVersion }
     */
    public static SemanticVersion parse(String versionNumber) {
        if (versionNumber == null || versionNumber.isEmpty()) {
            return INIT_VERSION;
        }

        final String retVersionNumber = versionNumber.trim();
        final int versionLength = retVersionNumber.length();
        if (versionLength == 0) {
            return INIT_VERSION;
        }
        final StringBuilder segmentBuilder = new StringBuilder();
        int index = 0;
        do {
            char c = retVersionNumber.charAt(index);
            if (c == '.') {
                index++;
                break;
            }

            if (c < '0' || c > '9') {
                index++;
                continue;
            }

            segmentBuilder.append(c);

        } while ((++index) < versionLength);

        final long majorVersion = Long.parseLong(segmentBuilder.toString());
        if (index >= versionLength) {
            return new SemanticVersion(majorVersion, 0, 0, null);
        }
        segmentBuilder.setLength(0);

        while (index < versionLength) {
            char c = retVersionNumber.charAt(index);
            index++;
            if (c == '.') {
                break;
            }

            if (c < '0' || c > '9') {
                continue;
            }

            segmentBuilder.append(c);
        }

        final long minorVersion = Long.parseLong(segmentBuilder.toString());
        if (index >= versionLength) {
            return new SemanticVersion(majorVersion, minorVersion, 0, null);
        }
        segmentBuilder.setLength(0);

        while (index < versionLength) {
            char c = retVersionNumber.charAt(index);
            index++;
            if (c == '.' || c == '-' || c == '+') {
                break;
            }

            if (c < '0' || c > '9') {
                continue;
            }

            segmentBuilder.append(c);
        }

        final long patchVersion = Long.parseLong(segmentBuilder.toString());
        if (index >= versionLength) {
            return new SemanticVersion(majorVersion, minorVersion, patchVersion, null);
        }
        final String preRelease = retVersionNumber.substring(index);
        return new SemanticVersion(majorVersion, minorVersion, patchVersion, preRelease);
    }
}