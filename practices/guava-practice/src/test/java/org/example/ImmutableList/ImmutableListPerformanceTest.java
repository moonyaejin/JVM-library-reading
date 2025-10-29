package org.example.ImmutableList;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step 7: ImmutableList 성능 & 메모리 효율성 측정
 *
 * 주의: 이 테스트는 마이크로벤치마크로 참고용입니다.
 * JVM 워밍업, GC, JIT 컴파일 등의 영향을 받을 수 있습니다.
 */
public class ImmutableListPerformanceTest {

    private static final int WARMUP_ITERATIONS = 5000;
    private static final int TEST_SIZE = 10000;
    private static final int ITERATION_COUNT = 100000;

    @Test
    void testCreationPerformance() {
        System.out.println("📊 생성 성능 비교 (원소 " + TEST_SIZE + "개)");
        System.out.println("=".repeat(60));

        // 워밍업
        warmup();

        // ArrayList 생성 + 데이터 추가
        long start = System.nanoTime();
        List<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            arrayList.add(i);
        }
        long arrayListTime = System.nanoTime() - start;

        // ImmutableList.copyOf() 생성
        List<Integer> source = createSourceList(TEST_SIZE);
        start = System.nanoTime();
        ImmutableList<Integer> immutableCopy = ImmutableList.copyOf(source);
        long copyOfTime = System.nanoTime() - start;

        // ImmutableList.builder() 생성
        start = System.nanoTime();
        ImmutableList.Builder<Integer> builder = ImmutableList.builder();
        for (int i = 0; i < TEST_SIZE; i++) {
            builder.add(i);
        }
        ImmutableList<Integer> immutableBuilder = builder.build();
        long builderTime = System.nanoTime() - start;

        // 결과 출력
        System.out.println("ArrayList 생성:           " + formatNanos(arrayListTime));
        System.out.println("ImmutableList.copyOf():   " + formatNanos(copyOfTime));
        System.out.println("ImmutableList.builder():  " + formatNanos(builderTime));

        System.out.println("\n배율 (ArrayList 기준):");
        System.out.println("copyOf():  " + String.format("%.2fx", (double)copyOfTime / arrayListTime));
        System.out.println("builder(): " + String.format("%.2fx", (double)builderTime / arrayListTime));

