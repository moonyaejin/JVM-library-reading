package org.example.ImmutableSet_Map;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImmutableSet 기본 사용법 테스트
 * - 생성 방법들
 * - 중복 제거
 * - null 처리
 * - 불변성 확인
 */
public class ImmutableSetBasicTest {

    @Test
    void of_메서드로_생성() {
        ImmutableSet<String> set = ImmutableSet.of("a", "b", "c");

        assertEquals(3, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));

        System.out.println("✅ of() 생성: " + set);
    }

    @Test
    void of_메서드_중복_자동_제거() {
        // of()는 중복을 허용하지만 Set 특성상 하나만 저장됨
        ImmutableSet<String> set = ImmutableSet.of("a", "b", "a", "c", "b");

        assertEquals(3, set.size());
        System.out.println("✅ 중복 제거: " + set);
    }

    @Test
    void copyOf_컬렉션에서_생성() {
        Set<String> original = new HashSet<>(Arrays.asList("x", "y", "z", "x"));
        ImmutableSet<String> set = ImmutableSet.copyOf(original);

        assertEquals(3, set.size());
        System.out.println("✅ copyOf() 생성: " + set);
    }

    @Test
    void copyOf_이미_ImmutableSet이면_그대로_반환() {
        ImmutableSet<String> set1 = ImmutableSet.of("a", "b", "c");
        ImmutableSet<String> set2 = ImmutableSet.copyOf(set1);

        assertSame(set1, set2);  // 동일 객체!
        System.out.println("✅ copyOf() 최적화: set1 == set2? " + (set1 == set2));
    }

    @Test
    void builder_패턴으로_생성() {
        ImmutableSet<String> set = ImmutableSet.<String>builder()
                .add("a")
                .add("b")
                .add("c")
                .add("a")  // 중복
                .build();

        assertEquals(3, set.size());
        System.out.println("✅ Builder 패턴: " + set);
    }

    @Test
    void builder_addAll_사용() {
        ImmutableSet<String> set = ImmutableSet.<String>builder()
                .add("a", "b")
                .addAll(Arrays.asList("c", "d", "e"))
                .build();

        assertEquals(5, set.size());
        System.out.println("✅ Builder addAll: " + set);
    }

    @Test
    void null_원소_추가시_NPE() {
        assertThrows(NullPointerException.class, () -> {
            ImmutableSet.of("a", null, "b");
        });
        System.out.println("✅ null 거부 확인");
    }

    @Test
    void null_컬렉션_copyOf시_NPE() {
        assertThrows(NullPointerException.class, () -> {
            ImmutableSet.copyOf(Arrays.asList("a", null, "b"));
        });
        System.out.println("✅ null 포함 컬렉션 거부");
    }

    @Test
    void 빈_Set_생성() {
        ImmutableSet<String> empty1 = ImmutableSet.of();
        ImmutableSet<String> empty2 = ImmutableSet.of();

        assertEquals(0, empty1.size());
        assertSame(empty1, empty2);  // 싱글톤!
        System.out.println("✅ 빈 Set 싱글톤: " + (empty1 == empty2));
    }

    @Test
    void 원소_1개_Set() {
        ImmutableSet<String> singleton = ImmutableSet.of("alone");

        assertEquals(1, singleton.size());
        assertTrue(singleton.contains("alone"));
        System.out.println("✅ Singleton Set: " + singleton);
    }

    @Test
    void add_메서드_호출시_예외() {
        ImmutableSet<String> set = ImmutableSet.of("a", "b", "c");

        assertThrows(UnsupportedOperationException.class, () -> {
            set.add("d");
        });
        System.out.println("✅ add() 차단 확인");
    }

    @Test
    void remove_메서드_호출시_예외() {
        ImmutableSet<String> set = ImmutableSet.of("a", "b", "c");

        assertThrows(UnsupportedOperationException.class, () -> {
            set.remove("a");
        });
        System.out.println("✅ remove() 차단 확인");
    }

    @Test
    void clear_메서드_호출시_예외() {
        ImmutableSet<String> set = ImmutableSet.of("a", "b", "c");

        assertThrows(UnsupportedOperationException.class, () -> {
            set.clear();
        });
        System.out.println("✅ clear() 차단 확인");
    }

    @Test
    void iterator_remove_호출시_예외() {
        ImmutableSet<String> set = ImmutableSet.of("a", "b", "c");

        assertThrows(UnsupportedOperationException.class, () -> {
            set.iterator().remove();
        });
        System.out.println("✅ Iterator.remove() 차단 확인");
    }
}