package com.itgate.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itgate.ecommerce.models.Coureur;

public interface CoureurRepository extends JpaRepository<Coureur,Long> {
}
