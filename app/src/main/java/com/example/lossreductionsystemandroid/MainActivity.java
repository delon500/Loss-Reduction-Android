package com.example.lossreductionsystemandroid;

import static java.lang.String.valueOf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN";
    private static final String LOGIN_URL = "http://10.0.2.2/loss_reduction_backend/login.php";

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private CircularProgressIndicator progressBar;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String LOGIN_KEY = "isLoggedIn";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bindViews();
        requestQueue = Volley.newRequestQueue(this);
        btnLogin.setOnClickListener(v->attemptLogin());

    }

    /*Find views*/
    private void bindViews(){
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.loginProgress);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0,16,0,16);
    }

    /*Validate & send Volley request*/
    private void attemptLogin(){
       final String username = valueOf(etUsername);
       final String password = valueOf(etPassword);

       if(!validate(username,password)) return;
       hideKeyboard();

       // Disable button & show progress bar
        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        StringRequest loginReq = new StringRequest(
                Request.Method.POST,
                LOGIN_URL,
                response -> handleSuccess(response),
                error -> handleVolleyError(error)
        ){
            @Override
          protected Map<String,String> getParams() throws AuthFailureError {
              Map<String,String> params = new HashMap<>();
              params.put("username",username);
              params.put("password",password);
              return params;
          }
        };
        requestQueue.add(loginReq);
    }

    /** Handle JSON from server*/
    private void handleSuccess(String response){
        Log.d(TAG, "raw -> " + response);
        btnLogin.setEnabled(true);

        try {
            JSONObject json = new JSONObject(response);
            boolean ok = json.getBoolean("success");
            String msg = json.optString("message","");

            if(ok){
                markLoggedIn();
                Intent intent = new Intent(this, LeadingPage.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            } else {
                Toast.makeText(this,msg.isEmpty() ? "Login failed" : msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Log.e(TAG,"parse error",e);
            Toast.makeText(this,"Invalid server response", Toast.LENGTH_SHORT).show();
        }
    }
    /** Volley error branch */
    private void handleVolleyError(Exception error){
        Log.e(TAG,"network error ",error);
        btnLogin.setEnabled(true);
        btnLogin.setIcon(null);
        Toast.makeText(this,"Network error",Toast.LENGTH_SHORT).show();
    }

    /** Persist login flag */
    private void markLoggedIn(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        prefs.edit().putBoolean(LOGIN_KEY, true).apply();
    }

    // ───────────────────────── helpers ──────────────────────────
    private boolean validate(String username, String password){
        if(TextUtils.isEmpty(username)){
            etUsername.setError("Enter username");
            return false;
        }
        if(TextUtils.isEmpty(password)){
            etPassword.setError("Enter password");
            return false;
        }
        return true;
    }

    private static String valueOf(TextInputEditText et){
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }
}