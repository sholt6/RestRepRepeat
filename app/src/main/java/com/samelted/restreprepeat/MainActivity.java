package com.samelted.restreprepeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    TextView repCountView;
    int seconds = 0;
    long startTime = 0;
    long elapsedTime = 0;
    long pausedTime = 0;
    Button timerButton;
    boolean running;
    boolean wasRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        running = false;
        timerButton = findViewById(R.id.toggleTimerButton);
        repCountView = findViewById(R.id.repCountView);

        if (savedInstanceState != null) {
            String repCount = savedInstanceState.getString("RepCount");
            repCountView.setText(repCount);

            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        runTimer();

    }

    public void toggleTimer(View view) {
        if (running) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    public void startTimer() {
        running = true;
        timerButton.setText(R.string.pause);
//        startTime = SystemClock.elapsedRealtime();
        Log.d(LOG_TAG, "Pressed Start");
    }

    public void stopTimer() {
        running = false;
        timerButton.setText(R.string.start);
        Log.d(LOG_TAG, "Pressed Pause");
    }

    public void resetTimer(View view) {
        if (running){
            stopTimer();
        }
        Log.d(LOG_TAG, "Pressed Reset");
        startTime = 0;
        elapsedTime = 0;
        pausedTime = 0;
        seconds = 0;
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.timeView);

        final Handler handler = new Handler();

        handler.post(new Runnable() {

            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(),
                                     "%d:%02d:%02d", hours, minutes, secs);

                timeView.setText(time);

                if (running) {
//                    Log.d(LOG_TAG, "Start Time is: " + startTime);
                    if (startTime == 0) {
                        Log.d(LOG_TAG, "Start Time is: " + startTime);
                        startTime = SystemClock.elapsedRealtime();
                    }
                    elapsedTime = SystemClock.elapsedRealtime() - (startTime + pausedTime);
//                    Log.d(LOG_TAG, "Elapsed time: " + elapsedTime + " = " + SystemClock.elapsedRealtime() + " - ( " + startTime + " + " + pausedTime + " )" );
                    seconds = (int) elapsedTime / 1000;
                } else if (!running && elapsedTime > 0) {
                    pausedTime = SystemClock.elapsedRealtime() - (startTime + elapsedTime);
//                    Log.d(LOG_TAG, "Paused time: " + pausedTime + " = " + SystemClock.elapsedRealtime() + " - ( " + startTime + " + " + elapsedTime + " )" );
                }

                handler.post(this);
            }
        });
    }

    public void decrementRepCount(View view) {
        int repCount = getRepCount();
        repCount -= 1;
        repCountView.setText(String.valueOf(repCount));
    }

    public void incrementRepCount(View view) {
        int repCount = getRepCount();
        repCount += 1;
        repCountView.setText(String.valueOf(repCount));
    }

    public int getRepCount() {
        int repCount = Integer.parseInt((String) repCountView.getText());
        return repCount;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String repCount = String.valueOf(repCountView.getText());
        outState.putString("RepCount", repCount);

        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (wasRunning) {
            running = true;
        }
    }
}