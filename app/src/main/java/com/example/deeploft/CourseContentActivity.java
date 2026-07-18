package com.example.deeploft;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deeploft.adapters.LessonAdapter;
import com.example.deeploft.models.Certificate;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.Lesson;
import com.example.deeploft.models.LessonProgress;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseContentActivity extends AppCompatActivity {
    private final List<Lesson> lessonList = new ArrayList<>();
    private final List<Long> completedLessonIds = new ArrayList<>();
    private ExoPlayer player;
    private PlayerView playerView;
    private Button btnQuality;
    private LessonAdapter adapter;
    private SessionManager sessionManager;
    private Course course;
    private boolean isFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);

        course = (Course) getIntent().getSerializableExtra("COURSE_OBJECT");
        Long lastLessonId = (Long) getIntent().getSerializableExtra("LAST_LESSON_ID");
        sessionManager = new SessionManager(this);
        
        TextView tvTitle = findViewById(R.id.tv_content_title);
        TextView tvInstructor = findViewById(R.id.tv_content_instructor);
        RecyclerView rvLessons = findViewById(R.id.rv_lessons);
        playerView = findViewById(R.id.player_view);
        btnQuality = findViewById(R.id.btn_video_quality);
        FloatingActionButton fabAskAi = findViewById(R.id.fab_ask_ai);
        Button btnTakeQuiz = findViewById(R.id.btn_take_quiz);
        Button btnStudyPlan = findViewById(R.id.btn_view_study_plan);

        if (course != null) {
            tvTitle.setText(course.getTitle());
            tvInstructor.setText("by " + course.getInstructor());
            if (course.getLessons() != null) {
                lessonList.addAll(course.getLessons());
                
                // If we have a last lesson ID, play it immediately
                if (lastLessonId != null) {
                    for (Lesson l : lessonList) {
                        if (l.getId().equals(lastLessonId)) {
                            playVideo(l);
                            break;
                        }
                    }
                }
            }
            btnTakeQuiz.setVisibility(View.VISIBLE);
        }

        btnTakeQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("COURSE_ID", course.getId());
            startActivity(intent);
        });

        btnStudyPlan.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudyPlanActivity.class);
            intent.putExtra("COURSE_TITLE", course.getTitle());
            startActivity(intent);
        });

        adapter = new LessonAdapter(lessonList, completedLessonIds, this::playVideo);
        rvLessons.setLayoutManager(new LinearLayoutManager(this));
        rvLessons.setAdapter(adapter);

        fabAskAi.setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseAIChatActivity.class);
            intent.putExtra("COURSE_OBJECT", course);
            startActivity(intent);
        });

        playerView.setFullscreenButtonClickListener(isFullScreen -> toggleFullScreen());
        btnQuality.setOnClickListener(this::showQualityMenu);

        // Handle back button for full screen
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullScreen) {
                    toggleFullScreen();
                } else {
                    finish();
                }
            }
        });

        fetchProgress();
        initializePlayer();
    }

    private void showQualityMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Tracks tracks = player.getCurrentTracks();
        
        popup.getMenu().add(0, -1, 0, "Auto");

        List<Tracks.Group> trackGroups = tracks.getGroups();
        int qualityIndex = 1;
        for (int i = 0; i < trackGroups.size(); i++) {
            Tracks.Group group = trackGroups.get(i);
            if (group.getType() == C.TRACK_TYPE_VIDEO) {
                for (int j = 0; j < group.length; j++) {
                    Format format = group.getTrackFormat(j);
                    String label = format.height + "p";
                    int finalI = i;
                    int finalJ = j;
                    popup.getMenu().add(0, qualityIndex++, 0, label).setOnMenuItemClickListener(item -> {
                        TrackSelectionParameters params = player.getTrackSelectionParameters()
                                .buildUpon()
                                .setOverrideForType(new TrackSelectionOverride(group.getMediaTrackGroup(), finalJ))
                                .build();
                        player.setTrackSelectionParameters(params);
                        btnQuality.setText("Quality: " + label);
                        return true;
                    });
                }
            }
        }

        popup.getMenu().findItem(-1).setOnMenuItemClickListener(item -> {
            player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().clearOverrides().build());
            btnQuality.setText("Quality: Auto");
            return true;
        });

        popup.show();
    }

    private void toggleFullScreen() {
        if (isFullScreen) {
            // Exit Full Screen
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) getSupportActionBar().show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            
            playerView.getLayoutParams().height = (int) (220 * getResources().getDisplayMetrics().density);
            findViewById(R.id.course_details_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.fab_ask_ai).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_video_quality).setVisibility(View.VISIBLE);
        } else {
            // Enter Full Screen
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            if (getSupportActionBar() != null) getSupportActionBar().hide();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            findViewById(R.id.course_details_layout).setVisibility(View.GONE);
            findViewById(R.id.fab_ask_ai).setVisibility(View.GONE);
            findViewById(R.id.btn_video_quality).setVisibility(View.GONE);
        }
        isFullScreen = !isFullScreen;
        
        // Ensure player continues playing if it was playing
        if (player != null && player.getPlayWhenReady()) {
            player.play();
        }
    }

    private void fetchProgress() {
        if (course == null || course.getId() == null) return;
        
        RetrofitClient.getApiService().getProgress(sessionManager.getEmail(), course.getId()).enqueue(new Callback<List<LessonProgress>>() {
            @Override
            public void onResponse(@NonNull Call<List<LessonProgress>> call, @NonNull Response<List<LessonProgress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    completedLessonIds.clear();
                    for (LessonProgress p : response.body()) {
                        if (p.isCompleted()) {
                            completedLessonIds.add(p.getLessonId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LessonProgress>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    markLessonAsCompleted();
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Toast.makeText(CourseContentActivity.this, "Video Player Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private Lesson currentLesson;

    private void playVideo(Lesson lesson) {
        currentLesson = lesson;
        Toast.makeText(this, "Loading: " + lesson.getTitle(), Toast.LENGTH_SHORT).show();
        
        if (course != null && lesson.getId() != null) {
            RetrofitClient.getApiService().updateLastWatchedLesson(sessionManager.getEmail(), course.getId(), lesson.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
            });
        }

        CardView cvSummary = findViewById(R.id.cv_ai_summary);
        TextView tvSummary = findViewById(R.id.tv_lesson_summary);
        
        if (lesson.getAiSummary() != null && !lesson.getAiSummary().isEmpty()) {
            cvSummary.setVisibility(View.VISIBLE);
            tvSummary.setText(lesson.getAiSummary());
        } else {
            cvSummary.setVisibility(View.GONE);
        }

        String videoUrl = lesson.getVideoUrl();
        if (videoUrl == null || videoUrl.isEmpty()) {
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        } else if (!videoUrl.startsWith("http")) {
            videoUrl = RetrofitClient.getServiceUrl() + "uploads/videos/" + videoUrl;
        }

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void markLessonAsCompleted() {
        if (currentLesson == null || course == null) return;
        
        LessonProgress progress = new LessonProgress(sessionManager.getEmail(), course.getId(), currentLesson.getId(), true);
        RetrofitClient.getApiService().updateProgress(progress).enqueue(new Callback<LessonProgress>() {
            @Override
            public void onResponse(@NonNull Call<LessonProgress> call, @NonNull Response<LessonProgress> response) {
                if (response.isSuccessful()) {
                    if (!completedLessonIds.contains(currentLesson.getId())) {
                        completedLessonIds.add(currentLesson.getId());
                        adapter.notifyDataSetChanged();
                        checkCourseCompletion();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LessonProgress> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void checkCourseCompletion() {
        if (course != null && course.getLessons() != null) {
            if (completedLessonIds.size() >= course.getLessons().size()) {
                saveCertificateToBackend();
            }
        }
    }

    private void saveCertificateToBackend() {
        Certificate cert = new Certificate(sessionManager.getEmail(), sessionManager.getName(), course.getTitle(), course.getId());
        RetrofitClient.getApiService().saveCertificate(cert).enqueue(new Callback<Certificate>() {
            @Override
            public void onResponse(@NonNull Call<Certificate> call, @NonNull Response<Certificate> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseContentActivity.this, "Congratulations! Course Completed!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CourseContentActivity.this, CertificateActivity.class);
                    intent.putExtra("COURSE_NAME", course.getTitle());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Certificate> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}