package com.litsynp.mileageservice.api;

import static com.litsynp.mileageservice.test.util.ResponseFieldDescriptorUtils.withPageDescriptorsIgnored;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.litsynp.mileageservice.test.util.FieldUtils;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.domain.UserPoint;
import com.litsynp.mileageservice.dto.response.UserPointHistoryResponseDto;
import com.litsynp.mileageservice.global.config.JacksonConfig;
import com.litsynp.mileageservice.global.config.WebConfig;
import com.litsynp.mileageservice.service.UserPointService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointHistoryApiController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import({HttpEncodingAutoConfiguration.class, WebConfig.class, JacksonConfig.class})
@DisplayName("포인트 기록 API")
class PointHistoryApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserPointService userPointService;

    @Test
    @DisplayName("사용자 포인트 기록 조회 - 200 OK")
    void getTotalPoints() throws Exception {
        // given
        User user = new User(UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e"),
                "test@example.com", "12345678");

        List<UserPoint> content = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Place place = new Place(
                    UUID.fromString("eaa5b92d-45e1-443d-938f-eb30f24ebcc9"), "장소 " + i);
            Review review = new Review(
                    UUID.fromString("abcee424-e5b2-4fe5-a08d-38461a808c07"), user, place, "좋아요!");

            // 포인트 생성 후 Reflection으로 날짜 수정
            UserPoint userPoint = new UserPoint(
                    UUID.fromString("d674fedf-0ba1-4a10-943f-905f1f30674b"), user, review, 1L);
            FieldUtils.writeSuperField(userPoint, "createdOn",
                    LocalDateTime.of(LocalDate.of(2022, 7, 10), LocalTime.of(10, 0)));

            content.add(userPoint);
        }

        // Response content 생성
        Page<UserPoint> pageResponse = new PageImpl<>(content, PageRequest.of(0, 10),
                content.size());

        Page<UserPointHistoryResponseDto> response = pageResponse.map(
                UserPointHistoryResponseDto::from);

        given(userPointService.search(any(), eq(user.getId()), eq(null)))
                .willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/point-histories")
                        .queryParam("user-id", user.getId().toString())
                        .queryParam("review-id", "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
                .andDo(document("user-point-history",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("user-id")
                                        .description("사용자 ID"),
                                parameterWithName("review-id")
                                        .description("리뷰 ID")),
                        responseFields(
                                withPageDescriptorsIgnored(
                                        fieldWithPath("content.[].id")
                                                .type(JsonFieldType.STRING)
                                                .description("기록 ID"),
                                        fieldWithPath("content.[].userId")
                                                .type(JsonFieldType.STRING)
                                                .description("사용자 ID"),
                                        fieldWithPath("content.[].reviewId")
                                                .type(JsonFieldType.STRING)
                                                .description("리뷰 ID"),
                                        fieldWithPath("content.[].amount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("점수"),
                                        fieldWithPath("content.[].createdOn")
                                                .type(JsonFieldType.STRING)
                                                .description("생성 시각")
                                )
                        )));
    }
}
