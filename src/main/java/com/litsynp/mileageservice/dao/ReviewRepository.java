package com.litsynp.mileageservice.dao;

import com.litsynp.mileageservice.domain.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

}