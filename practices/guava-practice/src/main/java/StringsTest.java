import com.google.common.base.Strings;

public class StringsTest {
    public static void main(String[] args) {
        System.out.println("Guava 테스트 시작!");
        System.out.println("null 체크: " + Strings.isNullOrEmpty(null));
        System.out.println("빈 문자열: " + Strings.isNullOrEmpty(""));
        System.out.println("일반 문자열: " + Strings.isNullOrEmpty("hello"));
    }
}