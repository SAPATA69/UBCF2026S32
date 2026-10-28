package cs.pattharaphon.atthajak.ubcf2026s3;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cs.pattharaphon.atthajak.ubcf2026s3.adapter.CandleAdapter;
import cs.pattharaphon.atthajak.ubcf2026s3.model.Candle;

public class TempleDetailActivity extends AppCompatActivity {
    public static final String EXTRA_TEMPLE_ID = "temple_id";
    private static final String TEMPLE_URL_PREFIX =
            "https://pattharaphon.pythonanywhere.com/api/uboncandlefest/temples/";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private CandleAdapter candleAdapter;
    private final ArrayList<Candle> candles = new ArrayList<>();
    private TextView templeName;
    private TextView templeDescription;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_detail);

        templeName = findViewById(R.id.tv_temple_detail_name);
        templeDescription = findViewById(R.id.tv_temple_detail_description);
        emptyMessage = findViewById(R.id.tv_temple_detail_empty);
        RecyclerView recyclerView = findViewById(R.id.recycler_temple_candles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        candleAdapter = new CandleAdapter(this, candles);
        recyclerView.setAdapter(candleAdapter);

        int templeId = getIntent().getIntExtra(EXTRA_TEMPLE_ID, -1);
        if (templeId < 0) {
            Toast.makeText(this, "ไม่พบรหัสวัด", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        loadTemple(templeId);
    }

    private void loadTemple(int templeId) {
        executor.execute(() -> {
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(TEMPLE_URL_PREFIX + templeId).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                if (connection.getResponseCode() < 200 || connection.getResponseCode() >= 300) {
                    throw new IllegalStateException("API returned HTTP " + connection.getResponseCode());
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } finally {
                    connection.disconnect();
                }

                JSONObject object = new JSONObject(response.toString());
                String name = object.optString("name", "ไม่ระบุชื่อวัด");
                String description = object.optString("description", "");
                JSONArray candleArray = object.optJSONArray("candles");
                ArrayList<Candle> loaded = new ArrayList<>();

                if (candleArray != null) {
                    for (int i = 0; i < candleArray.length(); i++) {
                        JSONObject candle = candleArray.getJSONObject(i);
                        loaded.add(new Candle(
                                candle.optInt("id"),
                                candle.optString("name", ""),
                                candle.optString("owner", "ไม่ระบุ"),
                                name
                        ));
                    }
                }

                runOnUiThread(() -> {
                    templeName.setText(name);
                    templeDescription.setText(description);
                    candles.clear();
                    candles.addAll(loaded);
                    candleAdapter.notifyDataSetChanged();
                    emptyMessage.setVisibility(loaded.isEmpty() ? TextView.VISIBLE : TextView.GONE);
                });
            } catch (Exception error) {
                runOnUiThread(() -> Toast.makeText(
                        this,
                        "โหลดข้อมูลวัดไม่สำเร็จ: " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
