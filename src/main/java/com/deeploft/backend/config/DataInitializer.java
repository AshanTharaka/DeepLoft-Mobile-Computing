package com.deeploft.backend.config;

import com.deeploft.backend.model.Course;
import com.deeploft.backend.model.Lesson;
import com.deeploft.backend.model.Question;
import com.deeploft.backend.model.Quiz;
import com.deeploft.backend.repository.CourseRepository;
import com.deeploft.backend.repository.QuizRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CourseRepository repository, QuizRepository quizRepository) {
        return args -> {
            Lesson l1 = new Lesson(null, "Introduction to Android", "vid1", 10, "In this lesson, we cover the history of Android, the ecosystem, and why Java is a powerful choice for mobile development.");
            Lesson l2 = new Lesson(null, "Setting up Android Studio", "vid2", 15, "Step-by-step guide to installing Android Studio, configuring the SDK, and running your first 'Hello World' app on an emulator.");
            Course c1 = repository.save(new Course(null, "Android Development with Java", "Ashan", "ashan@deeploft.com", 49.99, "Learn Android from scratch.", "", "Development", "PUBLISHED", null, null, Arrays.asList(l1, l2)));

            // Add Quiz for Android Course
            Question q1 = new Question(null, "What language is primarily used in this course?", Arrays.asList("Swift", "Kotlin", "Java", "Python"), 2);
            Question q2 = new Question(null, "Which tool is used for Android development?", Arrays.asList("Xcode", "Android Studio", "VS Code", "Eclipse"), 1);
            quizRepository.save(new Quiz(null, "Android Basics Quiz", c1.getId(), Arrays.asList(q1, q2)));

            Lesson l3 = new Lesson(null, "What is Artificial Intelligence?", "vid3", 12, "An overview of AI, defining intelligence in machines, and exploring the difference between Narrow AI and General AI.");
            Lesson l4 = new Lesson(null, "Machine Learning Basics", "vid4", 20, "Introduction to algorithms that learn from data. We discuss supervised vs. unsupervised learning and real-world examples.");
            Course c2 = repository.save(new Course(null, "AI for Beginners", "DeepLoft AI", "ai@deeploft.com", 0.00, "Intro to AI concepts.", "", "AI", "PUBLISHED", null, null, Arrays.asList(l3, l4)));

            // Add Quiz for AI Course
            Question q3 = new Question(null, "What does AI stand for?", Arrays.asList("Apple Intel", "Artificial Intelligence", "All Information", "Auto Index"), 1);
            quizRepository.save(new Quiz(null, "AI Fundamentals Quiz", c2.getId(), Arrays.asList(q3)));

            repository.save(new Course(null, "Web Dev Bootcamp", "John Doe", "john@example.com", 89.99, "Fullstack web development.", "", "Development", "PUBLISHED", null, null, Arrays.asList(
                    new Lesson(null, "HTML Basics", "vid5", 8, ""),
                    new Lesson(null, "CSS for Beginners", "vid6", 25, "")
            )));
        };
    }
}