package com.prayertimes.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prayertimes.app.R;
import com.prayertimes.app.model.Prayer;

import java.util.List;

public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.ViewHolder> {

    private List<Prayer> prayers;
    private int nextPrayerIndex = -1;

    public PrayerAdapter(List<Prayer> prayers) { this.prayers = prayers; }

    public void update(List<Prayer> prayers, int nextIdx) {
        this.prayers         = prayers;
        this.nextPrayerIndex = nextIdx;
        notifyDataSetChanged();
    }

    public void update(List<Prayer> prayers, int nextIdx, double[] times) {
        update(prayers, nextIdx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_prayer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Prayer p = prayers.get(position);
        boolean isNext = (position == nextPrayerIndex);

        h.tvName.setText(p.name);
        h.tvTime.setText(p.timeFormatted);

        // وقت الإقامة
        if (p.iqamaFormatted != null && !p.iqamaFormatted.isEmpty()) {
            h.tvIqama.setVisibility(View.VISIBLE);
            h.tvIqama.setText(p.iqamaFormatted);
        } else {
            h.tvIqama.setVisibility(View.GONE);
        }

        // المؤشر الذهبي — يظهر فقط للصلاة القادمة
        if (h.viewIndicator != null) {
            h.viewIndicator.setVisibility(isNext ? View.VISIBLE : View.INVISIBLE);
        }

        // خلفية مختلفة للصلاة القادمة
        h.itemView.setBackgroundResource(
            isNext ? R.drawable.bg_next_prayer : R.drawable.bg_prayer_item
        );

        // اللون أفتح للصلاة القادمة
        h.tvName.setTextColor(isNext
            ? 0xFFFFFFFF
            : 0xFFEEF2F7);
        h.tvTime.setTextColor(isNext
            ? 0xFFE2C068
            : 0xFFC8A84B);
    }

    @Override
    public int getItemCount() { return prayers != null ? prayers.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvIqama;
        View     viewIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tv_prayer_name);
            tvTime        = itemView.findViewById(R.id.tv_prayer_time);
            tvIqama       = itemView.findViewById(R.id.tv_iqama_time);
            viewIndicator = itemView.findViewById(R.id.view_indicator);
        }
    }
}
