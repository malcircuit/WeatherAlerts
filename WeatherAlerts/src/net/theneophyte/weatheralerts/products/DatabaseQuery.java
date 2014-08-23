package net.theneophyte.weatheralerts.products;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

//TODO Documentation
public class DatabaseQuery {
	private static final int COPY_LIMIT = 2;
	private static int sCopyAttempts = 0;
	
	private final ErrorHandler mErrorHandler;
	private final Context mContext;
	private final int mResId;
	private final String 
			mTable,
			mFilename,
			mPathname;
	
	private SQLiteDatabase mDatabase;
	
	public DatabaseQuery(Context context, String table, int resId) {
		mContext = context;
		mTable = table;
		mFilename = table + ".sqlite";
		mPathname = mContext.getFilesDir().getPath() + "/" + mFilename;
		mResId = resId;
		mErrorHandler = new ErrorHandler();
		mDatabase = openDb();
	}
	
	private SQLiteDatabase openDb(){
		SQLiteDatabase db;
		try{
			db = SQLiteDatabase.openDatabase(mPathname, null, SQLiteDatabase.OPEN_READONLY, mErrorHandler);
		}
		catch (SQLiteException sqle){
			if (copyDb()){
				db = SQLiteDatabase.openDatabase(mPathname, null, SQLiteDatabase.OPEN_READONLY, mErrorHandler);
			}
			else {
				throw new RuntimeException(mTable + " DB did not copy successfully.");
			}
		}
		
		return db;
	}
	
	private boolean copyDb(){
		InputStream in = mContext.getResources().openRawResource(mResId);
		FileOutputStream out = null;
		boolean result = false;
		try{
			out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			
			final byte[] buffer = new byte[512];
			int bytesRead = in.read(buffer);
			while (bytesRead >= 0){
				out.write(buffer, 0, bytesRead);
				out.flush();
				bytesRead = in.read(buffer);
			}
			
			result = true;		
		}
		catch (FileNotFoundException fnfe){
			/*
			 * There is no good reason (that I can think of) for the database
			 * file to not be found. We're creating it from scratch; it has 
			 * never existed. If we ever get to this point there is something 
			 * very weird happening in the system and we should give up.
			 */
			throw new AssertionError(fnfe);
		} catch (IOException ioe) {
			/*
			 * Basically the same as above.  If we get here that means we can't
			 * read/write the database file and there's not really anything that 
			 * can be done to counter it.  We need this file to run properly. 
			 */
			throw new RuntimeException(ioe);
		}
		finally{
			try {
				in.close();
				if (out != null){
					out.close();
				}
			}
			catch (IOException ioe){
				/*
				 * Seriously, what the hell am I supposed to do here? Manually
				 * shift the bits around to close the stream?  If the VM can't
				 * do it, who can?  Sometimes I wonder why I ever bother with
				 * this computer stuff...
				 */
				throw new RuntimeException(ioe);
			}
		}
		
		return result;
	}
	
	private class ErrorHandler implements DatabaseErrorHandler{
		
		@Override
		public void onCorruption(SQLiteDatabase dbObj) {
			if (sCopyAttempts <= COPY_LIMIT){
				copyDb();
				dbObj = openDb();
				sCopyAttempts++;
			}
			else {
				throw new RuntimeException(mTable + " DB is corrupted");
			}
		}

	}
	
	public Cursor query(boolean distinct, String[] columns, String selection, String[] selectionArgs){
		if (mDatabase == null || !mDatabase.isOpen()){
			mDatabase = openDb();
		}
		
		return mDatabase.query(distinct, mTable, columns, selection, selectionArgs, null, null, null, null);
	}
}
