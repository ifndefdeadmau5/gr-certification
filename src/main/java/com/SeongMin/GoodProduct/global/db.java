package com.SeongMin.GoodProduct.global;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class db {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    private static final String TAG = "NotesDbAdapter";
    /**
     * Database creation sql statement
     */

    private static final String DATABASE_COMPANY_CREATE = "CREATE TABLE 'company' (" +
            "'companyname'    TEXT," +
            "'representative' TEXT," +
            "'address'        TEXT," +
            "'favorite'    INTEGER DEFAULT 0," +
            "'stdcount'    INTEGER," +
            "FOREIGN KEY('companyname') REFERENCES grlist ( companyname )" +
            ");";

    private static final String DATABASE_FAVORITE_CREATE = "CREATE TABLE IF NOT EXISTS favorite ( "
            + "'areacode'      TEXT,"
            + "'standardcode'  TEXT,"
            + "'standardname'  TEXT,"
            + "'orddate'       TEXT,"
            + "'udtdate'       TEXT,"
            + "'attachfile'    TEXT,"
            + "'attachfileUrl' TEXT,"
            + "'companycount', INTEGER);";

    private static final String DATABASE_GRLIST_CREATE = "CREATE TABLE IF NOT EXISTS 'grlist' (" +
            "'companyname'  TEXT," +
            "'standardname' TEXT," +
            "'standardcode' TEXT," +
            "'areacode'     TEXT," +
            "'type'         TEXT," +
            "'enddate'      TEXT" +
            ");";

    private static final String DATABASE_FAVORITE_DROP = "drop table favorite;";
    private static final String DATABASE_COMPANY_DROP = "drop table company;";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_COMPANY = "company";
    private static final String DATABASE_TABLE_FAVORITE = "favorite";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public db(Context ctx) {
        this.mCtx = ctx;
    }

    public db open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        //mDb = mDbHelper.getReadableDatabase();

        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertFavorite(String areacode, String standardcode, String standardname, String orddate, String udtdate, String attachfile, String attachfileUrl, int Companycount) {
        mDb.execSQL("INSERT INTO favorite(areacode,standardcode,standardname,orddate,udtdate,attachfile,attachfileUrl,Companycount) VALUES('" + areacode + "'," +
                " '" + standardcode + "'," +
                " '" + standardname + "'," +
                " '" + orddate + "'," +
                " '" + udtdate + "'," +
                " '" + attachfile + "'," +
                " '" + attachfileUrl + "'," +
                " '" + Companycount + "')");
    }

    public void deleteFavorite(String code) {

        mDb.execSQL("DELETE FROM favorite WHERE standardcode=('" + code + "')");
    }

    public void updateCompany(String compname, boolean like) {
        mDb.execSQL("UPDATE company SET favorite = '" + (like ? 1 : 0) + "' WHERE companyname='" + compname + "'");
    }

    public void updateCompanyStdcount(String compname) {
        mDb.execSQL("UPDATE company SET stdcount = stdcount +1 WHERE companyname='" + compname + "'");
    }

    public boolean isCompanyLiked(String compname) {
        Cursor mCursor;
        int isLiked;
        // 이중 쿼리
        mCursor = mDb.rawQuery("SELECT favorite FROM company WHERE companyname='" + compname + "'", null);
        mCursor.moveToFirst();
        isLiked = mCursor.getInt(0);
        mCursor.close();

        if (0 == isLiked)
            return false;
        else
            return true;
    }

    public Cursor selectAllFavorite() {
        return mDb.rawQuery("SELECT * from favorite", null);
    }

    public void createCompanyData(String companyname, String representative, String address) {
        mDb.execSQL("INSERT INTO company ( companyname, representative, address ) VALUES ('" + companyname + "'," +
                " '" + representative + "'," +
                " '" + address + "')");
    }

    public void createGRlistdata(String companyname, String standardname, String standardcode, String areacode, String type, String enddate) {
        mDb.execSQL("INSERT INTO grlist ( companyname, standardname, standardcode, areacode, type, enddate ) VALUES ('" + companyname + "'," +
                " '" + standardname + "'," +
                " '" + standardcode + "'," +
                " '" + areacode + "'," +
                " '" + type + "'," +
                " '" + enddate + "')");
    }


    /*
     * 표준번호로 업체검색. 종류가 포함되지 않은 쿼리이므로 중복된 업체가 검색된다
     * 우선은 DISTINCT 사용
     */
    public Cursor SelectAllCompanyByCode(String Code) throws SQLException {
        Cursor mCursor;
        // 이중 쿼리
        mCursor = mDb.rawQuery("SELECT * FROM company WHERE companyname IN \n" +
                "( SELECT companyname FROM grlist WHERE standardcode='" + Code + "');", null);

        return mCursor;
    }

    public Cursor SelectCompanyByCompanyname(String compname) throws SQLException {
        Cursor mCursor;
        // 이중 쿼리
        mCursor = mDb.rawQuery("SELECT * FROM company WHERE companyname LIKE '%" + compname + "%';", null);

        return mCursor;
    }

    public Cursor SelectAllLikedCompany() throws SQLException {
        Cursor mCursor;
        // 이중 쿼리
        mCursor = mDb.rawQuery("SELECT * FROM company WHERE favorite = 1", null);

        return mCursor;
    }

    public int getCompanyCountByCode(String Code) throws SQLException {
        Cursor mCursor;
        int count = 0;
        mCursor = mDb.rawQuery("SELECT COUNT(*) FROM company WHERE companyname IN \n" +
                "( SELECT companyname FROM grlist WHERE standardcode='" + Code + "')", null);

        mCursor.moveToFirst();
        count = mCursor.getInt(0);
        mCursor.close();

        return count;
    }

    public int getStandardCountByCompanyName(String companyname) {
        Cursor mCursor;
        int count = 0;

        mCursor = mDb.rawQuery("SELECT COUNT(*) FROM GRLIST WHERE companyname='" + companyname + "'", null);
        mCursor.moveToFirst();
        count = mCursor.getInt(0);
        mCursor.close();

        return count;
    }

    public Cursor getStandardListByCompanyName(String companyname) {
        Cursor mCursor;

        mCursor = mDb.rawQuery("select * from grlist where companyname = '" + companyname + "'", null);

        return mCursor;
    }

    public boolean isDuplicate(String code) {
        Cursor mCursor;
        int result;
        mCursor = mDb.rawQuery("SELECT count(*) FROM favorite WHERE standardcode = '" + code + "'", null);
        mCursor.moveToFirst();
        result = mCursor.getInt(0);
        mCursor.close();
        return (1 == result) ? true : false;
    }

    public boolean isEmpty() {
        Cursor mCursor;
        mCursor = mDb.rawQuery("SELECT count(*) FROM company", null);
        boolean isCursorEmpty = !mCursor.moveToFirst();

        if (mCursor.moveToFirst()) {
            for (; ; ) {
                Log.e("mytag", "테이블 : " + mCursor.getInt(0));
                if (!mCursor.moveToNext())
                    break;
            }
        }

        if (!isCursorEmpty) {
            mCursor.moveToFirst();
            if (mCursor.getInt(0) < 5) {
                return true;
            } else {
                return false;
            }
        }
        Log.i("my", "CursorEmtpy");
        mCursor.close();
        return true;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_COMPANY_CREATE);
            db.execSQL(DATABASE_FAVORITE_CREATE);
            db.execSQL(DATABASE_GRLIST_CREATE);
            Log.w("DB", "excuted");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS company");
            db.execSQL("DROP TABLE IF EXISTS favorite");
            onCreate(db);
        }
    }
}
