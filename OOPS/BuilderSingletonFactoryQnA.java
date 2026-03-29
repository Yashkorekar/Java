package OOPS;

/**
 * Builder, Singleton, and Factory patterns (Q&A + runnable demos)
 *
 * Run:
 *   javac .\OOPS\BuilderSingletonFactoryQnA.java
 *   java OOPS.BuilderSingletonFactoryQnA
 */
public class BuilderSingletonFactoryQnA {

    static final class HttpRequest {
        private final String method;
        private final String url;
        private final int timeoutMs;
        private final boolean retryEnabled;

        private HttpRequest(Builder builder) {
            this.method = builder.method;
            this.url = builder.url;
            this.timeoutMs = builder.timeoutMs;
            this.retryEnabled = builder.retryEnabled;
        }

        @Override
        public String toString() {
            return "HttpRequest{method='" + method + "', url='" + url + "', timeoutMs=" + timeoutMs
                    + ", retryEnabled=" + retryEnabled + "}";
        }

        static final class Builder {
            private final String method;
            private final String url;
            private int timeoutMs = 1000;
            private boolean retryEnabled;

            Builder(String method, String url) {
                if (method == null || method.isBlank() || url == null || url.isBlank()) {
                    throw new IllegalArgumentException("method and url must not be blank");
                }
                this.method = method;
                this.url = url;
            }

            Builder timeoutMs(int timeoutMs) {
                this.timeoutMs = timeoutMs;
                return this;
            }

            Builder retryEnabled(boolean retryEnabled) {
                this.retryEnabled = retryEnabled;
                return this;
            }

            HttpRequest build() {
                return new HttpRequest(this);
            }
        }
    }

    static final class AppConfig {
        private AppConfig() {
        }

        static AppConfig getInstance() {
            return Holder.INSTANCE;
        }

        String mode() {
            return "prod";
        }

        private static final class Holder {
            private static final AppConfig INSTANCE = new AppConfig();
        }
    }

    interface Notification {
        String send(String message);
    }

    static final class EmailNotification implements Notification {
        @Override
        public String send(String message) {
            return "email => " + message;
        }
    }

    static final class SmsNotification implements Notification {
        @Override
        public String send(String message) {
            return "sms => " + message;
        }
    }

    static final class NotificationFactory {
        static Notification create(String type) {
            return switch (type.toLowerCase()) {
                case "email" -> new EmailNotification();
                case "sms" -> new SmsNotification();
                default -> throw new IllegalArgumentException("unknown type: " + type);
            };
        }
    }

    private static void builderDemo() {
        System.out.println("=== Builder pattern ===");
        HttpRequest request = new HttpRequest.Builder("GET", "https://api.example.com/users")
                .timeoutMs(3000)
                .retryEnabled(true)
                .build();
        System.out.println(request);
    }

    private static void singletonDemo() {
        System.out.println("\n=== Singleton pattern ===");
        AppConfig first = AppConfig.getInstance();
        AppConfig second = AppConfig.getInstance();
        System.out.println("same instance => " + (first == second));
        System.out.println("mode => " + first.mode());
    }

    private static void factoryDemo() {
        System.out.println("\n=== Factory pattern ===");
        Notification notification = NotificationFactory.create("email");
        System.out.println(notification.send("interview prep"));
    }

    public static void main(String[] args) {
        builderDemo();
        singletonDemo();
        factoryDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- Builder is great when many optional parameters exist.");
        System.out.println("- Singleton gives one shared instance, but overuse can create hidden global state.");
        System.out.println("- Factory centralizes object creation and hides concrete class choice.");
    }
}