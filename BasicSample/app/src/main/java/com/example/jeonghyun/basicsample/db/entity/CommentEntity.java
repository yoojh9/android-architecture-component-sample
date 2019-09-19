package com.example.jeonghyun.basicsample.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.jeonghyun.basicsample.model.Comment;

import java.util.Date;

@Entity(tableName = "comments",
        foreignKeys = {
            @ForeignKey(entity = ProductEntity.class,
                    parentColumns = "id",
                    childColumns = "productId",
                    onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value="productId")
        })
public class CommentEntity implements Comment {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int productId;
    private String text;
    private Date postedAt;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getProductId() {
        return productId;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getPostedAt() {
        return postedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public CommentEntity(){

    }
}
