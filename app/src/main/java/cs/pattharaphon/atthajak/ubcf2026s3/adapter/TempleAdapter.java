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
import cs.pattharaphon.atthajak.ubcf2026s3.model.Temple;

public class TempleAdapter extends RecyclerView.Adapter<TempleAdapter.TempleViewHolder> {
    public interface OnTempleClickListener {
        void onTempleClick(Temple temple);
    }

    private final Context context;
    private final List<Temple> temples;
    private final OnTempleClickListener listener;

    public TempleAdapter(Context context, List<Temple> temples, OnTempleClickListener listener) {
        this.context = context;
        this.temples = temples;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TempleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.temple_card, parent, false);
        return new TempleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempleViewHolder holder, int position) {
        Temple temple = temples.get(position);
        holder.name.setText(temple.getName());
        holder.description.setText(temple.getDescription());
        holder.itemView.setOnClickListener(view -> listener.onTempleClick(temple));
    }

    @Override
    public int getItemCount() {
        return temples.size();
    }

    static class TempleViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;

        TempleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_temple_card_name);
            description = itemView.findViewById(R.id.tv_temple_card_description);
        }
    }
}
