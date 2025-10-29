package org.example.ImmutableSet_Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * Immutable 컬렉션들 통합 성능 테스트
 */
public class ImmutableCollectionsPerformanceTest {

    @Test
    void List_vs_Set_vs_Map_생성_시간() {
        int size = 10000;

        // 소스 데이터 준비
        List<Integer> sourceList = new ArrayList<>();
        Set<Integer> sourceSet = new HashSet<>();
        Map<Integer, String> sourceMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            sourceList.add(i);
            sourceSet.add(i);
            sourceMap.put(i, "value" + i);
        }

        // ImmutableList 생성
        long start1 = System.nanoTime();
        ImmutableList<Integer> immutableList = ImmutableList.copyOf(sourceList);
        long time1 = System.nanoTime() - start1;

        // ImmutableSet 생성
        long start2 = System.nanoTime();
        ImmutableSet<Integer> immutableSet = ImmutableSet.copyOf(sourceSet);
        long time2 = System.nanoTime() - start2;

        // ImmutableMap 생성
        long start3 = System.nanoTime();
        ImmutableMap<Integer, String> immutableMap = ImmutableMap.copyOf(sourceMap);
        long time3 = System.nanoTime() - start3;

        System.out.println("\n📊 생성 시간 비교 (" + size + "개):");
        System.out.printf("  ImmutableList: %.2f ms\n", time1 / 1_000_000.0);
        System.out.printf("  ImmutableSet:  %.2f ms\n", time2 / 1_000_000.0);
        System.out.printf("  ImmutableMap:  %.2f ms\n", time3 / 1_000_000.0);
    }

    @Test
    void List_vs_Set_vs_Map_조회_성능() {
        int size = 1000;

        // 데이터 준비
        List<Integer> sourceList = new ArrayList<>();
        Set<Integer> sourceSet = new HashSet<>();
        Map<Integer, String> sourceMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            sourceList.add(i);
            sourceSet.add(i);
            sourceMap.put(i, "value" + i);
        }

        ImmutableList<Integer> list = ImmutableList.copyOf(sourceList);
        ImmutableSet<Integer> set = ImmutableSet.copyOf(sourceSet);
        ImmutableMap<Integer, String> map = ImmutableMap.copyOf(sourceMap);

        int iterations = 100000;

        // List 인덱스 조회
        long start1 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (int i = 0; i < size; i++) {
                Integer v = list.get(i);
            }
        }
        long time1 = System.nanoTime() - start1;

        // Set contains 조회
        long start2 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (int i = 0; i < size; i++) {
                boolean b = set.contains(i);
            }
        }
        long time2 = System.nanoTime() - start2;

        // Map get 조회
        long start3 = System.nanoTime();
        for (int rep = 0; rep < iterations; rep++) {
            for (int i = 0; i < size; i++) {
                String v = map.get(i);
            }
        }
        long time3 = System.nanoTime() - start3;

        System.out.println("\n📊 조회 성능 (" + iterations + "회):");
        System.out.printf("  List.get():      %.2f ms\n", time1 / 1_000_000.0);
        System.out.printf("  Set.contains():  %.2f ms\n", time2 / 1_000_000.0);
        System.out.printf("  Map.get():       %.2f ms\n", time3 / 1_000_000.0);
    }

    @Test
    void 메모리_사용량_추정() {
        int size = 10000;

        List<Integer> sourceList = new ArrayList<>();
        Set<Integer> sourceSet = new HashSet<>();
        Map<Integer, String> sourceMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            sourceList.add(i);
            sourceSet.add(i);
            sourceMap.put(i, "value" + i);
        }

        ImmutableList<Integer> list = ImmutableList.copyOf(sourceList);
        ImmutableSet<Integer> set = ImmutableSet.copyOf(sourceSet);
        ImmutableMap<Integer, String> map = ImmutableMap.copyOf(sourceMap);

        System.out.println("\n📊 메모리 사용량 추정 (" + size + "개):");
        System.out.println("  ImmutableList:");
        System.out.println("    - 배열: " + size + " * 4 bytes = " + (size * 4 / 1024) + " KB");
        System.out.println("    - 메타데이터: ~48 bytes");
        System.out.println("  ImmutableSet:");
        System.out.println("    - 해시 테이블 + 엔트리: ~" + (size * 12 / 1024) + " KB");
        System.out.println("  ImmutableMap:");
        System.out.println("    - 키 + 값 + 테이블: ~" + (size * 24 / 1024) + " KB");
    }

    @Test
    void 실전_시나리오_설정값_저장() {
        // 실전: 애플리케이션 설정값 저장
        Map<String, String> configMap = new HashMap<>();
        configMap.put("db.host", "localhost");
        configMap.put("db.port", "5432");
        configMap.put("app.name", "MyApp");
        configMap.put("app.version", "1.0.0");

        ImmutableMap<String, String> config = ImmutableMap.copyOf(configMap);

        // 여러 스레드에서 동시 접근 시뮬레이션
        long start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            String host = config.get("db.host");
            String port = config.get("db.port");
        }
        long time = System.nanoTime() - start;

        System.out.println("\n✅ 설정값 조회 (100,000회): " + String.format("%.2f ms", time / 1_000_000.0));
        System.out.println("   스레드 안전, 동기화 불필요!");
    }

    @Test
    void 실전_시나리오_API_응답_캐싱() {
        // 실전: API 응답 캐싱
        Set<String> allowedCountries = new HashSet<>(
                Arrays.asList("KR", "US", "JP", "CN", "UK", "DE", "FR", "CA", "AU", "IN")
        );

        ImmutableSet<String> cache = ImmutableSet.copyOf(allowedCountries);

        // 캐시 조회 성능
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            boolean valid = cache.contains("KR");
        }
        long time = System.nanoTime() - start;

        System.out.println("\n✅ 캐시 조회 (1,000,000회): " + String.format("%.2f ms", time / 1_000_000.0));
        System.out.println("   불변 보장, 방어적 복사 불필요!");
    }

    @Test
    void 실전_시나리오_enum_대체() {
        // 실전: 런타임에 결정되는 상수 리스트
        List<String> statusList = Arrays.asList(
                "PENDING", "PROCESSING", "COMPLETED", "FAILED", "CANCELLED"
        );

        ImmutableList<String> statuses = ImmutableList.copyOf(statusList);

        // 순회 성능
        long start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            for (String status : statuses) {
                String s = status;
            }
        }
        long time = System.nanoTime() - start;

        System.out.println("\n✅ 상수 리스트 순회 (100,000회): " + String.format("%.2f ms", time / 1_000_000.0));
        System.out.println("   불변 보장, 의도 명확!");
    }
}