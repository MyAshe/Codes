package com.example.admin.messageboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class More extends AppCompatActivity {

    TextView textView = null;
    String content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        Intent intent = this.getIntent();
        textView = (TextView)findViewById(R.id.contentIn);
        content = intent.getStringExtra("text");

        textView.setText(content);
    }
}