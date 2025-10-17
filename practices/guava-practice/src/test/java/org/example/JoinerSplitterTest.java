package org.example;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JoinerSplitterTest {

    @Test
    void joiner_재사용_가능성_테스트() {
        Joiner joiner = Joiner.on(",");

        assertEquals("A,B,C", joiner.join("A", "B", "C"));
        assertEquals("1,2,3", joiner.join(Arrays.asList(1, 2, 3)));
    }

    @Test
    void joiner_null_처리_동시_설정_불가() {
        Joiner joiner = Joiner.on(",").skipNulls();

        assertThrows(UnsupportedOperationException.class, () -> {
            joiner.useForNull("N/A");
        });
    }

    @Test
    void splitter_vs_string_split_빈문자열() {
        String data = "a,,b";

        List<String> guavaResult = Splitter.on(',').splitToList(data);
        String[] jdkResult = data.split(",");

        // 둘 다 3개
        assertEquals(3, guavaResult.size());
        assertEquals(3, jdkResult.length);

        // 둘 다 빈 문자열 포함
        assertEquals("", guavaResult.get(1));
        assertEquals("", jdkResult[1]);
    }

    @Test
    void string_split의_trailing_empty_제거() {
        // 이게 진짜 문제!
        String data = "a,b,";  // ← 마지막에 빈 문자열

        List<String> guavaResult = Splitter.on(',').splitToList(data);
        String[] jdkResult = data.split(",");

        assertEquals(3, guavaResult.size());  // [a, b, ]
        assertEquals("", guavaResult.get(2)); // 마지막 빈 문자열 유지

        assertEquals(2, jdkResult.length);    // [a, b] ← 마지막 제거!
        // 이게 String.split()의 예상치 못한 동작!
    }

    @Test
    void splitter_일관된_동작() {
        // Splitter는 항상 일관됨
        assertEquals(3, Splitter.on(',').splitToList("a,,b").size());
        assertEquals(3, Splitter.on(',').splitToList("a,b,").size());
        assertEquals(3, Splitter.on(',').splitToList(",a,b").size());

        // String.split()은 일관되지 않음
        assertEquals(3, "a,,b".split(",").length);  // 중간 빈 문자열: 유지
        assertEquals(2, "a,b,".split(",").length);  // 끝 빈 문자열: 제거!
        assertEquals(3, ",a,b".split(",").length);  // 앞 빈 문자열: 유지
    }

    @Test
    void splitter_omitEmptyStrings_동작() {
        String data = "a,,b,,,c";

        List<String> result = Splitter.on(',')
                .omitEmptyStrings()
                .splitToList(data);

        assertEquals(Arrays.asList("a", "b", "c"), result);
    }

    @Test
    void mapSplitter_쿼리스트링_파싱() {
        String query = "name=John&age=30&city=Seoul";

        Map<String, String> params = Splitter.on('&')
                .withKeyValueSeparator('=')
                .split(query);

        assertEquals("John", params.get("name"));
        assertEquals("30", params.get("age"));
        assertEquals("Seoul", params.get("city"));
    }

    @Test
    void mapSplitter_잘못된_형식() {
        Splitter.MapSplitter splitter = Splitter.on('&')
                .withKeyValueSeparator('=');

        assertThrows(IllegalArgumentException.class, () -> {
            splitter.split("name=John&invalid&age=30");
        });
    }

    @Test
    void mapSplitter_중복_키() {
        Splitter.MapSplitter splitter = Splitter.on('&')
                .withKeyValueSeparator('=');

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            splitter.split("name=John&name=Jane");
        });

        assertTrue(ex.getMessage().contains("Duplicate key"));
    }
}