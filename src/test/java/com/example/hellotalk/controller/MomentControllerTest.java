package com.example.hellotalk.controller;

import capital.scalable.restdocs.AutoDocumentation;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.service.moment.MomentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MomentController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MomentControllerTest {

    private UUID momentId;
    private Moment momentRequest;
    private Moment momentResponse;
    private String jsonRequest;
    private String jsonResponse;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MomentService momentService;

    @BeforeAll
    void initData() {
        momentId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        momentRequest = Moment.builder()
                .text("anyText")
                .build();

        jsonRequest = jsonStringFromObject(momentRequest);
        momentResponse = convertToNewObject(momentRequest, Moment.class);
        momentResponse.setId(momentId);

        jsonResponse = jsonStringFromObject(momentResponse);
    }

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
                        .and().snippets()
                        .withDefaults(CliDocumentation.curlRequest(),
                                HttpDocumentation.httpRequest(),
                                HttpDocumentation.httpResponse(),
                                AutoDocumentation.requestFields(),
                                AutoDocumentation.responseFields(),
                                AutoDocumentation.pathParameters(),
                                AutoDocumentation.requestParameters(),
                                AutoDocumentation.description(),
                                AutoDocumentation.methodAndPath(),
                                AutoDocumentation.section()))
                .alwaysDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    void testGetMoment() throws Exception {

        Moment moment = this.momentResponse;
        when(momentService.getMoment(momentId)).thenReturn(moment);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}", momentId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-moment",
                        resource("Get a moment's details")))
                .andReturn();
    }

    @Test
    void testGetAllMoments() throws Exception {

        Moment moment = this.momentResponse;
        when(momentService.getAllMoments()).thenReturn(List.of(moment));

        List<Moment> momentList = new ArrayList<>();
        momentList.add(momentResponse);

        String jsonResponse = jsonStringFromObject(momentList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-moments",
                        resource("Get a list of moments")))
                .andReturn();
    }

    @Test
    void testCreateMoment() throws Exception {

        Moment moment = momentResponse;
        when(momentService.createMoment(any())).thenReturn(moment);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/moments").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("create-moment",
                        resource("Create a moment")))
                .andReturn();
    }

    @Test
    void testUpdateMoment() throws Exception {

        Moment moment = momentResponse;
        when(momentService.updateMoment(any(), any())).thenReturn(moment);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}", momentId)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("update-moment",
                        resource("Update a moment's details")))
                .andReturn();
    }

    @Test
    void testUpdateMoment_ThrowsExceptionWhenUserDoesNotExist() throws Exception {

        when(momentService.updateMoment(any(), any())).thenThrow(new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        String jsonError = """
                {"message": "jsonError"}
                """;
        jsonError = jsonError.replace("jsonError", MOMENT_NOT_FOUND_EXCEPTION);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}", momentId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("momentId", String.valueOf(momentId)))
                .andExpect(status().is(404))
                .andExpect(content().json(jsonError))
                .andDo(document("update-moment-throws-exception",
                        resource("Updating a moment's details throws exception when moment does not exist")))
                .andReturn();
    }

    @Test
    void testDeleteMoment() throws Exception {


        String json = """
                {"message": "Moment Deleted"}
                """;
        Moment moment = momentResponse;
        when(momentService.getMoment(any())).thenReturn(moment);
        when(momentService.deleteMoment(any())).thenReturn(json);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/moments/{momentId}", momentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("momentId", String.valueOf(momentId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(json))
                .andDo(document("delete-moment",
                        resource("Delete a moment")))
                .andReturn();
    }

    @Test
    void testDeleteMoment_ThrowsExceptionWhenMomentNotFound() throws Exception { //todo: to find out why this gets printed before the delete exception on swagger - 04/02/2023

        doThrow(new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION)).when(momentService).deleteMoment(any());

        String jsonError = """
                {"message": "jsonError"}
                """;
        jsonError = jsonError.replace("jsonError", MOMENT_NOT_FOUND_EXCEPTION);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/moments/{momentId}", momentId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json(jsonError))
                .andDo(document("delete-moment-throws-exception",
                        resource("Deleting a moment throws exception when moment does not exist")))
                .andReturn();
    }

}
