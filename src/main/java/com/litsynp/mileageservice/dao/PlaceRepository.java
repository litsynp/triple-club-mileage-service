package com.litsynp.mileageservice.dao;

import com.litsynp.mileageservice.domain.Place;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, UUID> {

}
