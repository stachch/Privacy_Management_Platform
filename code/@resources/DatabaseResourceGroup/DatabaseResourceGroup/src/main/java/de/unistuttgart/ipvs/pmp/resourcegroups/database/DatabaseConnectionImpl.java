/*
 * Copyright 2012 pmp-android development team
 * Project: DatabaseResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.database;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.SetPrivacySetting;

@SuppressWarnings("rawtypes")
public class DatabaseConnectionImpl extends IDatabaseConnection.Stub {
    
    // Regular expressions used to check SQL queries from apps 
    private static final String TABLE_NAME = "^[a-zA-Z0-9_]+$";
    private static final String COLUMN_NAME = "^((_id\\s+){0,1}[a-z0-9_]+)$";
    private static final String COLUMN_TYPE = "^[a-z0-9_\\s]+$";
    private static final String TABLE_CONSTRAINTS = "^[a-z\\s]+\\(\\s*[a-z0-9_]+\\s*(\\s*[,]\\s*[a-z0-9_]+)*\\)[a-z0-9_\\s]*$";
    
    private String dbName;
    private int dbVersion = 1;
    private String appID;
    
    private Context context;
    private DatabaseResource resource;
    private DatabaseOpenHelper helper;
    private SQLiteDatabase db;
    
    private Cursor cursor;
    
    
    public DatabaseConnectionImpl(Context context, DatabaseResource resource, String appIdentifier) {
        this.context = context;
        this.resource = resource;
        this.appID = appIdentifier;
        this.dbName = null;
    }
    
    
    @Override
    public void open(String databaseName) throws RemoteException {
        /* Database should be at least allowed to be read and database name should be allowed. */
        if (!isDatabaseAllowed(databaseName) || (!isRead() && !isModify())) {
            throw new SecurityException("The Database is not allowed to be accessed.");
        }
        
        close();
        
        if (databaseName.equals("private")) {
            this.dbName = "dbRg_private_" + this.appID;
        } else {
            this.dbName = "dbRg_public_" + databaseName;
        }
    }
    
    
    @Override
    public void close() throws RemoteException {
        if (this.cursor != null) {
            this.cursor.close();
            this.cursor = null;
        }
        if (this.db != null) {
            this.db.close();
            this.db = null;
        }
        if (this.helper != null) {
            this.helper.close();
            this.helper = null;
        }
    }
    
    
    @Override
    public boolean isTableExisted(String tableName) throws RemoteException {
        if (isRead() || isModify()) {
            String[] args = new String[1];
            args[0] = strip(tableName);
            closeCursor();
            openReadableDB();
            try {
                this.cursor = this.db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", args);
                if (this.cursor.getCount() > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLiteException e) {
                Log.v(this, "Caught SQLiteExcetion: ", e);
                return false;
            }
            //            if (cursor == null || cursor.getCount()<1) {
            //                return false;
            //            } else {
            //            }
        } else {
            throw new SecurityException();
        }
    }
    
    
    @Override
    public boolean createTable(String tableName, Map columns, String tableConstraint) throws RemoteException {
        if (isCreate()) {
            if ("".equals(tableName.trim()) || columns == null) {
                return false;
            }
            if (columns.isEmpty()) {
                return false;
            }
            
            // Check the tableName
            tableName = strip(tableName);
            if (!Pattern.matches(TABLE_NAME, tableName)) {
                Log.d(this, "Table name '" + tableName + "' invalid. " + TABLE_NAME);
                return false;
            }
            
            // Build a SQL Statement
            StringBuffer sql = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
            // CREATE TABLE IF NOT EXISTS "main"."appointments" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "description" TEXT, "appointment" TEXT)
            sql.append(tableName).append(" (");
            Pattern pColName = Pattern.compile(COLUMN_NAME, Pattern.CASE_INSENSITIVE);
            Matcher m1 = pColName.matcher("");
            Pattern pType = Pattern.compile(COLUMN_TYPE, Pattern.CASE_INSENSITIVE);
            Matcher m2 = pType.matcher("");
            
            // Check the column descriptions
            for (Object obj : columns.entrySet()) {
                Entry e = (Entry) obj;
                if (m1.reset(strip(e.getKey().toString())).find() && m2.reset(strip(e.getValue().toString())).find()) {
                    sql.append("\"").append(strip(e.getKey().toString())).append("\" ")
                            .append(strip(e.getValue().toString())).append(", ");
                } else {
                    Log.d(this, "Column description is not valid: " + e.toString());
                    return false;
                }
            }
            if (tableConstraint != null) {
                Pattern p = Pattern.compile(TABLE_CONSTRAINTS, Pattern.CASE_INSENSITIVE);
                m1 = p.matcher(strip(tableConstraint));
                if (m1.find()) {
                    sql.append(strip(tableConstraint)).append(")");
                }
            } else {
                sql.delete(sql.length() - 2, sql.length()).append(")");
            }
            
            // Execute the query and return result
            closeCursor();
            openWritableDB();
            Log.v(this, "Execute create table SQL query: " + sql.toString());
            try {
                this.cursor = this.db.rawQuery(sql.toString(), null);
            } catch (SQLiteException e) {
                Log.e(this, "Error creating table: ", e);
            }
            Log.v(this, "Table creation result: " + this.cursor.getCount());
            if (this.cursor.getCount() != -1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    
    @Override
    public long insert(String table, String nullColumnHack, Map values) throws RemoteException {
        if (isModify()) {
            openWritableDB();
            long result = -1;
            try {
                result = this.db.insert(table, nullColumnHack, getContentValues(values));
            } catch (SQLiteException e) {
                Log.v(this, "Caught SQLiteExcetion: ", e);
            }
            return result;
        } else {
            throw new SecurityException();
        }
    }
    
    
    @Override
    public int delete(String table, String whereClause, String[] whereArgs) throws RemoteException {
        if (isModify()) {
            openWritableDB();
            return this.db.delete(table, whereClause, whereArgs);
        } else {
            throw new SecurityException();
        }
    }
    
    
    @Override
    public int update(String table, Map values, String whereClause, String[] whereArgs) throws RemoteException {
        if (isModify()) {
            openWritableDB();
            return this.db.update(table, getContentValues(values), whereClause, whereArgs);
        } else {
            throw new SecurityException();
        }
    }
    
    
    @Override
    public String[] getCurrentRow() throws RemoteException {
        if (this.cursor == null || !isRead()) {
            throw new SecurityException();
        } else if (this.cursor.isAfterLast() || this.cursor.isBeforeFirst()) {
            return null;
        } else {
            String[] row = new String[this.cursor.getColumnCount()];
            for (int i = 0; i < this.cursor.getColumnCount(); i++) {
                row[i] = this.cursor.getString(i);
            }
            return row;
        }
    }
    
    
    @Override
    public double getAsDouble(int column) throws RemoteException {
        if (this.cursor == null || !isRead()) {
            throw new SecurityException();
        } else if (this.cursor.getColumnCount() <= column) {
            RemoteException ex = new RemoteException();
            ex.initCause(new IndexOutOfBoundsException());
            throw ex;
        } else {
            return (this.cursor.getDouble(column));
        }
    }
    
    
    @Override
    public int getAsInteger(int column) throws RemoteException {
        if (this.cursor == null || !isRead()) {
            throw new SecurityException();
        } else if (this.cursor.getColumnCount() <= column) {
            RemoteException ex = new RemoteException();
            ex.initCause(new IndexOutOfBoundsException());
            throw ex;
        } else {
            return (this.cursor.getInt(column));
        }
    }
    
    
    @Override
    public String[] getRowAndNext() throws RemoteException {
        if (!isRead()) {
            throw new SecurityException();
        } else if (this.cursor == null) {
            return null;
        } else if (this.cursor.isAfterLast() || this.cursor.isBeforeFirst()) {
            return null;
        } else {
            String[] row = new String[this.cursor.getColumnCount()];
            for (int i = 0; i < this.cursor.getColumnCount(); i++) {
                row[i] = this.cursor.getString(i);
            }
            this.cursor.moveToNext();
            return row;
        }
        
    }
    
    
    @Override
    public String[] getRowAt(int position) throws RemoteException {
        if (this.cursor == null || !isRead()) {
            throw new SecurityException();
        } else if ((this.cursor.getCount() <= position) || (position < 0)) {
            return null;
        } else {
            this.cursor.moveToPosition(position);
            String[] row = new String[this.cursor.getColumnCount()];
            for (int i = 0; i < this.cursor.getColumnCount(); i++) {
                row[i] = this.cursor.getString(i);
            }
            return row;
        }
    }
    
    
    @Override
    public long getRowPosition() throws RemoteException {
        if (this.cursor != null || !isRead()) {
            return this.cursor.getPosition();
        } else {
            return -1;
        }
    }
    
    
    @Override
    public String getAsString(int column) throws RemoteException {
        if (this.cursor == null || !isRead()) {
            throw new SecurityException();
        } else if (this.cursor.getColumnCount() <= column) {
            RemoteException ex = new RemoteException();
            ex.initCause(new IndexOutOfBoundsException());
            throw ex;
        } else {
            return this.cursor.getString(column);
        }
    }
    
    
    @Override
    public boolean goToRowPosition(int position) throws RemoteException {
        if (this.cursor == null || !isRead()) {
            return false;
        } else {
            return this.cursor.moveToPosition(position);
        }
    }
    
    
    @Override
    public boolean next() throws RemoteException {
        if (this.cursor == null || !isRead()) {
            return false;
        } else {
            return this.cursor.moveToNext();
        }
    }
    
    
    @Override
    public long query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
            String having, String orderBy) throws RemoteException {
        if (isRead()) {
            closeCursor();
            openReadableDB();
            try {
                this.cursor = this.db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            } catch (SQLiteException e) {
                Log.v(this, "Caught SQLiteExcetion: ", e);
            }
            if (this.cursor == null) {
                return -1;
            } else {
                this.cursor.moveToNext();
                return this.cursor.getCount();
            }
        } else {
            throw new SecurityException();
        }
    }
    
    
    @Override
    public long queryWithLimit(String table, String[] columns, String selection, String[] selectionArgs,
            String groupBy, String having, String orderBy, String limit) throws RemoteException {
        if (isRead()) {
            closeCursor();
            openReadableDB();
            try {
                this.cursor = this.db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            } catch (SQLiteException e) {
                Log.v(this, "Caught SQLiteException: ", e);
            }
            if (this.cursor == null) {
                return -1;
            } else {
                this.cursor.moveToNext();
                return this.cursor.getCount();
            }
        } else {
            throw new SecurityException();
        }
    }
    
    
    @Override
    public boolean deleteTable(String tableName) throws RemoteException {
        if (isCreate()) {
            if ("".equals(tableName.trim())) {
                return false;
            }
            
            // Check the tableName
            tableName = strip(tableName);
            if (!Pattern.matches(DatabaseConnectionImpl.TABLE_NAME, tableName)) {
                Log.d(this, "Table name '" + tableName + "' invalid. " + DatabaseConnectionImpl.TABLE_NAME);
                return false;
            }
            
            // Build a SQL Statement
            String sql = "DROP TABLE IF EXISTS " + tableName;
            closeCursor();
            openWritableDB();
            try {
                this.cursor = this.db.rawQuery(sql, null);
            } catch (SQLiteException e) {
                Log.v(this, "Caught SQLiteExcetion: ", e);
                return false;
            }
            
            // TODO Check table's existence
            if (this.cursor.getCount() >= 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    
    @Override
    public boolean isAfterLast() throws RemoteException {
        if (this.cursor == null) {
            return true;
        } else {
            return this.cursor.isAfterLast();
        }
    }
    
    
    /**
     * Close the cursor and release it resources.
     */
    private void closeCursor() {
        if (this.cursor != null) {
            this.cursor.close();
        }
    }
    
    
    // TODO Check content type!
    private ContentValues getContentValues(Map values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        ContentValues cv = new ContentValues();
        for (Object value : values.entrySet()) {
            // TODO test if set is in right order??????????????????????????????
            Entry e = (Entry) value;
            if (String.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (String) e.getValue());
            } else if (Integer.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (Integer) e.getValue());
            } else if (Long.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (Long) e.getValue());
            } else if (Short.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (Short) e.getValue());
            } else if (Boolean.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (Boolean) e.getValue());
            } else if (Float.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (Float) e.getValue());
            } else if (Double.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (Double) e.getValue());
            } else if (Byte.class.equals(e.getValue().getClass())) {
                cv.put(e.getKey().toString(), (Byte) e.getValue());
            } else {
                try {
                    byte[] b = (byte[]) e.getValue();
                    cv.put(e.getKey().toString(), b);
                } catch (Exception e2) {
                    cv.put(e.getKey().toString(), e.getValue().toString());
                }
            }
        }
        return cv;
    }
    
    
    private String strip(String s) {
        return s.trim().replaceAll("(\\s+)", " ");
    }
    
    
    private boolean isCreate() {
        try {
            return ((BooleanPrivacySetting) this.resource.getPrivacySetting(Database.PS_CREATE)).permits(this.appID,
                    true);
        } catch (PrivacySettingValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    
    private boolean isModify() {
        try {
            return ((BooleanPrivacySetting) this.resource.getPrivacySetting(Database.PS_MODIFY)).permits(this.appID,
                    true);
        } catch (PrivacySettingValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    
    private boolean isRead() {
        try {
            return ((BooleanPrivacySetting) this.resource.getPrivacySetting(Database.PS_READ))
                    .permits(this.appID, true);
        } catch (PrivacySettingValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    
    @SuppressWarnings("unchecked")
    private boolean isDatabaseAllowed(String databaseName) {
        try {
            Set<String> referenceSet = new HashSet<String>();
            referenceSet.add(databaseName);
            return ((SetPrivacySetting<String>) this.resource.getPrivacySetting(Database.PS_ALLOWED_DATABASES))
                    .permits(this.appID, referenceSet);
        } catch (PrivacySettingValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return false;
    }
    
    
    /**
     * Open a writable database object if necessary.
     */
    private void openWritableDB() {
        if (this.db == null) {
            // Open a database connection
            if (this.helper == null) {
                // TODO Feature: open other databases
                this.helper = new DatabaseOpenHelper(this.context, this.dbName, this.dbVersion);
            }
            this.db = this.helper.getWritableDatabase();
        } else if (this.db.isReadOnly()) {
            this.db.close();
            if (this.helper == null) {
                // TODO Feature: open other databases
                this.helper = new DatabaseOpenHelper(this.context, this.dbName, this.dbVersion);
            }
            this.db = this.helper.getWritableDatabase();
        }
    }
    
    
    /**
     * Open a read-only database object if necessary. If a database's already available, it will be used, even though it
     * could be writable, since every actions are carefully checked
     */
    private void openReadableDB() {
        if (this.db == null) {
            // Open a database connection
            if (this.helper == null) {
                // TODO Feature: open other databases
                this.helper = new DatabaseOpenHelper(this.context, this.dbName, this.dbVersion);
            }
            this.db = this.helper.getReadableDatabase();
        }
    }
}
