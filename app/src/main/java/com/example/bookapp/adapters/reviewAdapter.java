package com.example.bookapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.models.Review;

import java.util.List;

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.MyViewHolder>{



    private List<Review> itmesList;
    private SessionManager session;
    private Context _context = null;

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener{


        private TextView comment;
        private TextView commentDate;
        private RatingBar rating;
        private Button edit_rev;
        private Button delete_rev;


        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }


        public MyViewHolder(View view) {
            super(view);
            rating = (RatingBar) view.findViewById(R.id.rating);
            comment = (TextView) view.findViewById(R.id.commentTextLbl);
            commentDate = (TextView) view.findViewById(R.id.commentDate);
            delete_rev = (Button) view.findViewById(R.id.delete_rev);
            edit_rev = (Button) view.findViewById(R.id.edit_rev);

        }
    }

    public reviewAdapter(Activity context , List<Review> itmesList) {
        this.itmesList = itmesList;
        _context = context;
        session = new SessionManager(_context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Review itemRow = itmesList.get(position);
        holder.commentDate.setText(itemRow.getRev_date());
        holder.comment.setText(itemRow.getComment());
        holder.rating.setRating(Float.parseFloat(itemRow.getRate()));

        if(itemRow.getBuyer_id().equals(session.getUserID())) {
            holder.delete_rev.setVisibility(View.VISIBLE);
            holder.edit_rev.setVisibility(View.VISIBLE);
            com.example.bookapp.buyer.BookDetailsActivity tmp = (com.example.bookapp.buyer.BookDetailsActivity)_context;
            holder.edit_rev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tmp.updateReview(itemRow);
                }
            });
            holder.delete_rev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tmp.removeReview(itemRow);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itmesList.size();
    }



}
