package com.example.admin.messageboard;

import android.content.Intent;
import android.os.*;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout layout = null;
    private List<String> list = new ArrayList<>();
    private ListView listView = null;
    private ListAdapter adapter = null;
    private MyDataBase mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.lv_bwlList);

        /*
         * 取出数据
         */
        mdb = new MyDataBase(this);
        list = mdb.getArray();
        //调整排列
        Collections.reverse(list);
        adapter = new ListAdapter(list,MainActivity.this);
        listView.setAdapter(adapter);

        addMsg();
        refreshData();

        /*
		 * 点击listView里面的item,查看留言
		 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent=new Intent(MainActivity.this,More.class);
                intent.putExtra("text",list.get(position));
                startActivity(intent);
            }
        });

    }

    private void addMsg(){

        /*
         * 点击新建留言
         */
        ImageButton imageButton = (ImageButton)findViewById(R.id.buttonAdd);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewMessage.class);
                startActivity(intent);
            }
        });
    }

    private void refreshData(){

        /*
         * 下拉刷新的代码
         */

        layout = (SwipeRefreshLayout)findViewById(R.id.activity_main);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //线程发送网络请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        BufferedReader reader = null;
                        HttpURLConnection connection = null;
                        try {
                            URL url = new URL("http://mclee.cn/getJson.php");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(8000);
                            connection.setReadTimeout(8000);
                            InputStream in = connection.getInputStream();
                            reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            /*
                             * 操作数据，跳转到UI操作
                             */
                            if (list.size() == 0) {
                                downLoadData(response.toString());
                            } else {
                                upDataData(response.toString());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (connection != null) {
                                connection.disconnect();
                            }
                        }
                    }
                }).start();
            }
        });

        /*
         * 解决 SwipeRefreshLayout 下拉刷新与 ListView 下拉的冲突
         */
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            //只有 ListView 到最顶部才刷新
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    layout.setEnabled(true);
                else
                    layout.setEnabled(false);
            }
        });
    }

    /*
     * 进行UI操作
     */
    private void downLoadData(final String response){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jsonObject = array.getJSONObject(i);
                        String result = jsonObject.getString("message");
                        list.add(result);
                    }

                    /*
                     * 数据库增加数据
                     */
                    for (String string:list) {

                        mdb.toInsert(string);
                    }

                    //调整排列顺序
                    Collections.reverse(list);
                    
                    adapter = new ListAdapter(list, MainActivity.this);
                    listView.setAdapter(adapter);

                    layout.setRefreshing(false);

                    Toast.makeText(MainActivity.this, "网络请求成功", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
     * 进行UI操作
     */
    private void upDataData(final String response){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                list.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jsonObject = array.getJSONObject(i);
                        String result = jsonObject.getString("message");
                        list.add(result);
                    }
                    Collections.reverse(list); // 调整排列顺序
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                layout.setRefreshing(false);
            }
        });
    }
}
