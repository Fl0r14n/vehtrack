package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Fleet;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FleetRepository extends PagingAndSortingRepository<Fleet, Long> {
}
