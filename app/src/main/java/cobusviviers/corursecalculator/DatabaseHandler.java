package cobusviviers.corursecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Cobus Viviers on 2016/04/14.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private final static String DATABASENAME = "TargetDB";
    private final static int VERSION = 2;

    private final static String T_TARGET = "tblTarget";
    private final static String C_ID = "ID";
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
                +"("+ C_ID +" INTEGER PRIMARY KEY, "
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

    public Target add(Target target) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // values.put(C_ID, target.getNo());
        values.put(C_DISTANCE, target.getDistance());
        values.put(C_REDUCER, target.getReducer());
        values.put(C_POSITIONAL, target.isPositional());

       long id = db.insert(T_TARGET, null, values);

       target.setiD((int)id);
       return target;
    }

    public Target[] getTargets(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ T_TARGET, null );
        Target[] targets = new Target[cursor.getCount()];
        if (cursor.moveToFirst()) {
            for (int i = 0; i < targets.length; i++) {
                int id = cursor.getInt(cursor.getColumnIndex(C_ID));
                int distance = cursor.getInt(cursor.getColumnIndex(C_DISTANCE));
                int reducer = cursor.getInt(cursor.getColumnIndex(C_REDUCER));
                boolean isPositional = "1".equals(cursor.getString(cursor.getColumnIndex(C_POSITIONAL)));
                targets[i] = new Target(id, i+1, distance, reducer, isPositional, _context);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return targets;
    }

    public void deleteTarget(Target target){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ T_TARGET +" WHERE " + C_ID + " = "+ target.getiD()+";");
    }

    public void updateTarget(Target target){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_POSITIONAL, target.isPositional());
        values.put(C_REDUCER, target.getReducer());
        values.put(C_DISTANCE, target.getDistance());

        db.update(T_TARGET, values, C_ID + " = ?", new String[]{Integer.toString(target.getiD())});
    }
}
