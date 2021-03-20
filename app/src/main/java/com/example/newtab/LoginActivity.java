package com.example.newtab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private String password;
    private String name;
    private CheckBox checkBoxRemember;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String nameFinal, pswFinal;
    private ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        checkBoxControl();
    }

    private void initViews() {
        editTextUsername = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogIn = findViewById(R.id.logIn);
        checkBoxRemember = findViewById(R.id.checkBox);
        sharedPreferences = getSharedPreferences(
                Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(Const.IS_FIRST, true);
        editor.apply();
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                if (!(editTextUsername.getText().toString().trim().matches("") && editTextPassword.getText().toString().trim().matches(""))) {
                    loginAttempt();
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.empty, Toast.LENGTH_SHORT).show();
                    checkBoxRemember.setChecked(false);
                     nDialog.dismiss();
                }
            }
        });

        checkBoxRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(Const.CHECK, true);
                editor.apply();
            }
        });

    }

    private void loginAttempt() {
        String p = editTextPassword.getText().toString();
        nameFinal = editTextUsername.getText().toString();
        String psw = md5(p);
        pswFinal = psw.toUpperCase();
        downloadData();

    }

    private void checkBoxControl() {

        if (sharedPreferences.getBoolean(Const.CHECK, false)) {
            editor.putBoolean(Const.IS_FIRST, false);
            editor.apply();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void downloadData() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        GitHubService service = retrofit.create(GitHubService.class);
        service.login().enqueue(new Callback<UserLoginModel>() {
            @Override
            public void onResponse(@NonNull Call<UserLoginModel> call,
                                   @NonNull Response<UserLoginModel> response) {
                nDialog.dismiss();
                password = response.body().getPassword();
                name = response.body().getName();
                if ((nameFinal.matches(name)) && pswFinal.matches(password)) {
                    Toast.makeText(LoginActivity.this, R.string.success,
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(
                            LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    if (checkBoxRemember.isChecked()) {
                        editor.putBoolean(Const.CHECK, true);
                        editor.apply();
                    } else {
                        editor.putBoolean(Const.IS_FIRST, false);
                        editor.apply();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.invalid, Toast.LENGTH_SHORT).show();
                    setCheckBox();

                }
            }

            @Override
            public void onFailure(@NonNull Call<UserLoginModel> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, R.string.error + " " + t, Toast.LENGTH_SHORT).show();
                setCheckBox();
                nDialog.dismiss();
            }
        });
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showLoadingDialog() {
        nDialog = new ProgressDialog(LoginActivity.this);
        nDialog.setMessage("Going To Person List");
        nDialog.setTitle(R.string.title_dialog);
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();
    }

    public void setCheckBox() {
        checkBoxRemember.setChecked(false);
        editor.putBoolean(Const.CHECK, false);
        editor.apply();
    }
}
