package com.example.bookapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.LoadImgFromURL;
import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.buyer.BookDetailsActivity;
import com.example.bookapp.models.Book;
import com.example.bookapp.seller.addBookActivity;
import com.example.bookapp.seller.booksList;
import com.example.bookapp.seller.copiesList;

import java.util.List;

public class suggestAdapter extends RecyclerView.Adapter<suggestAdapter.MyViewHolder> {

    List<Book> list;
    Activity context;
    SessionManager session;
    public suggestAdapter(List<Book> list, Activity context) {
        this.list = list;
        this.context = context;
        session = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_sugg_book, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(list.get(position).getBookName());

        String imgURL = list.get(position).getImage();
        new LoadImgFromURL(holder.image, 0 , 0).execute(imgURL);
         holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book i = list.get(position);
                Intent intent = null;
                intent = new Intent(context, BookDetailsActivity.class);
                intent.putExtra("Book",  i);
                context.startActivity(intent);
            }
        });
         holder.visitBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Book i = list.get(position);
                 Intent intent = null;
                 intent = new Intent(context, BookDetailsActivity.class);
                 intent.putExtra("Book",  i);
                 context.startActivity(intent);
             }
         });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        Button visitBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            visitBtn = itemView.findViewById(R.id.visitBtn);

        }
    }
}
