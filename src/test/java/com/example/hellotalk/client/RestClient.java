package com.example.hellotalk.client;

import com.example.hellotalk.config.AppConfigProperties;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClient {

    private final AppConfigProperties appConfigProperties;

    public RequestSpecification buildRequestHeader() {
        return RestAssured.given().header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .baseUri(Objects.requireNonNull(appConfigProperties).getBaseUrl())
                .config(RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()));
    }

    public Response getRequest(String basePath) {
        return buildRequestHeader().basePath(basePath).log().all().get();
    }
}
