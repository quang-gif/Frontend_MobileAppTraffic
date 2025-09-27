package com.example.frontend_mobileapptraffic.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend_mobileapptraffic.Adapter.ChatAdapter;
import com.example.frontend_mobileapptraffic.Presenter.ChatbotPresenter;
import com.example.frontend_mobileapptraffic.R;
import com.example.frontend_mobileapptraffic.model.ChatMessage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class chatbotactivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private FloatingActionButton sendButton;
    private ChipGroup quickQuestionsChipGroup;

    private ChatAdapter chatAdapter;
    private final List<ChatMessage> messages = new ArrayList<>();

    private ChatbotPresenter presenter;

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot);

        presenter = new ChatbotPresenter(this);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput     = findViewById(R.id.messageInput);
        sendButton       = findViewById(R.id.sendButton);
        topAppBar = findViewById(R.id.topAppBar);
        setupRecyclerView();
        setupSendButton();
        topAppBar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(chatbotactivity.this, homeactivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> {
            String question = messageInput.getText().toString().trim();
            if (TextUtils.isEmpty(question)) {
                Toast.makeText(this, "Bạn chưa nhập câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }

            addMessage(new ChatMessage(question, true)); // user
            messageInput.setText("");

            presenter.sendQuestion(question, new ChatbotPresenter.ChatbotCallback() {
                @Override public void onSuccess(String answer) {
                    addMessage(new ChatMessage(answer, false)); // bot
                }
                @Override public void onError(String errorMessage) {
                    addMessage(new ChatMessage("⚠️ " + errorMessage, false));
                }
            });
        });
    }

    private void addMessage(ChatMessage msg) {
        messages.add(msg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
    }

}
