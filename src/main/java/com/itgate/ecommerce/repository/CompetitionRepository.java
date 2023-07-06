package com.itgate.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itgate.ecommerce.models.Competition;

public interface CompetitionRepository extends JpaRepository<Competition,Long> {
}
