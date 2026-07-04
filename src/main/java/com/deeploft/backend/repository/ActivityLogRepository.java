package com.deeploft.backend.repository;

import com.deeploft.backend.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByInstructorNameOrderByTimestampDesc(String instructorName);
}