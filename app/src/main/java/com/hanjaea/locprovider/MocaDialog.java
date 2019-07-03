package com.hanjaea.locprovider;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hanjaea.locprovider.utils.LogUtil;

public class MocaDialog implements OnCheckedChangeListener {
    private final String TAG = "MocaDialog";

    private Context mContext;

    private Dialog mocaDlg;

    private TextView titleView;
    private TextView messageView;
    private Button btnAction;
    private Button btnCancel;
    private ImageView imgDivice;
    private ListView listView;
    private RadioGroup radioGroup_dlg;
    private ScrollView scrollView_radio;

    private Handler btnActionHandler;
    private Handler btnCancelHandler;

    private String mStrTag;

    private int mIntIndex;

    public MocaDialog(Context context) {
        this.mContext = context;
        mocaDlg = new Dialog(mContext);
        mocaDlg.getLayoutInflater();
        mocaDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mocaDlg.setContentView(R.layout.dlg_confirm);
        mocaDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mocaDlg.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        titleView = (TextView) mocaDlg.findViewById(R.id.lbl_pop_title);
        messageView = (TextView) mocaDlg.findViewById(R.id.lbl_pop_msg);
        btnAction = (Button) mocaDlg.findViewById(R.id.btn_pop_action);
        btnCancel = (Button) mocaDlg.findViewById(R.id.btn_pop_cancel);
        imgDivice = (ImageView) mocaDlg.findViewById(R.id.line_dlg_devide);
        listView = (ListView) mocaDlg.findViewById(R.id.list_dlg_radio);
        radioGroup_dlg = (RadioGroup) mocaDlg.findViewById(R.id.radioGroup_dlg);
        scrollView_radio = (ScrollView) mocaDlg.findViewById(R.id.scrollView_radio);
    }


    public MocaDialog setTitle(int titleId) {
        return setTitle(mContext.getResources().getString(titleId));
    }

    public MocaDialog setTitle(String title) {
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * wooya510@20150915:ADD
     * @param obj
     * @return
     */
    private Object mObj;
    public MocaDialog setData(Object obj) {
        mObj = obj;
        return this;
    }

    public MocaDialog setMessage(int messageId) {
        return setMessage(mContext.getResources().getString(messageId));
    }

    public MocaDialog setMessage(String message) {
        messageView.setText(message);
        messageView.setVisibility(View.VISIBLE);
        return this;
    }

    public MocaDialog setListAdapter(ListAdapter listAdapter) {
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(listAdapter);
        return this;
    }

    public MocaDialog setActionButton(int buttonTextId) {
        return setActionButton(mContext.getResources().getString(buttonTextId));
    }

    public MocaDialog setRadioGroup(String[] name, String[] cate, int index) {
        int size = name.length;
        RadioButton[] radio_btn = new RadioButton[size];
        LogUtil.d(TAG, "size : " + size);
        for (int i = 0; i < size; i++) {
            radio_btn[i] = new RadioButton(mContext);
            radio_btn[i].setId(i);
            radio_btn[i].setTag(cate[i]);
//			radio_btn[i].setButtonDrawable(R.drawable.radio_pop);
            radio_btn[i].setText(name[i]);
            LogUtil.d(TAG, "name : " + name[i]);
            LogUtil.d(TAG, "cate : " + cate[i]);
            radioGroup_dlg.addView(radio_btn[i]);
        }
        radioGroup_dlg.setVisibility(View.VISIBLE);
        scrollView_radio.setVisibility(View.VISIBLE);
        radio_btn[index].setChecked(true);
        radioGroup_dlg.setOnCheckedChangeListener(this);
        return this;
    }

    public MocaDialog setActionButton(String buttonText) {
        return setActionButton(buttonText, null);
    }

    public MocaDialog setActionButton(int buttonTextId, Handler h) {
        return setActionButton(mContext.getResources().getString(buttonTextId), h);
    }

    public MocaDialog setActionButton(String buttonText, Handler h) {
        btnActionHandler = h;

        btnAction.setVisibility(View.VISIBLE);
        btnAction.setText(buttonText);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnActionHandler != null) {
                    Message msg = btnActionHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = mObj;
                    btnActionHandler.sendMessage(msg);
                }
                mocaDlg.dismiss();
            }
        });

        return this;
    }

    // BenefitsFragment로 cate_id를 전송
    public MocaDialog setRadioActionButton(int buttonText, Handler h) {
        btnActionHandler = h;

        btnAction.setVisibility(View.VISIBLE);
        btnAction.setText(mContext.getString(buttonText));
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnActionHandler != null) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("TAG", mStrTag);
                    bundle.putInt("INDEX", mIntIndex);
                    msg.setData(bundle);
                    btnActionHandler.sendMessage(msg);
                }
                mocaDlg.dismiss();

            }
        });

        return this;
    }

    public MocaDialog setCancelButton(int buttonTextId) {
        return setCancelButton(mContext.getResources().getString(buttonTextId));
    }

    public MocaDialog setCancelButton(String buttonText) {
        return setCancelButton(buttonText, null);
    }

    public void setActionButton(String buttonText, boolean handler) {
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText(buttonText);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mocaDlg.dismiss();
            }
        });
    }

    public void setActionButton(int buttonText, boolean handler) {
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText(buttonText);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mocaDlg.dismiss();
            }
        });
    }

    public MocaDialog setCancelButton(int buttonTextId, Handler h) {
        return setCancelButton(mContext.getResources().getString(buttonTextId), h);
    }

    public MocaDialog setCancelButton(String buttonText, Handler h) {
        btnCancelHandler = h;

        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText(buttonText);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCancelHandler != null) {
                    btnCancelHandler.sendEmptyMessage(0);
                }
                mocaDlg.dismiss();
            }
        });

        return this;
    }

    synchronized public void show() {
        if (btnAction.getVisibility() == View.VISIBLE && btnCancel.getVisibility() == View.VISIBLE) {
            imgDivice.setVisibility(View.VISIBLE);
        }
        mocaDlg.setCancelable(false);

        try {
            if (!mocaDlg.isShowing() && mContext != null) {
                mocaDlg.show();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mStrTag = group.getChildAt(checkedId).getTag().toString();
        mIntIndex = checkedId;
        LogUtil.d(TAG, "tag : " + group.getChildAt(checkedId).getTag());
    }

    public boolean isShow() {
        boolean isShow = mocaDlg.isShowing();
        return isShow;
    }

    public void dismiss() {
        try {
            if (mocaDlg != null && mContext != null) {
                mocaDlg.dismiss();
            }
        } catch (Exception e) {
        }
    }
}
