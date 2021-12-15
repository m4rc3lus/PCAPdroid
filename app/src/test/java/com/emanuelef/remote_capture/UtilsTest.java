package com.emanuelef.remote_capture;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void formatBytes_bytesAtBoundary_returnsCorrectSuffixAndValue() {
        String result = Utils.formatBytes(1024);
        assertThat(result).isEqualTo("1.0 KB");
    }

    @Test
    public void formatBytes_bytesBelowBoundary_returnsCorrectSuffixAndValue() {
        String result = Utils.formatBytes((1024*1024*1024)-60001);
        assertThat(result).isEqualTo("1023.9 MB");
    }

    @Test
    public void proto2str_protocolNumberUDP_ReturnsProtocolStringName() {
        String result = Utils.proto2str(17);
        assertThat(result).isEqualTo("UDP");
    }

    @Test
    public void shorten_stringLengthEqualsMasLength_returnUnchangedString() {
        String result = Utils.shorten("TestString", 10);
        assertThat(result).isEqualTo("TestString");
    }

    @Test
    public void shorten_longString_returnStringLengthEqualMaxLength() {
        String result = Utils.shorten("TestString", 5);
        assertThat(result).isEqualTo("Test…");
    }
}