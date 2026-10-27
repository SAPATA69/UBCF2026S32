package cs.pattharaphon.atthajak.ubcf2026s3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cs.pattharaphon.atthajak.ubcf2026s3.R;
import cs.pattharaphon.atthajak.ubcf2026s3.model.Candle;

public class CandleAdapter extends RecyclerView.Adapter<CandleAdapter.CandleViewHolder> {
    private final Context context;
    private final List<Candle> candles;

    public CandleAdapter(Context context, List<Candle> candles) {
        this.context = context;
        this.candles = candles;
    }

    @NonNull
    @Override
    public CandleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.candle_card, parent, false);
        return new CandleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandleViewHolder holder, int position) {
        Candle candle = candles.get(position);
        holder.templeName.setText(candle.getTempleName());
        holder.owner.setText(candle.getOwner());
    }

    @Override
    public int getItemCount() {
        return candles.size();
    }

    static class CandleViewHolder extends RecyclerView.ViewHolder {
        final TextView templeName;
        final TextView owner;

        CandleViewHolder(@NonNull View itemView) {
            super(itemView);
            templeName = itemView.findViewById(R.id.tv_candle_card_temple_name);
            owner = itemView.findViewById(R.id.tv_candle_card_owner);
        }
    }
}
