package com.example.hellotalk.utils;

import au.com.dius.pact.core.model.RequestResponseInteraction;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.dzieciou.testing.curl.CurlRestAssuredConfigFactory;
import com.github.dzieciou.testing.curl.Options;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpRequest;

import java.lang.reflect.Type;
import java.util.Map;

import static com.example.hellotalk.config.Constants.MOCK_PACT_URL;
import static io.restassured.RestAssured.given;

@Slf4j
public class Utils {

    public static RequestSpecification getRequestSpecification() {

        /* Enables printing request as curl under the terminal as per https://github.com/dzieciou/curl-logger */
        Options options = Options.builder()
                .printMultiliner()
                .updateCurl(curl -> curl
                        .removeHeader("Host")
                        .removeHeader("User-Agent")
                        .removeHeader("Connection"))
                .build();

        RestAssuredConfig config = CurlRestAssuredConfigFactory.createConfig(options).objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                new Jackson2ObjectMapperFactory() {
                    @Override
                    public ObjectMapper create(Type type, String charset) {
                        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
                        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                        return om;
                    }

                }));

        return given()
                .config(config)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .urlEncodingEnabled(false)
                .when()
                .log()
                .everything();
    }

    public static void logCurlFromPact(PactVerificationContext context, HttpRequest request, String baseUri) {

        String bodyParam = ((RequestResponseInteraction) context.getInteraction()).getRequest().getBody().valueAsString();

        String bodyResponse = ((RequestResponseInteraction) context.getInteraction()).getResponse().getBody().valueAsString();

        String method = ((RequestResponseInteraction) context.getInteraction()).getRequest().getMethod();

        String url = baseUri + request.getPath();

        Header[] headers = request.getHeaders();

        String headersString = "";
        for (Header s : headers) {
            headersString = headersString + "--header " + "'" + s.getName() + ": " + s.getValue() + "'" + "\\" + "\n";
        }

        String curl = """
                curl --request $method $url $headersString
                --data-binary $bodyParam
                --compressed --insecure --verbose
                """;

        // log.debug(curl + "\n\n " + bodyResponse + "\n ---- \n\n");
        System.out.println((curl + "\n\n " + bodyResponse + "\n ---- \n\n"));
    }

    public static RequestSpecification getMockRequest(Map<String, String> headers) {
        return getRequestSpecification().baseUri(MOCK_PACT_URL).headers(headers);
    }
}
