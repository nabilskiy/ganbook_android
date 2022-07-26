package com.ganbook.dao;

import android.util.Log;

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

/**
 * Created by dmytro_vodnik on 9/12/15.
 * working on ganbook1 project
 */
public class PictureAnswerDAO extends BaseDaoImpl<PictureAnswer, String> {

    private static final String TAG = PictureAnswerDAO.class.getName();

    public PictureAnswerDAO(ConnectionSource connectionSource,
                            Class<PictureAnswer> dataClass) throws SQLException {
        super(connectionSource, dataClass);

    }

    public List<PictureAnswer> getAllPictures() throws SQLException {

        return this.queryForAll();
    }

    public List<PictureAnswer> getPicturesByAlbumId(String albumId) throws SQLException {

        QueryBuilder<PictureAnswer, String> queryBuilder =
                HelperFactory.getHelper().getPictureAnswerDAO().queryBuilder();

        Where<PictureAnswer, String> where = queryBuilder.where();

        SelectArg albumIdArg = new SelectArg();

        where.eq(PictureAnswer.ALBUM_ID_FIELD, albumIdArg);

        // prepare it so it is ready for later query or iterator calls
        PreparedQuery<PictureAnswer> preparedQuery = queryBuilder.prepare();

        albumIdArg.setValue(albumId);

        List<PictureAnswer> pictureAnswers = HelperFactory.getHelper().getPictureAnswerDAO().query(preparedQuery);

        Log.d(TAG, "picture models == " + pictureAnswers);

        if (pictureAnswers!=null && pictureAnswers.size() > 0)
            return pictureAnswers;

        return null;
    }
}
