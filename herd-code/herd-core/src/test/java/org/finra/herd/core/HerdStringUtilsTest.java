/*
* Copyright 2015 herd contributors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.finra.herd.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test driver for the {@link HerdStringUtils} class
 */
public class HerdStringUtilsTest extends AbstractCoreTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String CSV_INJECTION_ERROR_MSG = "One or more schema column fields start with a prohibited character.";

    @Test
    public void testDecodeBase64()
    {
        // Test decode using hard coded values.
        assertEquals("UT_SomeText", HerdStringUtils.decodeBase64("VVRfU29tZVRleHQ="));

        // Test decode using random string and encoder.
        String encodedText = StringUtils.toEncodedString(Base64.getEncoder().encode(STRING_VALUE.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        assertEquals(STRING_VALUE, HerdStringUtils.decodeBase64(encodedText));
    }

    @Test
    public void testGetMinimizedFilePath()
    {
        final String directoryPath = "this/is/a/test/path";
        final String fileName = "fileName1.csv";
        final String filePath = directoryPath + "/" + fileName;

        // Happy path test.
        assertEquals("Did not minimize correctly.", fileName, HerdStringUtils.getMinimizedFilePath(filePath, directoryPath));

        // Directory path not in file path.
        assertEquals("Did not minimize correctly.", filePath, HerdStringUtils.getMinimizedFilePath(filePath, directoryPath + "FOO"));
    }

    @Test
    public void testGetShortDescription()
    {
        String longString = RandomStringUtils.randomAlphabetic(500);
        String result = HerdStringUtils.getShortDescription(longString, 10);

        assertEquals(result.length(), 10);
    }

    @Test
    public void testStripHtmlDirtyFragment()
    {
        String result = HerdStringUtils.stripHtml("<li>fragment with <b>html</b></li>");
        assertEquals("fragment with html", result);
    }

    @Test
    public void testStripHtmlCleanFragment()
    {
        String result = HerdStringUtils.stripHtml("fragment\nwith no html");
        assertEquals("fragment\nwith no html", result);
    }

    @Test
    public void testStripHtmlDirtyFragmentWithPartialTags()
    {
        String result = HerdStringUtils.stripHtml("fragment<li><b> with no</b> html<l");
        assertEquals("fragment with no html", result);
    }

    @Test
    public void testStripHtmlDirtyFragmentWithEscapedHtml()
    {
        String result = HerdStringUtils.stripHtml("&lt;li&gt;fragment with escaped &lt;b&gt;html&lt;/b&gt;&lt;/li&gt;");
        assertEquals("fragment with escaped html", result);
    }

    @Test
    public void testStripHtmlDirtyFragmentWithEscapedHtmlAndUnrecognizedEntity()
    {
        String result = HerdStringUtils.stripHtml("&lt;li&gt;fragment with escaped &lt;b&gt;html&lt;/b&gt;&lt;/li&gt; & unrecognized entity &zzz;x");
        assertEquals("fragment with escaped html &amp; unrecognized entity &amp;zzz;x", result);
    }

    @Test
    public void testStripHtmlDirtyFragmentWithWhitelist()
    {
        String result = HerdStringUtils.stripHtml("fragment<li><b> with <hlt>no</hlt></b> html</li>", "<hlt>");
        assertEquals("fragment with <hlt>no</hlt> html", result);
    }

    @Test
    public void testStripHtmlDirtyFragmentWithWhitelistWithStyle()
    {
        String result = HerdStringUtils.stripHtml("fragment<li><b> with <hlt class=\"highlight\">no</hlt></b> html</li>", "<hlt>");
        assertEquals("fragment with <hlt class=\"highlight\">no</hlt> html", result);
    }

    @Test
    public void testStripHtmlDirtyFragmentWithMultipleWhitelistTags()
    {
        String result = HerdStringUtils.stripHtml("fragment<li><b> with <hlt>no</hlt></b> html</li>", "<hlt>", "<b>");
        assertEquals("fragment<b> with <hlt>no</hlt></b> html", result);
    }

    @Test
    public void testCheckCsvInjectionStartsWithEqualsToCharacter()
    {
        // Specify the expected exception.
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is(CSV_INJECTION_ERROR_MSG));

        HerdStringUtils.checkCsvInjection("=abc", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionStartsWithPlusCharacter()
    {
        // Specify the expected exception.
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is(CSV_INJECTION_ERROR_MSG));

        HerdStringUtils.checkCsvInjection("+abc", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionStartsWithAtCharacter()
    {
        // Specify the expected exception.
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is(CSV_INJECTION_ERROR_MSG));

        HerdStringUtils.checkCsvInjection("@abc", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionStartsWithMinusCharacter()
    {
        // Specify the expected exception.
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is(CSV_INJECTION_ERROR_MSG));

        HerdStringUtils.checkCsvInjection("-abc", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionValidEmptyCharacter()
    {
        HerdStringUtils.checkCsvInjection("", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionValidNull()
    {
        HerdStringUtils.checkCsvInjection(null, CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionValidStartsWithNormalCharacter()
    {
        HerdStringUtils.checkCsvInjection("hello", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionValidPlusCharacterInMiddle()
    {
        HerdStringUtils.checkCsvInjection("abc+def", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testCheckCsvInjectionValidStartsWithBlank()
    {
        HerdStringUtils.checkCsvInjection(" bc+def", CSV_INJECTION_ERROR_MSG);
    }

    @Test
    public void testConvertStringToInteger()
    {
        assertEquals(INTEGER_VALUE, HerdStringUtils.convertStringToInteger(INTEGER_VALUE.toString(), INTEGER_VALUE_2));
    }

    @Test
    public void testConvertStringToIntegerBlankStringValue()
    {
        assertEquals(INTEGER_VALUE, HerdStringUtils.convertStringToInteger(null, INTEGER_VALUE));
        assertEquals(INTEGER_VALUE, HerdStringUtils.convertStringToInteger(EMPTY_STRING, INTEGER_VALUE));
    }

    @Test
    public void testConvertStringToIntegerInvalidIntegerValue()
    {
        try
        {
            HerdStringUtils.convertStringToInteger(INVALID_INTEGER_VALUE, INTEGER_VALUE);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(String.format("Failed to convert \"%s\" value to %s.", INVALID_INTEGER_VALUE, Integer.class.getName()), e.getMessage());
        }
    }

    @Test
    public void testLoggingPasswordMasked()
    {
        String message = "\"hive.server2.keystore.name\":\"testname1\"," + "\"hive.server2.keystore.PASSWORD\":\"test-123\"," +
            "\"hive.server3.keystore.password\":\"TEST$2!1\"," + "\"hive.server2.keystore.name\":\"testname1\"";
        String expectedMessage =
            "\"hive.server2.keystore.name\":\"testname1\"," + "\"hive.server2.keystore.PASSWORD\":\"" + HerdStringUtils.HIDDEN_TEXT + "\"," +
                "\"hive.server3.keystore.password\":\"" + HerdStringUtils.HIDDEN_TEXT + "\"," + "\"hive.server2.keystore.name\":\"testname1\"";
        String sanitizedMessage = HerdStringUtils.sanitizeLogText(message);
        assertEquals(expectedMessage, sanitizedMessage);

        String messsage2 = "{\"name\": \"jdbc.user\", \"value\": \"user\"}," +
            "{\"name\": \"hive.server2.keystore.password\", \"value\": \"!This-is-password\"}, {\"name\": \"password\", \"value\": \"pass\"}\", {\"name\": \"jdbc.url\", \"value\": \"AURL\"}";
        String expectedMessage2 =
            "{\"name\": \"jdbc.user\", \"value\": \"user\"}," + "{\"name\": \"hive.server2.keystore.password\", \"value\": \"" + HerdStringUtils.HIDDEN_TEXT +
                "\"}, {\"name\": \"password\", \"value\": \"" + HerdStringUtils.HIDDEN_TEXT + "\"}\", {\"name\": \"jdbc.url\", \"value\": \"AURL\"}";
        String sanitizedMessage2 = HerdStringUtils.sanitizeLogText(messsage2);
        assertEquals(expectedMessage2, sanitizedMessage2);

        String message3 = "<username>tester</username><password>@!pass_dd</password><url>a url</url>";
        String expectedMessage3 = "<username>tester</username><password>" + HerdStringUtils.HIDDEN_TEXT + "</password><url>a url</url>";
        String sanitizedMessage3 = HerdStringUtils.sanitizeLogText(message3);
        assertEquals(expectedMessage3, sanitizedMessage3);

        String message4 = "<hive.password>hive!pass</hive.password><username>tester</username><jdbc.password>@!pass_dd</jdbc.password><url>a url</url>";
        String expectedMessage4 =
            "<hive.password>" + HerdStringUtils.HIDDEN_TEXT + "</hive.password><username>tester</username>" + "<jdbc.password>" + HerdStringUtils.HIDDEN_TEXT +
                "</jdbc.password><url>a url</url>";
        String sanitizedMessage4 = HerdStringUtils.sanitizeLogText(message4);
        assertEquals(expectedMessage4, sanitizedMessage4);

        String message5 = "\\\"jdbc.password\\\":\\\"pass!word\\\",\\\"username\\\":\\\"user1\\\",\\\"password\\\":\\\"pass!word\\\",\\\"databaseType\\\":\\\"POSTGRES\\\"";
        String expectedMessage5 = "\\\"jdbc.password\\\":\\\"" + HerdStringUtils.HIDDEN_TEXT + "\\\",\\\"username\\\":\\\"user1\\\",\\\"password\\\":\\\"" +  HerdStringUtils.HIDDEN_TEXT + "\\\",\\\"databaseType\\\":\\\"POSTGRES\\\"";
        String sanitizedMessage5 = HerdStringUtils.sanitizeLogText(message5);
        assertEquals(expectedMessage5, sanitizedMessage5);

        String message6 = "&quot;username&quot;:&quot;user1&quot;,&quot;password&quot;:&quot;pass!word&quot;,&quot;databaseType&quot;:&quot;POSTGRES&quot;";
        String expectedMessage6 = "\"username\":\"user1\",\"password\":\"hidden\",\"databaseType\":\"POSTGRES\"";
        String sanitizedMessage6 = HerdStringUtils.sanitizeLogText(message6);
        assertEquals(expectedMessage6, sanitizedMessage6);

        String message7 = null;
        String expectedMessage7 = "";
        String sanitizedMessage7 = HerdStringUtils.sanitizeLogText(message7);
        assertEquals(expectedMessage7, sanitizedMessage7);

        String otherText =
            "To become registered, securities professionals are required to pass qualification exams to demonstrate competence in their particular securities " +
                "activities. The information below can help guide you through the membership and registration process.";
        StringBuilder messageSB =
            new StringBuilder(message).append(" ").append(messsage2).append(" ").append(message3).append(" ").append(message4).append(otherText);
        String message8 = messageSB.toString();
        StringBuilder expectedMessageSB =
            new StringBuilder(expectedMessage).append(" ").append(expectedMessage2).append(" ").append(expectedMessage3).append(" ").append(expectedMessage4)
                .append(otherText);
        String expectedMessage8 = expectedMessageSB.toString();
        String sanitizedMessage8 = HerdStringUtils.sanitizeLogText(message8);
        assertEquals(expectedMessage8, sanitizedMessage8);
    }

    @Test
    public void testHerdVersionConformingString()
    {
        List<String> conformingStrings = Arrays.asList("0.140.0", "0.140.0-SNAPSHOT", "0.1000.0", "0.10000.0-SNAPSHOT");
        List<String> nonConformingStrings = Arrays.asList("0.140.0%0a%0aINFO:+User+logged+out%3dMr.+Attacker", "bar\r\n", "\rbar<?php>");

        for (String conformingString : conformingStrings)
        {
            assertTrue(HerdStringUtils.verifyHerdVersionConformingString(conformingString));
        }

        for (String nonConformingString: nonConformingStrings)
        {
            assertFalse(HerdStringUtils.verifyHerdVersionConformingString(nonConformingString));
        }
    }

    @Test
    public void testGetFirstLevelPrefix()
    {
        assertNull(HerdStringUtils.getFirstLevelPrefix(null));
        assertEquals(EMPTY_STRING, HerdStringUtils.getFirstLevelPrefix(EMPTY_STRING));
        assertEquals(BLANK_TEXT, HerdStringUtils.getFirstLevelPrefix(BLANK_TEXT));
        assertEquals(STRING_VALUE, HerdStringUtils.getFirstLevelPrefix(STRING_VALUE));
        assertEquals("/", HerdStringUtils.getFirstLevelPrefix("/"));
        assertEquals("/", HerdStringUtils.getFirstLevelPrefix("/" + STRING_VALUE));
        assertEquals(STRING_VALUE + "/", HerdStringUtils.getFirstLevelPrefix(STRING_VALUE + "/"));
        assertEquals(STRING_VALUE + "/", HerdStringUtils.getFirstLevelPrefix(STRING_VALUE + "/" + STRING_VALUE_2));
        assertEquals(STRING_VALUE + "/", HerdStringUtils.getFirstLevelPrefix(STRING_VALUE + "/" + STRING_VALUE_2 + "/" + RANDOM_SUFFIX));
    }
}
