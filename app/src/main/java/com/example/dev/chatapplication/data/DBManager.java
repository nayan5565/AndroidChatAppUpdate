package com.example.dev.chatapplication.data;

import android.content.ContentValues;
import android.util.Log;

import com.example.dev.chatapplication.model.FriendNew;
import com.example.dev.chatapplication.tools.MainApplication;
import com.google.gson.Gson;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ASUS on 1/22/2018.
 */

public class DBManager {
    public static final String TABLE_FRIEND_CHAT = "tbl_friend_chat";



    private static final String DB_NAME = "friend_chat.db";


    private static final String CREATE_TABLE_WALLPAPER_CATEGORY = DBQuery.init()
            .newTable(TABLE_FRIEND_CHAT)
            .addField("categoryId", DBQuery.INTEGER_PRI)
            .addField("categoryTotalItem", DBQuery.INTEGER)
            .addField("categoryType", DBQuery.INTEGER)
            .addField("ordering", DBQuery.INTEGER)
            .addField("categoryUpdateAvailable", DBQuery.INTEGER)

            .addField("categoryKeyword", DBQuery.TEXT)
            .addField("categoryTitle", DBQuery.TEXT)
            .addField("categoryDetails", DBQuery.TEXT)
            .addField("categoryPhoto", DBQuery.TEXT)
            .addField("categoryThumb", DBQuery.TEXT)
            .getTable();


    private static DBManager instance;
    private final String TAG = getClass().getSimpleName();
    private SQLiteDatabase db;

    private DBManager() {
        openDB();
        createTable();
    }

    public static DBManager getInstance() {
        if (instance == null)
            instance = new DBManager();
        return instance;
    }

    public static String getQueryAll(String table, String primaryKey, String value) {
        return "select * from " + table + " where " + primaryKey + "='" + value + "'";
    }

    public static String getQueryDate(String table, String primaryKey) {
        return "select * from " + table + " where " + primaryKey + "='";
    }

    public static String getQueryAll(String table) {
        return "select * from " + table;
    }


    public static String getRecipeQueryJointTable(int id) {
        return "select a.Id,a.CategoryId,a.TypeOne,a.TypeTwo,a.TypeThree,a.TypeFour,a.TypeFive,a.recipeDelete,a.fav,a.view,a.Title,a.Thumb,a.Photo,a.Ingredients,a.Process,a.PPhoto,a.CategoryTitle,a.SearchTag,a.Video,a.price,a.DiscountRate,a.cartStatus,a.addCart,a.quantity,a.status,a.sizeView,a.color,b.isNew from tbl_recipe a left join tbl_new b on a.CategoryId=b.CategoryId AND a.Id=b.productId where a.CategoryId='" + id + "'";
    }




    private void openDB() {
        SQLiteDatabase.loadLibs(MainApplication.getInstance().getContext());
        File databaseFile = MainApplication.getInstance().getContext().getDatabasePath(DB_NAME);
        if (!databaseFile.exists()) {
            databaseFile.mkdirs();
            databaseFile.delete();
        }
        db = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
    }

    private void createTable() {
        db.execSQL(CREATE_TABLE_WALLPAPER_CATEGORY);
    }


    private String getStringValue(Cursor cursor, String key) {

        if (cursor.getColumnIndex(key) == -1)
            return "na";
        else
            return cursor.getString(cursor.getColumnIndex(key));
    }


    public <T> ArrayList<T> getData(String tableName, Object dataModelClass) {

        String sql = "select * from " + tableName;
        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonObject = new JSONObject();
        final ArrayList<JSONObject> data = new ArrayList<JSONObject>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                jsonObject = new JSONObject();
                try {
                    Class myClass = dataModelClass.getClass();
                    Field[] fields = myClass.getDeclaredFields();

                    for (Field field : fields) {
                        //for getting access of private field
                        field.setAccessible(true);
                        String name = field.getName();

                        jsonObject.put(name, getStringValue(cursor, name));

                    }
                    data.add(jsonObject);

                } catch (SecurityException ex) {
                } catch (IllegalArgumentException ex) {
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        Gson gson = new Gson();
        ArrayList<T> output = new ArrayList<T>();
        for (int i = 0; i < data.size(); i++) {
            dataModelClass = gson.fromJson(data.get(i).toString(), dataModelClass.getClass());
            output.add((T) dataModelClass);
        }


        return output;
    }



    public void addDownload(FriendNew mdownload, String tableName) {
        android.database.Cursor cursor = null;
        try {
            ContentValues values = new ContentValues();
            values.put("downloadFile", mdownload.getId());
            values.put("downloadFile", mdownload.getName());
            values.put("downloadFile", mdownload.getIdRoom());
            values.put("downloadFile", mdownload.getAvata());
            values.put("downloadFile", mdownload.getEmail());


            String sql = "select * from " + tableName + " where downloadFile='" + mdownload.getId() + "'";
            cursor = db.rawQuery(sql, null);
            Log.e("cu", "has" + cursor);
            if (cursor != null && cursor.getCount() > 0) {
                int update = db.update(tableName, values, "downloadFile=?", new String[]{mdownload.getId() + ""});
                Log.e("sublevel", "sub level update : " + update);
            } else {
                long v = db.insert(tableName, null, values);
                Log.e("sublevel", "sub level insert : " + v);

            }


        } catch (Exception e) {

        }
        if (cursor != null)
            cursor.close();
    }
}
