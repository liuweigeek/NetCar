package com.imagine.scott.netcar.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.MainActivity;

/**
 * Created by Scott on 15/12/7.
 */
public class SetServerAddrDialog extends DialogFragment {


    EditText serverAddrView;

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_set_serveraddr, null);
        String serverIP = MainActivity.mainActivity.addrPreferences.getString("IP", null);
        serverAddrView = (EditText) rootView.findViewById(R.id.server_addr_edit);
        serverAddrView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.setServerAddr || id == EditorInfo.IME_NULL) {
                    MainActivity.mainActivity.addrEditor.putString("IP", serverAddrView.getText().toString()).commit();
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        serverAddrView.setText(serverIP == null ? "" : serverIP);
        builder.setView(rootView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.mainActivity.addrEditor.putString("IP", serverAddrView.getText().toString()).commit();
                    }
                })
                .setNeutralButton("使用阿里云IP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serverAddrView.setText("120.27.6.25:8080");
                        MainActivity.mainActivity.addrEditor.putString("IP", "118.190.90.99").commit();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
