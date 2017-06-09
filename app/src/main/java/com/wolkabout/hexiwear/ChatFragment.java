package com.wolkabout.hexiwear;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatFragment extends Fragment {

    private ListView mConversationView;
    private EditText mToSendEditText;
    private Button mSendMessageButton;

    // Used in conjunction with the list view to show messages
    private ArrayAdapter<String> mConversationArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

}
