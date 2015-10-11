/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
 * Project-Site: https://github.com/stachch/Privacy_Management_Platform
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import de.unistuttgart.ipvs.pmp.shared.Log;

/**
 * This is a helper for opening the database used by PMP.<br/>
 * It automatically creates the required tables in the SQLite database.<br/>
 * A database instance can be got by calling {@link DatabaseOpenHelper#getWritableDatabase()}.
 * 
 * @author Jakob Jarosch
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    
    private static final String TAG = "DatabaseOpenHelper";
    
    /**
     * Name of the database.
     */
    private static final String DB_NAME = "pmp-database";
    
    /**
     * Current database version.
     */
    private static final int DB_VERSION = 5;
    
    /**
     * The context used to open the files from assets folder.
     */
    private Context context;
    
    /**
     * The query builder
     */
    private SQLiteQueryBuilder sqlqb;
    
    
    /**
     * List of all SQL-files for database-creation, the key is the version of the database.
     */
    private static final String whereIsSql(int dbVersion) {
        return String.format("database-v%d.sql", dbVersion);
    }
    
    
    /**
     * List of all SQL-files for database-clean, the key is the version of the database.
     */
    private static final String whereIsCleanSql(int dbVersion) {
        return String.format("database-v%d-clean.sql", dbVersion);
    }
    
    
    /**
     * DatabaseHelper-Constructor.
     */
    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.sqlqb = new SQLiteQueryBuilder();
    }
    
    
    /**
     * Called when the database is opened first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(this, "Creating database structure");
        
        String sqlQueries = readSqlFile(whereIsSql(DB_VERSION));
        
        if (sqlQueries != null) {
            Log.d(this, "Executing " + whereIsSql(DB_VERSION) + " ...");
            if (DatabaseOpenHelper.executeMultipleQueries(db, sqlQueries)) {
                Log.d(this, "Created database");
            } else {
                Log.w(this, "Database not created");
            }
            
        }
    }
    
    
    /**
     * Called when upgrading from a previous version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this, "Update request " + oldVersion + " to " + newVersion);
        
        if ((newVersion == DB_VERSION) && (oldVersion < DB_VERSION)) {
            Log.d(this, "Forcing db re-creation");
            
            // delete everything in sight
            Cursor c = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata'", null);
            if (c.moveToFirst()) {
                do {
                    db.execSQL("DROP TABLE " + c.getString(c.getColumnIndex("name")));
                } while (c.moveToNext());
            }
            c.close();
            
            // rerun creation
            onCreate(db);
        }
    }
    
    
    /**
     * Cleans all data from the tables.
     */
    public void cleanTables() {
        Log.d(this, "Cleaning database.");
        
        String sqlQueries = readSqlFile(whereIsCleanSql(DB_VERSION));
        
        if (sqlQueries != null) {
            Log.d(this, "Executing " + whereIsCleanSql(DB_VERSION) + " ...");
            SQLiteDatabase sqldb = getWritableDatabase();
            try {
                if (DatabaseOpenHelper.executeMultipleQueries(sqldb, sqlQueries)) {
                    Log.d(this, "Cleaned database");
                } else {
                    Log.w(this, "Database not cleaned");
                }
            } finally {
                sqldb.close();
            }
        }
    }
    
    
    /**
     * Read a SQL file from assets folder.
     * 
     * @param filename
     *            Filename of the SQL file.
     * 
     * @return String represented SQL query from the file. null if the file could not be READ.
     */
    public String readSqlFile(String filename) {
        String sqlQuery = null;
        
        try {
            InputStream is = this.context.getAssets().open(filename);
            InputStreamReader bis = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(bis, 4096);
            StringBuilder sb = new StringBuilder();
            try {
                String curLine = null;
                while ((curLine = br.readLine()) != null) {
                    sb.append(curLine);
                    sb.append("\n");
                }
                
            } finally {
                br.close();
                bis.close();
                is.close();
            }
            
            sqlQuery = sb.toString();
            
        } catch (IOException e) {
            Log.e(this, "Reading the SQL file from " + filename + " failed.", e);
            sqlQuery = null;
        }
        
        return sqlQuery;
    }
    
    
    /**
     * This method executes multiple queries, which are concatenated by a semicolon.
     * 
     * @param db
     *            The {@link SQLiteDatabase} which should be used to execute the queries.
     * @param queries
     *            The queries which should be executed.
     * @return true, if and only if all queries were executed
     */
    public static boolean executeMultipleQueries(SQLiteDatabase db, String queries) {
        boolean result = true;
        
        Log.v(TAG, "------- Executing SQL Queries  ------");
        
        for (String query : queries.split(";")) {
            
            /* Skipping, empty query */
            if (query.trim().length() == 0) {
                continue;
            }
            
            Log.v(TAG, query);
            
            try {
                db.execSQL(query);
            } catch (SQLException e) {
                Log.e(TAG, "Got an SQLException while executing query", e);
                result = false;
            }
        }
        
        Log.v(TAG, "------- SQL Queries Complete   ------");
        
        return result;
    }
    
    
    public SQLiteQueryBuilder builder() {
        return this.sqlqb;
    }
    
    
    /**
     * Go in there, print everything you can find on LogCat.
     */
    public void debug() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata'", null);
            if (c.moveToFirst()) {
                do {
                    String tbl = c.getString(c.getColumnIndex("name"));
                    Log.d(this, "TABLE '" + tbl + "'");
                    this.sqlqb.setTables(tbl);
                    
                    // null anyone?
                    Cursor c2 = this.sqlqb.query(db, null, null, null, null, null, null);
                    if (c2.moveToFirst()) {
                        do {
                            Log.d(this, "  row " + (1 + c2.getPosition()) + " of " + c2.getCount());
                            
                            for (int i = 0; i < c2.getColumnCount(); i++) {
                                Log.d(this, "     " + c2.getColumnName(i) + " => '" + c2.getString(i) + "'");
                            }
                            
                        } while (c2.moveToNext());
                    } else {
                        Log.d(this, "  empty table");
                    }
                    c2.close();
                    
                } while (c.moveToNext());
            }
            c.close();
        } finally {
            db.close();
        }
    }
    
}
