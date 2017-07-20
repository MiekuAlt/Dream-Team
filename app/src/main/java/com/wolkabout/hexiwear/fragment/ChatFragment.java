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

package com.wolkabout.hexiwear.fragment;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.model.Globals;
import com.wolkabout.hexiwear.service.BluetoothService;


public class ChatFragment extends Fragment {
    private BluetoothService bluetoothService;

    private EditText mToSendEditText;
    private Button mSendMessageButton;

    DatabaseReference mMessageDatabase;
    private static final int mToKeep = 50; //how many messages to keep
    private static int mNumMessages = 0;

    // Used in conjunction with the list view to show messages
    private ListView mConversationView;
    private ArrayAdapter<String> mConversationArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMessageDatabase = FirebaseDatabase.getInstance().getReference("messages");
    }

    @Override
    public void onStart() {
        super.onStart();
        setupChat();

        mMessageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {

                mConversationArrayAdapter.clear();

                for (DataSnapshot messageSnapshot : data.getChildren()) {
                    String msg = messageSnapshot.getValue(String.class);
                    mConversationArrayAdapter.add(msg);
                }

                mConversationView.setAdapter(mConversationArrayAdapter);

                if(!Globals.isCoach()) {
                    BluetoothService bs = new BluetoothService();
                    bs.vibrateWatch(10);
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mNumMessages - mToKeep > 0) {
            mMessageDatabase.limitToFirst(mNumMessages - mToKeep).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot data) {
                    //only start to delete the messages if there are
                    for (DataSnapshot messageSnapshot : data.getChildren()) {
                        messageSnapshot.getRef().removeValue();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
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

    /**
     * Initialize the variables required for the chat
     */
    private void setupChat() {
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

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
        updateNumMessages();
    }

    /**
     * Accessed by a user identified as either a coach or an athlete.
     *
     * Sends a message to be stored on the firebase server and displayed
     * in the chat fragment.
     *
     * @param msg Message to be sent
     */
    private void sendMessage(String msg) {
        if (msg.length() > 0) {
            mNumMessages ++;
            String id = mMessageDatabase.push().getKey();
            if(Globals.isCoach()) {
                mMessageDatabase.child(id).setValue("Coach:  " + msg);


            } else {
                mMessageDatabase.child(id).setValue("Athlete: " + msg);

            }
            mToSendEditText.setText("");

        }
    }



    /**
     * Record the number of messages
     */
    private void updateNumMessages() {
        mMessageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public final void onDataChange(DataSnapshot data) {
                mNumMessages = (int) data.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }




}
