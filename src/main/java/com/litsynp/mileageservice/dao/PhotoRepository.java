package com.litsynp.mileageservice.dao;

import com.litsynp.mileageservice.domain.Photo;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

}
