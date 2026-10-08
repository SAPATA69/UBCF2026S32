package cs.pattharaphon.atthajak.ubcf2026s3;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cs.pattharaphon.atthajak.ubcf2026s3.model.Candle;

public class CandleActivity extends AppCompatActivity {
    private RecyclerView

    private CandleAdaper

    private List<Candle

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

        matchview();
        candles = new ArrayList<>()
    }
    private void matchview() {
        string url = https://pattharaphon.pythonanywhere.com/candles/candle
        JSONArrayRequest request;
        request = new JSONArrayRequest(
                url
         public void onRespone(JSONArray JsonArray) {
                    int size = jsonArray kength();
                    for (int i=0; i<size; i++) {
                        try {
                            JSONObject object = jsonArray.getJsonobject(i);
                            Candle candle = new Candle
                                  object.getInt("id")
                                  object.getInt("name")
                                  object.getInt("owder")

                        }
                    }
        }


        );

    }
}