package mhci.uk.ac.gla.heartrate;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.DatePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;


public class MainActivity extends ActionBarActivity {
public class MainActivity extends ActionBarActivity  implements DataApi.DataListener, AdapterView.OnItemSelectedListener{

    private Spinner workout_spinner;

    private TextView warm_up_info;
    private TextView workout_info;
    private TextView cool_down_info;


    private int year;
    private int month;
    private int day;
    static final int BIRTHDAY_DIALOG_ID = 999;


    //maybe define default somewhere for consistency
    private int warm_up_minutes = 8;
    private int warm_up_seconds = 0;

    private int workout_minutes = 30;
    private int workout_seconds = 0;

    private int cool_down_minutes = 5;
    private int cool_down_seconds = 0;

    static final int WARM_UP_DIALOG_ID = 0;
    static final int WORKOUT_DIALOG_ID = 1;
    static final int COOL_DOWN_DIALOG_ID = 2;

    private static final String WORKOUT_KEY = "mhci.uk.ac.gla.heartrate.key.workout";
    private DataMap workoutTimeAndHeartRate= new DataMap();

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String START_WORKOUT_PATH = "/start/workout";

    private GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    Log.d(TAG, "onConnected: " + connectionHint);
                    // Can use WearableAPI
                }
                @Override
                public void onConnectionSuspended(int cause) {
                    Log.d(TAG, "onConnectionSuspended: " + cause);
                }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    Log.d(TAG, "onConnectionFailed: " + result);
                }
            })
            .addApi(Wearable.API)
            .build();

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        warm_up_info = (TextView) findViewById(R.id.warm_up_time);
//        workout_info = (TextView) findViewById(R.id.workout_time);
//        addListenerOnSpinnerItemSelection();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        //String myDate = sharedPreferences.getString("birthday", "not set");

        if(!sharedPreferences.contains("birthday")) {
            showDialog(BIRTHDAY_DIALOG_ID);
        }



    }


    public void addListenerOnSpinnerItemSelection() {
//        workout_spinner = (Spinner) findViewById(R.id.workout_selector);
//        workout_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        Spinner spinner = (Spinner) findViewById(R.id.workout_selector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.workout_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }




    public void pickTimeWarmUp(View view) {

        showDialog(WARM_UP_DIALOG_ID);
    }
    public void pickTimeWorkout(View view) {

        showDialog(WORKOUT_DIALOG_ID);
    }
    public void pickTimeCoolDown(View view) {

        showDialog(COOL_DOWN_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case WARM_UP_DIALOG_ID:
                // set time picker as current time
                return new DurationPicker(this,
                        warmUpDurationPickerListener, warm_up_minutes, warm_up_seconds);
            case WORKOUT_DIALOG_ID:
                // set time picker as current time
                return new DurationPicker(this,
                        workoutDurationPickerListener, workout_minutes, workout_seconds);
            case COOL_DOWN_DIALOG_ID:
                // set time picker as current time
                return new DurationPicker(this,
                        coolDownDurationPickerListener, cool_down_minutes, cool_down_seconds);

            case BIRTHDAY_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }



    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this);
            StringBuilder builder = new StringBuilder();
            builder.append(day + "/" + month + "/" + year + "\n");
            SharedPreferences sharedPreferences = getPreferences(MODE_APPEND);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("birthday", builder.toString());
            editor.commit();
            String myDate = sharedPreferences.getString("birthday", "not set");
            builder.append("Birthday: " + myDate + "\n");
            //tvDisplayDate.setText(builder.toString());



        }
    };


    private DurationPicker.OnTimeSetListener warmUpDurationPickerListener =
            new DurationPicker.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedMinutes,
                                      int selectedSeconds) {
                    warm_up_minutes = selectedMinutes;
                    warm_up_seconds = selectedSeconds;

                    TextView warmupMinutes = (TextView) findViewById(R.id.warmupDurationMinutes);
                    TextView warmupSeconds = (TextView) findViewById(R.id.warmupDurationSeconds);

                    warmupMinutes.setText(warm_up_minutes + " " + "Minutes");
                    warmupSeconds.setText(warm_up_seconds + " " + "Seconds");
                    long millis=selectedMinutes*60*1000;
                    millis+=(selectedSeconds*1000);

                    workoutTimeAndHeartRate.putLong("warmup", millis);

                    // set current time into textview
//                    warm_up_info.setText(new StringBuilder().append("Warm up duration: ").append(pad(warm_up_minutes))
//                            .append(":").append(pad(warm_up_seconds)));

                }
            };

    private DurationPicker.OnTimeSetListener workoutDurationPickerListener =
            new DurationPicker.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedMinutes,
                                      int selectedSeconds) {
                    workout_minutes = selectedMinutes;
                    workout_seconds = selectedSeconds;

                    TextView workoutMinutes = (TextView) findViewById(R.id.workoutDurationMinutes);
                    TextView workoutSeconds = (TextView) findViewById(R.id.workoutDurationSeconds);

                    workoutMinutes.setText(workout_minutes + " " + "Minutes");
                    workoutSeconds.setText(workout_seconds + " " + "Seconds");
                    long millis=selectedMinutes*60*1000;
                    millis+=(selectedSeconds*1000);

                    workoutTimeAndHeartRate.putLong("workout", millis);

                    // set current time into textview
//                    workout_info.setText(new StringBuilder().append("Workout duration: ").append(pad(workout_minutes))
//                            .append(":").append(pad(workout_seconds)));

                }
            };

    private DurationPicker.OnTimeSetListener coolDownDurationPickerListener =
            new DurationPicker.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedMinutes,
                                      int selectedSeconds) {
                    cool_down_minutes = selectedMinutes;
                    cool_down_seconds= selectedSeconds;

                    TextView coolDownMinutes = (TextView) findViewById(R.id.cooldownDurationMinutes);
                    TextView coolDownSeconds = (TextView) findViewById(R.id.cooldownDurationSeconds);

                    coolDownMinutes.setText(cool_down_minutes + " " + "Minutes");
                    coolDownSeconds.setText(cool_down_seconds + " " + "Seconds");
                    long millis=selectedMinutes*60*1000;
                    millis+=(selectedSeconds*1000);

                    workoutTimeAndHeartRate.putLong("cooldown", millis);

                    // set current time into textview
//                    cool_down_info.setText(new StringBuilder().append("Cool down duration: ").append(pad(cool_down_minutes))
//                            .append(":").append(pad(cool_down_seconds)));

                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private void addAgeToDataMap(){
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        String birth=prefs.getString("birthday","not set");
        if(birth!="not set"){
            String year=birth.substring(birth.lastIndexOf("/"),birth.lastIndexOf("/")+4);
            Calendar c=Calendar.getInstance();
            int age=c.YEAR-Integer.parseInt(year);
            workoutTimeAndHeartRate.putInt("age",age);
        }else{
            //default age I doubt it would ever be needed but just encase...
            workoutTimeAndHeartRate.putInt("age",21);
        }
    }

    // Needs to be called before data will be synced with the watch
    private void setWorkout() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/workout");
        putDataMapReq.getDataMap().putDataMap(WORKOUT_KEY, workoutTimeAndHeartRate);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    private void sendStartWorkoutMessage(String nodeId) {
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, nodeId, START_WORKOUT_PATH, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    private void sendStartWorkoutMessage(){
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            sendStartWorkoutMessage(node.getId());
        }
    }

    //On start workout button press
    public void workoutStarted(View view) {
        //Move to WorkoutStarted Activity
        addAgeToDataMap();
        setWorkout();
        sendStartWorkoutMessage();
        Intent workoutStartedIntent = new Intent(this, WorkoutStarted.class);
        startActivity(workoutStartedIntent);
    }


    public void changeWarmupDuration(View view) {

        showDialog(WARM_UP_DIALOG_ID);

    }

    public void changeWorkoutDuration(View view) {

        showDialog(WORKOUT_DIALOG_ID);

    }


    public void changeCoolDownDuration(View view) {

        showDialog(COOL_DOWN_DIALOG_ID);

    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        workoutTimeAndHeartRate.putString("workoutType",(String)parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        workoutTimeAndHeartRate.putString("workoutType","Athletic");
    }
}