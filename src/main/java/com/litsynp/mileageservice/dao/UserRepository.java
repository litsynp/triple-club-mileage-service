package com.litsynp.mileageservice.dao;

import com.litsynp.mileageservice.domain.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

}
