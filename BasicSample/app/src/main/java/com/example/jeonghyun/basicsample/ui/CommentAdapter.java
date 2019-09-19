package com.example.jeonghyun.basicsample.ui;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jeonghyun.basicsample.R;
import com.example.jeonghyun.basicsample.databinding.CommentItemBinding;
import com.example.jeonghyun.basicsample.model.Comment;
import com.example.jeonghyun.basicsample.ui.event.CommentClickCallback;

import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<? extends Comment> mCommentList;

    @Nullable
    private final CommentClickCallback mCommentClickCallback;


    public CommentAdapter(@Nullable CommentClickCallback commentClickCallback){
        mCommentClickCallback = commentClickCallback;
    }

    public void setCommentList(final List<? extends Comment> comments){
        if(mCommentList == null){
            mCommentList = comments;
            notifyItemRangeInserted(0, comments.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mCommentList.size();
                }

                @Override
                public int getNewListSize() {
                    return comments.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Comment oldComment = mCommentList.get(oldItemPosition);
                    Comment newComment = mCommentList.get(newItemPosition);
                    return oldComment.getId() == newComment.getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                   Comment oldComment = mCommentList.get(oldItemPosition);
                   Comment newComment = mCommentList.get(newItemPosition);
                   return oldComment.getId() == newComment.getId()
                           && oldComment.getPostedAt() == newComment.getPostedAt()
                           && oldComment.getProductId() == newComment.getProductId()
                           && Objects.equals(oldComment.getText(), newComment.getText());
                }
            });
            mCommentList = comments;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.comment_item, parent, false);
        binding.setCallback(mCommentClickCallback);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.binding.setComment(mCommentList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mCommentList == null ? 0 : mCommentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        final CommentItemBinding binding;

        CommentViewHolder(CommentItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
