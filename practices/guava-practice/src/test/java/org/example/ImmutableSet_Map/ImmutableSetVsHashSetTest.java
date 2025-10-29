package org.example.ImmutableSet_Map;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImmutableSet vs HashSet vs unmodifiableSet 비교
 */
public class ImmutableSetVsHashSetTest {

    @Test
    void 원본_변경_영향_비교() {
        Set<String> original = new HashSet<>();
        original.add("a");
        original.add("b");
        original.add("c");

        // unmodifiableSet: 원본 래핑
        Set<String> unmodifiable = Collections.unmodifiableSet(original);

        // ImmutableSet: 복사본 생성
        ImmutableSet<String> immutable = ImmutableSet.copyOf(original);

        System.out.println("변경 전:");
        System.out.println("  원본: " + original);
        System.out.println("  unmodifiable: " + unmodifiable);
        System.out.println("  immutable: " + immutable);

        // 원본 변경
        original.add("d");

        System.out.println("\n원본에 'd' 추가 후:");
        System.out.println("  원본: " + original);
        System.out.println("  unmodifiable: " + unmodifiable + " ⚠️ 영향 받음!");
        System.out.println("  immutable: " + immutable + " ✅ 독립적!");

        assertEquals(4, original.size());
        assertEquals(4, unmodifiable.size());  // 원본 변경에 영향!
        assertEquals(3, immutable.size());     // 영향 없음!
    }

    @Test
    void null_허용_비교() {
        Set<String> hashSet = new HashSet<>();
        hashSet.add("a");
        hashSet.add(null);  // HashSet은 null 허용
        hashSet.add("b");

        Set<String> unmodifiable = Collections.unmodifiableSet(hashSet);

        assertEquals(3, hashSet.size());
        assertTrue(hashSet.contains(null));
        assertTrue(unmodifiable.contains(null));

        System.out.println("✅ HashSet null 허용: " + hashSet);
        System.out.println("✅ unmodifiableSet null 허용: " + unmodifiable);

        // ImmutableSet은 null 거부
        assertThrows(NullPointerException.class, () -> {
            ImmutableSet.copyOf(hashSet);
        });
        System.out.println("✅ ImmutableSet null 거부");
    }

    @Test
    void 생성_시간_비교() {
        int size = 10000;
        Set<Integer> source = new HashSet<>();
        for (int i = 0; i < size; i++) {
            source.add(i);
        }

        // unmodifiableSet 생성
        long start1 = System.nanoTime();
        Set<Integer> unmodifiable = Collections.unmodifiableSet(source);
        long time1 = System.nanoTime() - start1;

        // ImmutableSet 생성
        long start2 = System.nanoTime();
        ImmutableSet<Integer> immutable = ImmutableSet.copyOf(source);
        long time2 = System.nanoTime() - start2;

        System.out.println("\n📊 생성 시간 비교 (" + size + "개):");
        System.out.printf("  unmodifiableSet: %,d ns (%.2f μs)\n", time1, time1 / 1000.0);
        System.out.printf("  ImmutableSet:    %,d ns (%.2f ms)\n", time2, time2 / 1_000_000.0);
        System.out.printf("  비율: unmodifiable이 %.0f배 빠름\n", time2 / (double) time1);

        System.out.println("\n💡 unmodifiable은 래퍼만 씌우지만,");
        System.out.println("   ImmutableSet은 전체 복사본을 만듦!");
    }

    @Test
    void 조회_성능_비교() {
        int size = 10000;
        Set<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < size; i++) {
            hashSet.add(i);
        }

        Set<Integer> unmodifiable = Collections.unmodifiableSet(hashSet);
        ImmutableSet<Integer> immutable = ImmutableSet.copyOf(hashSet);

        // 워밍업
        for (int i = 0; i < 1000; i++) {
            hashSet.contains(i);
            unmodifiable.contains(i);
            immutable.contains(i);
        }

        // HashSet 조회
        long start1 = System.nanoTime();
        for (int rep = 0; rep < 10000; rep++) {
            for (int i = 0; i < size; i++) {
                hashSet.contains(i);
            }
        }
        long time1 = System.nanoTime() - start1;

        // unmodifiableSet 조회
        long start2 = System.nanoTime();
        for (int rep = 0; rep < 10000; rep++) {
            for (int i = 0; i < size; i++) {
                unmodifiable.contains(i);
            }
        }
        long time2 = System.nanoTime() - start2;

        // ImmutableSet 조회
        long start3 = System.nanoTime();
        for (int rep = 0; rep < 10000; rep++) {
            for (int i = 0; i < size; i++) {
                immutable.contains(i);
            }
        }
        long time3 = System.nanoTime() - start3;

        System.out.println("\n📊 조회 성능 (10000회 반복):");
        System.out.printf("  HashSet:         %.2f ms\n", time1 / 1_000_000.0);
        System.out.printf("  unmodifiableSet: %.2f ms\n", time2 / 1_000_000.0);
        System.out.printf("  ImmutableSet:    %.2f ms\n", time3 / 1_000_000.0);
    }

    @Test
    void 반복_성능_비교() {
        int size = 10000;
        Set<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < size; i++) {
            hashSet.add(i);
        }

        Set<Integer> unmodifiable = Collections.unmodifiableSet(hashSet);
        ImmutableSet<Integer> immutable = ImmutableSet.copyOf(hashSet);

        // 워밍업
        for (Integer i : hashSet) {}
        for (Integer i : unmodifiable) {}
        for (Integer i : immutable) {}

        // HashSet 반복
        long start1 = System.nanoTime();
        for (int rep = 0; rep < 1000; rep++) {
            for (Integer i : hashSet) {
                int x = i;
            }
        }
        long time1 = System.nanoTime() - start1;

        // unmodifiableSet 반복
        long start2 = System.nanoTime();
        for (int rep = 0; rep < 1000; rep++) {
            for (Integer i : unmodifiable) {
                int x = i;
            }
        }
        long time2 = System.nanoTime() - start2;

        // ImmutableSet 반복
        long start3 = System.nanoTime();
        for (int rep = 0; rep < 1000; rep++) {
            for (Integer i : immutable) {
                int x = i;
            }
        }
        long time3 = System.nanoTime() - start3;

        System.out.println("\n📊 반복 성능 (1000회):");
        System.out.printf("  HashSet:         %.2f ms\n", time1 / 1_000_000.0);
        System.out.printf("  unmodifiableSet: %.2f ms\n", time2 / 1_000_000.0);
        System.out.printf("  ImmutableSet:    %.2f ms\n", time3 / 1_000_000.0);
    }
}