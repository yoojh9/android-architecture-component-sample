package com.example.jeonghyun.basicsample.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.jeonghyun.basicsample.BasicApp;
import com.example.jeonghyun.basicsample.DataRepository;
import com.example.jeonghyun.basicsample.db.entity.CommentEntity;
import com.example.jeonghyun.basicsample.db.entity.ProductEntity;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private final LiveData<ProductEntity> mObservableProduct;

    private final LiveData<List<CommentEntity>> mObservableComments;

    private final int mProductId;

    /**
     * ObservabkeField를 이용하면 다음 두가지 케이스의 변화를 listen 할 수 있음
     *  1) when activity is visible to a user (LiveData)
     *  2) When activity is not visible (in the paused or destroying state)
     */
    public ObservableField<ProductEntity> product = new ObservableField<>();

    public ProductViewModel(@NonNull Application application, DataRepository repository, final int productId) {
        super(application);
        mProductId = productId;

        mObservableProduct = repository.loadProduct(mProductId);
        mObservableComments = repository.loadComments(mProductId);
    }

    /**
     * Expose the LiveData Comments query so the UI can observe it.
     */
    public LiveData<ProductEntity> getObservableProduct(){
        return mObservableProduct;
    }

    public LiveData<List<CommentEntity>> getComments(){
        return mObservableComments;
    }

    public void setProduct(ProductEntity product){
        this.product.set(product);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final int mProductId;

        private final DataRepository mRepositry;

        public Factory(@NonNull Application application, int productId){
            mApplication = application;
            mProductId = productId;
            mRepositry = ((BasicApp)application).getRepository();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ProductViewModel(mApplication, mRepositry, mProductId);
        }
    }

}
