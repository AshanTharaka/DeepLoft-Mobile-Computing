package com.example.deeploft.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "DeepLoftSessionFinal"; // Fresh start again
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createSession(String name, String email, String role) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "No Email Found");
    }

    public String getName() {
        return pref.getString(KEY_NAME, "No Name Found");
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "STUDENT");
    }

    public void logout() {
        pref.edit().clear().apply();
    }
}