        System.out.println("\n💡 copyOf()는 ArrayList에서 복사하므로 시간이 더 걸립니다.");
        System.out.println("   builder()는 직접 구축하므로 ArrayList와 비슷합니다.");
    }

    @Test
    void testGetPerformance() {
        System.out.println("\n📊 조회 성능 비교 (get() " + ITERATION_COUNT + "회)");
        System.out.println("=".repeat(60));

        List<Integer> arrayList = createSourceList(TEST_SIZE);
        ImmutableList<Integer> immutableList = ImmutableList.copyOf(arrayList);

        // 워밍업
        warmup();

        // ArrayList.get()
        long start = System.nanoTime();
        long sum1 = 0;
        for (int i = 0; i < ITERATION_COUNT; i++) {
            sum1 += arrayList.get(i % TEST_SIZE);
        }
        long arrayListGetTime = System.nanoTime() - start;

        // ImmutableList.get()
        start = System.nanoTime();
        long sum2 = 0;
        for (int i = 0; i < ITERATION_COUNT; i++) {
            sum2 += immutableList.get(i % TEST_SIZE);
        }
        long immutableGetTime = System.nanoTime() - start;

        System.out.println("ArrayList.get():      " + formatNanos(arrayListGetTime));
        System.out.println("ImmutableList.get():  " + formatNanos(immutableGetTime));
        System.out.println("배율: " + String.format("%.2fx", (double)immutableGetTime / arrayListGetTime));

        System.out.println("\n💡 둘 다 배열 기반이므로 조회 성능은 거의 동일합니다.");

        // 결과가 같은지 확인 (최적화 방지)
        assertEquals(sum1, sum2);
    }

    @Test
    void testIterationPerformance() {
        System.out.println("\n📊 반복 성능 비교 (전체 순회 1000회)");
        System.out.println("=".repeat(60));

        List<Integer> arrayList = createSourceList(TEST_SIZE);
        ImmutableList<Integer> immutableList = ImmutableList.copyOf(arrayList);

        // 워밍업
        warmup();

        int iterations = 1000;

        // ArrayList - enhanced for loop
        long start = System.nanoTime();
        long sum1 = 0;
        for (int iter = 0; iter < iterations; iter++) {
            for (Integer num : arrayList) {
                sum1 += num;
            }
        }
        long arrayListForTime = System.nanoTime() - start;

        // ImmutableList - enhanced for loop
        start = System.nanoTime();
        long sum2 = 0;
        for (int iter = 0; iter < iterations; iter++) {
            for (Integer num : immutableList) {
                sum2 += num;
            }
        }
        long immutableForTime = System.nanoTime() - start;

        // ArrayList - 인덱스 접근
        start = System.nanoTime();
        long sum3 = 0;
        for (int iter = 0; iter < iterations; iter++) {
            for (int i = 0; i < arrayList.size(); i++) {
                sum3 += arrayList.get(i);
            }
        }
        long arrayListIndexTime = System.nanoTime() - start;

        // ImmutableList - 인덱스 접근
        start = System.nanoTime();
        long sum4 = 0;
        for (int iter = 0; iter < iterations; iter++) {
            for (int i = 0; i < immutableList.size(); i++) {
                sum4 += immutableList.get(i);
            }
        }
        long immutableIndexTime = System.nanoTime() - start;

        System.out.println("ArrayList (for-each):         " + formatNanos(arrayListForTime));
        System.out.println("ImmutableList (for-each):     " + formatNanos(immutableForTime));
        System.out.println("ArrayList (index):            " + formatNanos(arrayListIndexTime));
        System.out.println("ImmutableList (index):        " + formatNanos(immutableIndexTime));

        System.out.println("\n💡 반복 성능도 거의 동일합니다.");

        assertEquals(sum1, sum2);
        assertEquals(sum1, sum3);
        assertEquals(sum1, sum4);
    }

    @Test
    void testBuilderVsCopyOf() {
        System.out.println("\n📊 Builder vs copyOf() 비교");
        System.out.println("=".repeat(60));

        List<Integer> source = createSourceList(TEST_SIZE);

        // 워밍업
        warmup();

        // copyOf() - 이미 있는 컬렉션에서 생성
        long start = System.nanoTime();
        ImmutableList<Integer> fromCopyOf = ImmutableList.copyOf(source);
        long copyOfTime = System.nanoTime() - start;

        // builder() - 처음부터 구축
        start = System.nanoTime();
        ImmutableList.Builder<Integer> builder = ImmutableList.builder();
        for (Integer num : source) {
            builder.add(num);
        }
        ImmutableList<Integer> fromBuilder = builder.build();
        long builderTime = System.nanoTime() - start;

        // builderWithExpectedSize() - 크기 힌트 제공
        start = System.nanoTime();
        ImmutableList.Builder<Integer> builderWithSize =
                ImmutableList.builderWithExpectedSize(TEST_SIZE);
        for (Integer num : source) {
            builderWithSize.add(num);
        }
        ImmutableList<Integer> fromBuilderWithSize = builderWithSize.build();
        long builderWithSizeTime = System.nanoTime() - start;

        System.out.println("copyOf():                    " + formatNanos(copyOfTime));
        System.out.println("builder():                   " + formatNanos(builderTime));
        System.out.println("builderWithExpectedSize():   " + formatNanos(builderWithSizeTime));

        System.out.println("\n배율 (copyOf 기준):");
        System.out.println("builder():                 " + String.format("%.2fx", (double)builderTime / copyOfTime));
        System.out.println("builderWithExpectedSize(): " + String.format("%.2fx", (double)builderWithSizeTime / copyOfTime));

        System.out.println("\n💡 이미 컬렉션이 있다면 copyOf()가 빠릅니다.");
        System.out.println("   동적으로 추가한다면 builderWithExpectedSize()를 고려하세요.");
    }

    @Test
    void testMemoryEfficiency() {
        System.out.println("\n📊 메모리 효율성 비교 (간접 측정)");
        System.out.println("=".repeat(60));

        // ArrayList - 여유 공간을 둠
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            arrayList.add(i);
        }

        // ImmutableList - 정확한 크기
        ImmutableList<Integer> immutableList = ImmutableList.copyOf(arrayList);

        System.out.println("ArrayList:");
        System.out.println("  size(): " + arrayList.size());
        try {
            java.lang.reflect.Field field = ArrayList.class.getDeclaredField("elementData");
            field.setAccessible(true);
            Object[] elementData = (Object[]) field.get(arrayList);
            System.out.println("  내부 배열 길이: " + elementData.length);
            System.out.println("  낭비: " + (elementData.length - arrayList.size()) + "칸");
        } catch (Exception e) {
            System.out.println("  (내부 구조 확인 불가)");
        }

        System.out.println("\nImmutableList:");
        System.out.println("  size(): " + immutableList.size());
        try {
            java.lang.reflect.Field field = immutableList.getClass().getDeclaredField("array");
            field.setAccessible(true);
            Object[] array = (Object[]) field.get(immutableList);
            System.out.println("  내부 배열 길이: " + array.length);
            System.out.println("  낭비: " + (array.length - immutableList.size()) + "칸");
        } catch (Exception e) {
            System.out.println("  (내부 구조 확인 불가)");
        }

        System.out.println("\n💡 ImmutableList는 정확한 크기로 메모리 낭비가 없습니다.");
    }

    @Test
    void testCopyOfOptimizationBenchmark() {
        System.out.println("\n📊 copyOf() 최적화 벤치마크");
        System.out.println("=".repeat(60));

        List<Integer> arrayList = createSourceList(TEST_SIZE);
        ImmutableList<Integer> immutableList = ImmutableList.copyOf(arrayList);

        // 워밍업
        warmup();

        int iterations = 10000;

        // ArrayList에서 copyOf() - 복사 발생
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ImmutableList.copyOf(arrayList);
        }
        long fromArrayListTime = System.nanoTime() - start;

        // ImmutableList에서 copyOf() - 복사 안 함!
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ImmutableList.copyOf(immutableList);
        }
        long fromImmutableTime = System.nanoTime() - start;

        System.out.println("copyOf(ArrayList) " + iterations + "회:");
        System.out.println("  " + formatNanos(fromArrayListTime));

        System.out.println("\ncopyOf(ImmutableList) " + iterations + "회:");
        System.out.println("  " + formatNanos(fromImmutableTime));

        System.out.println("\n속도 향상:");
        System.out.println("  " + String.format("%.0fx", (double)fromArrayListTime / fromImmutableTime));

        System.out.println("\n💡 이미 ImmutableList면 그대로 반환하므로 매우 빠릅니다!");
    }

    @Test
    void testPerformanceSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 성능 특성 요약");
        System.out.println("=".repeat(60));

        System.out.println("\n1. 생성 성능:");
        System.out.println("   - copyOf(): 복사 비용 있음 (ArrayList 대비 ~1.5-2x)");
        System.out.println("   - builder(): ArrayList와 비슷");
        System.out.println("   - builderWithExpectedSize(): 가장 효율적 (재할당 최소화)");

        System.out.println("\n2. 조회 성능:");
        System.out.println("   - get(): ArrayList와 동일 (둘 다 배열 기반)");
        System.out.println("   - 반복: ArrayList와 동일");

        System.out.println("\n3. 메모리 효율:");
        System.out.println("   - ArrayList: 여유 공간 둠 (capacity > size)");
        System.out.println("   - ImmutableList: 정확한 크기 (낭비 없음)");

        System.out.println("\n4. 최적화:");
        System.out.println("   - copyOf(ImmutableList): 복사 안 함 (매우 빠름)");
        System.out.println("   - 빈 리스트: 싱글톤 재사용");
        System.out.println("   - 1개 원소: SingletonImmutableList (메모리 최소)");

        System.out.println("\n5. Trade-off:");
        System.out.println("   - 생성 비용 ↑ but 메모리 효율 ↑");
        System.out.println("   - Thread-safe 보장");
        System.out.println("   - 변경 불가능 → 버그 감소");

        System.out.println("\n6. 사용 권장 시나리오:");
        System.out.println("   ✅ 생성 후 변경 없는 데이터");
        System.out.println("   ✅ Thread 간 공유하는 데이터");
        System.out.println("   ✅ API 반환값 (방어적 복사 불필요)");
        System.out.println("   ✅ 설정, 상수 리스트");
        System.out.println("=".repeat(60));

        System.out.println("\n⚠️  주의: 이 벤치마크는 참고용입니다.");
        System.out.println("   실제 성능은 JVM 버전, 하드웨어, 데이터 크기에 따라 달라집니다.");
        System.out.println("   중요한 성능 결정에는 JMH 같은 전문 도구를 사용하세요.");
    }

    // === 헬퍼 메서드 ===

    private void warmup() {
        // JVM 워밍업: JIT 컴파일러가 최적화하도록
        List<Integer> list = createSourceList(100);
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            ImmutableList.copyOf(list);
            list.get(i % 100);
        }
    }

    private List<Integer> createSourceList(int size) {
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }

    private String formatNanos(long nanos) {
        if (nanos < 1000) {
            return nanos + " ns";
        } else if (nanos < 1000000) {
            return String.format("%.2f μs", nanos / 1000.0);
        } else if (nanos < 1000000000) {
            return String.format("%.2f ms", nanos / 1000000.0);
        } else {
            return String.format("%.2f s", nanos / 1000000000.0);
        }
    }
}