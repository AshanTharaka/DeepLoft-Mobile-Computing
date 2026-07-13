package com.example.deeploft;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deeploft.adapters.LessonManagementAdapter;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.Lesson;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCourseActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etPrice;
    private Spinner spCategory;
    private ImageView ivPreview;
    private SwitchMaterial swStatus;
    private Button btnSave, btnDelete, btnPick, btnAddLesson;
    private boolean isEditMode = false;
    private Course existingCourse;
    private Uri selectedImageUri;
    private String uploadedImageUrl = "";
    
    private final List<Lesson> newLessonList = new ArrayList<>();
    private LessonManagementAdapter lessonAdapter;

    private final String[] categories = {"Development", "Design", "Science", "Business", "AI", "Music", "Academic"};

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivPreview.setImageURI(uri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> mAddLessonLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String lTitle = result.getData().getStringExtra("LESSON_TITLE");
                    String lUrl = result.getData().getStringExtra("VIDEO_URL");
                    int lDuration = result.getData().getIntExtra("VIDEO_DURATION", 0);
                    Lesson lesson = new Lesson(lTitle, lUrl);
                    lesson.setDurationMinutes(lDuration / 60);
                    newLessonList.add(lesson);
                    lessonAdapter.notifyItemInserted(newLessonList.size() - 1);
                    if (isEditMode) autoSaveCourse();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        Toolbar toolbar = findViewById(R.id.toolbar_create);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.et_course_title);
        etDescription = findViewById(R.id.et_course_description);
        etPrice = findViewById(R.id.et_course_price);
        spCategory = findViewById(R.id.sp_course_category);
        swStatus = findViewById(R.id.sw_course_status);
        ivPreview = findViewById(R.id.iv_course_preview);
        btnPick = findViewById(R.id.btn_pick_image);
        btnSave = findViewById(R.id.btn_save_course);
        btnDelete = findViewById(R.id.btn_delete_course);
        btnAddLesson = findViewById(R.id.btn_add_lesson);
        RecyclerView rvLessons = findViewById(R.id.rv_create_lessons);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        lessonAdapter = new LessonManagementAdapter(newLessonList, new LessonManagementAdapter.OnLessonActionListener() {
            @Override
            public void onEdit(Lesson lesson, int position) {
                showEditLessonDialog(lesson, position);
            }

            @Override
            public void onDelete(int position) {
                showDeleteLessonConfirmation(position);
            }

            @Override
            public void onReorder() {
                if (isEditMode) {
                    autoSaveCourse();
                }
            }
        });
        rvLessons.setLayoutManager(new LinearLayoutManager(this));
        rvLessons.setAdapter(lessonAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                lessonAdapter.onItemMove(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not supported
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvLessons);

        isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        if (isEditMode) {
            existingCourse = (Course) getIntent().getSerializableExtra("COURSE_OBJECT");
            populateFields();
            btnSave.setText("Update Course");
            btnDelete.setVisibility(View.VISIBLE);
        }

        swStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnSave.setText(isEditMode ? "Submit for Review" : "Publish (Submit for Review)");
            } else {
                btnSave.setText(isEditMode ? "Save as Draft" : "Create as Draft");
            }
        });

        btnPick.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnAddLesson.setOnClickListener(v -> {
            mAddLessonLauncher.launch(new Intent(this, AddLessonActivity.class));
        });
        btnSave.setOnClickListener(v -> {
            if (isEditMode) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Changes")
                        .setMessage("Are you sure you want to save these changes?")
                        .setPositiveButton("Yes", (dialog, which) -> handlePublish())
                        .setNegativeButton("No", null)
                        .show();
            } else {
                handlePublish();
            }
        });
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Course")
                    .setMessage("Are you sure you want to permanently delete this course?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteCourse())
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    private void autoSaveCourse() {
        if (!isEditMode || existingCourse == null) return;
        saveCourse(); // Reusing saveCourse for simplicity
    }

    private void showDeleteLessonConfirmation(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Lesson")
                .setMessage("Are you sure you want to remove this video from your course?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    newLessonList.remove(position);
                    lessonAdapter.notifyItemRemoved(position);
                    if (isEditMode) autoSaveCourse();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showEditLessonDialog(Lesson lesson, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Lesson Name");
        final EditText input = new EditText(this);
        input.setText(lesson.getTitle());
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                lesson.setTitle(newTitle);
                lessonAdapter.notifyItemChanged(position);
                if (isEditMode) autoSaveCourse();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void populateFields() {
        if (existingCourse != null) {
            etTitle.setText(existingCourse.getTitle());
            etDescription.setText(existingCourse.getDescription());
            etPrice.setText(String.valueOf(existingCourse.getPrice()));
            
            if (existingCourse.getCategory() != null) {
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equalsIgnoreCase(existingCourse.getCategory())) {
                        spCategory.setSelection(i);
                        break;
                    }
                }
            }

            swStatus.setChecked("PUBLISHED".equalsIgnoreCase(existingCourse.getStatus()) || "PENDING_APPROVAL".equalsIgnoreCase(existingCourse.getStatus()));
            uploadedImageUrl = existingCourse.getImageUrl();
            if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty()) {
                String fullUrl = RetrofitClient.getServiceUrl() + "uploads/images/" + uploadedImageUrl;
                Glide.with(this).load(fullUrl).into(ivPreview);
            }
            if (existingCourse.getLessons() != null) {
                newLessonList.clear(); 
                newLessonList.addAll(existingCourse.getLessons());
                lessonAdapter.notifyDataSetChanged();
            }
            
            if ("REJECTED".equalsIgnoreCase(existingCourse.getStatus())) {
                new AlertDialog.Builder(this)
                        .setTitle("Course Rejected by Admin")
                        .setMessage("Reason: " + existingCourse.getAdminReviewComment())
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    private void handlePublish() {
        if (selectedImageUri != null) {
            uploadImageAndSave();
        } else {
            saveCourse();
        }
    }

    private void uploadImageAndSave() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            File file = new File(getCacheDir(), "upload_temp.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            RetrofitClient.getApiService().uploadCourseImage(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedImageUrl = response.body().get("url");
                        saveCourse();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                    Toast.makeText(CreateCourseActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCourse() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        
        String status;
        if (swStatus.isChecked()) {
            status = "PENDING_APPROVAL";
        } else {
            status = "DRAFT";
        }

        if (title.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Title and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sessionManager = new SessionManager(this);
        Course courseToSave = new Course(isEditMode ? existingCourse.getId() : null, title, sessionManager.getName(), price, uploadedImageUrl);
        courseToSave.setInstructorEmail(sessionManager.getEmail());
        courseToSave.setDescription(description);
        courseToSave.setCategory(category);
        courseToSave.setStatus(status);
        courseToSave.setLessons(new ArrayList<>(newLessonList));

        Call<Course> call;
        if (isEditMode) {
            call = RetrofitClient.getApiService().updateCourse(existingCourse.getId(), courseToSave);
        } else {
            call = RetrofitClient.getApiService().createCourse(courseToSave);
        }

        call.enqueue(new Callback<Course>() {
            @Override
            public void onResponse(@NonNull Call<Course> call, @NonNull Response<Course> response) {
                if (response.isSuccessful()) {
                    String msg = "Course saved as Draft";
                    if ("PENDING_APPROVAL".equals(status)) msg = "Submitted for Admin Review";
                    Toast.makeText(CreateCourseActivity.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Course> call, @NonNull Throwable t) {
                Toast.makeText(CreateCourseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCourse() {
        if (existingCourse == null || existingCourse.getId() == null) return;
        RetrofitClient.getApiService().deleteCourse(existingCourse.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateCourseActivity.this, "Course deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
        });
    }
}