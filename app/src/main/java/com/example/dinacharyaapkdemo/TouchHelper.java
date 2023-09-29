package com.example.dinacharyaapkdemo;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dinacharyaapkdemo.Adapter.ToDoAdapter;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {
   private ToDoAdapter toDoAdapter;

    public TouchHelper(ToDoAdapter toDoAdapter) {
        super(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT);
        this.toDoAdapter = toDoAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final  int position = viewHolder.getAdapterPosition();
        if(direction == ItemTouchHelper.RIGHT){
            AlertDialog.Builder builder = new AlertDialog.Builder(toDoAdapter.getContext());
            builder.setMessage("Are you sure?").setTitle("Delete Task").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    toDoAdapter.deleteTask(position);

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    toDoAdapter.notifyItemChanged(position);

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            toDoAdapter.updateTask(position);
        }

    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
                .addSwipeRightBackgroundColor(Color.RED)
                .addSwipeLeftActionIcon(R.drawable.baseline_edit_note_24)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(toDoAdapter.getContext() , R.color.green))
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
