package org.example.ImmutableList;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step 4: ImmutableList 기본 사용법 테스트
 */
public class ImmutableListBasicTest {

    @Test
    void testCreationWithOf() {
        // 빈 리스트
        ImmutableList<String> empty = ImmutableList.of();
        assertEquals(0, empty.size());

        // 1개 원소
        ImmutableList<String> one = ImmutableList.of("a");
        assertEquals(1, one.size());
        assertEquals("a", one.get(0));

        // 여러 개 원소
        ImmutableList<String> many = ImmutableList.of("a", "b", "c", "d", "e");
        assertEquals(5, many.size());
        assertEquals("c", many.get(2));

        System.out.println("✅ of() 메서드로 생성 성공");
    }

    @Test
    void testCreationWithCopyOf() {
        // ArrayList에서 복사
        List<String> mutable = new ArrayList<>(Arrays.asList("x", "y", "z"));
        ImmutableList<String> immutable = ImmutableList.copyOf(mutable);

        assertEquals(3, immutable.size());
        assertEquals("y", immutable.get(1));

        // 원본 변경해도 불변 리스트는 영향 없음
        mutable.add("w");
        assertEquals(4, mutable.size());
        assertEquals(3, immutable.size()); // 여전히 3개

        System.out.println("✅ copyOf()로 생성 및 원본과 독립성 확인");
    }

    @Test
    void testCreationWithBuilder() {
        // Builder 사용
        ImmutableList<Integer> list = ImmutableList.<Integer>builder()
                .add(1)
                .add(2, 3)
                .addAll(Arrays.asList(4, 5, 6))
                .build();

        assertEquals(6, list.size());
        assertEquals(4, list.get(3));

        System.out.println("✅ Builder로 생성 성공");
    }

    @Test
    void testImmutability() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        // 변경 시도 시 예외 발생
        assertThrows(UnsupportedOperationException.class, () -> {
            list.add("d");
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            list.remove(0);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            list.set(0, "z");
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            list.clear();
        });

        System.out.println("✅ 불변성 확인: 모든 변경 시도 예외 발생");
    }

    @Test
    void testNullHandling() {
        // null 허용 안 함
        assertThrows(NullPointerException.class, () -> {
            ImmutableList.of("a", null, "c");
        });

        assertThrows(NullPointerException.class, () -> {
            ImmutableList.copyOf(Arrays.asList("a", null, "c"));
        });

        assertThrows(NullPointerException.class, () -> {
            ImmutableList.builder().add("a").add(null).build();
        });

        System.out.println("✅ null 허용 안 함 확인");
    }

    @Test
    void testCopyOfImmutableListOptimization() {
        // 이미 ImmutableList면 복사 안 함!
        ImmutableList<String> original = ImmutableList.of("a", "b", "c");
        ImmutableList<String> copy = ImmutableList.copyOf(original);

        // 같은 객체 참조!
        assertSame(original, copy);
        System.out.println("✅ copyOf() 최적화: 이미 ImmutableList면 그대로 반환");
        System.out.println("   original == copy: " + (original == copy));
    }

    @Test
    void testBuilderReuse() {
        // Builder 재사용 가능
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        ImmutableList<String> list1 = builder.add("a").add("b").build();
        ImmutableList<String> list2 = builder.add("c").add("d").build();

        assertEquals(2, list1.size());
        assertEquals(4, list2.size()); // 누적됨!

        System.out.println("✅ Builder 재사용 확인");
        System.out.println("   list1: " + list1);
        System.out.println("   list2: " + list2);
    }

    @Test
    void testSubList() {
        ImmutableList<Integer> list = ImmutableList.of(1, 2, 3, 4, 5);

        ImmutableList<Integer> subList = list.subList(1, 4);

        assertEquals(3, subList.size());
        assertEquals(2, subList.get(0));
        assertEquals(4, subList.get(2));

        // subList도 불변!
        assertThrows(UnsupportedOperationException.class, () -> {
            subList.add(10);
        });

        System.out.println("✅ subList도 불변 리스트 반환");
    }
}