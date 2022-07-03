package com.litsynp.mileageservice.dao;

import java.util.UUID;

public interface UserPointQueryRepository {

    Long getUserPoints(UUID userId);
}
