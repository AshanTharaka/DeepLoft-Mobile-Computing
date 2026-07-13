package com.deeploft.backend.config;

import com.deeploft.backend.model.*;
import com.deeploft.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CourseRepository repository, QuizRepository quizRepository, EnrollmentRepository enrollmentRepository, ActivityLogRepository logRepository, LessonProgressRepository progressRepository, SavedCardRepository savedCardRepository) {
        return args -> {
            // --- DEVELOPMENT ---
            Lesson l1 = new Lesson(null, "Introduction to Android", "vid1", 10, "In this lesson, we cover the history of Android, the ecosystem, and why Java is a powerful choice for mobile development.");
            Lesson l2 = new Lesson(null, "Setting up Android Studio", "vid2", 15, "Step-by-step guide to installing Android Studio, configuring the SDK, and running your first 'Hello World' app on an emulator.");
            Course c1 = repository.save(new Course(null, "Android Development with Java", "Ashan", "ashan@deeploft.com", 49.99, "Learn Android from scratch.", "https://images.pexels.com/photos/1181244/pexels-photo-1181244.jpeg?auto=compress&cs=tinysrgb&w=800", "Development", "PUBLISHED", null, null, Arrays.asList(l1, l2)));

            repository.save(new Course(null, "Fullstack Web Bootcamp", "John Doe", "john@example.com", 89.99, "Master HTML, CSS, JS, and React.", "https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=800", "Development", "PUBLISHED", null, null, Arrays.asList(
                    new Lesson(null, "HTML Basics", "vid5", 8, "Learn the structure of the web."),
                    new Lesson(null, "Modern CSS", "vid6", 25, "Styling your pages with Flexbox and Grid.")
            )));

            // --- DESIGN ---
            repository.save(new Course(null, "Graphic Design Masterclass", "Jane Smith", "jane@example.com", 59.99, "Learn Adobe Photoshop and Illustrator from pro designers.", "https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=800", "Design", "PUBLISHED", null, null, Arrays.asList(
                    new Lesson(null, "Color Theory", "vid7", 12, "Understanding colors and their psychological impact."),
                    new Lesson(null, "Typography Basics", "vid8", 18, "How to choose fonts for your brand.")
            )));

            repository.save(new Course(null, "UI/UX Design for Mobile", "Ashan", "ashan@deeploft.com", 45.00, "Create beautiful mobile app interfaces.", "https://images.pexels.com/photos/196645/pexels-photo-196645.jpeg?auto=compress&cs=tinysrgb&w=800", "Design", "PUBLISHED", null, null, null));

            // --- SCIENCE ---
            repository.save(new Course(null, "Astrophysics for Everyone", "Dr. Stella", "stella@example.com", 29.99, "Explore the wonders of the universe, from black holes to distant galaxies.", "https://images.pexels.com/photos/2150/sky-space-dark-galaxy.jpg?auto=compress&cs=tinysrgb&w=800", "Science", "PUBLISHED", null, null, Arrays.asList(
                    new Lesson(null, "The Big Bang", "vid9", 15, "How everything started."),
                    new Lesson(null, "Black Holes Explained", "vid10", 22, "Gravity at its extreme.")
            )));

            repository.save(new Course(null, "Modern Chemistry", "Prof. Chem", "chem@example.com", 19.99, "Fundamentals of organic and inorganic chemistry.", "https://images.pexels.com/photos/2280571/pexels-photo-2280571.jpeg?auto=compress&cs=tinysrgb&w=800", "Science", "PUBLISHED", null, null, null));

            // --- BUSINESS ---
            repository.save(new Course(null, "Startup Launchpad", "Mark Cuban", "mark@example.com", 99.99, "How to build, scale, and fund your own business startup.", "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=500", "Business", "PUBLISHED", null, null, Arrays.asList(
                    new Lesson(null, "Finding Your Niche", "vid11", 10, "Validating your business idea."),
                    new Lesson(null, "Pitching to Investors", "vid12", 20, "Getting the funding you need.")
            )));

            repository.save(new Course(null, "Digital Marketing 101", "Emma Growth", "emma@example.com", 34.50, "Master SEO, SEM, and social media advertising.", "https://images.unsplash.com/photo-1533750349088-cd871a92f312?w=500", "Business", "PUBLISHED", null, null, null));

            // --- AI ---
            Lesson l3 = new Lesson(null, "What is Artificial Intelligence?", "vid3", 12, "An overview of AI, defining intelligence in machines.");
            Lesson l4 = new Lesson(null, "Machine Learning Basics", "vid4", 20, "Introduction to algorithms that learn from data.");
            Course c2 = repository.save(new Course(null, "AI for Beginners", "DeepLoft AI", "ai@deeploft.com", 0.00, "Intro to AI concepts.", "https://images.pexels.com/photos/8386440/pexels-photo-8386440.jpeg?auto=compress&cs=tinysrgb&w=800", "AI", "PUBLISHED", null, null, Arrays.asList(l3, l4)));

            repository.save(new Course(null, "Natural Language Processing", "DeepLoft AI", "ai@deeploft.com", 79.99, "Build chatbots and translation engines.", "https://images.pexels.com/photos/373543/pexels-photo-373543.jpeg?auto=compress&cs=tinysrgb&w=800", "AI", "PUBLISHED", null, null, null));

            // --- MUSIC ---
            repository.save(new Course(null, "Mastering the Guitar", "Music Pro", "music@example.com", 39.99, "From basics to advanced solos.", "https://images.unsplash.com/photo-1525201548942-d8732f6617a0?w=500", "Music", "PUBLISHED", null, null, Arrays.asList(
                    new Lesson(null, "First Chords", "vid13", 15, "Getting your fingers moving."),
                    new Lesson(null, "Rhythm and Strumming", "vid14", 20, "Playing in time.")
            )));

            // --- ACADEMIC ---
            repository.save(new Course(null, "Advanced Calculus", "Math Expert", "math@example.com", 49.99, "Comprehensive guide to university-level calculus.", "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=500", "Academic", "PUBLISHED", null, null, Arrays.asList(
                    new Lesson(null, "Limits and Continuity", "vid15", 30, "The foundation of calculus."),
                    new Lesson(null, "Derivatives Deep Dive", "vid16", 45, "Mastering differentiation.")
            )));

            // --- MOCK DATA FOR DASHBOARD ---
            // Add Mock Enrollments to give Ashan a balance of $120.00
            enrollmentRepository.save(new Enrollment(null, c1, "student1@example.com", LocalDateTime.now(), 49.99, 40.00, 9.99, null));
            enrollmentRepository.save(new Enrollment(null, c1, "student2@example.com", LocalDateTime.now(), 49.99, 40.00, 9.99, null));
            enrollmentRepository.save(new Enrollment(null, c1, "student3@example.com", LocalDateTime.now(), 49.99, 40.00, 9.99, null));
            
            // Add Mock Saved Cards for Testing
            savedCardRepository.save(new SavedCard(null, "pesa@gmail.com", "Pesa Primary", "**** **** **** 4242", "12/28", "VISA"));
            savedCardRepository.save(new SavedCard(null, "pesa@gmail.com", "Pesa Secondary", "**** **** **** 8888", "06/27", "MASTERCARD"));
            savedCardRepository.save(new SavedCard(null, "ashan@deeploft.com", "Ashan Personal", "**** **** **** 1111", "01/29", "VISA"));
            
            // Add some lesson progress to make it look real
            progressRepository.save(new LessonProgress(null, "student1@example.com", c1.getId(), l1.getId(), true));
            progressRepository.save(new LessonProgress(null, "student2@example.com", c1.getId(), l1.getId(), true));
            progressRepository.save(new LessonProgress(null, "student2@example.com", c1.getId(), l2.getId(), true));

            ActivityLog log = new ActivityLog();
            log.setInstructorName("Ashan");
            log.setMessage("Total earnings reached $120.00! You can now request a withdrawal.");
            log.setTimestamp(LocalDateTime.now());
            logRepository.save(log);

            // Add Mock Enrollments for 'pesa@gmail.com' so the learning part works immediately
            enrollmentRepository.save(new Enrollment(null, c1, "pesa@gmail.com", LocalDateTime.now().minusDays(5), 49.99, 40.00, 9.99, l1.getId()));
            Course designCourse = repository.findAll().stream().filter(c -> c.getCategory().equals("Design")).findFirst().orElse(null);
            if (designCourse != null) {
                enrollmentRepository.save(new Enrollment(null, designCourse, "pesa@gmail.com", LocalDateTime.now().minusDays(2), 59.99, 48.00, 11.99, null));
            }

            // Add Quiz for Android Course
            Question q1 = new Question(null, "What language is primarily used in this course?", Arrays.asList("Swift", "Kotlin", "Java", "Python"), 2);
            Question q2 = new Question(null, "Which tool is used for Android development?", Arrays.asList("Xcode", "Android Studio", "VS Code", "Eclipse"), 1);
            quizRepository.save(new Quiz(null, "Android Basics Quiz", c1.getId(), Arrays.asList(q1, q2)));

            // Add Quiz for AI Course
            Question q3 = new Question(null, "What does AI stand for?", Arrays.asList("Apple Intel", "Artificial Intelligence", "All Information", "Auto Index"), 1);
            quizRepository.save(new Quiz(null, "AI Fundamentals Quiz", c2.getId(), Arrays.asList(q3)));
        };
    }
}