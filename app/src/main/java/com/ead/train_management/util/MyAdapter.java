package com.ead.train_management.util;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ead.train_management.R;
import com.ead.train_management.models.cancelBookingModel;
import com.ead.train_management.models.viewBookingModel;
import com.ead.train_management.service.BookingService;
import com.ead.train_management.ViewBookingsActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<viewBookingModel> dataList;
    private BookingService bookingService;
    private ViewBookingsActivity viewBookingsActivity;

    public MyAdapter(List<viewBookingModel> dataList, ViewBookingsActivity viewBookingsActivity) {
        this.dataList = dataList;
        this.viewBookingsActivity = viewBookingsActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bookingService = RetrofitClient.getClient().create(BookingService.class);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        viewBookingModel item = dataList.get(position);
        holder.textViewName.setText("Name: " + item.getName());
        holder.textViewName2.setText("Date: " + item.getDate());
        holder.textViewName3.setText("Tickets: " + String.valueOf(item.getNum()));
        holder.buttonDelete.setOnClickListener(v -> {
            // Create a confirmation dialog before deleting
            AlertDialog.Builder builder = new AlertDialog.Builder(viewBookingsActivity);
            builder.setTitle("Confirm Cancellation");
            builder.setMessage("Are you sure you want to cancel this bookingModel?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                boolean violateFlag = true;
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelBookingModel d = new cancelBookingModel();
                    d.setAcc(true);
                    Call<String> data = bookingService.removeBooking(item.getId());

                    data.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call1, Response<String> response1) {
                            if (response1.isSuccessful() && response1.body() != null) {
                                violateFlag = false;
                                dataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, dataList.size());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            violateFlag = false;
                            Toast.makeText(holder.itemView.getContext(), "Booking Cancelled", Toast.LENGTH_SHORT).show();
                            dataList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, dataList.size());
                        }

                    });
                    if(violateFlag){
                        Toast.makeText(holder.itemView.getContext(), "Cancellation Failed, Refer the Policy", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                // User clicked No, do nothing
            });

            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewName2;
        TextView textViewName3;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.vname);
            textViewName2 = itemView.findViewById(R.id.vdate);
            textViewName3 = itemView.findViewById(R.id.vnum);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
