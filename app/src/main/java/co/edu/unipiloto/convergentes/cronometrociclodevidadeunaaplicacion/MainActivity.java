package co.edu.unipiloto.convergentes.cronometrociclodevidadeunaaplicacion;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvTimer;
    private Button btnStart, btnStop, btnReset, btnLap;
    private ListView lvLaps;

    private Handler handler;
    private long startTime, timeInMilliseconds, timeSwapBuff, updatedTime;
    private boolean isRunning;

    private ArrayList<String> lapTimes;
    private ArrayAdapter<String> adapter;
    private int lapCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnLap = findViewById(R.id.btnLap);
        lvLaps = findViewById(R.id.lvLaps);

        //registrar vueltas
        lapTimes = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lapTimes);
        lvLaps.setAdapter(adapter);

        handler = new Handler();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis();
                    handler.postDelayed(updateTimerThread, 0);
                    isRunning = true;
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimerThread);
                    isRunning = false;
                }
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSwapBuff = 0;
                timeInMilliseconds = 0;
                updatedTime = 0;
                lapTimes.clear();
                adapter.notifyDataSetChanged();
                lapCount = 1;
                tvTimer.setText("00:00:00");
            }
        });

        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {  // Solo registra la vuelta si el cronómetro está activo
                    String lapTime = tvTimer.getText().toString();
                    lapTimes.add("Vuelta " + lapCount + ": " + lapTime);
                    lapCount++;
                    adapter.notifyDataSetChanged();
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);

            tvTimer.setText(String.format("%02d:%02d:%03d", mins, secs, milliseconds));
            handler.postDelayed(this, 0);
        }
    };
}