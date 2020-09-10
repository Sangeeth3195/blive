package com.blive.fragment.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    private TimePickerDialog dialog;
    private OnTimeSetListener listener;

    public void setListener(OnTimeSetListener listener) {
        this.listener = listener;
    }

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date_picker_blue as the default date_picker_blue in the picker
        // Get current time from calendar
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        this.dialog = new TimePickerDialog(getActivity(), this, hour, minute, false);
        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        // Do something with the time chosen by the user
        if (listener != null) {
            listener.onTimeSet(timePicker, hour, minute);
        }
    }

    public interface OnTimeSetListener {
        void onTimeSet(TimePicker view, int hour, int minute);
    }
}
