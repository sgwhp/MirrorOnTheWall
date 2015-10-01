package com.github.sgwhp.mirroronthewall.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.sql.SQLException;

/**
 * Created by robust on 2015/9/23.
 */
@EBean
public class CityDao {
    @RootContext
    Context context;
//    private Dao<City, String> dao;
//
//    @AfterInject
//    void init(){
//        try {
//            dao = DatabaseHelper.getInstance(context).getDao(City.class);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public String getCityCode(@NonNull String city) throws SQLException {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
        String sql = "select code from city where like(name||'%', ?);";
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(sql, new String[]{city});
            if(cursor.moveToNext()) {
                return cursor.getString(0);
            }
            return null;
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }

}
