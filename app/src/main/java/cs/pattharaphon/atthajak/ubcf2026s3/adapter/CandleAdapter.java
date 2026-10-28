package cs.pattharaphon.atthajak.ubcf2026s3.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cs.pattharaphon.atthajak.ubcf2026s3.R;
import cs.pattharaphon.atthajak.ubcf2026s3.model.Candle;

public class CandleAdapter extends RecyclerView.Adapter<CandleAdapter.CandleViewHolder> {
    private static final String IMAGE_BASE_URL =
            "https://pattharaphon.pythonanywhere.com/static/images/";
    private static final ExecutorService IMAGE_EXECUTOR = Executors.newFixedThreadPool(4);

    private final List<Candle> candles;

    public CandleAdapter(android.content.Context context, List<Candle> candles) {
        this.candles = candles;
    }

    @NonNull
    @Override
    public CandleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candle_card, parent, false);
        return new CandleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandleViewHolder holder, int position) {
        Candle candle = candles.get(position);
        holder.templeName.setText(candle.getTempleName());
        holder.owner.setText(candle.getOwner());

        String imageUrl = IMAGE_BASE_URL + candle.getName();
        holder.image.setTag(imageUrl);
        holder.image.setImageResource(R.drawable.ubcandle_logo);

        IMAGE_EXECUTOR.execute(() -> {
            Bitmap bitmap = downloadBitmap(imageUrl);
            if (bitmap != null) {
                holder.image.post(() -> {
                    if (imageUrl.equals(holder.image.getTag())) {
                        holder.image.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    private Bitmap downloadBitmap(String imageUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            try (InputStream inputStream = connection.getInputStream()) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public int getItemCount() {
        return candles.size();
    }

    static class CandleViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView templeName;
        final TextView owner;

        CandleViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgv_candle_card);
            templeName = itemView.findViewById(R.id.tv_candle_card_temple_name);
            owner = itemView.findViewById(R.id.tv_candle_card_owner);
        }
    }
}
