package com.web_tracking.repository;

import com.bellelanco_api.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Admin findByAdminId(String adminId);
}
