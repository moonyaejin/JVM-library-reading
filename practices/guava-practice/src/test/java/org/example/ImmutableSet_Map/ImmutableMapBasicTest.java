package org.example.ImmutableSet_Map;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImmutableMap 기본 사용법 테스트
 * - 생성 방법들
 * - null 처리
 * - 불변성 확인
 */
public class ImmutableMapBasicTest {

    @Test
    void of_메서드로_생성() {
        ImmutableMap<String, Integer> map = ImmutableMap.of(
                "a", 1,
                "b", 2,
                "c", 3
        );

        assertEquals(3, map.size());
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));

        System.out.println("✅ of() 생성: " + map);
    }

    @Test
    void of_메서드_최대_5개_제한() {
        // of() 메서드는 최대 5개 엔트리까지만 지원
        ImmutableMap<String, Integer> map = ImmutableMap.of(
                "a", 1,
                "b", 2,
                "c", 3,
                "d", 4,
                "e", 5
        );

        assertEquals(5, map.size());
        System.out.println("✅ of() 최대 5개: " + map);

        // 6개 이상은 builder 사용 필요
    }

    @Test
    void copyOf_HashMap에서_생성() {
        Map<String, Integer> original = new HashMap<>();
        original.put("x", 10);
        original.put("y", 20);
        original.put("z", 30);

        ImmutableMap<String, Integer> map = ImmutableMap.copyOf(original);

        assertEquals(3, map.size());
        assertEquals(10, map.get("x"));
        System.out.println("✅ copyOf() 생성: " + map);
    }

    @Test
    void copyOf_이미_ImmutableMap이면_그대로_반환() {
        ImmutableMap<String, Integer> map1 = ImmutableMap.of("a", 1, "b", 2);
        ImmutableMap<String, Integer> map2 = ImmutableMap.copyOf(map1);

        assertSame(map1, map2);  // 동일 객체!
        System.out.println("✅ copyOf() 최적화: map1 == map2? " + (map1 == map2));
    }

    @Test
    void builder_패턴으로_생성() {
        ImmutableMap<String, Integer> map = ImmutableMap.<String, Integer>builder()
                .put("a", 1)
                .put("b", 2)
                .put("c", 3)
                .put("d", 4)
                .put("e", 5)
                .put("f", 6)
                .build();

        assertEquals(6, map.size());
        System.out.println("✅ Builder 패턴 (6개 이상): " + map);
    }

    @Test
    void builder_putAll_사용() {
        Map<String, Integer> source = new HashMap<>();
        source.put("a", 1);
        source.put("b", 2);

        ImmutableMap<String, Integer> map = ImmutableMap.<String, Integer>builder()
                .put("x", 10)
                .putAll(source)
                .put("z", 30)
                .build();

        assertEquals(4, map.size());
        System.out.println("✅ Builder putAll: " + map);
    }

    @Test
    void builder_중복_키_시_예외() {
        assertThrows(IllegalArgumentException.class, () -> {
            ImmutableMap.<String, Integer>builder()
                    .put("a", 1)
                    .put("a", 2)  // 중복 키!
                    .build();
        });
        System.out.println("✅ 중복 키 거부 확인");
    }

    @Test
    void null_key_추가시_NPE() {
        assertThrows(NullPointerException.class, () -> {
            ImmutableMap.of(null, 1);
        });
        System.out.println("✅ null key 거부");
    }

    @Test
    void null_value_추가시_NPE() {
        assertThrows(NullPointerException.class, () -> {
            ImmutableMap.of("a", null);
        });
        System.out.println("✅ null value 거부");
    }

    @Test
    void 빈_Map_생성() {
        ImmutableMap<String, Integer> empty1 = ImmutableMap.of();
        ImmutableMap<String, Integer> empty2 = ImmutableMap.of();

        assertEquals(0, empty1.size());
        assertSame(empty1, empty2);  // 싱글톤!
        System.out.println("✅ 빈 Map 싱글톤: " + (empty1 == empty2));
    }

    @Test
    void 엔트리_1개_Map() {
        ImmutableMap<String, Integer> singleton = ImmutableMap.of("key", 100);

        assertEquals(1, singleton.size());
        assertEquals(100, singleton.get("key"));
        System.out.println("✅ Singleton Map: " + singleton);
    }

    @Test
    void put_메서드_호출시_예외() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            map.put("c", 3);
        });
        System.out.println("✅ put() 차단 확인");
    }

    @Test
    void remove_메서드_호출시_예외() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            map.remove("a");
        });
        System.out.println("✅ remove() 차단 확인");
    }

    @Test
    void clear_메서드_호출시_예외() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            map.clear();
        });
        System.out.println("✅ clear() 차단 확인");
    }

    @Test
    void keySet_수정_시도시_예외() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            map.keySet().remove("a");
        });
        System.out.println("✅ keySet() 수정 차단");
    }

    @Test
    void values_수정_시도시_예외() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            map.values().clear();
        });
        System.out.println("✅ values() 수정 차단");
    }

    @Test
    void entrySet_수정_시도시_예외() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            map.entrySet().clear();
        });
        System.out.println("✅ entrySet() 수정 차단");
    }
}