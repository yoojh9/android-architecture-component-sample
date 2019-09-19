package com.example.jeonghyun.basicsample.ui;


import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.jeonghyun.basicsample.R;
import com.example.jeonghyun.basicsample.databinding.FragmentProductListBinding;
import com.example.jeonghyun.basicsample.db.entity.ProductEntity;
import com.example.jeonghyun.basicsample.generated.callback.OnClickListener;
import com.example.jeonghyun.basicsample.model.Product;
import com.example.jeonghyun.basicsample.ui.event.ProductClickCallback;
import com.example.jeonghyun.basicsample.viewmodel.ProductListViewModel;

import java.util.List;

public class ProductListFragment extends Fragment {

    public static final String TAG = "ProductListFragment";

    private ProductAdapter mProductAdapter;

    private FragmentProductListBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false);

        mProductAdapter = new ProductAdapter(mProductClickCallback);
        mBinding.productsList.setAdapter(mProductAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ProductListViewModel viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);

        mBinding.productsSearchBtn.setOnClickListener(v->{
            Editable query = mBinding.productSearchBox.getText();
            if(query == null || query.toString().isEmpty()){
                subscribeUi(viewModel.getProducts());
            } else {
                subscribeUi(viewModel.searchProducts("*" + query + "*"));
            }
        });
        subscribeUi(viewModel.getProducts());

    }

    private void subscribeUi(LiveData<List<ProductEntity>> liveData){
        liveData.observe(this, new Observer<List<ProductEntity>>() {
            @Override
            public void onChanged(List<ProductEntity> productEntities) {
                if(productEntities != null){
                    mBinding.setIsLoading(false);
                    mProductAdapter.setProductList(productEntities);
                } else {
                    mBinding.setIsLoading(true);
                }
                mBinding.executePendingBindings();
            }
        });
    }


    private final ProductClickCallback mProductClickCallback = new ProductClickCallback() {
        @Override
        public void onClick(Product product) {
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
                ((MainActivity) getActivity()).show(product);
            }
        }
    };
}
