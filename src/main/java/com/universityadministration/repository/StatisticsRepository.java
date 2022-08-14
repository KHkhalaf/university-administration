package com.universityadministration.repository;

import com.energymanagementsystem.ems.model.Statistics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface StatisticsRepository extends CrudRepository<Statistics, Integer> {
}
