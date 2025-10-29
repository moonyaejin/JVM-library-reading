package org.example.ImmutableSet_Map;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImmutableSet 내부 구조 분석
 * - Reflection으로 내부 필드 확인
 * - 원소 개수별 최적화 확인
 * - 해시 테이블 구조 확인
 */
public class ImmutableSetInternalTest {

    @Test
    void 빈_Set의_클래스_확인() {
        ImmutableSet<String> empty = ImmutableSet.of();

        String className = empty.getClass().getSimpleName();
        System.out.println("📦 빈 Set 클래스: " + className);

        assertTrue(className.contains("Empty") || className.contains("Regular"));
    }

    @Test
    void 원소_1개_Set의_클래스_확인() {
        ImmutableSet<String> singleton = ImmutableSet.of("alone");

        String className = singleton.getClass().getSimpleName();
        System.out.println("📦 원소 1개 Set 클래스: " + className);

        assertTrue(className.contains("Singleton") || className.contains("Single"));
    }

    @Test
    void 원소_여러개_Set의_클래스_확인() {
        ImmutableSet<String> regular = ImmutableSet.of("a", "b", "c", "d", "e");

        String className = regular.getClass().getSimpleName();
        System.out.println("📦 일반 Set 클래스: " + className);

        assertTrue(className.contains("Regular"));
    }

    @Test
    void RegularImmutableSet_내부_구조_확인() throws Exception {
        ImmutableSet<String> set = ImmutableSet.of("a", "b", "c", "d", "e");

        Class<?> clazz = set.getClass();
        System.out.println("\n🔍 RegularImmutableSet 내부 필드:");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(set);

            System.out.println("  - " + field.getName() + ": " +
                    field.getType().getSimpleName() +
                    (value != null ? " (크기: " + getSize(value) + ")" : ""));
        }
    }

    @Test
    void 해시_테이블_구조_확인() throws Exception {
        ImmutableSet<Integer> set = ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Class<?> clazz = set.getClass();
        Field tableField = findField(clazz, "table");

        if (tableField != null) {
            tableField.setAccessible(true);
            Object table = tableField.get(set);

            if (table != null && table.getClass().isArray()) {
                int length = java.lang.reflect.Array.getLength(table);
                System.out.println("📊 해시 테이블 크기: " + length);
                System.out.println("   원소 개수: " + set.size());
                System.out.println("   로드 팩터: " + String.format("%.2f", set.size() / (double) length));
            }
        }
    }

    @Test
    void 원소_개수별_내부_구조_비교() {
        testSetSize(0);
        testSetSize(1);
        testSetSize(2);
        testSetSize(5);
        testSetSize(10);
        testSetSize(20);
        testSetSize(50);
        testSetSize(100);
    }

    private void testSetSize(int size) {
        Integer[] elements = new Integer[size];
        for (int i = 0; i < size; i++) {
            elements[i] = i;
        }

        ImmutableSet<Integer> set = ImmutableSet.copyOf(Arrays.asList(elements));
        String className = set.getClass().getSimpleName();

        System.out.printf("📦 크기 %3d: %-25s\n", size, className);
    }

    @Test
    void Singleton_내부_필드_확인() throws Exception {
        ImmutableSet<String> singleton = ImmutableSet.of("alone");

        Class<?> clazz = singleton.getClass();
        System.out.println("\n🔍 SingletonImmutableSet 내부:");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(singleton);
            System.out.println("  - " + field.getName() + ": " + value);
        }
    }

    // 헬퍼 메서드들
    private int getSize(Object value) {
        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value);
        }
        return -1;
    }

    private Field findField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), name);
            }
            return null;
        }
    }
}