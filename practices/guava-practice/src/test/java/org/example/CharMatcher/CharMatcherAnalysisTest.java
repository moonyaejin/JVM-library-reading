package org.example.CharMatcher;

import com.google.common.base.CharMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CharMatcher 내부 구현 분석을 위한 테스트
 *
 * 주요 발견사항:
 * 1. CharMatcher는 정규식을 전혀 사용하지 않음
 * 2. 다양한 최적화 전략을 사용하는 구현체들이 존재
 * 3. precomputed()는 BitSet으로 변환하여 O(1) 조회 달성
 */
public class CharMatcherAnalysisTest {

    @Test
    @DisplayName("CharMatcher.anyOf()의 최적화 전략")
    public void testAnyOfOptimization() {
        // anyOf는 문자 개수에 따라 다른 구현체를 반환

        // 0개: None 반환
        CharMatcher empty = CharMatcher.anyOf("");
        assertFalse(empty.matches('a'));
        assertEquals("hello", empty.removeFrom("hello")); // 아무것도 제거하지 않음

        // 1개: Is 반환 (단순 == 비교)
        CharMatcher single = CharMatcher.anyOf("a");
        assertTrue(single.matches('a'));
        assertFalse(single.matches('b'));

        // 2개: IsEither 반환 (두 개 비교)
        CharMatcher two = CharMatcher.anyOf("ab");
        assertTrue(two.matches('a'));
        assertTrue(two.matches('b'));
        assertFalse(two.matches('c'));

        // 3개 이상: AnyOf 반환 (Arrays.binarySearch 사용)
        CharMatcher many = CharMatcher.anyOf("abcdefghijk");
        assertTrue(many.matches('e'));
        assertFalse(many.matches('z'));

        // AnyOf는 내부적으로 문자를 정렬하여 이진 탐색 사용
        String result = many.removeFrom("hello world");
        assertEquals("llo worl", result); // h, e, d 제거됨
    }

    @Test
    @DisplayName("Whitespace 매처의 영리한 해시 기법")
    public void testWhitespaceHashTrick() {
        CharMatcher whitespace = CharMatcher.whitespace();

        // 일반적인 공백 문자들
        assertTrue(whitespace.matches(' '));
        assertTrue(whitespace.matches('\t'));
        assertTrue(whitespace.matches('\n'));
        assertTrue(whitespace.matches('\r'));
        assertTrue(whitespace.matches('\f'));

        // 유니코드 공백들도 처리
        assertTrue(whitespace.matches('\u2000')); // EN QUAD
        assertTrue(whitespace.matches('\u2001')); // EM QUAD

        // Whitespace 구현의 핵심:
        // TABLE.charAt(MULTIPLIER * c >>> SHIFT) == c
        // 이는 완벽한 해시 함수로 O(1) 조회를 달성

        String text = "Hello\tWorld\n";
        assertEquals("HelloWorld", whitespace.removeFrom(text));
    }

    @Test
    @DisplayName("InRange의 효율적인 범위 체크")
    public void testInRangeImplementation() {
        // InRange는 단순한 범위 비교로 구현
        CharMatcher digits = CharMatcher.inRange('0', '9');

        // startInclusive <= c && c <= endInclusive
        assertTrue(digits.matches('5'));
        assertFalse(digits.matches('a'));

        // 여러 범위 조합 가능
        CharMatcher alphaNumeric = CharMatcher.inRange('0', '9')
                .or(CharMatcher.inRange('a', 'z'))
                .or(CharMatcher.inRange('A', 'Z'));

        String password = "MyP@ssw0rd!";
        String onlyAlphaNum = alphaNumeric.retainFrom(password);
        assertEquals("MyPssw0rd", onlyAlphaNum);
    }

    @Test
    @DisplayName("precomputed()의 BitSet 최적화")
    public void testPrecomputedOptimization() {
        // 많은 문자를 체크하는 경우 precomputed() 사용
        String vowelsStr = "aeiouAEIOU";
        CharMatcher vowels = CharMatcher.anyOf(vowelsStr);
        CharMatcher precomputedVowels = vowels.precomputed();

        // precomputed는 내부적으로 BitSet을 사용
        // 65536개 비트 (char의 전체 범위) 중 해당하는 위치만 set

        String text = "Programming is fun!";
        String consonants1 = vowels.removeFrom(text);
        String consonants2 = precomputedVowels.removeFrom(text);

        assertEquals(consonants1, consonants2);
        assertEquals("Prgrmmng s fn!", consonants1);

        // 성능 차이 테스트 (긴 문자열에서 더 명확)
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longText.append("Hello World ");
        }

        long start = System.nanoTime();
        vowels.removeFrom(longText.toString());
        long normalTime = System.nanoTime() - start;

        start = System.nanoTime();
        precomputedVowels.removeFrom(longText.toString());
        long precomputedTime = System.nanoTime() - start;

        System.out.println("Normal: " + normalTime + " ns");
        System.out.println("Precomputed: " + precomputedTime + " ns");
        System.out.println("Speedup: " + (double)normalTime/precomputedTime + "x");

