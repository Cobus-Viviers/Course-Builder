package cobusviviers.corursecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Cobus Viviers on 2016/04/14.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private final static String DATABASENAME = "TargetDB";
    private final static int VERSION = 2;

    private final static String T_TARGET = "tblTarget";
    private final static String C_NO = "Number";
    private final static String C_DISTANCE = "Distance";
    private final static String C_REDUCER = "Reducer";
    private final static String C_POSITIONAL = "IsPositional";

    private Context _context;

    public DatabaseHandler(Context context) {
        super(context, DATABASENAME, null, VERSION);
        _context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + T_TARGET
                +"("+C_NO+" INTEGER, "
                +C_DISTANCE + " INTEGER, "
                +C_REDUCER + " INTEGER, "
                +C_POSITIONAL + " TEXT);";
        db.execSQL(createTable);
        Log.v("DEBUG", createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ T_TARGET);
    }

    public void add(Target target){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_NO, target.getNo());
        values.put(C_DISTANCE, target.getDistance());
        values.put(C_REDUCER, target.getReducer());
        values.put(C_POSITIONAL, target.isPositional());

        db.insert(T_TARGET, null, values);

    }

    public Target[] getTargets(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ T_TARGET, null );
        Target[] targets = new Target[cursor.getCount()];
        if (cursor.moveToFirst()) {
            for (int i = 0; i < targets.length; i++) {
                int distance = cursor.getInt(cursor.getColumnIndex(C_DISTANCE));
                int reducer = cursor.getInt(cursor.getColumnIndex(C_REDUCER));
                boolean isPositional = "1".equals(cursor.getString(cursor.getColumnIndex(C_POSITIONAL)));
                targets[i] = new Target(i+1, distance, reducer, isPositional, _context);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return targets;
    }

    public void deleteTarget(Target target){
        //TODO THIS COULD BE OPTIMISED SHOULD YOU RUN INTO PERFORMANCE ISSUES
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ T_TARGET +
                " WHERE (SELECT COUNT(*) FROM " + T_TARGET + " i WHERE i = '"+ (target.getNo()-1)+"');");
    }
}
