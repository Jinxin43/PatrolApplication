package com.example.dingtu2.myapplication.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.dingtu2.myapplication.db.GenDataBase.DATABASE_NAME;

/**
 * Created by Dingtu2 on 2017/6/9.
 */

public class DatabaseCreator {

    private static final Object LOCK = new Object();
    private static DatabaseCreator sInstance;
    private static AtomicBoolean mInitializing = new AtomicBoolean(true);
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();
    private GenDataBase mDb;

    public synchronized static DatabaseCreator getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new DatabaseCreator();
                }
            }
        }

        return sInstance;
    }

    /**
     * Used to observe when the database initialization is done
     */
    public LiveData<Boolean> isDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    @Nullable
    public GenDataBase getDatabase() {
        return mDb;
    }

    public void createDB(Context context) {
        Log.d("DatabaseCreator", "Creating DB from " + Thread.currentThread().getName());
        if (!mInitializing.compareAndSet(true, false)) {
            return;//如果已经初始化过，不在初始化
        }

        mIsDatabaseCreated.setValue(false);
        new AsyncTask<Context, Void, Void>() {
            @Override
            protected Void doInBackground(Context... params) {

                Log.d("DatabaseCreator",
                        "Starting bg job " + Thread.currentThread().getName());
                Context context = params[0].getApplicationContext();
//                context.deleteDatabase(DATABASE_NAME);
                GenDataBase db = Room.databaseBuilder(context.getApplicationContext(), GenDataBase.class, DATABASE_NAME).allowMainThreadQueries().build();

                mDb = db;

                return null;
            }

            protected void onPostExecute(Void ignored) {
                mIsDatabaseCreated.setValue(true);
            }
        }.execute(context.getApplicationContext());

    }
}
