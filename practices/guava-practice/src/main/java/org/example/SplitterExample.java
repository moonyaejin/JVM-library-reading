package org.example;

import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Guava Splitter 클래스 분석
 *
 * 학습 목표:
 * 1. String.split()과의 차이점
 * 2. 지연 평가(lazy evaluation) 방식
 * 3. 복잡한 파싱 시나리오 처리
 */
public class SplitterExample {

    public static void main(String[] args) {
        basicSplitting();
        trimAndOmitEmpty();
        limitSplitting();
        mapSplitting();
        lazySplitting();
    }

    /**
     * 기본 분할: String.split()과 비교
     */
    private static void basicSplitting() {
        System.out.println("=== 기본 분할 ===");

        String data = "apple,banana,cherry";

        // Guava Splitter
        List<String> result1 = Splitter.on(',').splitToList(data);
        System.out.println("Splitter: " + result1);

        // JDK String.split()
        String[] result2 = data.split(",");
        System.out.println("String.split: " + Arrays.toString(result2));

        // 예상: 결과는 같지만 동작 방식이 다를 것
    }

    /**
     * 공백 제거와 빈 문자열 처리
     *
     * 핵심: String.split()의 예상치 못한 동작 개선
     */
    private static void trimAndOmitEmpty() {
        System.out.println("\n=== 공백 처리 ===");

        String messy = " apple , , banana ,  cherry  ";

        // trimResults: 각 항목의 앞뒤 공백 제거
        List<String> trimmed = Splitter.on(',')
                .trimResults()
                .splitToList(messy);
        System.out.println("trimResults: " + trimmed);

        // omitEmptyStrings: 빈 문자열 제거
        List<String> cleaned = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .splitToList(messy);
        System.out.println("omitEmptyStrings: " + cleaned);

        // String.split()과 비교
        String[] jdkResult = messy.split(",");
        System.out.println("String.split: " + Arrays.toString(jdkResult));
        // 문제: 공백이 포함되고 빈 문자열도 남음
    }

    /**
     * 분할 횟수 제한
     */
    private static void limitSplitting() {
        System.out.println("\n=== 분할 제한 ===");

        String log = "2025-01-17|INFO|User logged in|Extra data";

        // limit: 최대 분할 횟수 지정
        List<String> parts = Splitter.on('|')
                .limit(3)
                .splitToList(log);

        System.out.println("Parts: " + parts);
        // 예상 결과: [2025-01-17, INFO, User logged in|Extra data]
    }

    /**
     * MapSplitter: 키-값 파싱
     *
     * 예상: URL 쿼리 스트링, 설정 파일 파싱에 유용
     */
    private static void mapSplitting() {
        System.out.println("\n=== Map 분할 ===");

        String query = "name=John&age=30&city=Seoul";

        Map<String, String> params = Splitter.on('&')
                .withKeyValueSeparator('=')
                .split(query);

        System.out.println("Params: " + params);

        // 실전 활용: 설정 파일 파싱
        String config = "host=localhost;port=8080;timeout=3000";
        Map<String, String> settings = Splitter.on(';')
                .withKeyValueSeparator('=')
                .split(config);
        System.out.println("Settings: " + settings);
    }

    /**
     * 지연 평가(Lazy Splitting)
     *
     * 핵심: split()은 Iterable 반환, splitToList()는 즉시 평가
     */
    private static void lazySplitting() {
        System.out.println("\n=== 지연 평가 ===");

        String huge = "a,b,c,d,e,f,g,h,i,j"; // 실제로는 매우 큰 데이터 가정

        // split(): Iterator를 반환 (지연 평가)
        Iterable<String> lazy = Splitter.on(',').split(huge);
        System.out.println("Lazy split 완료 (아직 실제 분할 안 됨)");

        // 첫 3개만 필요한 경우
        int count = 0;
        for (String item : lazy) {
            System.out.println("Item: " + item);
            if (++count == 3) break;
        }

        // splitToList(): 즉시 전체를 List로 변환
        List<String> eager = Splitter.on(',').splitToList(huge);
        System.out.println("Eager split: " + eager.size() + " items");

        // 성능 비교 포인트: 대용량 데이터에서 차이가 클 것
    }
}