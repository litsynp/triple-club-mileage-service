package com.litsynp.mileageservice.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litsynp.mileageservice.domain.Photo;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.dto.ReviewCreateServiceDto;
import com.litsynp.mileageservice.dto.ReviewEventCreateRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventCreateResponseDto;
import com.litsynp.mileageservice.dto.ReviewEventDeleteRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventUpdateRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventUpdateResponseDto;
import com.litsynp.mileageservice.dto.ReviewUpdateServiceDto;
import com.litsynp.mileageservice.dto.UserPointResponseDto;
import com.litsynp.mileageservice.service.ReviewService;
import com.litsynp.mileageservice.service.UserPointService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
@DisplayName("포인트 API")
class PointApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserPointService userPointService;

    @Test
    @DisplayName("Get user points - 200 OK")
    void writeReview() throws Exception {
        // given
        User user = new User(UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e"),
                "test@example.com", "12345678");

        UserPointResponseDto response = UserPointResponseDto.builder()
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
                                        .description("사용자 ID")),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.STRING)
                                        .description("사용자 ID"),
                                fieldWithPath("points").type(JsonFieldType.NUMBER)
                                        .description("포인트 총점"))));
    }
}
