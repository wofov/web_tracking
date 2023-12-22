package com.web_tracking.repository;

import com.bellelanco_api.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminJwtRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByAdminId(String adminId);
}