        // Precomputed가 더 빠를 것으로 예상 (특히 많은 문자 체크 시)
    }

    @Test
    @DisplayName("CharMatcher 조합과 논리 연산")
    public void testComposition() {
        // And 연산자
        CharMatcher lowerVowel = CharMatcher.anyOf("aeiou")
                .and(CharMatcher.javaLowerCase());

        assertTrue(lowerVowel.matches('a'));
        assertFalse(lowerVowel.matches('A')); // 대문자는 제외

        // Or 연산자
        CharMatcher spaceOrTab = CharMatcher.is(' ')
                .or(CharMatcher.is('\t'));

        assertTrue(spaceOrTab.matches(' '));
        assertTrue(spaceOrTab.matches('\t'));
        assertFalse(spaceOrTab.matches('\n'));

        // Negate 연산자
        CharMatcher notDigit = CharMatcher.digit().negate();

        assertFalse(notDigit.matches('5'));
        assertTrue(notDigit.matches('a'));

        // 복잡한 조합
        CharMatcher safeFilename = CharMatcher.javaLetterOrDigit()
                .or(CharMatcher.anyOf("-_."))
                .precomputed();

        String filename = "my-file_2024.txt";
        String validated = safeFilename.retainFrom(filename);
        assertEquals("my-file_2024.txt", validated);

        String badFilename = "my file (copy).txt";
        String sanitized = safeFilename.retainFrom(badFilename);
        assertEquals("myfilecopy.txt", sanitized);
    }

    @Test
    @DisplayName("Fast vs Slow CharMatchers")
    public void testFastMatcherOptimization() {
        // FastMatcher는 이미 최적화되어 precomputed()가 자신을 반환
        CharMatcher is = CharMatcher.is('a');  // Is extends FastMatcher
        CharMatcher precomputedIs = is.precomputed();
        assertSame(is, precomputedIs); // 같은 인스턴스!

        // 일반 CharMatcher는 precomputed()가 새 인스턴스 생성
        CharMatcher anyOf = CharMatcher.anyOf("abcdefg");
        CharMatcher precomputedAnyOf = anyOf.precomputed();
        assertNotSame(anyOf, precomputedAnyOf); // 다른 인스턴스
    }

    @Test
    @DisplayName("실제 사용 예제: CSV 파싱")
    public void testRealWorldCSVParsing() {
        // CSV에서 유효한 필드값 추출
        CharMatcher validCsvChar = CharMatcher.javaLetterOrDigit()
                .or(CharMatcher.anyOf(".-_@"))
                .precomputed();

        String csvField = "  john.doe@example.com  ";

        // 앞뒤 공백 제거하고 유효 문자만 유지
        String cleaned = CharMatcher.whitespace().trimFrom(csvField);
        String validated = validCsvChar.retainFrom(cleaned);

        assertEquals("john.doe@example.com", cleaned);
        assertEquals("john.doe@example.com", validated);

        // 특수문자가 포함된 경우 - @ 기호는 유지됨
        String dirtyField = "john!@#$%^&*()doe@example.com";
        String sanitized = validCsvChar.retainFrom(dirtyField);
        assertEquals("john@doe@example.com", sanitized); // @ 기호는 허용 문자이므로 유지

        // @ 제거가 필요한 경우 별도 처리
        CharMatcher validWithoutAt = CharMatcher.javaLetterOrDigit()
                .or(CharMatcher.anyOf(".-_"))
                .precomputed();
        String withoutAt = validWithoutAt.retainFrom(dirtyField);
        assertEquals("johndoeexample.com", withoutAt);
    }

    @Test
    @DisplayName("성능 측정의 한계")
    public void testPerformanceLimitations() {
        // 짧은 문자열에서는 CharMatcher의 장점이 크지 않음
        String shortText = "Hello";
        CharMatcher vowels = CharMatcher.anyOf("aeiouAEIOU");

        // 초기화 오버헤드가 실제 처리보다 클 수 있음
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            vowels.removeFrom(shortText);
        }
        long elapsed = System.nanoTime() - start;

        System.out.println("Short string processing: " + elapsed + " ns");

        // JIT 컴파일러의 영향
        // 처음 몇 번의 실행은 느릴 수 있음
        long[] times = new long[5];
        for (int run = 0; run < 5; run++) {
            start = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                vowels.removeFrom("Programming is really fun!");
            }
            times[run] = System.nanoTime() - start;
            System.out.println("Run " + (run + 1) + ": " + times[run] + " ns");
        }

        // 보통 나중 실행이 더 빠름 (JIT 최적화)
        assertTrue(times[4] <= times[0] * 1.1); // 마지막이 첫 실행보다 빠르거나 비슷
    }

    @Test
    @DisplayName("CharMatcher vs Pattern 성능 비교")
    public void testCharMatcherVsRegex() {
        String text = generateRandomString(10000);

        // Pattern/Regex 방식
        long start = System.nanoTime();
        String regexResult = text.replaceAll("[0-9]", "");
        long regexTime = System.nanoTime() - start;

        // CharMatcher 방식 (일반)
        CharMatcher digits = CharMatcher.inRange('0', '9');
        start = System.nanoTime();
        String matcherResult = digits.removeFrom(text);
        long matcherTime = System.nanoTime() - start;

        // CharMatcher 방식 (precomputed)
        CharMatcher precomputed = CharMatcher.anyOf("0123456789").precomputed();
        start = System.nanoTime();
        String precomputedResult = precomputed.removeFrom(text);
        long precomputedTime = System.nanoTime() - start;

        System.out.println("\n=== Performance Comparison ===");
        System.out.println("Regex:       " + regexTime + " ns");
        System.out.println("CharMatcher: " + matcherTime + " ns (" +
                String.format("%.1fx", (double)regexTime/matcherTime) + " faster)");
        System.out.println("Precomputed: " + precomputedTime + " ns (" +
                String.format("%.1fx", (double)regexTime/precomputedTime) + " faster)");

        // 결과가 같은지 확인
        assertEquals(regexResult, matcherResult);
        assertEquals(regexResult, precomputedResult);
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 \t\n";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int)(Math.random() * chars.length())));
        }
        return sb.toString();
    }
}