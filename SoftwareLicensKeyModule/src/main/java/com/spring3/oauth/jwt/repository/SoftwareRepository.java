package com.spring3.oauth.jwt.repository;

import com.spring3.oauth.jwt.models.Softwares;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SoftwareRepository extends JpaRepository<Softwares, Long> {
}
