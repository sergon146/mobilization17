package com.sergon146.mobilization17.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbHelper extends SQLiteOpenHelper implements DbContract{
    private Context context;
    private static String DB_PATH;
    public  static SQLiteDatabase dataBase;

    public DbHelper(Context context) {
        super(context, DbContract.DB_NAME, null, 1);
        this.context = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        if (isDbExist()) {
            System.out.println("Database exists");
            openDataBase();
        } else {
            Log.w("DB", "Database doesn't exist");
            try {
                createDataBase();
            } catch (IOException e) {
                Log.w("DB", "Cannot create DB");
            }
        }
    }

    public void createDataBase() throws IOException {
        if (isDbExist()) {
            System.out.println(" Database exists.");
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean isDbExist() {

        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DbContract.DB_NAME;
            File dbFile = new File(myPath);
            checkdb = dbFile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copyDataBase() throws IOException {
        InputStream inStream = context.getAssets().open(DbContract.DB_NAME);
        String outFileName = DB_PATH + DbContract.DB_NAME;
        OutputStream myoutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inStream.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }

        myoutput.flush();
        myoutput.close();
        inStream.close();
    }

    private void openDataBase() throws SQLException {
        dataBase = SQLiteDatabase.openDatabase(DB_PATH + DbContract.DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if (dataBase != null) {
            dataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}