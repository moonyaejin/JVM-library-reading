package org.example.ImmutableList;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step 5: ImmutableList vs Collections.unmodifiableList 비교
 */
public class ImmutableVsUnmodifiableTest {

    @Test
    void testOriginalListModification() {
        List<String> original = new ArrayList<>(Arrays.asList("a", "b", "c"));

        // unmodifiableList - 원본 변경 시 영향 받음!
        List<String> unmodifiable = Collections.unmodifiableList(original);
        assertEquals(3, unmodifiable.size());

        original.add("d");
        assertEquals(4, unmodifiable.size()); // 같이 변경됨!
        assertEquals("d", unmodifiable.get(3));

        System.out.println("⚠️  unmodifiableList: 원본 변경 시 영향 받음");
        System.out.println("   원본 변경 후 unmodifiable 크기: " + unmodifiable.size());

        // ImmutableList - 원본 변경 시 영향 없음!
        original = new ArrayList<>(Arrays.asList("a", "b", "c"));
        ImmutableList<String> immutable = ImmutableList.copyOf(original);
        assertEquals(3, immutable.size());

        original.add("d");
        assertEquals(3, immutable.size()); // 변경 없음!

        System.out.println("✅ ImmutableList: 원본 변경해도 영향 없음");
        System.out.println("   원본 변경 후 immutable 크기: " + immutable.size());
    }

    @Test
    void testNullHandling() {
        // unmodifiableList - null 허용
        List<String> withNull = new ArrayList<>(Arrays.asList("a", null, "c"));
        List<String> unmodifiable = Collections.unmodifiableList(withNull);

        assertNull(unmodifiable.get(1)); // null 있음
        System.out.println("⚠️  unmodifiableList: null 허용");

        // ImmutableList - null 불가
        assertThrows(NullPointerException.class, () -> {
            ImmutableList.copyOf(Arrays.asList("a", null, "c"));
        });

        System.out.println("✅ ImmutableList: null 불가");
    }

    @Test
    void testIdentityOptimization() {
        // unmodifiableList - 항상 새로운 래퍼 객체 생성
        List<String> list = Arrays.asList("a", "b", "c");
        List<String> unmod1 = Collections.unmodifiableList(list);
        List<String> unmod2 = Collections.unmodifiableList(list);

        assertNotSame(unmod1, unmod2); // 다른 객체
        System.out.println("⚠️  unmodifiableList: 매번 새 래퍼 생성");
        System.out.println("   unmod1 == unmod2: " + (unmod1 == unmod2));

        // ImmutableList - 이미 immutable이면 그대로 반환
        ImmutableList<String> imm1 = ImmutableList.copyOf(Arrays.asList("a", "b", "c"));
        ImmutableList<String> imm2 = ImmutableList.copyOf(imm1);

        assertSame(imm1, imm2); // 같은 객체
        System.out.println("✅ ImmutableList: 이미 immutable이면 그대로 반환");
        System.out.println("   imm1 == imm2: " + (imm1 == imm2));
    }

    @Test
    void testTypeSafety() {
        // unmodifiableList - 타입이 명확하지 않음
        List<String> unmodifiable = Collections.unmodifiableList(
                new ArrayList<>(Arrays.asList("a", "b"))
        );

        // 반환 타입이 List<String>이지만 실제로는 UnmodifiableList
        System.out.println("⚠️  unmodifiableList 타입: " + unmodifiable.getClass().getSimpleName());

        // ImmutableList - 명확한 타입
        ImmutableList<String> immutable = ImmutableList.of("a", "b");
        System.out.println("✅ ImmutableList 타입: " + immutable.getClass().getSimpleName());

        // ImmutableList는 명확한 계약 제공
        assertTrue(immutable instanceof ImmutableList);
    }

    @Test
    void testPerformanceIndicators() {
        int size = 10000;
        List<Integer> source = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            source.add(i);
        }

        // unmodifiableList 생성 시간
        long start = System.nanoTime();
        List<Integer> unmodifiable = Collections.unmodifiableList(source);
        long unmodTime = System.nanoTime() - start;

        // ImmutableList 생성 시간
        start = System.nanoTime();
        ImmutableList<Integer> immutable = ImmutableList.copyOf(source);
        long immTime = System.nanoTime() - start;

        System.out.println("📊 생성 시간 비교 (10,000개 원소):");
        System.out.println("   unmodifiableList: " + unmodTime + " ns");
        System.out.println("   ImmutableList: " + immTime + " ns");
        System.out.println("   배율: " + String.format("%.2f", (double)immTime / unmodTime) + "x");

        // 조회 성능은 비슷할 것으로 예상 (둘 다 배열 기반)
        int iterations = 100000;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            unmodifiable.get(i % size);
        }
        long unmodGetTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            immutable.get(i % size);
        }
        long immGetTime = System.nanoTime() - start;

        System.out.println("\n📊 조회 성능 비교 (100,000회):");
        System.out.println("   unmodifiableList: " + unmodGetTime + " ns");
        System.out.println("   ImmutableList: " + immGetTime + " ns");
    }

    @Test
    void testThreadSafety() {
        // unmodifiableList - 원본이 변경되면 thread-safe하지 않음
        List<String> original = new ArrayList<>(Arrays.asList("a", "b", "c"));
        List<String> unmodifiable = Collections.unmodifiableList(original);

        // 다른 스레드에서 원본 변경 가능 → ConcurrentModificationException 위험
        System.out.println("⚠️  unmodifiableList: 원본 변경 가능 → thread-safe 아님");

        // ImmutableList - 진짜 불변이므로 thread-safe
        ImmutableList<String> immutable = ImmutableList.of("a", "b", "c");
        System.out.println("✅ ImmutableList: 진짜 불변 → thread-safe");
    }

    @Test
    void testMemoryFootprint() {
        List<String> original = new ArrayList<>(Arrays.asList("a", "b", "c"));

        // unmodifiableList - 원본 + 래퍼 객체
        List<String> unmodifiable = Collections.unmodifiableList(original);
        System.out.println("⚠️  unmodifiableList: 원본 참조 + 래퍼 객체 (2개 객체)");

        // ImmutableList - 복사본 하나만
        ImmutableList<String> immutable = ImmutableList.copyOf(original);
        System.out.println("✅ ImmutableList: 독립적인 배열 하나 (1개 객체)");

        // 원본을 버려도 됨
        original = null;
        assertNotNull(immutable); // immutable은 독립적
    }

    @Test
    void testSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 ImmutableList vs Collections.unmodifiableList 요약");
        System.out.println("=".repeat(60));
        System.out.println("\n1. 원본 변경 영향:");
        System.out.println("   - unmodifiable: 영향 받음 ⚠️");
        System.out.println("   - ImmutableList: 영향 없음 ✅");

        System.out.println("\n2. null 처리:");
        System.out.println("   - unmodifiable: 허용 ⚠️");
        System.out.println("   - ImmutableList: 불가 ✅");

        System.out.println("\n3. Thread-safety:");
        System.out.println("   - unmodifiable: 조건부 (원본 안 변경 시) ⚠️");
        System.out.println("   - ImmutableList: 완전 보장 ✅");

        System.out.println("\n4. 최적화:");
        System.out.println("   - unmodifiable: 매번 래퍼 생성 ⚠️");
        System.out.println("   - ImmutableList: 불필요한 복사 방지 ✅");

        System.out.println("\n5. 타입 안전성:");
        System.out.println("   - unmodifiable: List 인터페이스만 ⚠️");
        System.out.println("   - ImmutableList: 명확한 계약 ✅");
        System.out.println("=".repeat(60));
    }
}