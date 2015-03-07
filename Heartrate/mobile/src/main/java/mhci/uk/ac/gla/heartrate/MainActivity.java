package mhci.uk.ac.gla.heartrate;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.DatePicker;


public class MainActivity extends ActionBarActivity {

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        warm_up_info = (TextView) findViewById(R.id.warm_up_time);
//        workout_info = (TextView) findViewById(R.id.workout_time);
//        addListenerOnSpinnerItemSelection();
//        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
//        String myDate = sharedPreferences.getString("birthday", "not set");
//        if(myDate.equalsIgnoreCase("not set")) {
//            showDialog(BIRTHDAY_DIALOG_ID);
//        }

    }


    public void addListenerOnSpinnerItemSelection() {
//        workout_spinner = (Spinner) findViewById(R.id.workout_selector);
//        workout_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

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

                    // set current time into textview
                    warm_up_info.setText(new StringBuilder().append("Warm up duration: ").append(pad(warm_up_minutes))
                            .append(":").append(pad(warm_up_seconds)));

                }
            };

    private DurationPicker.OnTimeSetListener workoutDurationPickerListener =
            new DurationPicker.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedMinutes,
                                      int selectedSeconds) {
                    workout_minutes = selectedMinutes;
                    workout_seconds = selectedSeconds;

                    // set current time into textview
                    workout_info.setText(new StringBuilder().append("Workout duration: ").append(pad(workout_minutes))
                            .append(":").append(pad(workout_seconds)));

                }
            };

    private DurationPicker.OnTimeSetListener coolDownDurationPickerListener =
            new DurationPicker.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedMinutes,
                                      int selectedSeconds) {
                    cool_down_minutes = selectedMinutes;
                    cool_down_seconds= selectedSeconds;

                    // set current time into textview
                    cool_down_info.setText(new StringBuilder().append("Cool down duration: ").append(pad(cool_down_minutes))
                            .append(":").append(pad(cool_down_seconds)));

                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}