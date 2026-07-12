package com.example.deeploft.network;

import com.example.deeploft.models.ActivityLog;
import com.example.deeploft.models.Certificate;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.Enrollment;
import com.example.deeploft.models.LessonProgress;
import com.example.deeploft.models.Message;
import com.example.deeploft.models.PlatformSettings;
import com.example.deeploft.models.Quiz;
import com.example.deeploft.models.Review;
import com.example.deeploft.models.PlatformRevenue;
import com.example.deeploft.models.PayoutTransaction;
import com.example.deeploft.models.SalesAnalytics;
import com.example.deeploft.models.WithdrawalRequest;
import com.example.deeploft.models.SavedCard;
import com.example.deeploft.models.StudentProgress;
import com.example.deeploft.models.StudyPlan;
import com.example.deeploft.models.TeacherStats;
import com.example.deeploft.models.User;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @Multipart
    @POST("api/courses/upload")
    Call<Map<String, String>> uploadCourseImage(@Part MultipartBody.Part file);

    @Multipart
    @POST("api/courses/upload-video")
    Call<Map<String, String>> uploadCourseVideo(@Part MultipartBody.Part file);

    @GET("api/courses")
    Call<List<Course>> getCourses(
            @Query("query") String query,
            @Query("category") String category,
            @Query("freeOnly") Boolean freeOnly,
            @Query("status") String status
    );

    @GET("api/courses/recommendations/{email}")
    Call<List<Course>> getRecommendations(@Path("email") String email);

    @GET("api/courses/ai-search")
    Call<List<Course>> aiCourseSearch(@Query("intent") String intent);

    @GET("api/enrollments/{email}")
    Call<List<Enrollment>> getMyCourses(@Path("email") String email);

    @POST("api/enrollments")
    Call<Enrollment> enrollInCourse(@Body Enrollment enrollment);

    @PUT("api/enrollments/{email}/{courseId}/last-lesson/{lessonId}")
    Call<Void> updateLastWatchedLesson(@Path("email") String email, @Path("courseId") Long courseId, @Path("lessonId") Long lessonId);

    @POST("api/courses")
    Call<Course> createCourse(@Body Course course);

    @GET("api/certificates/user/{email}")
    Call<List<Certificate>> getCertificates(@Path("email") String email);

    @POST("api/certificates")
    Call<Certificate> saveCertificate(@Body Certificate certificate);

    @PUT("api/courses/{id}")
    Call<Course> updateCourse(@Path("id") Long id, @Body Course course);

    @PUT("api/courses/{id}/review")
    Call<Course> reviewCourse(@Path("id") Long id, @Query("status") String status, @Query("comment") String comment);

    @DELETE("api/courses/{id}")
    Call<Void> deleteCourse(@Path("id") Long id);

    @GET("api/reviews/course/{courseId}")
    Call<List<Review>> getCourseReviews(@Path("courseId") Long courseId);

    @POST("api/reviews")
    Call<Review> addReview(@Body Review review);

    @GET("api/quizzes/course/{courseId}")
    Call<Quiz> getQuizByCourse(@Path("courseId") Long courseId);

    @GET("api/study-plans/{email}/{courseTitle}")
    Call<StudyPlan> getStudyPlan(@Path("email") String email, @Path("courseTitle") String courseTitle);

    @POST("api/study-plans/generate")
    Call<StudyPlan> generateStudyPlan(@Body StudyPlan request);

    @GET("api/progress/{email}/{courseId}")
    Call<List<LessonProgress>> getProgress(@Path("email") String email, @Path("courseId") Long courseId);

    @POST("api/progress")
    Call<LessonProgress> updateProgress(@Body LessonProgress progress);

    @GET("api/activity/{instructorName}")
    Call<List<ActivityLog>> getActivityLogs(@Path("instructorName") String instructorName);

    @GET("api/analytics/{instructorName}")
    Call<List<SalesAnalytics>> getInstructorAnalytics(@Path("instructorName") String instructorName);

    @GET("api/analytics/platform-revenue")
    Call<PlatformRevenue> getPlatformRevenue();

    @GET("api/analytics/payouts/{name}")
    Call<List<PayoutTransaction>> getPayouts(@Path("name") String name);

    @GET("api/withdrawals/balance/{name}")
    Call<Map<String, Double>> getInstructorBalance(@Path("name") String name);

    @POST("api/withdrawals")
    Call<WithdrawalRequest> createWithdrawalRequest(@Body WithdrawalRequest request);

    @GET("api/withdrawals/pending")
    Call<List<WithdrawalRequest>> getPendingWithdrawals();

    @PUT("api/withdrawals/{id}")
    Call<WithdrawalRequest> updateWithdrawalStatus(@Path("id") Long id, @Query("status") String status, @Query("comment") String comment);

    @GET("api/saved-cards/{email}")
    Call<List<SavedCard>> getSavedCards(@Path("email") String email);

    @POST("api/saved-cards")
    Call<SavedCard> saveCard(@Body SavedCard card);

    @GET("api/analytics/students/{instructorName}")
    Call<List<StudentProgress>> getStudentProgressForInstructor(@Path("instructorName") String instructorName);

    @GET("api/analytics/teacher-stats/{instructorName}")
    Call<TeacherStats> getTeacherStats(@Path("instructorName") String instructorName);

    @GET("api/messages/{user1}/{user2}")
    Call<List<Message>> getChat(@Path("user1") String user1, @Path("user2") String user2);

    @GET("api/messages/conversations/{email}")
    Call<List<Message>> getConversations(@Path("email") String email);

    @POST("api/messages")
    Call<Message> sendMessage(@Body Message message);

    @GET("api/platform/settings")
    Call<PlatformSettings> getPlatformSettings();

    @POST("api/platform/settings")
    Call<PlatformSettings> updatePlatformSettings(@Body PlatformSettings settings);

    @GET("api/platform/total-commission")
    Call<Double> getTotalPlatformCommission();

    @GET("api/users/{email}")
    Call<User> getUser(@Path("email") String email);

    @POST("api/users/register")
    Call<User> register(@Body User user);

    @POST("api/users/login")
    Call<User> login(@Body User user);

    @PUT("api/users/update")
    Call<User> updateProfile(@Body User user);

    @GET("api/users")
    Call<List<User>> getUsersByRole(@Query("role") String role);

    @DELETE("api/users/{email}")
    Call<Void> deleteUser(@Path("email") String email);
}