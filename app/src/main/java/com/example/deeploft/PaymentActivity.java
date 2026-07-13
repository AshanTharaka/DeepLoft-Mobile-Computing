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
    private LinearLayout layoutCardDetails, layoutSavedCards, layoutPaypalDetails, layoutBankDetails;
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
        layoutPaypalDetails = findViewById(R.id.layout_paypal_details);
        layoutBankDetails = findViewById(R.id.layout_bank_details);
        
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiry = findViewById(R.id.et_expiry);
        etExpiry.addTextChangedListener(new android.text.TextWatcher() {
            private boolean isDeleting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isDeleting = count > after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isDeleting) return;
                
                String input = s.toString();
                if (input.length() == 2 && !input.contains("/")) {
                    etExpiry.setText(input + "/");
                    etExpiry.setSelection(etExpiry.getText().length());
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etCvv = findViewById(R.id.et_cvv);
        etCardName = findViewById(R.id.et_card_name);
        cbSaveCard = findViewById(R.id.cb_save_card);
        spSavedCards = findViewById(R.id.sp_saved_cards);

        tvTitle.setText(course.getTitle());
        tvInstructor.setText("by " + course.getInstructor());
        tvAmount.setText("$" + String.format("%.2f", course.getPrice()));

        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            layoutCardDetails.setVisibility(checkedId == R.id.rb_card ? View.VISIBLE : View.GONE);
            layoutSavedCards.setVisibility(checkedId == R.id.rb_card && !savedCards.isEmpty() ? View.VISIBLE : View.GONE);
            layoutPaypalDetails.setVisibility(checkedId == R.id.rb_paypal ? View.VISIBLE : View.GONE);
            layoutBankDetails.setVisibility(checkedId == R.id.rb_bank ? View.VISIBLE : View.GONE);
        });

        btnPay.setOnClickListener(v -> processPayment());
        
        spSavedCards.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SavedCard card = savedCards.get(position - 1);
                    etCardNumber.setText(card.getMaskedCardNumber());
                    etCardNumber.setEnabled(false);
                    etCardName.setText(card.getCardHolderName());
                    etCardName.setEnabled(false);
                    etExpiry.setText(card.getExpiryDate());
                    etExpiry.setEnabled(false);
                    cbSaveCard.setVisibility(View.GONE);
                } else {
                    etCardNumber.setText("");
                    etCardNumber.setEnabled(true);
                    etCardName.setText("");
                    etCardName.setEnabled(true);
                    etExpiry.setText("");
                    etExpiry.setEnabled(true);
                    cbSaveCard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
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
        int checkedId = rgPaymentMethods.getCheckedRadioButtonId();
        
        if (checkedId == R.id.rb_card) {
            if (spSavedCards.getSelectedItemPosition() > 0) {
                // Using saved card
            } else {
                if (!validateCardDetails()) return;
                if (cbSaveCard.isChecked()) {
                    saveCardToBackend();
                }
            }
        } else if (checkedId == R.id.rb_paypal) {
            Toast.makeText(this, "Redirecting to PayPal...", Toast.LENGTH_SHORT).show();
        } else if (checkedId == R.id.rb_bank) {
            Toast.makeText(this, "Waiting for bank confirmation...", Toast.LENGTH_SHORT).show();
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
        Button btn = findViewById(R.id.btn_confirm_payment);
        btn.setEnabled(enabled);
        btn.setText(enabled ? "Confirm and Pay" : "Processing...");
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