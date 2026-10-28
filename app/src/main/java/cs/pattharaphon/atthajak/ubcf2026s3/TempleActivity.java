package cs.pattharaphon.atthajak.ubcf2026s3;

import android.os.Bundle;
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

import cs.pattharaphon.atthajak.ubcf2026s3.adapter.TempleAdapter;
import cs.pattharaphon.atthajak.ubcf2026s3.model.Temple;

public class TempleActivity extends AppCompatActivity {
    private static final String TEMPLES_URL =
            "https://pattharaphon.pythonanywhere.com/api/uboncandlefest/temples";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ArrayList<Temple> temples = new ArrayList<>();
    private TempleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple);

        RecyclerView recyclerView = findViewById(R.id.rcv_temple);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TempleAdapter(this, temples);
        recyclerView.setAdapter(adapter);
        loadTemples();
    }

    private void loadTemples() {
        executor.execute(() -> {
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(TEMPLES_URL).openConnection();
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

                JSONArray jsonArray = new JSONArray(response.toString());
                ArrayList<Temple> loaded = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    loaded.add(new Temple(
                            object.optInt("id"),
                            object.optString("name", "ไม่ระบุชื่อวัด"),
                            object.optString("description", "")
                    ));
                }

                runOnUiThread(() -> {
                    temples.clear();
                    temples.addAll(loaded);
                    adapter.notifyDataSetChanged();
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
