package tr.unvercanunlu.fxmarket.config;

import java.time.format.DateTimeFormatter;

public class FxMarketConfig {

    private FxMarketConfig() {
    }

    public static class RestApi {

        private static final String VERSION = "v1";

        private static final String BASE = "/api/" + VERSION;

        public static final String PRICE = BASE + "/prices";

        private RestApi() {
        }
    }

    public static class Date {

        public static final String FORMAT = "dd-MM-yyyy HH:mm:ss:SSS";

        public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Date.FORMAT);

        private Date() {
        }
    }

    public static class CsvParser {

        public static final String VALUE_SEPARATOR = ",";

        public static final String LINE_SEPARATOR = "\n";

        private CsvParser() {
        }
    }
}
