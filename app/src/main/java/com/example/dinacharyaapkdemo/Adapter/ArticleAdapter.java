package com.example.dinacharyaapkdemo.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dinacharyaapkdemo.Articles;
import com.example.dinacharyaapkdemo.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Articles.ArticleModel> articleList;

    public ArticleAdapter(List<Articles.ArticleModel> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleAdapter.ViewHolder holder, int position) {
        Articles.ArticleModel article = articleList.get(position);

        holder.titleTextView.setText(article.getTitle());
        holder.contentTextView.setText(article.getContent());
        holder.urlTextView.setText(article.getUrl());

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.meditate) // Placeholder image while loading
                .error(R.drawable.meditate); // Error image if loading fails

        Glide.with(holder.imageView.getContext())
                .load(article.getImageUrl())
                .apply(requestOptions)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the article URL in a browser
                String url = article.getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(browserIntent);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Share the article content
                String shareText = article.getTitle() + "\n\n" + article.getUrl();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this article!");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                view.getContext().startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, urlTextView;
        ImageView imageView;
        ImageView share;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.postTitle);
            contentTextView = itemView.findViewById(R.id.postDescription);
            imageView = itemView.findViewById(R.id.showImage);
            urlTextView = itemView.findViewById(R.id.url);
            urlTextView.setVisibility(View.GONE);
            share = itemView.findViewById(R.id.share);
        }
    }
}
