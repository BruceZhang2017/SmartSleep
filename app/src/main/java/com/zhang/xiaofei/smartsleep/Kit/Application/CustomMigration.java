package com.zhang.xiaofei.smartsleep.Kit.Application;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * 升级数据库
 */
public class CustomMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0 && newVersion == 1) {
            RealmObjectSchema personSchema = schema.get("RecordModel");
            //新增@Required的id
            personSchema
                    .addField("isSyncCloud", Boolean.class, FieldAttribute.REQUIRED)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set("isSyncCloud", false);//为id设置值
                        }
                    });
            oldVersion++;
        }
    }
}
