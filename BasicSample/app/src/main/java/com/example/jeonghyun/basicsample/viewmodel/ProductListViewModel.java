package com.example.jeonghyun.basicsample.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.jeonghyun.basicsample.BasicApp;
import com.example.jeonghyun.basicsample.DataRepository;
import com.example.jeonghyun.basicsample.db.entity.ProductEntity;

import java.util.List;

public class ProductListViewModel extends AndroidViewModel {

    private final DataRepository mRepository;

    // MediatorLiveData는 여러개의 LiveData를 병합 가능 하도록 지원한다. MediatorLiveData 인스턴스의 관찰자는 원래의 LiveData 인스턴스들 중 하나라도 변경될 때마다 트리거된다.
    // cf) MutableLiveData - 추상클래스인 LiveData를 상속하여 setValue와 postValue 메소드를 노출한 클래스이다.
    // setValue: 활성화된 옵저버들에게 값을 전달하게 된다. 이 메소드는 반드시 메인스레드에서 불려야 하며 백그라운드 스레드에서 값을 설정하고 싶다면 postValue를 호출하도록 하자
    private final MediatorLiveData<List<ProductEntity>> mObservableProducts;

    public ProductListViewModel(Application application) {
        super(application);

        mObservableProducts = new MediatorLiveData<>();

        // set by default null, until we get data from the database
        mObservableProducts.setValue(null);

        mRepository = ((BasicApp) application).getRepository();

        LiveData<List<ProductEntity>> products = mRepository.getProducts();

        mObservableProducts.addSource(products, mObservableProducts::setValue);
    }

    /**
     * Expose the LiveData Products query so the UI can observe it
     */
    public LiveData<List<ProductEntity>> getProducts(){
        return mObservableProducts;
    }

    public LiveData<List<ProductEntity>> searchProducts(String query){
        return mRepository.searchProducts(query);
    }
}
