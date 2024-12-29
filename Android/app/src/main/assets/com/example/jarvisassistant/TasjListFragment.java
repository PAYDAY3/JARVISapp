package com.example.jarvisassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> tasks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasks = new ArrayList<>();
        // Add some sample tasks
        tasks.add(new Task("1", "Task 1", "Description 1", null, Task.Priority.HIGH, "Work"));
        tasks.add(new Task("2", "Task 2", "Description 2", null, Task.Priority.MEDIUM, "Personal"));
        tasks.add(new Task("3", "Task 3", "Description 3", null, Task.Priority.LOW, "Shopping"));

        adapter = new TaskAdapter(tasks);

        RecyclerViewDragDropManager dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);

        RecyclerView.Adapter wrappedAdapter = dragDropManager.createWrappedAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(wrappedAdapter);

        dragDropManager.attachRecyclerView(recyclerView);
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>
            implements DraggableItemAdapter<TaskAdapter.TaskViewHolder> {

        private List<Task> tasks;

        TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        @Override
        public boolean onCheckCanStartDrag(@NonNull TaskViewHolder holder, int position, int x, int y) {
            return true;
        }

        @Nullable
        @Override
        public ItemDraggableRange onGetItemDraggableRange(@NonNull TaskViewHolder holder, int position) {
            return null;
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            if (fromPosition == toPosition) {
                return;
            }

            Task movedItem = tasks.remove(fromPosition);
            tasks.add(toPosition, movedItem);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }

        @Override
        public void onItemDragStarted(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
            notifyDataSetChanged();
        }

        class TaskViewHolder extends AbstractDraggableItemViewHolder {
            // Add views for task item

            TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views
            }

            void bind(Task task) {
                // Bind task data to views
            }
        }
    }
}

