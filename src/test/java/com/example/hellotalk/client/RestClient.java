package com.example.hellotalk.client;

import com.example.hellotalk.config.Environment;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Slf4j
public class RestClient {

    private final Environment environment;

    @Autowired
    public RestClient(Environment environment) {
        this.environment = environment;
    }

    public RequestSpecification buildRequestHeader() {
        return RestAssured.given().header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .baseUri(Objects.requireNonNull(environment).getBaseUrl())
                .config(RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()));
    }

    public Response getRequest(String basePath) {
        return buildRequestHeader().basePath(basePath).log().all().get();
    }
}
