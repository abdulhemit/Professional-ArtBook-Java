package com.example.professionalartbookjava;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {
    static final String PROVIDER_NAME= "com.example.professionalartbookjava.ArtContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/arts";
    static final Uri Content_URI = Uri.parse(URL);


    static final String NAME = "name";
    static final String IMAGE = "image";

    static final int ARTS = 1;
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
    }


    private static HashMap<String,String> ART_PROJECTION_MAP;
    //------------Database--------------
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Arts";
    static final String ARTS_TABLE_NAME = "arts";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DATABASE_TABLE = "CREATE TABLE "+
            ARTS_TABLE_NAME + "(name  TEXT NOT NULL , "+
            "image BLOB NOT NULL);";


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + ARTS_TABLE_NAME);
            onCreate(db);
        }


    }

    //------------Database--------------


    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();

        return db!= null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String s1) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;

            default:
                //
        }
        if (s1 == null || s1.matches("")){
            s1 = NAME;
        }
        Cursor cursor = sqLiteQueryBuilder
                .query(db,projection,selection,selectionArgs,null,null,s1);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long rowId = db.insert(ARTS_TABLE_NAME,"",contentValues);
        if (rowId > 0){
            Uri newUri = ContentUris.withAppendedId(Content_URI,rowId);
            getContext().getContentResolver().notifyChange(newUri,null);
            return newUri;
        }
       throw new SQLException("Error!");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowCount = 0;
        switch (uriMatcher.match(uri)){
            case ARTS:
                //delete
                rowCount = db.delete(ARTS_TABLE_NAME,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Failed uri");

        }
        getContext().getContentResolver().notifyChange(uri,null );
        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        int rowCount = 0;
        switch (uriMatcher.match(uri)){
            case ARTS:
                //update
                rowCount = db.update(ARTS_TABLE_NAME,contentValues,s,strings);
                break;
            default:
                throw new IllegalArgumentException("Failed uri");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowCount;
    }
}
