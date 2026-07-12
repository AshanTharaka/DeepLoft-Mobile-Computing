package com.example.deeploft;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deeploft.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLessonActivity extends AppCompatActivity {
    private EditText etTitle;
    private TextView tvStatus;
    private Button btnSave;
    private Uri selectedVideoUri;
    private int videoDurationSeconds = 0;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedVideoUri = uri;
                    tvStatus.setText("Video selected: " + uri.getLastPathSegment());
                    extractVideoDuration(uri);
                }
            }
    );

    private void extractVideoDuration(Uri uri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillis = Long.parseLong(time);
            videoDurationSeconds = (int) (timeInMillis / 1000);
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        etTitle = findViewById(R.id.et_lesson_title);
        tvStatus = findViewById(R.id.tv_video_status);
        Button btnPick = findViewById(R.id.btn_pick_video);
        btnSave = findViewById(R.id.btn_save_lesson);

        btnPick.setOnClickListener(v -> mGetContent.launch("video/*"));
        btnSave.setOnClickListener(v -> handleSave());
    }

    private void handleSave() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter lesson title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedVideoUri == null) {
            Toast.makeText(this, "Please select a video", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadVideoAndFinish(title);
    }

    private void uploadVideoAndFinish(String title) {
        Toast.makeText(this, "Uploading video... please wait.", Toast.LENGTH_LONG).show();
        btnSave.setEnabled(false);

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedVideoUri);
            if (inputStream == null) {
                btnSave.setEnabled(true);
                return;
            }
            File file = new File(getCacheDir(), "upload_video_temp.mp4");
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            RetrofitClient.getApiService().uploadCourseVideo(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String uploadedVideoUrl = response.body().get("url");
                        
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("LESSON_TITLE", title);
                        resultIntent.putExtra("VIDEO_URL", uploadedVideoUrl);
                        resultIntent.putExtra("VIDEO_DURATION", videoDurationSeconds);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        btnSave.setEnabled(true);
                        String errorMsg = "Video upload failed";
                        try (ResponseBody errorBody = response.errorBody()) {
                            if (errorBody != null) {
                                errorMsg += ": " + errorBody.string();
                            }
                        } catch (IOException ignored) {}
                        Toast.makeText(AddLessonActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(AddLessonActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            btnSave.setEnabled(true);
            Toast.makeText(this, "Internal error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}