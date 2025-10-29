package org.example.ImmutableSet_Map;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImmutableMap 내부 구조 분석
 * - Reflection으로 내부 필드 확인
 * - 엔트리 개수별 최적화 확인
 * - 해시 테이블 구조 확인
 */
public class ImmutableMapInternalTest {

    @Test
    void 빈_Map의_클래스_확인() {
        ImmutableMap<String, Integer> empty = ImmutableMap.of();

        String className = empty.getClass().getSimpleName();
        System.out.println("📦 빈 Map 클래스: " + className);

        assertTrue(className.contains("Empty") || className.contains("Regular"));
    }

    @Test
    void 엔트리_1개_Map의_클래스_확인() {
        ImmutableMap<String, Integer> singleton = ImmutableMap.of("key", 1);

        String className = singleton.getClass().getSimpleName();
        System.out.println("📦 엔트리 1개 Map 클래스: " + className);

        assertTrue(className.contains("Singleton") || className.contains("Single"));
    }

    @Test
    void 엔트리_여러개_Map의_클래스_확인() {
        ImmutableMap<String, Integer> regular = ImmutableMap.of(
                "a", 1, "b", 2, "c", 3, "d", 4, "e", 5
        );

        String className = regular.getClass().getSimpleName();
        System.out.println("📦 일반 Map 클래스: " + className);

        assertTrue(className.contains("Regular"));
    }

    @Test
    void RegularImmutableMap_내부_구조_확인() throws Exception {
        Map<String, Integer> source = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            source.put("key" + i, i);
        }
        ImmutableMap<String, Integer> map = ImmutableMap.copyOf(source);

        Class<?> clazz = map.getClass();
        System.out.println("\n🔍 RegularImmutableMap 내부 필드:");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(map);

            System.out.println("  - " + field.getName() + ": " +
                    field.getType().getSimpleName() +
                    (value != null ? " (값: " + getValueInfo(value) + ")" : ""));
        }
    }

    @Test
    void 엔트리_개수별_내부_구조_비교() {
        testMapSize(0);
        testMapSize(1);
        testMapSize(2);
        testMapSize(5);
        testMapSize(10);
        testMapSize(20);
        testMapSize(50);
        testMapSize(100);
    }

    private void testMapSize(int size) {
        Map<String, Integer> source = new HashMap<>();
        for (int i = 0; i < size; i++) {
            source.put("key" + i, i);
        }

        ImmutableMap<String, Integer> map = ImmutableMap.copyOf(source);
        String className = map.getClass().getSimpleName();

        System.out.printf("📦 크기 %3d: %-30s\n", size, className);
    }

    @Test
    void Singleton_내부_필드_확인() throws Exception {
        ImmutableMap<String, Integer> singleton = ImmutableMap.of("key", 123);

        Class<?> clazz = singleton.getClass();
        System.out.println("\n🔍 SingletonImmutableMap 내부:");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(singleton);
            System.out.println("  - " + field.getName() + ": " + value);
        }
    }

    @Test
    void 해시_테이블_크기_확인() throws Exception {
        Map<Integer, String> source = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            source.put(i, "value" + i);
        }

        ImmutableMap<Integer, String> map = ImmutableMap.copyOf(source);

        Class<?> clazz = map.getClass();
        Field tableField = findField(clazz, "table");

        if (tableField != null) {
            tableField.setAccessible(true);
            Object table = tableField.get(map);

            if (table != null && table.getClass().isArray()) {
                int length = java.lang.reflect.Array.getLength(table);
                System.out.println("📊 해시 테이블 분석:");
                System.out.println("   테이블 크기: " + length);
                System.out.println("   엔트리 개수: " + map.size());
                System.out.println("   로드 팩터: " + String.format("%.2f", map.size() / (double) length));
            }
        }
    }

    @Test
    void 메모리_레이아웃_비교() throws Exception {
        int size = 100;

        // HashMap
        Map<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            hashMap.put(i, "value" + i);
        }

        // ImmutableMap
        ImmutableMap<Integer, String> immutableMap = ImmutableMap.copyOf(hashMap);

        System.out.println("\n📊 메모리 레이아웃 비교 (" + size + "개 엔트리):");
        System.out.println("  HashMap:");
        System.out.println("    기본 초기 용량: 16");
        System.out.println("    로드 팩터: 0.75");
        System.out.println("    " + size + "개 저장 시 예상 용량: 128 (2의 거듭제곱)");
        System.out.println("    예상 사용률: ~78%");

        // ImmutableMap 내부 확인 (Guava는 접근 가능)
        Class<?> immutableClazz = immutableMap.getClass();
        Field immutableTable = findField(immutableClazz, "table");
        if (immutableTable != null) {
            immutableTable.setAccessible(true);
            Object immutableTableObj = immutableTable.get(immutableMap);
            if (immutableTableObj != null && immutableTableObj.getClass().isArray()) {
                int immutableCapacity = java.lang.reflect.Array.getLength(immutableTableObj);
                System.out.println("\n  ImmutableMap:");
                System.out.println("    실제 테이블 크기: " + immutableCapacity);
                System.out.println("    실제 사용률: " + String.format("%.1f%%", size * 100.0 / immutableCapacity));
                System.out.println("\n  💡 ImmutableMap이 더 빡빡하게 패킹!");
                System.out.println("     (HashMap은 확장 여유 공간 유지)");
            }
        } else {
            System.out.println("\n  ImmutableMap: 내부 구조 확인 불가");
            System.out.println("  하지만 일반적으로 HashMap보다 메모리 효율적!");
        }
    }

    @Test
    void entrySet_구조_확인() {
        ImmutableMap<String, Integer> map = ImmutableMap.of(
                "a", 1, "b", 2, "c", 3
        );

        var entrySet = map.entrySet();
        System.out.println("\n🔍 EntrySet 정보:");
        System.out.println("  클래스: " + entrySet.getClass().getSimpleName());
        System.out.println("  크기: " + entrySet.size());
        System.out.println("  불변: " + entrySet.getClass().getName().contains("Immutable"));
    }

    // 헬퍼 메서드들
    private String getValueInfo(Object value) {
        if (value.getClass().isArray()) {
            return "배열[" + java.lang.reflect.Array.getLength(value) + "]";
        }
        return value.toString();
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
