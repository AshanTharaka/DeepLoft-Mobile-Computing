package com.deeploft.backend.repository;

import com.deeploft.backend.model.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    Optional<StudyPlan> findByUserEmailAndCourseTitle(String userEmail, String courseTitle);
}