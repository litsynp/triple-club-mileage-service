package com.litsynp.mileageservice.domain;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(name = "user_AK01", columnNames = {"email"})})
public class User {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    private String email;

    private String password;

    @Builder
    public User(UUID id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
}
