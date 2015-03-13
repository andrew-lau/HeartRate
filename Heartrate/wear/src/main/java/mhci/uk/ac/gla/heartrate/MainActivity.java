package mhci.uk.ac.gla.heartrate;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener{

    public static final String START_WORKOUT_PATH = "/start/workout";
    private static final String END_WORKOUT_PATH = "/end/workout";
    private static final String WORKOUT_KEY = "mhci.uk.ac.gla.heartrate.key.workout";
    private DataMap workoutTimeAndHeartRate = new DataMap();
    private GoogleApiClient mGoogleApiClient;

    private TextView mHeartBeatTextView;
    private TextView mDurationTextView;

    private ImageView mUpArrow;
    private ImageView mDownArrow;

    private boolean countdownRunning=false;
    private boolean stopWorkout=false;

    private VibrationController vc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                stub.findViewById(R.id.round_activity).setBackgroundColor(Color.rgb(51, 102, 153));
                mHeartBeatTextView = (TextView) stub.findViewById(R.id.heartrate);
                mDurationTextView = (TextView) stub.findViewById(R.id.duration);
                mUpArrow = (ImageView) stub.findViewById(R.id.arrow_up);
                mDownArrow = (ImageView) stub.findViewById(R.id.arrow_down);

                mUpArrow.setVisibility(View.INVISIBLE);
                mDownArrow.setVisibility(View.INVISIBLE);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // heart rate
        vc = new VibrationController(this);
        vc.setRange(20, 1000);
        vc.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void countdowns(DataMap workoutInfo){
        long warmup=workoutInfo.get("warmup");
        long workout=workoutInfo.get("workout");
        long cooldown=workoutInfo.get("cooldown");
        int age=workoutInfo.get("age");

        vc.setRange(80,(float)((220.0-age)*0.5));
        findViewById(R.id.watch_view_stub).findViewById(R.id.round_activity).setBackgroundColor(Color.RED);
        startCountdown(warmup, "Warmup");
        while(countdownRunning&&!stopWorkout){
            try {
                wait(250);
            } catch (InterruptedException e) {}
        }
        if(stopWorkout){
            return;
        }

        String workoutType=workoutTimeAndHeartRate.get("workoutType");
        if(workoutType.equals("Fat Burning"))
            vc.setRange((float)((220.0-age)*0.50),(float)((220.0-age)*0.65));
        if(workoutType.equals("Athletic"))
            vc.setRange((float)((220.0-age)*0.65),(float)((220.0-age)*0.85));
        if(workoutType.equals("High Intensity"))
            vc.setRange((float)((220.0-age)*0.85),(float)((220.0-age)*1));
        findViewById(R.id.watch_view_stub).findViewById(R.id.round_activity).setBackgroundColor(Color.GREEN);
        startCountdown(workout,"Workout");
        while(countdownRunning&&!stopWorkout){
            try {
                wait(250);
            } catch (InterruptedException e) {}
        }
        if(stopWorkout){
            return;
        }

        vc.setRange(80,(float)((220.0-age)*0.5));
        findViewById(R.id.watch_view_stub).findViewById(R.id.round_activity).setBackgroundColor(Color.BLUE);
        startCountdown(cooldown,"Cooldown");
        while(countdownRunning&&!stopWorkout){
            try {
                wait(250);
            } catch (InterruptedException e) {}
        }
        if(stopWorkout){
            return;
        }
    }

    private void startCountdown(long length, String type) {
        final String typeS=type;
        countdownRunning=true;
        new CountDownTimer(length, 1000) {

            public void onTick(long millisUntilFinished) {
                String zero = (millisUntilFinished / 1000 >= 10) ? "" : "0";
                mDurationTextView.setText("00:00:" + zero + millisUntilFinished / 1000);
                long seconds = (millisUntilFinished/1000)%60;
                long minutes = (millisUntilFinished/(1000*60))%60;
                mDurationTextView.setText(typeS+"\n"+minutes+":"+seconds);
            }

            public void onFinish() {
                mDurationTextView.setText("done!");
                countdownRunning=false;
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // workoutTimeAndHeartRate changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/workout") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateWorkout(dataMap.getDataMap(WORKOUT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // workoutTimeAndHeartRate deleted
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    private void updateWorkout(DataMap workout){
        workoutTimeAndHeartRate=workout;
    }

    public void update(Float heartRate) {
        if(mHeartBeatTextView != null) {
            if(heartRate > 0)
                mHeartBeatTextView.setText(heartRate.toString());
            else
                mHeartBeatTextView.setText("---");
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(START_WORKOUT_PATH)) {
            //Start workout
            countdowns(workoutTimeAndHeartRate);
        }
        if (messageEvent.getPath().equals(END_WORKOUT_PATH)){
            //stop workout
            stopWorkout=true;
        }
    }

    public void showUp() {
        if(mUpArrow != null)
            mUpArrow.setVisibility(View.VISIBLE);
    }

    public void showDown() {
        if(mDownArrow != null)
            mDownArrow.setVisibility(View.VISIBLE);
    }

    public void hideArrows() {
        if(mUpArrow != null && mDownArrow != null) {
            mUpArrow.setVisibility(View.INVISIBLE);
            mDownArrow.setVisibility(View.INVISIBLE);
        }
    }

}
