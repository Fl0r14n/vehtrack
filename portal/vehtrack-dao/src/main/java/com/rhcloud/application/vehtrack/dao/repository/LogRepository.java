package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Log;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LogRepository extends PagingAndSortingRepository<Log, Long> {
}
