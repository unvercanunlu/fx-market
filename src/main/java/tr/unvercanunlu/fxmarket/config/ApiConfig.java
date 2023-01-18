package tr.unvercanunlu.fxmarket.config;

public class ApiConfig {

    public static final String VERSION = "v1";

    public static final String BASE_URL = "/api/" + VERSION;

    public static final String PRICE_API = BASE_URL + "/prices";

    private ApiConfig() {
    }
}
