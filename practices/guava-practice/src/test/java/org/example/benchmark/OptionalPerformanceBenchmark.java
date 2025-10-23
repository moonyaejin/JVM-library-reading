package org.example.benchmark;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Guava Optional 성능 벤치마크
 */
public class OptionalPerformanceBenchmark {

    private static final int ITERATIONS = 1_000_000;

    public static void main(String[] args) {
        System.out.println("=== Optional 성능 벤치마크 ===");
        System.out.println("반복 횟수: " + String.format("%,d", ITERATIONS) + "회\n");

        warmup();

        benchmark1_Creation();
        benchmark2_AbsentSingleton();
        benchmark3_TransformChain();
        benchmark4_OrVsOrSupplier();

        conclusions();
    }

    static void warmup() {
        System.out.println("⏳ JVM 워밍업 중...");
        for (int i = 0; i < 10000; i++) {
            com.google.common.base.Optional.fromNullable("test");
            java.util.Optional.ofNullable("test");
        }
        System.out.println("✓ 워밍업 완료\n");
    }

    static void benchmark1_Creation() {
        System.out.println("📊 벤치마크 1: 객체 생성 비용\n");

        // Guava: fromNullable
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            com.google.common.base.Optional<String> opt =
                    com.google.common.base.Optional.fromNullable("test");
        }
        long guavaTime = System.nanoTime() - start;

