package com.ganbook.dao;

import android.util.Log;

import com.ganbook.models.DrawingAnswer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.utils.DBUtils.HelperFactory;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class DrawingAnswerDAO extends BaseDaoImpl<DrawingAnswer, String> {
    private static final String TAG = DrawingAnswerDAO.class.getName();

    public DrawingAnswerDAO(ConnectionSource connectionSource,
                            Class<DrawingAnswer> dataClass) throws SQLException {
        super(connectionSource, dataClass);

    }

    public List<DrawingAnswer> getAllDrawings() throws SQLException {

        return this.queryForAll();
    }

    public List<DrawingAnswer> getDrawingsByAlbumId(String albumId) throws SQLException {

        QueryBuilder<DrawingAnswer, String> queryBuilder =
                HelperFactory.getHelper().getDrawerAnswerDAO().queryBuilder();

        Where<DrawingAnswer, String> where = queryBuilder.where();

        SelectArg albumIdArg = new SelectArg();

        where.eq(PictureAnswer.ALBUM_ID_FIELD, albumIdArg);

        // prepare it so it is ready for later query or iterator calls
        PreparedQuery<DrawingAnswer> preparedQuery = queryBuilder.prepare();

        albumIdArg.setValue(albumId);

        List<DrawingAnswer> drawingAnswers = HelperFactory.getHelper().getDrawerAnswerDAO().query(preparedQuery);

        Log.d(TAG, "picture models == " + drawingAnswers);

        if (drawingAnswers !=null && drawingAnswers.size() > 0)
            return drawingAnswers;

        return null;
    }
}
