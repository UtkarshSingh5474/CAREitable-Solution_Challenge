package com.example.careitable.activity;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.careitable.Message;
import com.example.careitable.MessageAdapter;
import com.example.careitable.R;
import com.example.careitable.dao.CharitySuggestObject;
import com.example.careitable.databinding.ActivityChatBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    int nonAI = 0;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    String apikey = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference apikeyReg = db.collection("APIKeys");
        apikeyReg.document("openai").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    apikey = document.getString("apikey");
                    Log.d(TAG, "DocumentSnapshot data: " + apikey);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
//            if(question.toLowerCase().contains("suggest")){
//                messageList.add(new Message("Typing... ",Message.SENT_BY_BOT));
//                messageList.remove(messageList.size()-1);
//                addToChat("Based on your specified requirements, the most suitable charity for your donation would be the Pratham Educational Foundation. Tap on this message to Donate.",Message.SENT_BY_BOT);
//                welcomeTextView.setVisibility(View.GONE);
//            }else {
//                callAPI(question);
//                welcomeTextView.setVisibility(View.GONE);
//            }
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    void callAPI(String question){

        AtomicReference<String> systemPrompt = new AtomicReference<>("You are an assistive chatbot called CAREtaker for an Charities and Donations app called CAREitable.Your capabilities: 1.Suggest charity, 2.Charity/donation reasons 3.etc");

        if ((question.toLowerCase().contains("suggest") && (question.toLowerCase().contains("charity") || question.toLowerCase().contains("charities")))) {


            messageList.add(new Message("Typing... ",Message.SENT_BY_BOT));
            // Fetch data from "Charities" collection in Firebase
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference charitiesCollection = db.collection("Charities");

            charitiesCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    List<CharitySuggestObject> charities = new ArrayList<>();

                    // Filter the documents based on criteria (e.g., demographics, sectors)
                    for (DocumentSnapshot document : documents) {

                        String organisationName = document.getString("nameOrganisation");
                        String organisationType = document.getString("typeOrganisation");
                        String charitySectors = document.getString("sectorsCharity");
                        String charityDemographics = document.getString("demographicsCharity");
                        charities.add(new CharitySuggestObject(organisationName, organisationType, charitySectors, charityDemographics));
                    }

                    // Extract required fields from matchingDocuments
                    String extractedData = null;
                    for (int i=0;i<charities.size();i++) {

                        String name = charities.get(i).getOrganisationName();
                        String type = charities.get(i).getOrganisationType();
                        String sectors = charities.get(i).getCharitySector();
                        String demographics = charities.get(i).getCharityDemographics();

                        // Build a string with the extracted data and add it to the extractedData list
                        extractedData = extractedData+ " "+"Charity: " + (i+1) + " Name: " + name + ", Type: " + type + ", Sectors: " + sectors + ", Demographics: " + demographics +"\n";


                    }
                    systemPrompt.set(systemPrompt + "Charity Data:" + extractedData);

                    sendAndFetchResponse(systemPrompt.get(),question);

                    //suggest a charity that supports health, employement and education
        } });}else {
            messageList.add(new Message("Typing... ",Message.SENT_BY_BOT));
            sendAndFetchResponse(systemPrompt.get(),question);
            Log.d(TAG, "hiTest: "+systemPrompt.get()+" "+question);
        }
    }

    void sendAndFetchResponse(String systemPrompt,String question){
        //okhttp

        JSONObject jsonBody = new JSONObject();
        try {

            JSONArray messagesArray = new JSONArray();
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "system");
            messageObj.put("content", systemPrompt+"");
            messagesArray.put(messageObj);
            messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", question);
            messagesArray.put(messageObj);


            jsonBody.put("model","gpt-3.5-turbo");
            jsonBody.put("messages",messagesArray);
            jsonBody.put("max_tokens",256);
            jsonBody.put("temperature",0.5);


            Log.d(TAG, "callAPI: "+jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Log.d(TAG, "callAPI: "+body.toString());
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer "+apikey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to API KEY ISSUE or "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
//                        jsonObject = new JSONObject(response.body().string());
//                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
//                        String result = jsonArray.getJSONObject(0).getString("text");
//                        addResponse(result.trim());
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray choicesArray = jsonObject.getJSONArray("choices");
                        JSONObject choiceObj = choicesArray.getJSONObject(0);
                        JSONObject messageObj = choiceObj.getJSONObject("message");
                        addResponse(messageObj.getString("content").trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    addResponse("Failed to load response due to "+response.body().toString());
                }
            }
        });
    }
}