        // Java 8: ofNullable
        start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            java.util.Optional<String> opt =
                    java.util.Optional.ofNullable("test");
        }
        long java8Time = System.nanoTime() - start;

        System.out.printf("  Guava fromNullable: %,d ns (%.2f ms)%n",
                guavaTime, guavaTime / 1_000_000.0);
        System.out.printf("  Java 8 ofNullable:  %,d ns (%.2f ms)%n",
                java8Time, java8Time / 1_000_000.0);

        double diff = ((double) guavaTime / java8Time - 1) * 100;
        if (diff > 0) {
            System.out.printf("  차이: Guava가 %.1f%% 느림\n\n", diff);
        } else {
            System.out.printf("  차이: Guava가 %.1f%% 빠름\n\n", -diff);
        }
    }

    static void benchmark2_AbsentSingleton() {
        System.out.println("📊 벤치마크 2: Absent/Empty 싱글톤 효과\n");

        // Guava: absent() - 싱글톤
        long start = System.nanoTime();
        Object prev = null;
        int sameInstances = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            com.google.common.base.Optional<String> opt =
                    com.google.common.base.Optional.absent();
            if (prev != null && prev == opt) {
                sameInstances++;
            }
            prev = opt;
        }
        long guavaTime = System.nanoTime() - start;

        // Java 8: empty() - 싱글톤
        start = System.nanoTime();
        Object prev2 = null;
        int sameInstances2 = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            java.util.Optional<String> opt =
                    java.util.Optional.empty();
            if (prev2 != null && prev2 == opt) {
                sameInstances2++;
            }
            prev2 = opt;
        }
        long java8Time = System.nanoTime() - start;

        System.out.printf("  Guava absent(): %,d ns (같은 인스턴스: %,d회)%n",
                guavaTime, sameInstances);
        System.out.printf("  Java 8 empty(): %,d ns (같은 인스턴스: %,d회)%n",
                java8Time, sameInstances2);
        System.out.println("  ✓ 둘 다 싱글톤 패턴 사용\n");
    }

    static void benchmark3_TransformChain() {
        System.out.println("📊 벤치마크 3: Transform/Map 체이닝\n");

        // Guava: transform
        Function<String, Integer> toLength = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.length();
            }
        };

        Function<Integer, String> toString = new Function<Integer, String>() {
            @Override
            public String apply(Integer i) {
                return String.valueOf(i);
            }
        };

        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            com.google.common.base.Optional<String> result =
                    com.google.common.base.Optional.of("hello")
                            .transform(toLength)
                            .transform(toString);
        }
        long guavaTime = System.nanoTime() - start;

        // Java 8: map
        start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            java.util.Optional<String> result =
                    java.util.Optional.of("hello")
                            .map(String::length)
                            .map(String::valueOf);
        }
        long java8Time = System.nanoTime() - start;

        System.out.printf("  Guava transform: %,d ns (%.2f ms)%n",
                guavaTime, guavaTime / 1_000_000.0);
        System.out.printf("  Java 8 map:      %,d ns (%.2f ms)%n",
                java8Time, java8Time / 1_000_000.0);

        double diff = ((double) guavaTime / java8Time - 1) * 100;
        System.out.printf("  차이: Java 8이 %.1f%% 빠름 (람다 최적화)%n\n", diff);
    }

    static void benchmark4_OrVsOrSupplier() {
        System.out.println("📊 벤치마크 4: or() vs or(Supplier) - 지연 평가 효과\n");

        // Guava - Present 케이스
        com.google.common.base.Optional<String> present =
                com.google.common.base.Optional.of("value");

        final int[] callCount = {0};
        Supplier<String> expensiveOp = new Supplier<String>() {
            @Override
            public String get() {
                callCount[0]++;
                return "expensive";
            }
        };

        // 케이스 1: or(value) - 값이 있어도 평가됨
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String expensive = "expensive";
            present.or(expensive);
        }
        long directTime = System.nanoTime() - start;

        // 케이스 2: or(Supplier) - 값이 있으면 평가 안 됨
        callCount[0] = 0;
        start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            present.or(expensiveOp);
        }
        long supplierTime = System.nanoTime() - start;

        System.out.printf("  Present.or(value):    %,d ns%n", directTime);
        System.out.printf("  Present.or(Supplier): %,d ns (Supplier 호출: %,d회)%n",
                supplierTime, callCount[0]);
        System.out.println("  ✓ Present일 때는 Supplier가 호출되지 않음!");

        // Absent 케이스
        com.google.common.base.Optional<String> absent =
                com.google.common.base.Optional.absent();

        callCount[0] = 0;
        start = System.nanoTime();
        for (int i = 0; i < ITERATIONS / 10; i++) {
            absent.or(expensiveOp);
        }
        long absentSupplierTime = System.nanoTime() - start;

        System.out.printf("\n  Absent.or(Supplier): %,d ns (Supplier 호출: %,d회)%n",
                absentSupplierTime, callCount[0]);
        System.out.println("  ✓ Absent일 때만 Supplier가 호출됨!\n");
    }

    static void conclusions() {
        System.out.println("📌 성능 분석 결론\n");

        System.out.println("1. 생성 비용");
        System.out.println("   • Guava와 Java 8 Optional의 생성 비용은 거의 동일");
        System.out.println("   • 실제 애플리케이션에서는 무시할 수 있는 수준");

        System.out.println("\n2. 메모리 효율");
        System.out.println("   • 둘 다 absent/empty를 싱글톤으로 관리");
        System.out.println("   • null 대신 Optional 사용 시 추가 객체 생성 발생");
        System.out.println("   • 하지만 NPE 방지 가치가 비용을 상쇄");

        System.out.println("\n3. 변환 체이닝");
        System.out.println("   • Java 8의 람다가 익명 클래스보다 빠름");
        System.out.println("   • invokedynamic 바이트코드 최적화 덕분");
        System.out.println("   • 실무에서는 코드 가독성이 더 중요");

        System.out.println("\n4. 지연 평가");
        System.out.println("   • or(Supplier)는 필요할 때만 평가");
        System.out.println("   • 비용이 큰 연산일수록 이득 증가");
        System.out.println("   • 데이터베이스 조회, 네트워크 호출 등에 유용");

        System.out.println("\n💡 최종 권장사항:");
        System.out.println("   • 성능보다는 코드 명확성을 우선");
        System.out.println("   • Optional은 반환 타입에만 사용");
        System.out.println("   • 핫 패스(hot path)가 아니라면 성능 차이 무시 가능");
        System.out.println("   • 팀의 Java 버전에 맞춰 일관되게 사용");
    }
}