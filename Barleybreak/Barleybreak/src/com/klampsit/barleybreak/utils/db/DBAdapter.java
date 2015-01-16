package com.klampsit.barleybreak.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "NAME";
	public static final String KEY_PTS = "POINTS";
	public static final String TAG = "DbAdapter";
	public static final String DATABASE_TABLE = "RECORDS";

	private static final String DATABASE_NAME = "RDB.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = String
			.format("create table %s (%s integer primary key autoincrement, %s text not null, %s integer not null);",
					DATABASE_TABLE, KEY_ROWID, KEY_NAME, KEY_PTS);

	public final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx){
		this.context = ctx;
		dbHelper = new DatabaseHelper(ctx);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(String.format("DROP TABLE IF EXISTS %s", DATABASE_TABLE));
			onCreate(db);
		}
	}

	public DBAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long insertRecord(String name, int points) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PTS, points);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public Cursor getAllRecords() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME,
				KEY_PTS }, null, null, null, null, KEY_PTS);
	}
	
	public void removeAllRecords(){
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", DATABASE_TABLE));
		db.execSQL(DATABASE_CREATE);
	}
}
