package com.litsynp.mileageservice.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.dto.ReviewCreateServiceDto;
import com.litsynp.mileageservice.dto.ReviewEventCreateRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventCreateResponseDto;
import com.litsynp.mileageservice.service.ReviewService;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventApiController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DisplayName("이벤트 API")
class EventApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("Write review - 201 CREATED")
    void writeReview() throws Exception {
        // given
        User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
        Place place = new Place(UUID.randomUUID(), "Place 1");

        UUID reviewId = UUID.randomUUID();
        Review expected = Review.builder()
                .id(reviewId)
                .user(user)
                .place(place)
                .content("a")
                .build();

        ReviewEventCreateRequestDto request = ReviewEventCreateRequestDto.builder()
                .type("REVIEW")
                .action("ADD")
                .reviewId(reviewId)
                .userId(user.getId())
                .placeId(place.getId())
                .attachedPhotoIds(new HashSet<>())
                .content("a")
                .build();

        ReviewEventCreateResponseDto response = ReviewEventCreateResponseDto.builder()
                .id(reviewId)
                .userId(user.getId())
                .placeId(place.getId())
                .attachedPhotoIds(new HashSet<>())
                .content("a")
                .build();

        given(reviewService.writeReview(any(ReviewCreateServiceDto.class)))
                .willReturn(expected);

        // when & then
        mockMvc.perform(post("/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
                .andDo(document("event-write-review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("이벤트 타입"),
                                fieldWithPath("action").type(JsonFieldType.STRING)
                                        .description("이벤트 액션"),
                                fieldWithPath("reviewId").type(JsonFieldType.STRING)
                                        .description("리뷰 ID"),
                                fieldWithPath("userId").type(JsonFieldType.STRING)
                                        .description("리뷰의 작성자 ID"),
                                fieldWithPath("placeId").type(JsonFieldType.STRING)
                                        .description("리뷰가 작성된 장소의 ID"),
                                fieldWithPath("attachedPhotoIds[]").type(JsonFieldType.ARRAY)
                                        .description("리뷰에 첨부된 이미지들의 ID 배열"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("리뷰의 내용")),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING)
                                        .description("리뷰 ID"),
                                fieldWithPath("userId").type(JsonFieldType.STRING)
                                        .description("리뷰의 작성자 ID"),
                                fieldWithPath("placeId").type(JsonFieldType.STRING)
                                        .description("리뷰가 작성된 장소의 ID"),
                                fieldWithPath("attachedPhotoIds[]").type(JsonFieldType.ARRAY)
                                        .description("리뷰에 첨부된 이미지들의 ID 배열"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("리뷰의 내용"))));
    }
}
