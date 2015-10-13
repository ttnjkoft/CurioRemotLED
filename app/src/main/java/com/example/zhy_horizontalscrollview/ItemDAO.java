package com.example.zhy_horizontalscrollview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NONO on 2015/8/31.
 */
// 資料功能類別
public class ItemDAO {
    // 表格名稱
    public static final String TABLE_NAME = "gelicadata";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String MAC_COLUMN = "mac";
    public static final String DEVICE_NAME_COLUMN = "devicename";
    public static final String SHAKE_COLUMN = "shake";


//    public static final String mac;
//    public static final String deviceNme;
//    public static final Boolean shakeFlag;


    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE "+ TABLE_NAME +"("+
//                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    MAC_COLUMN +"TEXT NOT NULL,"+
                    MAC_COLUMN +" TEXT PRIMARY KEY, "+
                    SHAKE_COLUMN+" INTEGER NOT NULL,"+
                     DEVICE_NAME_COLUMN+" TEXT NOT NULL);";



    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public ItemDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改

    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public boolean insert(String mac,int shake,String devicename) {
        // 建立準備新增資料的ContentValues物件
        String sql="insert or ignore into "+TABLE_NAME +" values('"
                +mac+"',"+shake+",'"+devicename+"')";

//        ContentValues cv = new ContentValues();
//        // 加入ContentValues物件包裝的新增資料
//        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
//        cv.put(MAC_COLUMN, mac);
//        cv.put(SHAKE_COLUMN,shake);
//        cv.put(DEVICE_NAME_COLUMN, devicename);



        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
//        long id = db.insert(TABLE_NAME, null, cv);
        db.execSQL(sql);

        // 回傳結果
//        return db.insert(TABLE_NAME, null, cv)>0;
        return true;
    }

    // 修改參數指定的物件
    public boolean update(String mac,Integer shake,String devicename)
    {
//        // 建立準備修改資料的ContentValues物件
//        ContentValues cv = new ContentValues();
//
//        // 加入ContentValues物件包裝的修改資料
//        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
//        cv.put(MAC_COLUMN, mac);
//        cv.put(SHAKE_COLUMN,shake);
//        cv.put(DEVICE_NAME_COLUMN, devicename);

//
//        // 設定修改資料的條件為編號
//        // 格式為「欄位名稱＝資料」
//        String where = MAC_COLUMN + "='" + mac+"'" ;
//
//        // 執行修改資料並回傳修改的資料數量是否成功
//        return db.update(TABLE_NAME, cv, where, null) > 0;

        String sql="UPDATE "+TABLE_NAME +" SET "+
                        SHAKE_COLUMN+" = "+shake+","+
                        DEVICE_NAME_COLUMN+" ='"+devicename+"' where "+
                        MAC_COLUMN + " = '" + mac+"'" ;
        db.execSQL(sql);
        return  true;
    }

    // 刪除參數指定編號的資料
    public boolean delete(String mac){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = MAC_COLUMN + "=" + mac;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // 讀取所有記事資料
    public List<Item> select(String mac) {
        List<Item> itemlist=new ArrayList<Item>();
        Item result = new Item();
        String where =MAC_COLUMN+ "=" +mac;
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+MAC_COLUMN+" = '"+mac +"'", null);
//        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null, null);
        while (cursor.moveToNext()) {
            result.setMac(cursor.getString(0));
            result.setShake(cursor.getInt(1));
            result.setDevicename(cursor.getString(2));
            itemlist.add(result);
        }
        cursor.close();
        return itemlist;
    }




    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }


}