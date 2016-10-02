package com.github.ggggxiaolong.xmpp.setting;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.utils.CommonField;
import com.github.ggggxiaolong.xmpp.utils.FabTransform;
import com.github.ggggxiaolong.xmpp.utils.PreferencesUtils;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;
import com.sdsmdg.tastytoast.TastyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

public class SetServerActivity extends Activity implements ServerView {

    @BindView(R.id.container)
    ViewGroup mContainer;
    @BindView(R.id.name)
    TextInputEditText mName;
    @BindView(R.id.address)
    TextInputEditText mAddress;
    @BindView(R.id.port)
    TextInputEditText mPort;
    @BindView(R.id.confirm)
    Button mConfirm;
    private ServerPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_server);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FabTransform.setup(this, mContainer);
        }

        mPresenter = new ServerPresenter();
        mPresenter.onAttach(this);

        //set watcher action
        initListener();
        //set the latest data
        initData();
    }

    private void initData() {
        String name = PreferencesUtils.getString(CommonField.SERVER_NAME);
        String address = PreferencesUtils.getString(CommonField.SERVER_ADDRESS);
        int port = PreferencesUtils.getInt(CommonField.SERVER_PORT);
        Timber.i("name = %s, address = %s, port = %s", name, address, port);
        if (!isEmpty(address) && !isEmpty(name) && port != 0) {
            mName.setText(name);
            mAddress.setText(address);
            mPort.setText(String.valueOf(port));
        }
    }

    private void initListener() {
        mName.addTextChangedListener(mTextWatcher);
        mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mAddress.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mAddress.addTextChangedListener(mTextWatcher);
        mAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPort.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPort.addTextChangedListener(mTextWatcher);
        mPort.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && isSettingValid()) {
                    mConfirm.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    @OnClick(R.id.cancel)
    public void dismiss(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    @OnClick(R.id.confirm)
    void setServer() {
        Timber.i("sure clicked");
        ThreadUtil.runONWorkThread(new Runnable() {
            @Override
            public void run() {
                String address = mAddress.getText().toString();
                String name = mName.getText().toString();
                int port = Integer.parseInt(mPort.getText().toString());
                mPresenter.Connect(name, address, port);
            }
        });
    }

    private boolean isSettingValid() {
        return mName.length() > 0 && mAddress.length() > 0 && mPort.length() > 0;
    }

    @Override
    public void connectFail() {
        Timber.i("connectFail");
        TastyToast.makeText(getApplicationContext(), "Connect Fail!", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
    }

    @Override
    public void connectSuccess() {
        Timber.i("connectSuccess");
        dismiss(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mConfirm.setEnabled(isSettingValid());
        }
    };
}
