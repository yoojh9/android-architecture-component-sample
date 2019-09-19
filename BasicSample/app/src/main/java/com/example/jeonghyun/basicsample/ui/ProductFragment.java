package com.example.jeonghyun.basicsample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.jeonghyun.basicsample.R;
import com.example.jeonghyun.basicsample.databinding.FragmentProductBinding;
import com.example.jeonghyun.basicsample.databinding.FragmentProductListBinding;
import com.example.jeonghyun.basicsample.db.entity.CommentEntity;
import com.example.jeonghyun.basicsample.db.entity.ProductEntity;
import com.example.jeonghyun.basicsample.model.Comment;
import com.example.jeonghyun.basicsample.ui.event.CommentClickCallback;
import com.example.jeonghyun.basicsample.viewmodel.ProductViewModel;

import java.util.List;

public class ProductFragment extends Fragment {

    private static final String KEY_PRODUCT_ID = "product_id";

    private FragmentProductBinding mBinding;

    private CommentAdapter mCommentAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate this data binding layout
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false);

        mCommentAdapter = new CommentAdapter(mCommentClickCallback);
        mBinding.commentList.setAdapter(mCommentAdapter);
        return mBinding.getRoot();
    };

    private final CommentClickCallback mCommentClickCallback = new CommentClickCallback() {
        @Override
        public void onClick(Comment comment) {
            //
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ProductViewModel.Factory factory = new ProductViewModel.Factory(requireActivity().getApplication(), getArguments().getInt(KEY_PRODUCT_ID));

        final ProductViewModel model = new ViewModelProvider(this, factory).get(ProductViewModel.class);
        mBinding.setProductViewModel(model);

        subscribeToModel(model);

    }

    private void subscribeToModel(final ProductViewModel model){
        // Observe Product data
        model.getObservableProduct().observe(this, new Observer<ProductEntity>() {
            @Override
            public void onChanged(ProductEntity productEntity) {
                model.setProduct(productEntity);
            }
        });

        // Observe comments
        model.getComments().observe(this, new Observer<List<CommentEntity>>() {
            @Override
            public void onChanged(List<CommentEntity> commentEntities) {
                if(commentEntities != null){
                    mBinding.setIsLoading(false);
                    mCommentAdapter.setCommentList(commentEntities);
                } else {
                    mBinding.setIsLoading(true);
                }
            }
        });
    }

    /** Create product fragment for specific product ID */
    public static ProductFragment forProduct(int productId) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }
}
