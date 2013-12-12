package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Journey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JourneyRepository extends PagingAndSortingRepository<Journey, Long> {
}
