package org.example;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;

/**
 * Guava Joiner 클래스 분석
 *
 * 학습 목표:
 * 1. Fluent API 패턴 이해
 * 2. null 처리 전략 (skipNulls vs useForNull)
 * 3. MapJoiner의 확장 방식
 */
public class JoinerExample {

    public static void main(String[] args) {
        basicJoining();
        nullHandling();
        mapJoining();
        appendToBuilder();
    }

    /**
     * 기본 사용: 리스트를 문자열로 조인
     */
    private static void basicJoining() {
        System.out.println("=== 기본 조인 ===");

        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");

        // 예상: 내부적으로 StringBuilder를 사용할 것 같다
        String result = Joiner.on(", ").join(fruits);
        System.out.println(result); // Apple, Banana, Cherry

        // 궁금증: Joiner 인스턴스는 재사용 가능할까?
        Joiner commaJoiner = Joiner.on(", ");
        System.out.println(commaJoiner.join("A", "B", "C"));
        System.out.println(commaJoiner.join(Arrays.asList(1, 2, 3)));
    }

    /**
     * null 처리: skipNulls vs useForNull
     *
     * 예상:
     * - skipNulls()는 null을 무시
     * - useForNull()은 null을 다른 값으로 대체
     *
     * 질문: 두 메서드를 동시에 호출하면 어떻게 될까?
     */
    private static void nullHandling() {
        System.out.println("\n=== Null 처리 ===");

        List<String> withNulls = Arrays.asList("A", null, "B", null, "C");

        // skipNulls: null 제거
        String skipped = Joiner.on(",").skipNulls().join(withNulls);
        System.out.println("skipNulls: " + skipped); // A,B,C

        // useForNull: null을 다른 값으로 대체
        String replaced = Joiner.on(",").useForNull("N/A").join(withNulls);
        System.out.println("useForNull: " + replaced); // A,N/A,B,N/A,C

        // 실험: 두 메서드 동시 호출 시도
        try {
            Joiner.on(",").skipNulls().useForNull("X").join(withNulls);
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getClass().getSimpleName());
            // 예상: IllegalStateException 또는 UnsupportedOperationException
        }
    }

    /**
     * MapJoiner: 키-값 쌍 조인
     *
     * 예상: Joiner의 확장으로 구현되어 있을 것
     */
    private static void mapJoining() {
        System.out.println("\n=== Map 조인 ===");

        ImmutableMap<String, Integer> map = ImmutableMap.of(
                "Apple", 100,
                "Banana", 80,
                "Cherry", 120
        );

        // withKeyValueSeparator()로 MapJoiner 생성
        String result = Joiner.on(", ")
                .withKeyValueSeparator("=")
                .join(map);

        System.out.println(result); // Apple=100, Banana=80, Cherry=120

        // 궁금증: null 값이 있는 Map은?
    }

    /**
     * StringBuilder에 직접 추가
     *
     * 예상: 새 문자열 생성 없이 기존 StringBuilder 활용
     */
    private static void appendToBuilder() {
        System.out.println("\n=== StringBuilder 활용 ===");

        StringBuilder sb = new StringBuilder("Fruits: ");
        Joiner.on(", ").appendTo(sb, "Apple", "Banana", "Cherry");

        System.out.println(sb.toString());

        // 성능 비교 포인트: join() vs appendTo()
    }
}