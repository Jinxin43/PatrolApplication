package com.example.dingtu2.myapplication.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.example.dingtu2.myapplication.db.DatabaseCreator;
import com.example.dingtu2.myapplication.db.entity.RoundEntity;

/**
 * Created by Dingtu2 on 2017/6/17.
 */


public class RoundViewModel extends AndroidViewModel {

    private static final MutableLiveData ABSENT = new MutableLiveData();
    final DatabaseCreator databaseCreator;
    private LiveData<RoundEntity> mObervableOnGoingRound;

    {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    public RoundViewModel(Application application, final String userKey) {
        super(application);

        databaseCreator = DatabaseCreator.getInstance(application);
        final LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();

        mObervableOnGoingRound = Transformations.switchMap(databaseCreated, new Function<Boolean, LiveData<RoundEntity>>() {
            @Override
            public LiveData<RoundEntity> apply(Boolean aBoolean) {
                if (Boolean.TRUE.equals(aBoolean)) {
                    return databaseCreator.getDatabase().roundDao().getGoingRound(userKey);
                } else {
                    return ABSENT;
                }
            }
        });
    }

    public LiveData<RoundEntity> getmObervableOnGoingRound() {
        return mObervableOnGoingRound;
    }

    public void saveRound(RoundEntity round) {
        if (databaseCreator != null) {
            if (databaseCreator.getDatabase() != null) {
                databaseCreator.getDatabase().roundDao().insertRound(round);
            }
        }

    }

}
