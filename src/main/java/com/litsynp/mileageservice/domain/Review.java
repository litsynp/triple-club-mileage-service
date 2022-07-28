package com.litsynp.mileageservice.domain;

import com.litsynp.mileageservice.global.domain.entity.BaseTimeEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID placeId;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Photo> attachedPhotos = new HashSet<>();

    @NotNull
    private String content;

    @Builder
    public Review(UUID id, UUID userId, UUID placeId, String content) {
        this.id = id;
        this.userId = userId;
        this.placeId = placeId;
        this.content = content;
    }

    public void addPhoto(Photo photo) {
        attachedPhotos.add(photo);
        photo.setReview(this);
    }

    public void deletePhoto(Photo photo) {
        attachedPhotos.remove(photo);
        photo.setReview(null);
    }

    public void update(UUID userId, UUID placeId, String content) {
        this.userId = userId;
        this.placeId = placeId;
        this.content = content;
    }
}
