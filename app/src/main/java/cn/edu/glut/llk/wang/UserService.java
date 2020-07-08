package cn.edu.glut.llk.wang;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserService {
    private DatabaseHelper dbHelper;
    public UserService(Context context){
        dbHelper=new DatabaseHelper(context);
    }

    public boolean login(String username,String password){
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="select * from user where username=? and password=?";
        Cursor cursor=sdb.rawQuery(sql, new String[]{username,password});
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        return false;
    }
    public boolean CheckUsername(String username){
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="select * from user where username=?";
        Cursor cursor=sdb.rawQuery(sql, new String[]{username});
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        return false;
    }
    public boolean register(String username,String password){
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="insert into user(username,password) values(?,?)";
        Object obj[]={ username, password};
        sdb.execSQL(sql, obj);
        return true;
    }
    public boolean savedata(String username,String score){
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="update user set maxscore=? where username=?";
        Object obj[]={score,username};
        sdb.execSQL(sql, obj);
        return true;
    }
    public ArrayList<String[]> getdata(){
        ArrayList<String[]> result = new ArrayList<>();
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="select * from user order by maxscore desc";
        Cursor cursor = sdb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);//获取第二列的值
            String score = cursor.getString(3);//获取第三列的值
            String[] list=new String[2];
            list[0]=name;
            list[1]=score;
            result.add(list);
        }
        cursor.close();
        return result;
    }
    public Map<String,String> getScore(){
        Map<String,String> result= new HashMap<>();
        int i=0;
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="select * from user order by maxscore desc";
        Cursor cursor = sdb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);//获取第二列的值
            String score = cursor.getString(3);//获取第三列的值
            result.put(name,score);
        }
        cursor.close();
        return result;
    }
}
