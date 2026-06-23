package com.prayertimes.app.ui.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prayertimes.app.R;
import com.prayertimes.app.ai.GeminiClient;
import com.prayertimes.app.model.ChatMessage;
import com.prayertimes.app.ui.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class AiChatFragment extends Fragment {

    private RecyclerView  rvChat;
    private EditText      etMessage;
    private View          layoutTyping;
    private ChatAdapter   adapter;
    private GeminiClient  geminiClient;

    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        geminiClient = new GeminiClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_ai_chat, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        rvChat       = v.findViewById(R.id.rv_chat);
        etMessage    = v.findViewById(R.id.et_message);
        layoutTyping = v.findViewById(R.id.layout_typing);

        adapter = new ChatAdapter(messages);
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);
        rvChat.setLayoutManager(llm);
        rvChat.setAdapter(adapter);

        v.findViewById(R.id.btn_send).setOnClickListener(x -> sendMessage());
        v.findViewById(R.id.btn_clear_chat).setOnClickListener(x -> clearChat());

        etMessage.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        addWelcomeMessage();
    }

    private void addWelcomeMessage() {
        messages.add(new ChatMessage(
            "السلام عليكم ورحمة الله وبركاته 🌙\n\n" +
            "أنا مساعدك الإسلامي. يمكنك سؤالي عن:\n" +
            "• تفسير آيات القرآن الكريم\n" +
            "• أحكام الصلاة والعبادات\n" +
            "• الأحاديث النبوية الشريفة\n" +
            "• المسائل الفقهية\n\n" +
            "بم يمكنني مساعدتك؟",
            ChatMessage.TYPE_AI
        ));
        adapter.notifyItemInserted(0);
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        etMessage.setText("");

        messages.add(new ChatMessage(text, ChatMessage.TYPE_USER));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        layoutTyping.setVisibility(View.VISIBLE);

        geminiClient.sendMessage(text, new GeminiClient.Callback() {
            @Override
            public void onResponse(String response) {
                if (!isAdded()) return;
                layoutTyping.setVisibility(View.GONE);
                messages.add(new ChatMessage(response, ChatMessage.TYPE_AI));
                adapter.notifyItemInserted(messages.size() - 1);
                scrollToBottom();
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                layoutTyping.setVisibility(View.GONE);
                messages.add(new ChatMessage(error, ChatMessage.TYPE_AI));
                adapter.notifyItemInserted(messages.size() - 1);
                scrollToBottom();
            }
        });
    }

    private void clearChat() {
        int size = messages.size();
        messages.clear();
        adapter.notifyItemRangeRemoved(0, size);
        addWelcomeMessage();
    }

    private void scrollToBottom() {
        if (messages.isEmpty()) return;
        rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (geminiClient != null) geminiClient.shutdown();
    }
}
