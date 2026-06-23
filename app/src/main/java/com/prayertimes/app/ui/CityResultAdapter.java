package com.prayertimes.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prayertimes.app.R;
import com.prayertimes.app.location.CityEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CityResultAdapter extends RecyclerView.Adapter<CityResultAdapter.VH> {

    public interface OnCityClickListener {
        void onCityClick(CityEntry city);
    }

    private List<CityEntry>      results = new ArrayList<>();
    private final OnCityClickListener listener;

    public CityResultAdapter(OnCityClickListener listener) {
        this.listener = listener;
    }

    public void setResults(List<CityEntry> list) {
        results = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_city_result, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CityEntry city = results.get(pos);
        h.tvName.setText(city.displayName());
        h.tvCoords.setText(String.format(Locale.US,
            "%.4f°N, %.4f°E  —  alt %dm", city.latitude, city.longitude, (int) city.elevation));
        h.itemView.setOnClickListener(v -> listener.onCityClick(city));
    }

    @Override
    public int getItemCount() { return results.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvCoords;
        VH(@NonNull View v) {
            super(v);
            tvName   = v.findViewById(R.id.tv_city_name);
            tvCoords = v.findViewById(R.id.tv_city_coords);
        }
    }
}
