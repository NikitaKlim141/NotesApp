package com.mirea.kt.ribo.notesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText etLogin, etPassword;
    private Button btnLogin;
    private TextView tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString();
            String password = etPassword.getText().toString();

            if (!login.isEmpty() && !password.isEmpty()) {
                new LoginTask().execute(login, password);
            } else {
                Toast.makeText(LoginActivity.this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String login = params[0];
            String password = params[1];
            String group = "RIBO-01-22";

            try {
                URL url = new URL("https://android-for-students.ru/coursework/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "lgn=" + login + "&pwd=" + password + "&g=" + group;
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (Exception e) {
                Log.e("LoginTask", "Error: " + e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    int resultCode = jsonResponse.getInt("result_code");
                    if (resultCode == 1) {
                        String variant = jsonResponse.getString("variant");
                        String title = jsonResponse.getString("title");
                        String task = jsonResponse.getString("task");

                        String noteTitle = "Задание курсовой работы";
                        String noteContent = "Вариант: " + variant + "\nНазвание: " + title + "\nЗадание: " + task;

                        dbHelper.insertNote(noteTitle, noteContent);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("variant", variant);
                        intent.putExtra("title", title);
                        intent.putExtra("task", task);
                        startActivity(intent);
                        finish();
                    } else {
                        tvError.setText("Invalid login or password");
                        tvError.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    Log.e("LoginTask", "JSON Parsing error: " + e.getMessage(), e);
                }
            } else {
                Toast.makeText(LoginActivity.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}