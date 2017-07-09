package com.example.admin.messageboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/*
 * 进行数据库操作的类
 */
public class MyDataBase {

    Context context;
    MyOpenHelper myHelper;
    SQLiteDatabase myDatabase;

    //构造函数里面创建数据库
    public MyDataBase(Context con){
        this.context=con;
        myHelper=new MyOpenHelper(context);
    }

    /*
     * 得到ListView的数据，从数据库里找
     */
    public List<String> getArray(){

        List<String> array = new ArrayList<String>();

        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select content from Message" , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){

            String content = cursor.getString(cursor.getColumnIndex("content"));
            array.add(content);
            cursor.moveToNext();
        }
        myDatabase.close();

        return array;
    }

    /*
     * 增加新数据
     */
    public void toInsert(String string){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("insert into Message(content)values('"
                +string
                +"')");
        myDatabase.close();
    }
}
