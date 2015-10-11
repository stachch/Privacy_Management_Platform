package de.unistuttgart.ipvs.pmp.resourcegroups.database;

import java.util.Map;

/**
 * Base on the {@link android.database.sqlite.SQLiteDatabase} class, this interface provides methods to easily
 * create, modify, delete databases as well as execute SQL commands.
 */
interface IDatabaseConnection {

	/**
	 * Open a database for read/write access. Should be done before any other method call.
	 */
	 void open(String databaseName);

    /**
     *  Close and clean up the current connection. 
     */
    void close();
    
    /**
     * Return wether the requested table exists
     * 
     * @param tableName Name of the table
     * @return true if the table exists, false otherwise
     */
    boolean isTableExisted(String tableName);

    /**
     * Create a table if it doesn't already exist.
     * 
     * @param tableName Name of the table to be created. Table name can only
     * contains alphanumeric and underscore characters.
     * @param column A Map of column names and their descriptions, both must be
     * Strings and don't contain special characters. 
     * @param tableConstraint Describe the constraints for the table.
     * @return True if successful, false otherwise.
     */
    boolean createTable(String tableName, in Map columns, String tableConstraint);
    
    /**
     * Delete a table if it already exists.
     * 
     * @param tableName Name of the table to be deleted.
     * @return true if successful, false otherwise
     */
    boolean deleteTable(String tableName);

    /**
     * Convenience method for inserting a row into the database.
     * 
     * @param table the table to insert the row into
     * @param nullColumnHack optional; may be null. SQL doesn't allow inserting
     * a completely empty row without naming at least one column name. If your
     * provided values is empty, no column names are known and an empty row
     * can't be inserted. If not set to null, the nullColumnHack parameter
     * provides the name of nullable column name to explicitly insert a NULL
     * into in the case where your values is empty.
     * @param values this map contains the initial column values for the row.
     * The keys should be the column names in String and their values the
     * column's values in simple data types suitable for Interprocess Procedure
     * Call (IPC) supported by Android (String, Integer, Short, Long, Float,
     * Double, Byte, Boolean and []byte). Data types can be mixed and stored in
     * a Map<String, Object> object, incompatible types will be converted to
     * String using their .toString() methods so they may not be accurate or
     * worse, may lead to Exceptions.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    long insert(String table, String nullColumnHack, in Map values);

    /**
     * Convenience method for updating rows in the database.
     * 
     * @param table the table to update in
     * @param values a map from column names to new column values.
     * @param whereClause the optional WHERE clause to apply when updating.
     * Passing null will update all rows.
     * @param whereArgs Arguments for the where-clause if necessary.
     * @return the number of rows affected
     */
    int update(String table, in Map values, String whereClause, in String[] whereArgs);

    /**
     * Convenience method for deleting rows in the database.
     * 
     * @param table the table to delete from
     * @param whereClause the optional WHERE clause to apply when deleting.
     * Passing null will delete all rows.
     * @param whereArgs Arguments for the where-clause if necessary.
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise. To remove all rows and get a count pass "1" as the
     * whereClause.
     */
    int delete(String table, String whereClause, in String[] whereArgs);
    
    /**
     * <p>Query the given table, returning a cursor with limited number of rows to
     * this DatabaseConnection object, which can be accessed using the
     * {@link #getCurrentRow()}, {@link #getRowAt(int)}... methods.</p>
     * 
     * <p><b>CAUTION:</b> Unlike Android's Cursor, the cursor return from this
     * method will be pointed at the first row if it exists.</p>
     * 
     * @param table The table name to compile the query against.
     * @param columns A list of which columns to return. Passing null will
     * return all columns, which is discouraged to prevent reading data from
     * storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an
     * SQL WHERE clause (excluding the WHERE itself). Passing null will return
     * all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     * replaced by the values from selectionArgs, in order that they appear in
     * the selection. The values will be bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL
     * GROUP BY clause (excluding the GROUP BY itself). Passing null will cause
     * the rows to not be grouped.
     * @param having A filter declare which row groups to include in the cursor,
     * if row grouping is being used, formatted as an SQL HAVING clause
     * (excluding the HAVING itself). Passing null will cause all row groups to
     * be included, and is required when row grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause
     * (excluding the ORDER BY itself). Passing null will use the default sort
     * order, which may be unordered.
     * @param limit Limits the number of rows returned by the query, formatted
     * as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return Number of rows in the result
     */
    long queryWithLimit(String table, in String[] columns, String selection, in String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
    
    /**
     * <p>Query the given table, returning a cursor with limited number of rows to
     * this DatabaseConnection object, which can be accessed using the
     * {@link #getCurrentRow()}, {@link #getRowAt(int)}... methods.</p>
     * 
     * <p><b>CAUTION:</b> Unlike Android's Cursor, the cursor return from this
     * method will be pointed at the first row if it exists.</p>
     * 
     * @param table The table name to compile the query against.
     * @param columns A list of which columns to return. Passing null will
     * return all columns, which is discouraged to prevent reading data from
     * storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an
     * SQL WHERE clause (excluding the WHERE itself). Passing null will return
     * all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     * replaced by the values from selectionArgs, in order that they appear in
     * the selection. The values will be bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL
     * GROUP BY clause (excluding the GROUP BY itself). Passing null will cause
     * the rows to not be grouped.
     * @param having A filter declare which row groups to include in the cursor,
     * if row grouping is being used, formatted as an SQL HAVING clause
     * (excluding the HAVING itself). Passing null will cause all row groups to
     * be included, and is required when row grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause
     * (excluding the ORDER BY itself). Passing null will use the default sort
     * order, which may be unordered.
     * @param limit Limits the number of rows returned by the query, formatted
     * as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return Number of rows in the result
     */
    long query(String table, in String[] columns, String selection, in String[] selectionArgs, String groupBy, String having, String orderBy);
    
    /**
     * @return the current position of the cursor
     */
    long getRowPosition();

    /**
     * Return the value of a column at the current row as a String 
     * 
     * @param column index of the column
     * @return value of the requested column
     */
    String getAsString(int column);

    /**
     * Return the value of a column at the current row as an integer 
     * 
     * @param column index of the column
     * @return value of the requested column
     */
    int getAsInteger(int column);
    
    /**
     * Return the value of a column at the current row as a double 
     * 
     * @param column index of the column
     * @return value of the requested column
     */
    double getAsDouble(int column);

    /**
     * Move the cursor to a specific row and return all data values of that row
     * as a String array
     * 
     * @param position position of the appointed row
     * @return the data values of the requested row
     */
    String[] getRowAt(int position);
    
    /**
     * Return all data values from the current row as a String array
     * 
     * @return the data values of the current row
     */
    String[] getCurrentRow();

    /**
     * Return all data values from the current row as a String array and move
     * the cursor to the next row if possible.
     * 
     * @return the data values of the current row
     */
    String[] getRowAndNext();
    
    /**
     * Returns whether the cursor is pointing to the position after the last
     * row.
     * 
     * @return whether the cursor is pointing to the position after the last
     * row.
     */
    boolean isAfterLast();
    
    /**
     * Move the cursor to the next row.
     * 
     * @return whether the move succeeded.
     */
    boolean next();
    
    /**
     * Move the cursor to a new row
     * @param position the new row to move to
     * @return true if the the request destination is reachable, false otherwise
     */
    boolean goToRowPosition(int position);
}