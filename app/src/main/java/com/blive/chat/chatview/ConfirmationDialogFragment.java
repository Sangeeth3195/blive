package com.blive.chat.chatview;

import android.app.Dialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.blive.R;

public class ConfirmationDialogFragment extends DialogFragment {
    private String title, message;
    private View.OnClickListener yesClickListener, noClickListener;

    public ConfirmationDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_confirmation, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);

        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.message)).setText(message);
        view.findViewById(R.id.yes).setOnClickListener(view1 -> {
            dismiss();
            yesClickListener.onClick(view1);
        });

        view.findViewById(R.id.no).setOnClickListener(view12 -> {
            dismiss();
            noClickListener.onClick(view12);
        });

        return dialog;
    }

    public static ConfirmationDialogFragment newInstance(String title, String message,
                                                         View.OnClickListener yesClickListener,
                                                         View.OnClickListener noClickListener) {
        ConfirmationDialogFragment dialogFragment = new ConfirmationDialogFragment();
        dialogFragment.title = title;
        dialogFragment.message = message;
        dialogFragment.yesClickListener = yesClickListener;
        dialogFragment.noClickListener = noClickListener;
        return dialogFragment;
    }
}
