package com.github.ggggxiaolong.xmpp.user;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.ggggxiaolong.xmpp.main.MainActivity;
import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.customView.OwlView;
import com.github.ggggxiaolong.xmpp.service.XMPPService;
import com.github.ggggxiaolong.xmpp.setting.ServerPresenter;
import com.github.ggggxiaolong.xmpp.setting.ServerView;
import com.github.ggggxiaolong.xmpp.setting.SetServerActivity;
import com.github.ggggxiaolong.xmpp.utils.CommonField;
import com.github.ggggxiaolong.xmpp.utils.FabTransform;
import com.github.ggggxiaolong.xmpp.utils.PreferencesUtils;
import com.sdsmdg.tastytoast.TastyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity implements LoginView, ServerView {

    @BindView(R.id.login_pane)
    ViewGroup mContainer;
    @BindView(R.id.action_panel)
    ViewGroup mActionPanel;
    private OwlView mOwlView;
    private EditText mUsernameEdit, mPasswordEdit;
    private View mLoginView;
    private LoginPresenter mPresenter;
    private ServerPresenter mServerPresenter;
    private ImageButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mOwlView = (OwlView) findViewById(R.id.owl);
        mUsernameEdit = (EditText) findViewById(R.id.username);
        mPasswordEdit = (EditText) findViewById(R.id.password);
        mLoginView = findViewById(R.id.login);
        fab = (ImageButton) findViewById(R.id.fab);
        fab.setOnClickListener(mFabListener);

        mPasswordEdit.setOnFocusChangeListener(mFocusChangeListener);
        mLoginView.setOnClickListener(mOnClickListener);

        mPresenter = new LoginPresenter();
        mPresenter.onAttach(this);
        mServerPresenter = new ServerPresenter();
        mServerPresenter.onAttach(this);

        initData();
    }

    private void initData() {
        String username = PreferencesUtils.getString(CommonField.USER_NAME);
        if (!isEmpty(username)){
            mUsernameEdit.setText(username);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = mUsernameEdit.getText().toString();
            String password = mPasswordEdit.getText().toString();
            mPresenter.Login(username, password);
        }
    };

    private View.OnClickListener mFabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplication(), SetServerActivity.class);
            Timber.i("start Activity in %d", Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                FabTransform.addExtras(intent, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), R.drawable.ic_setting);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, getString(R.string.transition_server_setting));
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }
    };

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mOwlView.open();
            } else {
                mOwlView.close();
            }
        }
    };

    @Override
    public void loginSuccess() {
        TastyToast.makeText(getApplicationContext(), "Login Success!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
        startService(new Intent(getApplication(), XMPPService.class));
        startActivity(new Intent(getApplication(), MainActivity.class));
        finish();
    }

    @Override
    public void loginFail() {
        TastyToast.makeText(getApplicationContext(), "Login Fail!", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
    }

    @Override
    public void connect() {
        mServerPresenter.Connect();
    }

    @Override
    public void connectFail() {
        TastyToast.makeText(getApplicationContext(), "Set Server First!", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
    }

    @Override
    public void connectSuccess() {
        String username = mUsernameEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        mPresenter.Login(username, password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
        mServerPresenter.onDetach();
    }

}
