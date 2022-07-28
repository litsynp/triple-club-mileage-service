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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Photo> attachedPhotos = new HashSet<>();

    @NotNull
    private String content;

    @Builder
    public Review(UUID id, UUID userId, Place place, String content) {
        this.id = id;
        this.userId = userId;
        this.place = place;
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

    public void update(UUID userId, Place place, String content) {
        this.userId = userId;
        this.place = place;
        this.content = content;
    }
}
