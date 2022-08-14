package com.universityadministration.repository;

import com.energymanagementsystem.ems.model.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface CompanyRepository extends CrudRepository<Company, Integer> {
}
