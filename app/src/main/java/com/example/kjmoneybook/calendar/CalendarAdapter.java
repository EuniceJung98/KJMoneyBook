package com.example.kjmoneybook.calendar;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kjmoneybook.MainActivity;
import com.example.kjmoneybook.R;
import com.example.kjmoneybook.daily.DailyInAndOut;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    ArrayList<DailyInAndOut> items = new ArrayList<>();

    DailyInAndOut item;

    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder holder, final int position) {
        item = items.get(position);
        holder.setItem(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("date", item.getDate());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public  ArrayList<DailyInAndOut> getList(){
        return items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView category, money;
        NumberFormat numberFormat;

        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            money = itemView.findViewById(R.id.money);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Log.d("TAG", "onClick: " + pos);
                    }
                }
            });
        }

        public void setItem(DailyInAndOut item) {
            numberFormat = NumberFormat.getInstance(Locale.getDefault());
            category.setText(item.getCategoryName());
            if(item.getType() == "지출"){
                money.setText(Html.fromHtml("<font color=\"#ff0000\">" + numberFormat.format(item.getAmount()) + "</font>" + " 원"));
            }
            if(item.getType() == "수입"){
                money.setText(Html.fromHtml("<font color=\"#0000ff\">" + numberFormat.format(item.getAmount()) + "</font>" + " 원"));
            }
            if(item.getMemo() != null){
                category.setText(item.getCategoryName()+"(" + item.getMemo() + ")");
            }
        }

    }

    public void addItem(DailyInAndOut item){items.add(item);}

    public void setItems(ArrayList<DailyInAndOut> items) {this.items = items;}

    public DailyInAndOut getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, DailyInAndOut item){items.set(position, item);}

    public void clear(){
         items.clear();
    }
}
