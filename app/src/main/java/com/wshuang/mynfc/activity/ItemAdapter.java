package com.wshuang.mynfc.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wshuang.mynfc.R;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<CarsItem> {

    private int resourceId;

    public ItemAdapter(Context context, int textViewResourceId,
                       List<CarsItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CarsItem carsItem = getItem(position); // 获取当前项的Fruit实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.editFltDate = view.findViewById(R.id.CarsDate);
            viewHolder.CarsFltNr = view.findViewById(R.id.CarsFltNr);
            viewHolder.CarsTotal = view.findViewById(R.id.CarsTotal);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.editFltDate.setText(carsItem.getCarsDate());
        viewHolder.CarsFltNr.setText(carsItem.getCarsFltNr());
        viewHolder.CarsTotal.setText(carsItem.getCarsTotal());
        return view;
    }

    class ViewHolder {

        TextView editFltDate;
        TextView CarsFltNr;
        TextView CarsTotal;

    }


}
