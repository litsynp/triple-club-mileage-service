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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litsynp.mileageservice.domain.Photo;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.dto.request.ReviewEventCreateRequestDto;
import com.litsynp.mileageservice.dto.request.ReviewEventDeleteRequestDto;
import com.litsynp.mileageservice.dto.request.ReviewEventUpdateRequestDto;
import com.litsynp.mileageservice.dto.response.ReviewEventCreateResponseDto;
import com.litsynp.mileageservice.dto.response.ReviewEventUpdateResponseDto;
import com.litsynp.mileageservice.dto.service.ReviewCreateServiceDto;
import com.litsynp.mileageservice.dto.service.ReviewUpdateServiceDto;
import com.litsynp.mileageservice.service.ReviewService;
import java.util.Map;
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

@WebMvcTest(EventApiController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(HttpEncodingAutoConfiguration.class)
@DisplayName("이벤트 API")
class EventApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 작성 이벤트 - 201 CREATED")
    void writeReview() throws Exception {
        // given
        UUID userId = UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e");
        Place place = new Place(UUID.fromString("6c20dbf8-40a9-4dd0-bdab-ac490e960e38"),
                "Place 1");

        UUID reviewId = UUID.fromString("92dd8f6c-25ef-46ff-944b-4401ecd09e17");
        Review expected = Review.builder()
                .id(reviewId)
                .userId(userId)
                .place(place)
                .content("좋아요!")
                .build();

        Set<Photo> attachedPhotos = Set.of(
                new Photo(UUID.fromString("bc1735d7-c18c-4eee-96a6-74349739a7fc"),
                        "photo1", "https://photo.example.com/photo1"),
                new Photo(UUID.fromString("a183cb60-b5e4-4af1-989d-64bc600ebac5"),
                        "photo2", "https://photo.example.com/photo2")
        );
        attachedPhotos.forEach(expected::addPhoto);
        Set<UUID> attachedPhotoIds = attachedPhotos.stream()
                .map(Photo::getId)
                .collect(Collectors.toSet());

        ReviewEventCreateRequestDto request = ReviewEventCreateRequestDto.builder()
                .type("REVIEW")
                .action("ADD")
                .reviewId(reviewId)
                .userId(userId)
                .placeId(place.getId())
                .attachedPhotoIds(attachedPhotoIds)
                .content("좋아요!")
                .build();

        ReviewEventCreateResponseDto response = ReviewEventCreateResponseDto.builder()
                .id(reviewId)
                .userId(userId)
                .placeId(place.getId())
                .attachedPhotoIds(attachedPhotoIds)
                .content("좋아요!")
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

    @Test
    @DisplayName("리뷰 수정 이벤트 - 200 OK")
    void updateReview() throws Exception {
        // given
        UUID userId = UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e");
        Place place = new Place(UUID.fromString("6c20dbf8-40a9-4dd0-bdab-ac490e960e38"),
                "Place 1");

        UUID reviewId = UUID.fromString("92dd8f6c-25ef-46ff-944b-4401ecd09e17");
        Review existing = Review.builder()
                .id(reviewId)
                .userId(userId)
                .place(place)
                .content("좋아요!")
                .build();

        Set<Photo> attachedPhotos = Set.of(
                new Photo(UUID.fromString("bc1735d7-c18c-4eee-96a6-74349739a7fc"),
                        "photo1", "https://photo.example.com/photo1"),
                new Photo(UUID.fromString("a183cb60-b5e4-4af1-989d-64bc600ebac5"),
                        "photo2", "https://photo.example.com/photo2")
        );
        attachedPhotos.forEach(existing::addPhoto);

        Review updated = Review.builder()
                .id(reviewId)
                .userId(userId)
                .place(place)
                .content("좋아요!")
                .build();
        Set<Photo> updatedAttachedPhotos = Set.of(
                new Photo(UUID.fromString("bc1735d7-c18c-4eee-96a6-74349739a7fc"),
                        "photo1", "https://photo.example.com/photo1"),
                new Photo(UUID.fromString("a183cb60-b5e4-4af1-989d-64bc600ebac5"),
                        "photo2", "https://photo.example.com/photo2"),
                new Photo(UUID.fromString("a183cb60-b5e4-4af1-989d-64bc600ebac5"),
                        "photo2", "https://photo.example.com/photo3")
        );
        updatedAttachedPhotos.forEach(updated::addPhoto);
        Set<UUID> updatedAttachedPhotoIds = updatedAttachedPhotos.stream()
                .map(Photo::getId)
                .collect(Collectors.toSet());

        ReviewEventUpdateRequestDto request = ReviewEventUpdateRequestDto.builder()
                .type("REVIEW")
                .action("MOD")
                .reviewId(reviewId)
                .attachedPhotoIds(updatedAttachedPhotoIds)
                .content("좋아요!")
                .build();

        ReviewEventUpdateResponseDto response = ReviewEventUpdateResponseDto.builder()
                .id(reviewId)
                .userId(userId)
                .placeId(place.getId())
                .attachedPhotoIds(updatedAttachedPhotoIds)
                .content("좋아요!")
                .build();

        given(reviewService.updateReview(eq(reviewId), any(ReviewUpdateServiceDto.class)))
                .willReturn(updated);

        // when & then
        mockMvc.perform(post("/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
                .andDo(document("event-update-review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("이벤트 타입"),
                                fieldWithPath("action").type(JsonFieldType.STRING)
                                        .description("이벤트 액션"),
                                fieldWithPath("reviewId").type(JsonFieldType.STRING)
                                        .description("리뷰 ID"),
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

    @Test
    @DisplayName("리뷰 삭제 이벤트 - 204 No Content")
    void deleteReview() throws Exception {
        // given
        UUID userId = UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e");
        Place place = new Place(UUID.fromString("6c20dbf8-40a9-4dd0-bdab-ac490e960e38"),
                "Place 1");

        UUID reviewId = UUID.fromString("92dd8f6c-25ef-46ff-944b-4401ecd09e17");
        Review review = Review.builder()
                .id(reviewId)
                .userId(userId)
                .place(place)
                .content("좋아요!")
                .build();

        Set<Photo> attachedPhotos = Set.of(
                new Photo(UUID.fromString("bc1735d7-c18c-4eee-96a6-74349739a7fc"),
                        "photo1", "https://photo.example.com/photo1"),
                new Photo(UUID.fromString("a183cb60-b5e4-4af1-989d-64bc600ebac5"),
                        "photo2", "https://photo.example.com/photo2")
        );
        attachedPhotos.forEach(review::addPhoto);

        ReviewEventDeleteRequestDto request = ReviewEventDeleteRequestDto.builder()
                .type("REVIEW")
                .action("DELETE")
                .reviewId(reviewId)
                .build();

        willDoNothing()
                .given(reviewService).deleteReviewById(eq(reviewId));

        // when & then
        mockMvc.perform(post("/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(document("event-delete-review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("이벤트 타입"),
                                fieldWithPath("action").type(JsonFieldType.STRING)
                                        .description("이벤트 액션"),
                                fieldWithPath("reviewId").type(JsonFieldType.STRING)
                                        .description("리뷰 ID"))));
    }

    @Test
    @DisplayName("리뷰 이벤트 - 정의되지 않은 이벤트 타입 보내기 - 400")
    void sendTypeNotInReviewType() throws Exception {
        // given
        UUID userId = UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e");
        Place place = new Place(UUID.fromString("6c20dbf8-40a9-4dd0-bdab-ac490e960e38"),
                "Place 1");

        UUID reviewId = UUID.fromString("92dd8f6c-25ef-46ff-944b-4401ecd09e17");
        Review review = Review.builder()
                .id(reviewId)
                .userId(userId)
                .place(place)
                .content("좋아요!")
                .build();

        // 정의되지 않은 이벤트 타입 전송
        Map<String, ?> requestContent = Map.of(
                "type", "NOT_DEFINED", // Undefined type
                "action", "ADD",
                "reviewId", review.getId().toString(),
                "userId", userId.toString(),
                "placeId", place.getId().toString(),
                "attachedPhotoIds", new String[0],
                "content", "좋아요!"
        );

        // when & then
        mockMvc.perform(post("/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestContent)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 이벤트 - 정의되지 않은 리뷰 액션 보내기 - 400")
    void sendActionNotInReviewAction() throws Exception {
        // given
        UUID userId = UUID.fromString("8af7030a-6639-49e3-95de-fd56e2039d8e");
        Place place = new Place(UUID.fromString("6c20dbf8-40a9-4dd0-bdab-ac490e960e38"),
                "Place 1");

        UUID reviewId = UUID.fromString("92dd8f6c-25ef-46ff-944b-4401ecd09e17");
        Review review = Review.builder()
                .id(reviewId)
                .userId(userId)
                .place(place)
                .content("좋아요!")
                .build();

        // 정의되지 않은 이벤트 타입 전송
        Map<String, ?> requestContent = Map.of(
                "type", "REVIEW",
                "action", "NOT_DEFINED", // Undefined type
                "reviewId", review.getId().toString(),
                "userId", userId.toString(),
                "placeId", place.getId().toString(),
                "attachedPhotoIds", new String[0],
                "content", "좋아요!"
        );

        // when & then
        mockMvc.perform(post("/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestContent)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
