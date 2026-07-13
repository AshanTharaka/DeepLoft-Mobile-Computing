package com.deeploft.backend.repository;

import com.deeploft.backend.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByUserEmailAndCourseId(String userEmail, Long courseId);
    Optional<LessonProgress> findByUserEmailAndLessonId(String userEmail, Long lessonId);
}