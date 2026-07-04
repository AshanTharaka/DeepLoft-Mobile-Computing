package com.deeploft.backend.controller;

import com.deeploft.backend.model.Course;
import com.deeploft.backend.service.CourseService;
import com.deeploft.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/upload")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return handleFileUpload(file, "uploads/images/");
    }

    @PostMapping("/upload-video")
    public Map<String, String> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        return handleFileUpload(file, "uploads/videos/");
    }

    private Map<String, String> handleFileUpload(MultipartFile file, String subPath) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(subPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        Map<String, String> response = new HashMap<>();
        response.put("url", fileName);
        return response;
    }

    @GetMapping
    public List<Course> getAllCourses(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean freeOnly) {
        return courseService.getAllCourses(query, freeOnly);
    }

    @GetMapping("/recommendations/{email}")
    public List<Course> getRecommendations(@PathVariable String email) {
        return recommendationService.getRecommendations(email);
    }

    @GetMapping("/ai-search")
    public List<Course> aiSearch(@RequestParam String intent) {
        return courseService.getAiSearchRecommendations(intent);
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}