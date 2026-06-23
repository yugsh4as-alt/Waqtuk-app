package com.prayertimes.app.ui;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prayertimes.app.R;
import com.prayertimes.app.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ChatMessage msg = messages.get(pos);

        h.tvSender.setText(msg.isUser() ? "أنت" : "المساعد الإسلامي");
        h.tvMessage.setText(msg.text);

        LinearLayout container = (LinearLayout) h.itemView;

        if (msg.isUser()) {
            container.setGravity(Gravity.END);
            h.tvSender.setGravity(Gravity.END);
            h.tvMessage.setGravity(Gravity.END);
            h.tvMessage.setBackgroundColor(Color.parseColor("#1B3A52"));
        } else {
            container.setGravity(Gravity.START);
            h.tvSender.setGravity(Gravity.START);
            h.tvMessage.setGravity(Gravity.START);
            h.tvMessage.setBackgroundColor(Color.parseColor("#1A2D40"));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvMessage;

        ViewHolder(@NonNull View v) {
            super(v);
            tvSender  = v.findViewById(R.id.tv_sender);
            tvMessage = v.findViewById(R.id.tv_message);
        }
    }
}
