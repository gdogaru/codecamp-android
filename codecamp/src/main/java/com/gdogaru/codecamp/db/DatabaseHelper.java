package com.gdogaru.codecamp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.gdogaru.codecamp.model.*;

import java.sql.SQLException;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "codecamp.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Session, Long> simpleRuntimeSessionDao = null;
    private RuntimeExceptionDao<Speaker, Long> simpleRuntimeSpeakerDao = null;
    private RuntimeExceptionDao<Sponsor, Long> simpleRuntimeSponsorDao = null;
    private RuntimeExceptionDao<Track, Long> simpleRuntimeTrackDao = null;
    private RuntimeExceptionDao<FeedbackLog, Long> simpleRuntimeFeedbackDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Session.class);
            TableUtils.createTable(connectionSource, Speaker.class);
            TableUtils.createTable(connectionSource, Sponsor.class);
            TableUtils.createTable(connectionSource, Track.class);
            TableUtils.createTableIfNotExists(connectionSource, FeedbackLog.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Session.class, true);
            TableUtils.dropTable(connectionSource, Speaker.class, true);
            TableUtils.dropTable(connectionSource, Sponsor.class, true);
            TableUtils.dropTable(connectionSource, Track.class, true);
//            TableUtils.dropTable(connectionSource, FeedbackLog.class, true); //this one we want to keep
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public void recreateStructure() {
        onUpgrade(getDB(), getConnectionSource(), DATABASE_VERSION, DATABASE_VERSION);
    }

    public RuntimeExceptionDao<Session, Long> getSessionDao() {
        if (simpleRuntimeSessionDao == null) {
            simpleRuntimeSessionDao = getRuntimeExceptionDao(Session.class);
        }
        return simpleRuntimeSessionDao;
    }

    public RuntimeExceptionDao<Speaker, Long> getSpeakerDao() {
        if (simpleRuntimeSpeakerDao == null) {
            simpleRuntimeSpeakerDao = getRuntimeExceptionDao(Speaker.class);
        }
        return simpleRuntimeSpeakerDao;
    }

    public RuntimeExceptionDao<Sponsor, Long> getSponsorDao() {
        if (simpleRuntimeSponsorDao == null) {
            simpleRuntimeSponsorDao = getRuntimeExceptionDao(Sponsor.class);
        }
        return simpleRuntimeSponsorDao;
    }

    public RuntimeExceptionDao<Track, Long> getTrackDao() {
        if (simpleRuntimeTrackDao == null) {
            simpleRuntimeTrackDao = getRuntimeExceptionDao(Track.class);
        }
        return simpleRuntimeTrackDao;
    }

    public SQLiteDatabase getDB() {
        return getWritableDatabase();
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
    }
}