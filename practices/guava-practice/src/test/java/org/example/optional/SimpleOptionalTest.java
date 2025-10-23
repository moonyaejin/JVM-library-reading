package org.example.optional;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class SimpleOptionalTest {

    public static void main(String[] args) {
        System.out.println("=== SimpleOptional 테스트 시작 ===\n");

        testAbsentSingleton();
        testFromNullable();
        testOfNullRejection();
        testOr();
        testOrSupplier();
        testTransform();
        testTransformChaining();
        testTransformReturnsNull();
        testOrNull();
        testEquality();

        System.out.println("\n=== 모든 테스트 통과! ===");
    }

    static void testAbsentSingleton() {
        System.out.println("테스트 1: 싱글톤 검증");
        SimpleOptional<String> absent1 = SimpleOptional.absent();
        SimpleOptional<Integer> absent2 = SimpleOptional.absent();
        SimpleOptional<Object> absent3 = SimpleOptional.absent();

        // 타입이 다르므로 Object로 캐스팅해서 비교
        Object obj1 = absent1;
        Object obj2 = absent2;
        Object obj3 = absent3;

        assert obj1 == obj2 : "absent1과 absent2는 같은 인스턴스여야 함";
        assert obj2 == obj3 : "absent2와 absent3는 같은 인스턴스여야 함";

        System.out.println("  ✓ Absent 싱글톤 검증 완료\n");
    }

    static void testFromNullable() {
        System.out.println("테스트 2: fromNullable");
        SimpleOptional<String> present = SimpleOptional.fromNullable("hello");
        SimpleOptional<String> absent = SimpleOptional.fromNullable(null);

        assert present.isPresent() : "present는 값이 있어야 함";
        assert !absent.isPresent() : "absent는 값이 없어야 함";

        assert "hello".equals(present.get()) : "값이 hello여야 함";

        try {
            absent.get();
            assert false : "absent.get()은 예외를 던져야 함";
        } catch (IllegalStateException e) {
            // 예상된 예외
        }

        System.out.println("  ✓ fromNullable 검증 완료\n");
    }

    static void testOfNullRejection() {
        System.out.println("테스트 3: of는 null 거부");

        try {
            SimpleOptional.of(null);
            assert false : "of(null)은 예외를 던져야 함";
        } catch (NullPointerException e) {
            // 예상된 예외
        }

        System.out.println("  ✓ null 거부 검증 완료\n");
    }

    static void testOr() {
        System.out.println("테스트 4: or() 기본값");
        SimpleOptional<String> present = SimpleOptional.of("original");
        SimpleOptional<String> absent = SimpleOptional.absent();

        assert "original".equals(present.or("default")) : "present는 원래 값 반환";
        assert "default".equals(absent.or("default")) : "absent는 기본값 반환";

        System.out.println("  ✓ or() 검증 완료\n");
    }

    static void testOrSupplier() {
        System.out.println("테스트 5: or(Supplier) 지연 평가");

        final boolean[] supplierCalled = {false};

        Supplier<String> expensiveOperation = new Supplier<String>() {
            @Override
            public String get() {
                supplierCalled[0] = true;
                System.out.println("    → Supplier 실행됨 (비용이 큰 연산)");
                return "computed";
            }
        };

        // Present인 경우: Supplier 호출 안 됨
        System.out.println("  케이스 1: Present");
        SimpleOptional<String> present = SimpleOptional.of("exists");
        present.or(expensiveOperation);
        assert !supplierCalled[0] : "Present일 때 Supplier 호출 안 돼야 함";
        System.out.println("  ✓ Present: Supplier 호출 안 됨");

        // Absent인 경우: Supplier 호출됨
        System.out.println("\n  케이스 2: Absent");
        SimpleOptional<String> absent = SimpleOptional.absent();
        String result = absent.or(expensiveOperation);
        assert supplierCalled[0] : "Absent일 때 Supplier 호출돼야 함";
        assert "computed".equals(result) : "결과가 computed여야 함";
        System.out.println("  ✓ Absent: Supplier 호출됨\n");
    }

    static void testTransform() {
        System.out.println("테스트 6: transform()");

        Function<String, Integer> stringLength = new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return input.length();
            }
        };

        SimpleOptional<String> present = SimpleOptional.of("hello");
        SimpleOptional<Integer> transformed = present.transform(stringLength);

        assert transformed.isPresent() : "transform 결과는 present여야 함";
        assert transformed.get() == 5 : "길이가 5여야 함";

        // Absent를 transform해도 여전히 absent
        SimpleOptional<String> absent = SimpleOptional.absent();
        SimpleOptional<Integer> stillAbsent = absent.transform(stringLength);

        assert !stillAbsent.isPresent() : "absent를 transform하면 여전히 absent";

        System.out.println("  ✓ transform() 검증 완료\n");
    }

    static void testTransformChaining() {
        System.out.println("테스트 7: transform 체이닝");

        Function<String, Integer> toLength = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.length();
            }
        };

        Function<Integer, String> toHex = new Function<Integer, String>() {
            @Override
            public String apply(Integer i) {
                return Integer.toHexString(i);
            }
        };

        SimpleOptional<String> original = SimpleOptional.of("hello");
        SimpleOptional<String> result = original
                .transform(toLength)
                .transform(toHex);

        assert "5".equals(result.get()) : "결과가 '5'여야 함";
        System.out.println("  ✓ 체이닝: 'hello' → 5 → '5'\n");
    }

    static void testTransformReturnsNull() {
        System.out.println("테스트 8: transform이 null 반환");

        Function<String, String> returnsNull = new Function<String, String>() {
            @Override
            public String apply(String input) {
                return null;
            }
        };

        SimpleOptional<String> present = SimpleOptional.of("test");
        SimpleOptional<String> result = present.transform(returnsNull);

        assert !result.isPresent() : "null 반환 시 absent로 변환돼야 함";
        System.out.println("  ✓ transform이 null 반환 → absent\n");
    }

    static void testOrNull() {
        System.out.println("테스트 9: orNull()");

        assert "value".equals(SimpleOptional.of("value").orNull()) : "present는 값 반환";
        assert SimpleOptional.absent().orNull() == null : "absent는 null 반환";

        System.out.println("  ✓ orNull() 검증 완료\n");
    }

    static void testEquality() {
        System.out.println("테스트 10: equals & hashCode");

        SimpleOptional<String> opt1 = SimpleOptional.of("hello");
        SimpleOptional<String> opt2 = SimpleOptional.of("hello");
        SimpleOptional<String> opt3 = SimpleOptional.of("world");

        assert opt1.equals(opt2) : "같은 값은 equals여야 함";
        assert !opt1.equals(opt3) : "다른 값은 not equals여야 함";

        assert opt1.hashCode() == opt2.hashCode() : "같은 값은 같은 hashCode";

        System.out.println("  ✓ equals & hashCode 검증 완료\n");
    }
}