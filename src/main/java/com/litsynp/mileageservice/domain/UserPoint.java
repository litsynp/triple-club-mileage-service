package com.litsynp.mileageservice.domain;

import com.litsynp.mileageservice.global.domain.entity.BaseTimeEntity;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint extends BaseTimeEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = true)
    @Setter
    private Review review;

    @NotNull
    private Long amount;

    @Builder
    public UserPoint(UUID id, UUID userId, Review review, Long amount) {
        this.id = id;
        this.userId = userId;
        this.review = review;
        this.amount = amount;
    }
}
