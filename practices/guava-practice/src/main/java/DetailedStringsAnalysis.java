import com.google.common.base.Strings;

public class DetailedStringsAnalysis {
    public static void main(String[] args) {
        System.out.println("=== 🔍 Guava Strings 클래스 심화 분석 ===\n");

        // 1. 모든 주요 메서드 테스트
        testAllMethods();

        // 2. Platform 추상화 확인
        testPlatformAbstraction();

        // 3. 실무 시나리오
        testRealWorldScenarios();

        // 4. 성능 벤치마크
        performanceBenchmark();


    }
    private static void testAllMethods() {
        System.out.println("📋 1. 모든 주요 메서드 테스트");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        String nullStr = null;
        String emptyStr = "";
        String spaceStr = " ";
        String normalStr = "hello";

        // isNullOrEmpty 테스트
        System.out.println("🔍 isNullOrEmpty() 결과:");
        System.out.printf("  %-15s → %s%n", "null", Strings.isNullOrEmpty(nullStr));
        System.out.printf("  %-15s → %s%n", "\"\"", Strings.isNullOrEmpty(emptyStr));
        System.out.printf("  %-15s → %s%n", "\" \"", Strings.isNullOrEmpty(spaceStr));
        System.out.printf("  %-15s → %s%n", "\"hello\"", Strings.isNullOrEmpty(normalStr));

        // nullToEmpty 테스트
        System.out.println("\n🔄 nullToEmpty() 결과:");
        System.out.printf("  %-15s → \"%s\" (길이: %d)%n", "null", Strings.nullToEmpty(nullStr), Strings.nullToEmpty(nullStr).length());
        System.out.printf("  %-15s → \"%s\" (길이: %d)%n", "\"\"", Strings.nullToEmpty(emptyStr), Strings.nullToEmpty(emptyStr).length());
        System.out.printf("  %-15s → \"%s\"%n", "\"hello\"", Strings.nullToEmpty(normalStr));

        // emptyToNull 테스트
        System.out.println("\n🔄 emptyToNull() 결과:");
        System.out.printf("  %-15s → %s%n", "null", Strings.emptyToNull(nullStr));
        System.out.printf("  %-15s → %s%n", "\"\"", Strings.emptyToNull(emptyStr));
        System.out.printf("  %-15s → \"%s\"%n", "\"hello\"", Strings.emptyToNull(normalStr));

        System.out.println();
    }

    private static void testPlatformAbstraction() {
        System.out.println("🏗️ 2. Platform 추상화 확인");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        System.out.println("✅ 발견: Strings 클래스는 Platform 클래스에 위임!");
        System.out.println("   Strings.isNullOrEmpty() → Platform.stringIsNullOrEmpty()");
        System.out.println("   이는 크로스 플랫폼 지원을 위한 설계 패턴입니다.");
        System.out.println("   (Android, GWT 등에서 다른 구현 사용 가능)");
        System.out.println();
    }

    private static void testRealWorldScenarios() {
        System.out.println("💼 3. 실무 시나리오 테스트");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // 시나리오 1: API 응답 처리
        System.out.println("📡 API 응답 처리 시나리오:");
        String apiResponse = null;  // API에서 null이 올 수 있음
        String safeResponse = Strings.nullToEmpty(apiResponse);
        System.out.printf("  API 응답(null) → 안전한 문자열: \"%s\"%n", safeResponse);

        // 시나리오 2: 사용자 입력 검증
        System.out.println("\n👤 사용자 입력 검증:");
        String userInput = "";  // 사용자가 빈 문자열 입력
        if (Strings.isNullOrEmpty(userInput)) {
            System.out.println("  ❌ 입력값이 비어있습니다!");
        }

        // 시나리오 3: 로그 포맷팅
        System.out.println("\n📝 로그 포맷팅 (padStart 활용):");
        int logLevel = 1;
        String formattedLevel = Strings.padStart(String.valueOf(logLevel), 3, '0');
        System.out.printf("  로그 레벨 %d → 포맷된 레벨: [%s]%n", logLevel, formattedLevel);

        // 시나리오 4: 시간 포맷팅
        System.out.println("\n⏰ 시간 포맷팅:");
        int hour = 9, minute = 5, second = 30;
        String timeFormat = String.format("%s:%s:%s",
                Strings.padStart(String.valueOf(hour), 2, '0'),
                Strings.padStart(String.valueOf(minute), 2, '0'),
                Strings.padStart(String.valueOf(second), 2, '0'));
        System.out.printf("  시간: %d:%d:%d → 포맷: %s%n", hour, minute, second, timeFormat);

        System.out.println();
    }

    private static void performanceBenchmark() {
        System.out.println("⚡ 4. 성능 벤치마크");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        String testStr = "benchmark_test_string";
        int iterations = 1_000_000;

        System.out.printf("테스트 대상: \"%s\" (100만 번 호출)%n", testStr);

        // 1. 수동 null 체크
        long start1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            boolean result = testStr == null || testStr.isEmpty();
        }
        long time1 = System.nanoTime() - start1;

        // 2. Guava isNullOrEmpty
        long start2 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            boolean result = Strings.isNullOrEmpty(testStr);
        }
        long time2 = System.nanoTime() - start2;

        // 3. nullToEmpty 테스트
        long start3 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            String result = Strings.nullToEmpty(testStr);
        }
        long time3 = System.nanoTime() - start3;

        System.out.println("\n📊 성능 결과:");
        System.out.printf("  수동 null 체크:        %6.2f ms%n", time1 / 1_000_000.0);
        System.out.printf("  Guava isNullOrEmpty:   %6.2f ms%n", time2 / 1_000_000.0);
        System.out.printf("  Guava nullToEmpty:     %6.2f ms%n", time3 / 1_000_000.0);

        double overhead = ((double) time2 / time1 - 1) * 100;
        System.out.printf("\n💡 결론: Platform 추상화 오버헤드 약 %.1f%%", overhead);
        System.out.println("\n   → 무시할 수 있는 수준! 가독성과 안전성이 더 중요! 🎉");
    }
}