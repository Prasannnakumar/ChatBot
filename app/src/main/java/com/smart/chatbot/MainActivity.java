package com.smart.chatbot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    private RecyclerView chatsRV;
    private FloatingActionButton sendMsgFAB;
    private EditText userMsgEdt;
    // creating a variable for
    // our volley request queue.s

    // creating a variable for array list and adapter class.
    private ArrayList<ChatsModal> chatsModalArrayList;
    private MessageRVAdapter chatRVAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // on below line we are initializing all our views.
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgFAB = findViewById(R.id.idFABSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        chatsModalArrayList = new ArrayList<>();
        chatRVAdapter = new MessageRVAdapter(chatsModalArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatRVAdapter);

        // adding on click listener for send message button.

        sendMsgFAB.setOnClickListener(view -> {
            if (userMsgEdt.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Your Message", Toast.LENGTH_SHORT).show();
                return;
            }
            getResponse(userMsgEdt.getText().toString());
            userMsgEdt.setText("");
        });
    }

    private void getResponse(String message) {
        chatsModalArrayList.add(new ChatsModal(message, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=159341&key=5RocuiewXOd5AeDi&uid=[uid]&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if (response.isSuccessful()) {
                    MsgModal modal = response.body();
                    chatsModalArrayList.add(new ChatsModal(modal.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();

                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {

                chatsModalArrayList.add(new ChatsModal("Please revert Your question", BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
            }
        });
    }
}