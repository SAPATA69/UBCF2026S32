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

public class UserDetailActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "user_id";
    private static final String USER_URL_PREFIX =
            "https://pattharaphon.pythonanywhere.com/api/uboncandlefest/users/";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ArrayList<Candle> candles = new ArrayList<>();
    private CandleAdapter candleAdapter;
    private TextView username;
    private TextView email;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        username = findViewById(R.id.tv_user_detail_username);
        email = findViewById(R.id.tv_user_detail_email);
        emptyMessage = findViewById(R.id.tv_user_detail_empty);
        RecyclerView recyclerView = findViewById(R.id.recycler_user_candles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        candleAdapter = new CandleAdapter(this, candles);
        recyclerView.setAdapter(candleAdapter);

        int userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (userId < 0) {
            Toast.makeText(this, "ไม่พบรหัสเจ้าของ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        loadUser(userId);
    }

    private void loadUser(int userId) {
        executor.execute(() -> {
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(USER_URL_PREFIX + userId).openConnection();
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
                String loadedUsername = object.optString("username", "ไม่ระบุชื่อ");
                String loadedEmail = object.optString("email", "");
                JSONArray candleArray = object.optJSONArray("candles");
                ArrayList<Candle> loadedCandles = new ArrayList<>();

                if (candleArray != null) {
                    for (int i = 0; i < candleArray.length(); i++) {
                        JSONObject candle = candleArray.getJSONObject(i);
                        loadedCandles.add(new Candle(
                                candle.optInt("id"),
                                candle.optString("name", ""),
                                loadedUsername,
                                candle.optString("temple_name", "ไม่ระบุวัด")
                        ));
                    }
                }

                runOnUiThread(() -> {
                    username.setText(loadedUsername);
                    email.setText(loadedEmail);
                    candles.clear();
                    candles.addAll(loadedCandles);
                    candleAdapter.notifyDataSetChanged();
                    emptyMessage.setVisibility(
                            loadedCandles.isEmpty() ? TextView.VISIBLE : TextView.GONE
                    );
                });
            } catch (Exception error) {
                runOnUiThread(() -> Toast.makeText(
                        this,
                        "โหลดข้อมูลเจ้าของไม่สำเร็จ: " + error.getMessage(),
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
