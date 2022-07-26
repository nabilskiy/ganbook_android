package com.ganbook.utils.DBUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ganbook.dao.DrawingAnswerDAO;
import com.ganbook.dao.PictureAnswerDAO;
import com.ganbook.models.DrawingAnswer;
import com.ganbook.models.PictureAnswer;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getName();

    private static String DATABASE_NAME ="ganbook.db";

    private static final int DATABASE_VERSION = 2;

    private PictureAnswerDAO pictureAnswerDAO = null;
    private DrawingAnswerDAO drawingAnswerDAO = null;

    Context context;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try {

            TableUtils.createTableIfNotExists(connectionSource, PictureAnswer.class);
        }

        catch (SQLException e){

            Log.e(TAG, "error creating DB " + DATABASE_NAME);

            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){

        switch (oldVer) {
            case 1:
                updateFromVersion1(db, connectionSource, oldVer, newVer);
                break;
            default:
                // no updates needed
                break;
        }
    }

    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
        pictureAnswerDAO = null;
        drawingAnswerDAO = null;
    }

    private void updateFromVersion1(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {

        Log.d(TAG, "updating DB from ver 1, adding table alarms ");

        try {
            HelperFactory.getHelper().getPictureAnswerDAO().executeRaw("ALTER TABLE `pictureanswer` ADD COLUMN resid STRING;");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        onUpgrade(db, connectionSource, oldVer + 1, newVer);
    }

    public PictureAnswerDAO getPictureAnswerDAO() throws SQLException{
        if(pictureAnswerDAO == null){
            pictureAnswerDAO = new PictureAnswerDAO(getConnectionSource(), PictureAnswer.class);
        }
        return pictureAnswerDAO;
    }

    public DrawingAnswerDAO getDrawerAnswerDAO() throws SQLException{
        if(drawingAnswerDAO == null){
            drawingAnswerDAO = new DrawingAnswerDAO(getConnectionSource(), DrawingAnswer.class);
        }
        return drawingAnswerDAO;
    }
}
