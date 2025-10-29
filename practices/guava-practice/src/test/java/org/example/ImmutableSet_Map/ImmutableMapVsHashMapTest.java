package org.example.ImmutableSet_Map;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImmutableMap vs HashMap vs unmodifiableMap 비교
 */
public class ImmutableMapVsHashMapTest {

    @Test
    void 원본_변경_영향_비교() {
        Map<String, Integer> original = new HashMap<>();
        original.put("a", 1);
        original.put("b", 2);
        original.put("c", 3);

        // unmodifiableMap: 원본 래핑
        Map<String, Integer> unmodifiable = Collections.unmodifiableMap(original);

        // ImmutableMap: 복사본 생성
        ImmutableMap<String, Integer> immutable = ImmutableMap.copyOf(original);

        System.out.println("변경 전:");
        System.out.println("  원본: " + original);
        System.out.println("  unmodifiable: " + unmodifiable);
        System.out.println("  immutable: " + immutable);

        // 원본 변경
        original.put("d", 4);

        System.out.println("\n원본에 'd=4' 추가 후:");
        System.out.println("  원본: " + original);
        System.out.println("  unmodifiable: " + unmodifiable + " ⚠️ 영향 받음!");
        System.out.println("  immutable: " + immutable + " ✅ 독립적!");

        assertEquals(4, original.size());
        assertEquals(4, unmodifiable.size());  // 원본 변경에 영향!
        assertEquals(3, immutable.size());     // 영향 없음!
    }

    @Test
    void null_허용_비교() {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("a", 1);
        hashMap.put(null, 2);  // null key 허용
        hashMap.put("b", null);  // null value 허용

        Map<String, Integer> unmodifiable = Collections.unmodifiableMap(hashMap);

        assertEquals(3, hashMap.size());
        assertTrue(hashMap.containsKey(null));
        assertTrue(hashMap.containsValue(null));
        assertTrue(unmodifiable.containsKey(null));

        System.out.println("✅ HashMap null 허용: " + hashMap);
        System.out.println("✅ unmodifiableMap null 허용: " + unmodifiable);

        // ImmutableMap은 null 거부
        assertThrows(NullPointerException.class, () -> {
            ImmutableMap.copyOf(hashMap);
        });
        System.out.println("✅ ImmutableMap null 거부");
    }

    @Test
    void 생성_시간_비교() {
        int size = 10000;
        Map<Integer, String> source = new HashMap<>();
        for (int i = 0; i < size; i++) {
            source.put(i, "value" + i);
        }

        // unmodifiableMap 생성
        long start1 = System.nanoTime();
        Map<Integer, String> unmodifiable = Collections.unmodifiableMap(source);
        long time1 = System.nanoTime() - start1;

        // ImmutableMap 생성
        long start2 = System.nanoTime();
        ImmutableMap<Integer, String> immutable = ImmutableMap.copyOf(source);
        long time2 = System.nanoTime() - start2;

        System.out.println("\n📊 생성 시간 비교 (" + size + "개):");
        System.out.printf("  unmodifiableMap: %,d ns (%.2f μs)\n", time1, time1 / 1000.0);
        System.out.printf("  ImmutableMap:    %,d ns (%.2f ms)\n", time2, time2 / 1_000_000.0);
        System.out.printf("  비율: unmodifiable이 %.0f배 빠름\n", time2 / (double) time1);

        System.out.println("\n💡 unmodifiable은 래퍼만 씌우지만,");
        System.out.println("   ImmutableMap은 전체 복사 + 최적화된 구조 생성!");
    }

    @Test
    void 조회_성능_비교() {
        int size = 1000;
        Map<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            hashMap.put(i, "value" + i);
        }

        Map<Integer, String> unmodifiable = Collections.unmodifiableMap(hashMap);
        ImmutableMap<Integer, String> immutable = ImmutableMap.copyOf(hashMap);

        // 워밍업
        for (int i = 0; i < 100; i++) {
            hashMap.get(i);
            unmodifiable.get(i);
            immutable.get(i);
        }

        int iterations = 100000;

        // HashMap 조회
        long start1 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (int i = 0; i < size; i++) {
                hashMap.get(i);
            }
        }
        long time1 = System.nanoTime() - start1;

        // unmodifiableMap 조회
        long start2 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (int i = 0; i < size; i++) {
                unmodifiable.get(i);
            }
        }
        long time2 = System.nanoTime() - start2;

        // ImmutableMap 조회
        long start3 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (int i = 0; i < size; i++) {
                immutable.get(i);
            }
        }
        long time3 = System.nanoTime() - start3;

        System.out.println("\n📊 조회 성능 (" + iterations + "회 반복):");
        System.out.printf("  HashMap:         %.2f ms\n", time1 / 1_000_000.0);
        System.out.printf("  unmodifiableMap: %.2f ms\n", time2 / 1_000_000.0);
        System.out.printf("  ImmutableMap:    %.2f ms\n", time3 / 1_000_000.0);
    }

    @Test
    void 반복_성능_비교() {
        int size = 1000;
        Map<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            hashMap.put(i, "value" + i);
        }

        Map<Integer, String> unmodifiable = Collections.unmodifiableMap(hashMap);
        ImmutableMap<Integer, String> immutable = ImmutableMap.copyOf(hashMap);

        // 워밍업
        for (var entry : hashMap.entrySet()) {}
        for (var entry : unmodifiable.entrySet()) {}
        for (var entry : immutable.entrySet()) {}

        int iterations = 10000;

        // HashMap 반복
        long start1 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (var entry : hashMap.entrySet()) {
                String v = entry.getValue();
            }
        }
        long time1 = System.nanoTime() - start1;

        // unmodifiableMap 반복
        long start2 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (var entry : unmodifiable.entrySet()) {
                String v = entry.getValue();
            }
        }
        long time2 = System.nanoTime() - start2;

        // ImmutableMap 반복
        long start3 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (var entry : immutable.entrySet()) {
                String v = entry.getValue();
            }
        }
        long time3 = System.nanoTime() - start3;

        System.out.println("\n📊 반복 성능 (" + iterations + "회):");
        System.out.printf("  HashMap:         %.2f ms\n", time1 / 1_000_000.0);
        System.out.printf("  unmodifiableMap: %.2f ms\n", time2 / 1_000_000.0);
        System.out.printf("  ImmutableMap:    %.2f ms\n", time3 / 1_000_000.0);
    }

    @Test
    void copyOf_최적화_확인() {
        Map<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            hashMap.put("key" + i, i);
        }

        // HashMap -> ImmutableMap 복사
        long start1 = System.nanoTime();
        ImmutableMap<String, Integer> immutable1 = ImmutableMap.copyOf(hashMap);
        long time1 = System.nanoTime() - start1;

        // ImmutableMap -> ImmutableMap 복사
        long start2 = System.nanoTime();
        ImmutableMap<String, Integer> immutable2 = ImmutableMap.copyOf(immutable1);
        long time2 = System.nanoTime() - start2;

        System.out.println("\n📊 copyOf() 최적화:");
        System.out.printf("  HashMap 복사:      %.2f ms\n", time1 / 1_000_000.0);
        System.out.printf("  ImmutableMap 복사: %.2f μs\n", time2 / 1000.0);
        System.out.printf("  속도 차이: %.0f배\n", time1 / (double) time2);
        System.out.println("  동일 객체: " + (immutable1 == immutable2));
    }
}