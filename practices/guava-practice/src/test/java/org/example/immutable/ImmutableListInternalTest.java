package org.example.immutable;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step 6: ImmutableList 내부 구조 분석
 */
public class ImmutableListInternalTest {

    @Test
    void testEmptyListIsSingleton() {
        ImmutableList<String> empty1 = ImmutableList.of();
        ImmutableList<String> empty2 = ImmutableList.of();
        ImmutableList<Integer> empty3 = ImmutableList.of();

        // 모두 같은 싱글톤 객체!
        assertSame(empty1, empty2);
        // 타입이 달라도 런타임에는 같은 객체 (Object로 비교)
        assertSame((Object) empty1, (Object) empty3);

        System.out.println("✅ 빈 리스트는 싱글톤 (EMPTY 재사용)");
        System.out.println("   empty1 == empty2: " + (empty1 == empty2));
        System.out.println("   empty1 == empty3(Object 비교): " + ((Object) empty1 == (Object) empty3));
    }

    @Test
    void testSingletonOptimization() {
        ImmutableList<String> single = ImmutableList.of("hello");

        String className = single.getClass().getSimpleName();
        System.out.println("✅ 원소 1개: " + className);
        assertEquals("SingletonImmutableList", className);

        // SingletonImmutableList는 배열 없이 필드 하나만 사용
        try {
            Field elementField = single.getClass().getDeclaredField("element");
            elementField.setAccessible(true);
            Object element = elementField.get(single);
            assertEquals("hello", element);

            System.out.println("   내부 구조: element 필드만 존재 (배열 X)");
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }

    @Test
    void testRegularImmutableList() {
        ImmutableList<String> many = ImmutableList.of("a", "b", "c", "d");

        String className = many.getClass().getSimpleName();
        System.out.println("✅ 원소 2개 이상: " + className);
        assertEquals("RegularImmutableList", className);

        // RegularImmutableList는 배열 사용
        try {
            Field arrayField = many.getClass().getDeclaredField("array");
            arrayField.setAccessible(true);
            Object[] array = (Object[]) arrayField.get(many);

            assertEquals(4, array.length);
            assertEquals("a", array[0]);
            assertEquals("d", array[3]);

            System.out.println("   내부 구조: Object[] array 사용");
            System.out.println("   배열 크기: " + array.length);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }

    @Test
    void testBuilderCopyOnWrite() {
        // Builder는 build() 후에도 재사용 가능
        ImmutableList.Builder<String> builder = ImmutableList.<String>builderWithExpectedSize(3);
        builder.add("a").add("b").add("c");

        // 첫 번째 build()
        ImmutableList<String> list1 = builder.build();
        assertEquals(3, list1.size());
        assertEquals("[a, b, c]", list1.toString());

        // build() 후에도 add() 가능!
        builder.add("d");

        // 두 번째 build()
        ImmutableList<String> list2 = builder.build();
        assertEquals(4, list2.size());
        assertEquals("[a, b, c, d]", list2.toString());

        // 첫 번째 리스트는 영향 없음 (불변!)
        assertEquals(3, list1.size());
        assertEquals("[a, b, c]", list1.toString());

        System.out.println("✅ Builder 재사용 가능:");
        System.out.println("   첫 번째 build(): " + list1);
        System.out.println("   add('d') 후 build(): " + list2);
        System.out.println("   첫 번째 리스트: 여전히 " + list1 + " (불변!)");

        // 내부 동작 확인 (리플렉션)
        try {
            Field contentsField = builder.getClass().getDeclaredField("contents");
            contentsField.setAccessible(true);

            // 새 builder로 테스트
            ImmutableList.Builder<Integer> testBuilder =
                    ImmutableList.<Integer>builderWithExpectedSize(2);
            testBuilder.add(1).add(2);

            Object[] beforeBuild = (Object[]) contentsField.get(testBuilder);
            testBuilder.build();
            Object[] afterBuild = (Object[]) contentsField.get(testBuilder);

            // build() 전후 배열 동일 (copyOnWrite 최적화)
            assertSame(beforeBuild, afterBuild);
            System.out.println("\n💡 내부 최적화: build() 시 배열 복사 안 함 (지연 복사)");

            // add() 하면 복사
            testBuilder.add(3);
            Object[] afterAdd = (Object[]) contentsField.get(testBuilder);
            assertNotSame(afterBuild, afterAdd);
            System.out.println("   add() 시에만 복사 발생! (copyOnWrite 패턴)");

        } catch (NoSuchFieldException e) {
            System.out.println("\n💡 (내부 구조는 Guava 버전에 따라 다를 수 있음)");
        } catch (Exception e) {
            System.out.println("\n💡 내부 동작 확인 실패 (동작은 정상): " + e.getMessage());
        }
    }

    @Test
    void testCopyOfOptimization() {
        // 이미 ImmutableList면 복사 안 함
        ImmutableList<String> original = ImmutableList.of("a", "b", "c");
        ImmutableList<String> copy1 = ImmutableList.copyOf(original);

        assertSame(original, copy1);
        System.out.println("✅ copyOf(ImmutableList) → 복사 안 함");

        // ArrayList는 복사함
        ArrayList<String> mutable = new ArrayList<>(Arrays.asList("a", "b", "c"));
        ImmutableList<String> copy2 = ImmutableList.copyOf(mutable);

        assertNotSame(mutable, copy2);
        System.out.println("✅ copyOf(ArrayList) → 복사함");

        // 복사 후 원본 변경해도 영향 없음
        mutable.add("d");
        assertEquals(3, copy2.size());
        System.out.println("   원본 변경 후에도 영향 없음");
    }

    @Test
    void testSubListSharing() {
        ImmutableList<Integer> original = ImmutableList.of(1, 2, 3, 4, 5);
        ImmutableList<Integer> sub = original.subList(1, 4);

        assertEquals(3, sub.size());
        assertEquals(2, sub.get(0));

        // subList도 ImmutableList 타입
        assertTrue(sub instanceof ImmutableList);

        String subClassName = sub.getClass().getSimpleName();
        System.out.println("✅ subList 타입: " + subClassName);
        System.out.println("   원본: " + original);
        System.out.println("   subList: " + sub);

        // subList도 불변
        assertThrows(UnsupportedOperationException.class, () -> {
            sub.add(10);
        });
    }

    @Test
    void testMemoryEfficiency() {
        // 정확한 크기의 배열 사용 확인
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        try {
            Field arrayField = list.getClass().getDeclaredField("array");
            arrayField.setAccessible(true);
            Object[] array = (Object[]) arrayField.get(list);

            // 딱 필요한 크기만!
            assertEquals(3, array.length);
            System.out.println("✅ 정확한 크기의 배열 사용 (낭비 없음)");
            System.out.println("   원소 개수: 3, 배열 크기: " + array.length);

        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }

        // ArrayList는 여유 공간을 둠
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("a");
        arrayList.add("b");
        arrayList.add("c");

        System.out.println("\n   비교: ArrayList");
        System.out.println("   원소 개수: " + arrayList.size());

        // ArrayList의 내부 용량은 size()보다 큼 (보통 10 또는 15)
        try {
            Field elementDataField = ArrayList.class.getDeclaredField("elementData");
            elementDataField.setAccessible(true);
            Object[] elementData = (Object[]) elementDataField.get(arrayList);
            System.out.println("   내부 배열 크기: " + elementData.length + " (여유 공간 있음)");
        } catch (Exception e) {
            System.out.println("   (내부 구조 확인 실패)");
        }
    }

    @Test
    void testBuilderCapacityGrowth() {
        ImmutableList.Builder<Integer> builder = ImmutableList.builder();

        // 초기 용량 확인
        try {
            Field contentsField = builder.getClass().getDeclaredField("contents");
            contentsField.setAccessible(true);

            Object[] initialArray = (Object[]) contentsField.get(builder);
            int initialCapacity = initialArray.length;
            System.out.println("✅ Builder 초기 용량: " + initialCapacity);

            // 초기 용량을 넘어서 추가
            for (int i = 0; i < initialCapacity + 5; i++) {
                builder.add(i);
            }

            Object[] grownArray = (Object[]) contentsField.get(builder);
            int grownCapacity = grownArray.length;
            System.out.println("   확장 후 용량: " + grownCapacity);
            System.out.println("   확장 비율: " + String.format("%.2f", (double)grownCapacity / initialCapacity) + "x");

        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }

    @Test
    void testBuilderWithExpectedSize() {
        // 예상 크기를 지정하면 재할당 최소화
        int expectedSize = 100;
        ImmutableList.Builder<Integer> builder =
                ImmutableList.builderWithExpectedSize(expectedSize);

        try {
            Field contentsField = builder.getClass().getDeclaredField("contents");
            contentsField.setAccessible(true);

            Object[] array = (Object[]) contentsField.get(builder);
            System.out.println("✅ builderWithExpectedSize(" + expectedSize + ")");
            System.out.println("   초기 용량: " + array.length);

            // 예상 크기만큼 추가
            for (int i = 0; i < expectedSize; i++) {
                builder.add(i);
            }

            Object[] arrayAfter = (Object[]) contentsField.get(builder);

            if (array == arrayAfter) {
                System.out.println("   재할당 없음! (효율적)");
            } else {
                System.out.println("   재할당 발생: " + arrayAfter.length);
            }

        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }

    @Test
    void testInternalStructureSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 ImmutableList 내부 구조 요약");
        System.out.println("=".repeat(60));

        System.out.println("\n1. 원소 개수별 최적화:");
        System.out.println("   - 0개: RegularImmutableList.EMPTY (싱글톤)");
        System.out.println("   - 1개: SingletonImmutableList (필드만 사용)");
        System.out.println("   - 2개 이상: RegularImmutableList (배열 사용)");

        System.out.println("\n2. 메모리 효율:");
        System.out.println("   - 정확한 크기의 배열 (낭비 없음)");
        System.out.println("   - copyOf() 시 이미 ImmutableList면 그대로 반환");

        System.out.println("\n3. Builder 최적화:");
        System.out.println("   - copyOnWrite: build() 후 첫 add()에서만 복사");
        System.out.println("   - builderWithExpectedSize: 재할당 최소화");

        System.out.println("\n4. 불변성 보장:");
        System.out.println("   - 방어적 복사: 원본과 완전 독립");
        System.out.println("   - null 불가: 생성 시점에 체크");
        System.out.println("=".repeat(60));
    }
}