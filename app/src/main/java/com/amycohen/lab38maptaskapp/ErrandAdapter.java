package com.amycohen.lab38maptaskapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ErrandAdapter extends RecyclerView.Adapter<ErrandAdapter.MyViewHolder> {
    public List<Errand> errands;

    public ErrandAdapter () {
        errands = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.errand_item, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(errands.get(i));
    }

    @Override
    public int getItemCount() {
        return errands.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        TextView description;
        CheckBox checkbox;
        Errand errand;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnClickListener(this);

            description = itemView.findViewById(R.id.description);
            checkbox = itemView.findViewById(R.id.isComplete);
        }

        public void bind (Errand errand) {
            this.errand = errand;
            description.setText(errand.description);
            checkbox.setChecked(errand.isComplete);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mView.getContext(), MapsActivity.class);
            intent.putExtra("id", errand.id);
//            intent.putExtra("start", errand.start);
//            intent.putExtra("end", errand.end);
            mView.getContext().startActivity(intent);

        }
    }
}
