package org.example;

import static com.google.common.base.Preconditions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PreconditionsBasicTest {

    @Test
    void testCheckArgument() {
        // 성공 케이스
        assertDoesNotThrow(() -> checkArgument(5 > 0));

        // 실패 케이스
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> checkArgument(5 < 0, "Value must be positive")
        );
        assertEquals("Value must be positive", ex.getMessage());
    }

    @Test
    void testCheckState() {
        // 상태 검증 예제
        class Connection {
            private boolean connected = false;

            void send(String data) {
                checkState(connected, "Not connected");
                // send logic...
            }
        }

        Connection conn = new Connection();
        assertThrows(IllegalStateException.class, () -> conn.send("data"));
    }

    @Test
    void testCheckNotNull() {
        String value = "test";
        assertSame(value, checkNotNull(value));

        assertThrows(NullPointerException.class,
                () -> checkNotNull(null, "Value cannot be null"));
    }

    @Test
    void testFormattedMessages() {
        // 포맷팅된 메시지
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> checkArgument(false, "User %s not found with id %s", "John", 123)
        );

        System.out.println("Exception message: " + ex.getMessage());
        // 예상: "User John not found with id 123"
    }
}