package com.example.dingtu2.myapplication.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.dingtu2.myapplication.db.DatabaseCreator;
import com.example.dingtu2.myapplication.db.entity.UserEntity;

import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/10.
 */

public class UserViewModel extends AndroidViewModel {
    private static final MutableLiveData ABSENT = new MutableLiveData();
    private LiveData<List<UserEntity>> mObservableUsers;
    private LiveData<UserEntity> mObervableUser;

    {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    public UserViewModel(Application application) {
        super(application);

        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance(this.getApplication());

        LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();
        mObservableUsers = Transformations.switchMap(databaseCreated,
                new Function<Boolean, LiveData<List<UserEntity>>>() {
                    @Override
                    public LiveData<List<UserEntity>> apply(Boolean isDbCreated) {
                        if (!Boolean.TRUE.equals(isDbCreated)) { // Not needed here, but watch out for null
                            //noinspection unchecked
                            return ABSENT;
                        } else {
                            //noinspection ConstantConditions
                            return databaseCreator.getDatabase().userDao().loadLoginedUsers();
                        }
                    }
                });


        databaseCreator.createDB(this.getApplication());
    }

    public UserViewModel(Application application, final String loginKey) {
        super(application);

        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance(application);
        LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();
        mObervableUser = Transformations.switchMap(databaseCreated, new Function<Boolean, LiveData<UserEntity>>() {
            @Override
            public LiveData<UserEntity> apply(Boolean aBoolean) {
                if (!Boolean.TRUE.equals(aBoolean)) {
                    Log.d("get DB created", "false ");
                    return ABSENT;
                } else {
                    return databaseCreator.getDatabase().userDao().loadUser(loginKey);
                }
            }
        });

        databaseCreator.createDB(this.getApplication());
    }

    public LiveData<List<UserEntity>> getLoginedUsers() {
        return mObservableUsers;
    }

    public LiveData<UserEntity> getObservableUser() {

        return mObervableUser;
    }

    public boolean saveUser(UserEntity user) {
        return false;
    }


}
