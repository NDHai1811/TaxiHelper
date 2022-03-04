package com.google.mlkit.vision.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ViewDialog extends Dialog {

    public Dialog dialog = null;
    int count = 0;
    public ViewDialog(Context context) {
        super(context);
    }

    public void showDialog(Activity activity, String msg){
        dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        if (dialog == null && !dialog.isShowing()&&count==0) {
            dialog.show();
            count++;
        }

    }
}
