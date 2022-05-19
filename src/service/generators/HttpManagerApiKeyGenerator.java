package service.generators;

public class HttpManagerApiKeyGenerator {
    public static String generate() {
        return "HttpManager:" + System.currentTimeMillis();
    }
}
