package com.example.deeploft;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.Enrollment;
import com.example.deeploft.models.SavedCard;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.NotificationHelper;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private Course course;
    private SessionManager sessionManager;
    private LinearLayout layoutCardDetails, layoutSavedCards;
    private EditText etCardNumber, etExpiry, etCvv, etCardName;
    private RadioGroup rgPaymentMethods;
    private CheckBox cbSaveCard;
    private Spinner spSavedCards;
    private final List<SavedCard> savedCards = new ArrayList<>();
    private final List<String> cardLabels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        course = (Course) getIntent().getSerializableExtra("COURSE_OBJECT");
        sessionManager = new SessionManager(this);

        if (course == null) {
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tv_payment_course_title);
        TextView tvInstructor = findViewById(R.id.tv_payment_instructor);
        TextView tvAmount = findViewById(R.id.tv_payment_amount);
        Button btnPay = findViewById(R.id.btn_confirm_payment);
        
        rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        layoutCardDetails = findViewById(R.id.layout_card_details);
        layoutSavedCards = findViewById(R.id.layout_saved_cards);
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiry = findViewById(R.id.et_expiry);
        etCvv = findViewById(R.id.et_cvv);
        etCardName = findViewById(R.id.et_card_name);
        cbSaveCard = findViewById(R.id.cb_save_card);
        spSavedCards = findViewById(R.id.sp_saved_cards);

        tvTitle.setText(course.getTitle());
        tvInstructor.setText("by " + course.getInstructor());
        tvAmount.setText("$" + String.format("%.2f", course.getPrice()));

        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_card) {
                layoutCardDetails.setVisibility(View.VISIBLE);
                if (!savedCards.isEmpty()) layoutSavedCards.setVisibility(View.VISIBLE);
            } else {
                layoutCardDetails.setVisibility(View.GONE);
                layoutSavedCards.setVisibility(View.GONE);
            }
        });

        btnPay.setOnClickListener(v -> processPayment());
        
        fetchSavedCards();
    }

    private void fetchSavedCards() {
        RetrofitClient.getApiService().getSavedCards(sessionManager.getEmail()).enqueue(new Callback<List<SavedCard>>() {
            @Override
            public void onResponse(@NonNull Call<List<SavedCard>> call, @NonNull Response<List<SavedCard>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    savedCards.clear();
                    savedCards.addAll(response.body());
                    cardLabels.clear();
                    cardLabels.add("--- Select a saved card ---");
                    for (SavedCard card : savedCards) {
                        cardLabels.add(card.getMaskedCardNumber() + " (" + card.getCardHolderName() + ")");
                    }
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this, android.R.layout.simple_spinner_item, cardLabels);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSavedCards.setAdapter(adapter);
                    
                    if (rgPaymentMethods.getCheckedRadioButtonId() == R.id.rb_card) {
                        layoutSavedCards.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SavedCard>> call, @NonNull Throwable t) {}
        });
    }

    private void processPayment() {
        if (rgPaymentMethods.getCheckedRadioButtonId() == R.id.rb_card) {
            // Check if using a saved card (spinner index > 0)
            if (spSavedCards.getSelectedItemPosition() > 0) {
                // Using saved card - simulated success
            } else {
                if (!validateCardDetails()) return;
                if (cbSaveCard.isChecked()) {
                    saveCardToBackend();
                }
            }
        }

        Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show();
        btnPayEnabled(false);
        enrollUser();
    }

    private void saveCardToBackend() {
        String fullNum = etCardNumber.getText().toString().trim();
        String masked = "**** **** **** " + fullNum.substring(fullNum.length() - 4);
        SavedCard card = new SavedCard(sessionManager.getEmail(), etCardName.getText().toString().trim(), masked, etExpiry.getText().toString().trim(), "VISA");
        
        RetrofitClient.getApiService().saveCard(card).enqueue(new Callback<SavedCard>() {
            @Override
            public void onResponse(@NonNull Call<SavedCard> call, @NonNull Response<SavedCard> response) {}
            @Override
            public void onFailure(@NonNull Call<SavedCard> call, @NonNull Throwable t) {}
        });
    }

    private boolean validateCardDetails() {
        String number = etCardNumber.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();
        String name = etCardName.getText().toString().trim();

        if (number.length() != 16) {
            etCardNumber.setError("Enter 16-digit card number");
            return false;
        }
        if (expiry.isEmpty()) {
            etExpiry.setError("Required");
            return false;
        }
        if (cvv.length() < 3) {
            etCvv.setError("Invalid CVV");
            return false;
        }
        if (name.isEmpty()) {
            etCardName.setError("Required");
            return false;
        }
        return true;
    }

    private void btnPayEnabled(boolean enabled) {
        findViewById(R.id.btn_confirm_payment).setEnabled(enabled);
    }

    private void enrollUser() {
        Enrollment enrollment = new Enrollment(course, sessionManager.getEmail(), course.getPrice());

        RetrofitClient.getApiService().enrollInCourse(enrollment).enqueue(new Callback<Enrollment>() {
            @Override
            public void onResponse(@NonNull Call<Enrollment> call, @NonNull Response<Enrollment> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Payment Successful! Course Unlocked.", Toast.LENGTH_LONG).show();
                    NotificationHelper.showNotification(PaymentActivity.this, 
                        "Enrollment Successful!", 
                        "You now have full access to " + course.getTitle() + ".");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    btnPayEnabled(true);
                    Toast.makeText(PaymentActivity.this, "Enrollment failed after payment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Enrollment> call, @NonNull Throwable t) {
                btnPayEnabled(true);
                Toast.makeText(PaymentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}