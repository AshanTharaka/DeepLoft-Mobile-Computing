package com.example.deeploft;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.deeploft.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CertificateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        SessionManager sessionManager = new SessionManager(this);
        String courseName = getIntent().getStringExtra("COURSE_NAME");

        TextView tvName = findViewById(R.id.tv_cert_student_name);
        TextView tvCourse = findViewById(R.id.tv_cert_course_name);
        Button btnDownload = findViewById(R.id.btn_cert_download);
        Button btnShare = findViewById(R.id.btn_cert_share);
        Button btnClose = findViewById(R.id.btn_cert_close);

        tvName.setText(sessionManager.getName());
        if (courseName != null) tvCourse.setText(courseName);

        btnDownload.setOnClickListener(v -> generateAndSavePDF(false));
        btnShare.setOnClickListener(v -> generateAndSavePDF(true));
        btnClose.setOnClickListener(v -> finish());
    }

    private void generateAndSavePDF(boolean shouldShare) {
        View view = findViewById(R.id.certificate_card_container);
        if (view == null) return;

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas pageCanvas = page.getCanvas();
        pageCanvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        String fileName = "DeepLoft_Certificate_" + System.currentTimeMillis() + ".pdf";
        Uri resultUri = null;

        try {
            if (shouldShare) {
                // Save to cache for sharing
                File cachePath = new File(getCacheDir(), "certificates");
                cachePath.mkdirs();
                File file = new File(cachePath, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    document.writeTo(fos);
                }
                resultUri = FileProvider.getUriForFile(this, "com.example.deeploft.fileprovider", file);
            } else {
                // Save to Downloads
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    resultUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (resultUri != null) {
                        try (OutputStream outputStream = getContentResolver().openOutputStream(resultUri)) {
                            document.writeTo(outputStream);
                        }
                    }
                } else {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        document.writeTo(fos);
                    }
                    resultUri = Uri.fromFile(file);
                }
            }

            if (shouldShare && resultUri != null) {
                sharePDF(resultUri);
            } else if (resultUri != null) {
                Toast.makeText(this, "Certificate saved to Downloads", Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, "Error processing PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }

    private void sharePDF(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Certificate via"));
    }
}