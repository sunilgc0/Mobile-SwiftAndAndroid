package edu.asu.msse.sgowdru.moviemediaplayerrpc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/* Copyright 2016 Sunil Gowdru C,
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
        *
        * Purpose: Show Movie description and Video if available using JSON-RPC server streamer
        * By reading from Native Database if necessary or by requesting data from OMDB and storing in database
        * Also allowing Editing, removing, manual add etc
        *
        * I hereby give the instructors, TA right to use of building and evaluating
        * the software package for the purpose of determining grade and program assessment.
        *
        * SER 598 - Mobile Systems
        * @author Sunil Gowdru C
        * mailto:sunil.gowdru@asu.edu
        * Software Engineering, CIDSE, IAFSE, ASU Poly
        * @version April 2016
        */

public class MoviesDB extends SQLiteOpenHelper {
    private static final boolean debugon = false;
    private static final int DATABASE_VERSION = 3;
    private static String dbName = "moviesdb";
    private final Context context;
    private String dbPath;
    private SQLiteDatabase crsDB;

    public MoviesDB(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        this.context = context;
        dbPath = context.getFilesDir().getPath() + "/";
        android.util.Log.d(this.getClass().getSimpleName(), "dbpath: " + dbPath);
    }

    public void createDB() throws IOException {
        this.getReadableDatabase();
        try {
            copyDB();
        } catch (IOException e) {
            android.util.Log.w(this.getClass().getSimpleName(),
                    "createDB Error copying database " + e.getMessage());
        }
    }

    /**
     * does the database exist and has it been initialized? This method determines whether
     * the database needs to be copied to the data/data/pkgName/databases directory by
     * checking whether the file exists. If it does it checks to see whether the db is
     * uninitialized or whether it has the course table.
     *
     * @return false if the database file needs to be copied from the assets directory, true
     * otherwise.
     */
    private boolean checkDB() {    //does the database exist and is it initialized?
        SQLiteDatabase checkDB = null;
        boolean ret = false;
        try {
            String path = dbPath + dbName + ".db";
            debug("MoviesDB --> checkDB: path to db is", path);
            File aFile = new File(path);
            if (aFile.exists()) {
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
                if (checkDB != null) {
                    debug("MoviesDB --> checkDB", "opened db at: " + checkDB.getPath());
                    Cursor tabChk = checkDB.rawQuery("SELECT name FROM sqlite_master where type='table' and name='Movies';", null);
                    boolean crsTabExists = false;
                    if (tabChk == null) {
                        debug("MoviesDB --> checkDB", "check for Movies table result set is null");
                    } else {
                        tabChk.moveToNext();
                        debug("MoviesDB --> checkDB", "check for course table result set is: " +
                                ((tabChk.isAfterLast() ? "empty" : (String) tabChk.getString(0))));
                        crsTabExists = !tabChk.isAfterLast();
                    }
                    if (crsTabExists) {
                        Cursor c = checkDB.rawQuery("SELECT * FROM Movies", null);
                        c.moveToFirst();
                        while (!c.isAfterLast()) {
                            String crsName = c.getString(0);
                            int crsid = c.getInt(1);
                            debug("MoviesDB --> checkDB", "Movies table has Movie Names: " +
                                    crsName + "\tCourseID: " + crsid);
                            c.moveToNext();
                        }
                        ret = true;
                    }
                }
            }
        } catch (SQLiteException e) {
            android.util.Log.w("MoviesDB->checkDB", e.getMessage());
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return ret;
    }

    public void copyDB() throws IOException {
        try {
            if (!checkDB()) {
                // only copy the database if it doesn't already exist in my database directory
                debug("MoviesDB --> copyDB", "checkDB returned false, starting copy");
                InputStream ip = context.getResources().openRawResource(R.raw.moviedb);
                // make sure the database path exists. if not, create it.
                File aFile = new File(dbPath);
                if (!aFile.exists()) {
                    aFile.mkdirs();
                }
                String op = dbPath + dbName + ".db";
                OutputStream output = new FileOutputStream(op);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = ip.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                ip.close();
            }
        } catch (IOException e) {
            android.util.Log.w("MoviesDB --> copyDB", "IOException: " + e.getMessage());
        }
    }

    public SQLiteDatabase openDB() throws SQLException {
        String myPath = dbPath + dbName + ".db";
        if (checkDB()) {
            crsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            debug("MoviesDB --> openDB", "opened db at path: " + crsDB.getPath());
        } else {
            try {
                this.copyDB();
                crsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            } catch (Exception ex) {
                android.util.Log.w(this.getClass().getSimpleName(), "unable to copy and open db: " + ex.getMessage());
            }
        }
        return crsDB;
    }

    @Override
    public synchronized void close() {
        if (crsDB != null)
            crsDB.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void debug(String hdr, String msg) {
        if (debugon) {
            android.util.Log.d(hdr, msg);
        }
    }

}
