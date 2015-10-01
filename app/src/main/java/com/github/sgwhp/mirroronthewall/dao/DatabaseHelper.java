package com.github.sgwhp.mirroronthewall.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.sgwhp.mirroronthewall.util.Util;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;

/**
 * 数据库帮助类
 * Created by robust on 2015/9/23.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static volatile DatabaseHelper databaseHelper;
    /**数据库名称*/
    public static final String DB_NAME = "db.db";
    /**数据库版本，数据库升级除修改此处外，还需要执行"pragma user_version"升级assets数据库文件*/
    private static final int DB_VERSION = 4;

    private DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseHelper getInstance(Context context){
        if(databaseHelper == null){
            synchronized (DatabaseHelper.class){
                if(databaseHelper == null){
                    databaseHelper = new DatabaseHelper(context);
                }
            }
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    private static void execSQLs(SQLiteDatabase db, String[] sqls){
        final int N = sqls.length;
        for(int i = 0; i < N; i++){
            db.execSQL(sqls[i]);
        }
    }

    public static void copyDbFile(Context context){
        File dbFile = context.getDatabasePath(DB_NAME);
        if(!dbFile.exists()){
            Util.copyFileFromAsset(context, DB_NAME, dbFile.getParent(), DB_NAME);
        }
    }
}
