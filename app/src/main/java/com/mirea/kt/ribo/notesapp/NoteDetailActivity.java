package com.mirea.kt.ribo.notesapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DatabaseHelper(this);

        noteId = getIntent().getIntExtra("note_id", -1);
        if (noteId != -1) {
            loadNote();
        }

        btnSave.setOnClickListener(v -> saveNote());
    }

    @SuppressLint("Range")
    private void loadNote() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "id=?", new String[]{String.valueOf(noteId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            etTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
            etContent.setText(cursor.getString(cursor.getColumnIndex("content")));
            cursor.close();
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Заголовок или текст не могут быть пустыми", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("content", content);

        long result;
        if (noteId == -1) {
            result = db.insert("notes", null, contentValues);
        } else {
            result = db.update("notes", contentValues, "id=?", new String[]{String.valueOf(noteId)});
        }

        if (result == -1) {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
