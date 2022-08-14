package com.universityadministration.repository;

import com.energymanagementsystem.ems.model.dataset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface DatasetRepository extends CrudRepository<dataset, Integer> {
}
