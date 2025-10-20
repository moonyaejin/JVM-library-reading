package org.example;

import static com.google.common.base.Preconditions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PreconditionsDeepDiveTest {

    @Test
    void testElementIndexVsPositionIndex() {
        int size = 5;

        // ElementIndex: 0 ~ 4만 허용
        assertDoesNotThrow(() -> checkElementIndex(4, size));
        assertThrows(IndexOutOfBoundsException.class,
                () -> checkElementIndex(5, size));

        // PositionIndex: 0 ~ 5 허용 (끝에 추가 가능)
        assertDoesNotThrow(() -> checkPositionIndex(5, size));
        assertThrows(IndexOutOfBoundsException.class,
                () -> checkPositionIndex(6, size));
    }

    @Test
    void testPositionIndexesForSubList() {
        int size = 10;

        // 정상 범위
        assertDoesNotThrow(() -> checkPositionIndexes(2, 7, size));

        // start > end
        IndexOutOfBoundsException ex1 = assertThrows(
                IndexOutOfBoundsException.class,
                () -> checkPositionIndexes(7, 2, size));
        System.out.println("Error: " + ex1.getMessage());

        // end > size
        IndexOutOfBoundsException ex2 = assertThrows(
                IndexOutOfBoundsException.class,
                () -> checkPositionIndexes(2, 11, size));
        System.out.println("Error: " + ex2.getMessage());
    }

    @Test
    void testAutoBoxingAvoidance() {
        // Primitive 타입으로 직접 전달
        int count = 42;
        long timestamp = System.currentTimeMillis();
        char initial = 'J';

        // 이 코드들은 모두 boxing 없이 실행됨 (성공 케이스)
        checkArgument(count > 0, "Count: %s", count);
        checkArgument(timestamp > 0, "Timestamp: %s", timestamp);
        checkArgument(initial != 0, "Initial: %s", initial);

        // 실패 케이스에서만 boxing 발생
        try {
            checkArgument(false, "Count: %s, Time: %s", count, timestamp);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception with primitives: " + e.getMessage());
        }
    }

    @Test
    void testErrorMessageQuality() {
        try {
            checkElementIndex(-1, 5, "userIndex");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Negative index: " + e.getMessage());
            assertTrue(e.getMessage().contains("must not be negative"));
        }

        try {
            checkElementIndex(10, 5, "userIndex");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Too large index: " + e.getMessage());
            assertTrue(e.getMessage().contains("must be less than size"));
        }
    }

    @Test
    void testCheckNotNullChaining() {
        // 반환값 활용
        String name = checkNotNull("John", "Name required");
        assertEquals("John", name);

        // 체이닝 패턴
        class User {
            private final String name;
            private final String email;

            User(String name, String email) {
                this.name = checkNotNull(name, "Name required");
                this.email = checkNotNull(email, "Email required");
            }
        }

        assertDoesNotThrow(() -> new User("John", "john@example.com"));
        assertThrows(NullPointerException.class,
                () -> new User(null, "john@example.com"));
    }

    @Test
    void testCheckArgumentVsCheckState() {
        class Counter {
            private int count = 0;
            private boolean started = false;

            void start() {
                checkState(!started, "Already started");
                started = true;
            }

            void increment(int delta) {
                checkArgument(delta > 0, "Delta must be positive: %s", delta);
                checkState(started, "Not started yet");
                count += delta;
            }
        }

        Counter counter = new Counter();

        // 상태 오류
        assertThrows(IllegalStateException.class,
                () -> counter.increment(5));

        counter.start();

        // 인자 오류
        assertThrows(IllegalArgumentException.class,
                () -> counter.increment(-5));

        // 정상 동작
        assertDoesNotThrow(() -> counter.increment(10));
    }

    @Test
    void measureAutoBoxingOverhead() {
        int iterations = 10_000_000;

        // 1. Primitive 타입 오버로딩 (boxing 없음)
        long start1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            checkArgument(true, "Value: %s", i);
        }
        long nativeTime = System.nanoTime() - start1;

        // 2. 강제로 Integer 사용 (boxing 발생)
        long start2 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            checkArgument(true, "Value: %s", Integer.valueOf(i));
        }
        long boxedTime = System.nanoTime() - start2;

        System.out.println("Native (no boxing): " + nativeTime / 1_000_000 + "ms");
        System.out.println("Boxed (with boxing): " + boxedTime / 1_000_000 + "ms");
        System.out.println("Overhead: " + ((boxedTime - nativeTime) * 100.0 / nativeTime) + "%");
    }

    @Test
    void measureAutoBoxingOverheadOnFailure() {
        int iterations = 100_000;  // 예외 발생하므로 횟수 줄임

        // 1. Primitive 타입 오버로딩 (실패 케이스)
        long start1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try {
                checkArgument(false, "Value: %s", i);
            } catch (IllegalArgumentException e) {
                // 예외 무시
            }
        }
        long nativeTime = System.nanoTime() - start1;

        // 2. 강제 Boxing (실패 케이스)
        long start2 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try {
                checkArgument(false, "Value: %s", Integer.valueOf(i));
            } catch (IllegalArgumentException e) {
                // 예외 무시
            }
        }
        long boxedTime = System.nanoTime() - start2;

        System.out.println("\n=== Failure Case Performance ===");
        System.out.println("Native (no boxing): " + nativeTime / 1_000_000 + "ms");
        System.out.println("Boxed (with boxing): " + boxedTime / 1_000_000 + "ms");
        System.out.println("Overhead: " + ((boxedTime - nativeTime) * 100.0 / nativeTime) + "%");
    }
}
