package com.example.jeonghyun.basicsample.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.jeonghyun.basicsample.AppExecutors;
import com.example.jeonghyun.basicsample.db.converter.DateConverter;
import com.example.jeonghyun.basicsample.db.dao.CommentDao;
import com.example.jeonghyun.basicsample.db.dao.ProductDao;
import com.example.jeonghyun.basicsample.db.entity.CommentEntity;
import com.example.jeonghyun.basicsample.db.entity.ProductEntity;
import com.example.jeonghyun.basicsample.db.entity.ProductFtsEntity;


import java.util.List;

@Database(entities = {ProductEntity.class, ProductFtsEntity.class, CommentEntity.class}, version=2)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    public static final String DATABASE_NAME = "basic-sample-db";

    public abstract ProductDao productDao();

    public abstract CommentDao commentDao();

    // LiveData는 데이터를 업데이트 하는 public 메소드를 가지고 있지 않으므로, LiveData를 상속받은 MutableLiveData를 사용해야 한다.
    // MutableLiveData는 public SetValue(T)와 public postValue(T) 메서드를 가지고 있어서 이를 통해 데이터를 변경할 수 있다.
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(final Context appContext, final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback(){
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Log.e("Room.databaseBuilder","onCreate");
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            addDelay();

                            // Generate the data for pre-population
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            List<ProductEntity> products = DataGenerator.generateProducts();
                            Log.e("products ", products.size()+"");
                            List<CommentEntity> comments = DataGenerator.generateCommentsForProducts(products);

                            insertData(database, products, comments);

                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    private static void insertData(final AppDatabase database, final List<ProductEntity> products, final List<CommentEntity> comments) {
        database.runInTransaction(() -> {
            database.productDao().insertAll(products);
            database.commentDao().insertAll(comments);
        });
    }

    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {

        }
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
                    + "`name` TEXT, `description` TEXT, content=`products`)");
            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
                    + "SELECT `id`, `name`, `description` FROM products");

        }
    };
}
