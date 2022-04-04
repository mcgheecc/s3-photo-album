package com.cmg.springs3service.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class StringUtilTest {

    private final static String NULL = null;
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"null", ""})
    void isNotNull_expectFalse(String param) {
        boolean actual = StringUtil.isNotNull(param);
        assertThat(actual).isFalse();
    }

    @Test
    void isNotNull_givenString_expectTrue() {
        boolean actual = StringUtil.isNotNull("test");
        assertThat(actual).isTrue();
    }

}
