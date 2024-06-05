package com.mirea.kt.ribo.notesapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddNote;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddNote = findViewById(R.id.fabAddNote);
        dbHelper = new DatabaseHelper(this);

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, noteList, dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
            startActivity(intent);
        });
        Intent intent = getIntent();
        String variant = intent.getStringExtra("variant");
        String title = intent.getStringExtra("title");
        String task = intent.getStringExtra("task");

        loadNotes();
    }

    private void loadNotes() {
        Log.d("MainActivity", "Loading notes from database");
        noteList.clear();
        Cursor cursor = dbHelper.getAllNotes();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                noteList.add(new Note(id, title, content));
            } while (cursor.moveToNext());
            cursor.close();
        }
        noteAdapter.notifyDataSetChanged();
        Log.d("MainActivity", "Notes loaded: " + noteList.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }
}
