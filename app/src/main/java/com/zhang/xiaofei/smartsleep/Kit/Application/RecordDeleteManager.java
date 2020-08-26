package com.zhang.xiaofei.smartsleep.Kit.Application;

import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RecordDeleteManager {
    public static void delete7DaysBeforeData() {
        Date date = new Date();
        RealmResults<RecordModel> list = Realm.getDefaultInstance().where(RecordModel.class)
                .lessThan("time", date.getTime() / 1000 - 7 * 24 * 60 * 60).findAll();
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                list.deleteAllFromRealm();
            }
        });

    }
}
