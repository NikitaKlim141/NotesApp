package com.mirea.kt.ribo.notesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notes;
    private DatabaseHelper dbHelper;

    public NoteAdapter(Context context, List<Note> notes, DatabaseHelper dbHelper) {
        this.context = context;
        this.notes = notes;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvTitle.setText(note.getTitle());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetailActivity.class);
            intent.putExtra("note_id", note.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Удаление заметки")
                    .setMessage("Вы уверены что хотите удалить заметку?")
                    .setPositiveButton("ДА", (dialog, which) -> {
                        if (dbHelper.deleteNoteById(note.getId())) {
                            notes.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, notes.size());
                            Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        holder.btnShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, note.getContent());
            context.startActivity(Intent.createChooser(intent, "Поделиться"));
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        Button btnShare, btnDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
