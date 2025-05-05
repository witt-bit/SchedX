package org.schedx.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>{@link SemanticVersion}类相关的测试</p>
 * <p>创建于 2025-05-03 21:21 21:21 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SemanticVersionTest {

    @Test
    public void parse() {
        String version = "2.3.0";
        SemanticVersion versionObject = SemanticVersion.parse(version);
        Assert.assertEquals("SemanticVersion parse'" + versionObject + "' error", version, versionObject.toString());

        version = "2.7.3.RELEASE";
        versionObject = SemanticVersion.parse(version);
        Assert.assertEquals("SemanticVersion parse'" + versionObject + "' error", version, versionObject.toString());

        version = "  ";
        versionObject = SemanticVersion.parse(version);
        Assert.assertEquals("SemanticVersion parse'" + versionObject + "' error", "0.0.0", versionObject.toString());

        version = "";
        versionObject = SemanticVersion.parse(version);
        Assert.assertEquals("SemanticVersion parse'" + versionObject + "' error", "0.0.0", versionObject.toString());

        version = null;
        versionObject = SemanticVersion.parse(version);
        Assert.assertEquals("SemanticVersion parse'" + versionObject + "' error", "0.0.0", versionObject.toString());

        version = "1";
        versionObject = SemanticVersion.parse(version);
        Assert.assertEquals("SemanticVersion parse'" + versionObject + "' error", "1.0.0", versionObject.toString());

        version = "3.7";
        versionObject = SemanticVersion.parse(version);
        Assert.assertEquals("SemanticVersion parse'" + versionObject + "' error", "3.7.0", versionObject.toString());
    }

    @Test
    public void compare() {
        String version = "2.3.0";
        SemanticVersion versionObject = SemanticVersion.parse(version);
        Assert.assertTrue("SemanticVersion isEqualTo '" + versionObject + "'='" + version + "' error", versionObject.isEqualTo(version));
        version = "2.3";
        Assert.assertTrue("SemanticVersion isEqualTo '" + versionObject + "'='" + version + "' error", versionObject.isEqualTo(version));

        version = "2.3.0.RELEASE";
        Assert.assertTrue("SemanticVersion isLessThan '" + versionObject + "'<='" + version + "' error", versionObject.isLessThan(version));
        version = "2.3.1";
        Assert.assertTrue("SemanticVersion isLessThan '" + versionObject + "'<='" + version + "' error", versionObject.isLessThan(version));

        version = "1.9.6";
        Assert.assertTrue("SemanticVersion isGreaterThan '" + versionObject + "'>='" + version + "' error", versionObject.isGreaterThan(version));
        version = "1.9.6.RELEASE";
        Assert.assertTrue("SemanticVersion isGreaterThan '" + versionObject + "'>='" + version + "' error", versionObject.isGreaterThan(version));
    }
}