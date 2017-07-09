package com.example.admin.messageboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NewMessage extends AppCompatActivity {

    private String message = null;
    private MyDataBase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        myDatabase=new MyDataBase(this);

        sendMessage();
    }

    private void sendMessage(){

        /*
         * 新建留言
         */
        final EditText editText = (EditText)findViewById(R.id.content);
        ImageButton imageButton = (ImageButton)findViewById(R.id.saveButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = editText.getText().toString();

                if(!"".equals(message)){

                    myDatabase.toInsert(message);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            try {
//                                HttpURLConnection connection = null;
//                                URL url = new URL("http://mclee.cn/postJson.php");
//                                connection = (HttpURLConnection) url.openConnection();
//                                connection.setRequestMethod("POST");
//                                connection.setConnectTimeout(8000);
//                                connection.setReadTimeout(8000);
//                                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//
//                                JSONObject obj = new JSONObject();
//                                obj.put("message",message);
//                                out.writeBytes(obj.toString());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }).start();

                    RequestQueue queue = Volley.newRequestQueue(NewMessage.this);

                    StringRequest request = new StringRequest(
                            Request.Method.POST,
                            "http://mclee.cn/postJson.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> map = new HashMap<>();
                            map.put("json",message);
                            return map;
                        }
                    };
                    queue.add(request);

                    finish();
                }
                else{
                    Toast.makeText(NewMessage.this, "输入内容不要为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
