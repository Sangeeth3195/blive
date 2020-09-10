package com.blive.fragment.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by sans on 02/12/16.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialog dialogue;
    private OnDateSetListener listener;
    private Long maxTime = null;
    private Long minTime = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date_picker_blue as the default date_picker_blue in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        this.dialogue = new DatePickerDialog(getActivity(), this, year, month, day);
        if (maxTime != null) {
            dialogue.getDatePicker().setMaxDate(maxTime);
        } else if (minTime != null) {
            dialogue.getDatePicker().setMinDate(minTime);
        }
        return dialogue;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date_picker_blue chosen by the user
        if (listener != null) {
            listener.onDateSet(view, year, month, day);
        }
    }

    public void setMaxTime(long time) {
        this.maxTime = time;
    }

    public void setMinTime(Long minTime) {
        this.minTime = minTime;
    }

    public void setListener(OnDateSetListener listener) {
        this.listener = listener;
    }

    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int day);
    }
}