package com.litsynp.mileageservice.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.dto.response.UserPointTotalResponseDto;
import com.litsynp.mileageservice.service.UserPointService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointApiController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(HttpEncodingAutoConfiguration.class)
@DisplayName("????????? API")
class PointApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserPointService userPointService;

    @Test
    @DisplayName("????????? ????????? ?????? ?????? - 200 OK")
    void getTotalPoints() throws Exception {
        // given
        User user = new User(UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e"),
                "test@example.com", "12345678");

        UserPointTotalResponseDto response = UserPointTotalResponseDto.builder()
                .userId(user.getId())
                .points(3L)
                .build();

        given(userPointService.getUserPoints(user.getId()))
                .willReturn(3L);

        // when & then
        mockMvc.perform(get("/points")
                        .queryParam("user-id", user.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
                .andDo(document("user-total-points",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("user-id")
                                        .description("????????? ID")),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.STRING)
                                        .description("????????? ID"),
                                fieldWithPath("points").type(JsonFieldType.NUMBER)
                                        .description("????????? ??????"))));
    }
}
