package com.example.mohamed.news.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mohamed.news.Models.NewsResponse.ArticlesItem;
import com.example.mohamed.news.R;

import java.util.List;

public class NewsAdapter  extends  RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    List<ArticlesItem>newsList;
    onShareClickListener onShareClickListener;
    onItemClickListener onTextClick;

    public void setOnShareClickListener(onShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    public void setOnTextClick(onItemClickListener onTextClick) {
        this.onTextClick = onTextClick;
    }

    public NewsAdapter(List<ArticlesItem> list) {
        this.newsList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view_news,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        final ArticlesItem news=newsList.get(i);
        viewHolder.title.setText(news.getTitle());
        viewHolder.time.setText(news.getPublishedAt());
        if(news.getSource()!=null)
        viewHolder.sourceName.setText(news.getSource().getName());
        else viewHolder.sourceName.setText(news.getSourceName());
        Glide.with(viewHolder.itemView)
                .load(news.getUrlToImage())
                .into(viewHolder.image);

        if(onShareClickListener !=null){

            viewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShareClickListener.onShareClick(i,news);
                }
            });
        }

        if (onTextClick!=null){

            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTextClick.onItemClick(i,news);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(newsList==null)return 0 ;
        return newsList.size();
    }

    public void changeData(List<ArticlesItem>items){
        this.newsList=items;
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        TextView sourceName,title,time;
        ImageView image,share;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sourceName=itemView.findViewById(R.id.souce_name);
            time=itemView.findViewById(R.id.time);
            title=itemView.findViewById(R.id.title);
            image=itemView.findViewById(R.id.image);
            share=itemView.findViewById(R.id.share);
        }
    }

    public  interface onShareClickListener {


        void onShareClick(int pos,ArticlesItem articlesItem);

    }

    public  interface onItemClickListener {


        void onItemClick(int i, ArticlesItem articlesItem);

    }
}