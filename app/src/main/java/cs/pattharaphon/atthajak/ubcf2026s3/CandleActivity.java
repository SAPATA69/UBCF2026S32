package cs.pattharaphon.atthajak.ubcf2026s3;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class CandleActivity extends AppCompatActivity {
    private static final String CANDLES_URL =
            "https://pattharaphon.pythonanywhere.com/api/uboncandlefest/candles";

    private RecyclerView recyclerView;
    private CandleAdapter candleAdapter;
    private ArrayList<Candle> candles;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candle2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        candles = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_candles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        candleAdapter = new CandleAdapter(this, candles);
        recyclerView.setAdapter(candleAdapter);
        loadCandles();
    }

    private void loadCandles() {
        executor.execute(() -> {
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(CANDLES_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int status = connection.getResponseCode();
                if (status < 200 || status >= 300) {
                    throw new IllegalStateException("API returned HTTP " + status);
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

                JSONArray jsonArray = new JSONArray(response.toString());
                ArrayList<Candle> loadedCandles = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    loadedCandles.add(new Candle(
                            object.optInt("id"),
                            object.optString("name", ""),
                            object.optString("owner", "ไม่ระบุ"),
                            object.optString("temple_name", "ไม่ระบุ")
                    ));
                }

                runOnUiThread(() -> {
                    candles.clear();
                    candles.addAll(loadedCandles);
                    candleAdapter.notifyDataSetChanged();
                });
            } catch (Exception error) {
                runOnUiThread(() -> Toast.makeText(
                        this,
                        "โหลดข้อมูลเทียนไม่สำเร็จ: " + error.getMessage(),
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
