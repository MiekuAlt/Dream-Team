/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wolkabout.hexiwear;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatFragment extends Fragment {

    private EditText mToSendEditText;
    private Button mSendMessageButton;

    DatabaseReference messageDatabase;

    // Used in conjunction with the list view to show messages
    private ListView mConversationView;
    private ArrayAdapter<String> mConversationArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        messageDatabase = FirebaseDatabase.getInstance().getReference("messages");

    }

    @Override
    public void onStart() {
        super.onStart();
        setupChat();

        messageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {

                mConversationArrayAdapter.clear();

                for (DataSnapshot messageSnapshot : data.getChildren()) {
                    String msg = messageSnapshot.getValue(String.class);
                    mConversationArrayAdapter.add("Me: " + msg);
                }

                mConversationView.setAdapter(mConversationArrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.message_view);
        mToSendEditText = (EditText) view.findViewById(R.id.message_text);
        mSendMessageButton = (Button) view.findViewById(R.id.send_button);
    }

    private void setupChat() {
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        //mConversationView.setAdapter(mConversationArrayAdapter);

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View view = getView();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.message_text);
                    String msg = tv.getText().toString();
                    sendMessage(msg);
                }
            }
        });

        // Initialize the buffer for outgoing messages
        // mOutStringBuffer = new StringBuffer(""); for later
    }

    private void sendMessage(String msg) {
        if (msg.length() > 0) {
            String id = messageDatabase.push().getKey();
            messageDatabase.child(id).setValue(msg);
            mToSendEditText.setText("");
        }
    }

}