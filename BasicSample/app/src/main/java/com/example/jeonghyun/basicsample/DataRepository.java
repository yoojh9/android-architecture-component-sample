package com.example.jeonghyun.basicsample;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.jeonghyun.basicsample.db.AppDatabase;
import com.example.jeonghyun.basicsample.db.entity.CommentEntity;
import com.example.jeonghyun.basicsample.db.entity.ProductEntity;

import java.util.List;

public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<ProductEntity>> mObservableProducts;

    private DataRepository(final AppDatabase database){
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        /**
         * addSource(LiveData<S> source, Observer<S> onChanged)
         *  - Starts to listen the given source LiveData, onChanged observer will be called when source value was changed
         */
        mObservableProducts.addSource(mDatabase.productDao().loadAllProducts(),
                productEntities -> {
                    Log.e("productEntities", productEntities+"");
                    if(mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProducts.postValue(productEntities);
                    }
                });
    }


    public static DataRepository getInstance(final AppDatabase database) {
        if(sInstance == null){
            synchronized (DataRepository.class) {
                if(sInstance == null) {
                    sInstance = new DataRepository(database);
                }
             }
        }

        return sInstance;
    }

    public LiveData<List<ProductEntity>> getProducts() {
        return mObservableProducts;
    }

    public LiveData<ProductEntity> loadProduct(final int productId){
        return mDatabase.productDao().loadProduct(productId);
    }

    public LiveData<List<CommentEntity>> loadComments(final int productId){
        return mDatabase.commentDao().loadComments(productId);
    }

    public LiveData<List<ProductEntity>> searchProducts(String query){
        return mDatabase.productDao().searchAllProducts(query);
    }

}
