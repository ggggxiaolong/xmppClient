package com.github.ggggxiaolong.xmpp.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.datasource.model.Chat;
import com.github.ggggxiaolong.xmpp.datasource.model.Contact;
import com.github.ggggxiaolong.xmpp.datasource.model.ImmutableChat;
import com.github.ggggxiaolong.xmpp.utils.CommonField;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;
import com.github.ggggxiaolong.xmpp.utils.XMPPUtil;

import org.jivesoftware.smack.SmackException;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.HOLDER_ME;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.HOLDER_OTHER;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_CONTENT;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_FORM;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_HOLDER;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_TIME;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.content)
    EditText mContent;
    @BindView(R.id.send)
    TextView mSend;
    @BindView(R.id.action_panel)
    LinearLayout mActionPanel;
    @BindView(R.id.root)
    CoordinatorLayout mRoot;

    public static final String FROM_ID = "ChatActivity.fromId";
    private Contact mContact;
    private ChatAdapter mAdapter;
    private LocalBroadcastManager mBroadcastManager;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        init();
        initData();
    }

    /**
     * 更新数据
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    private void init() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBroadcastManager = LocalBroadcastManager.getInstance(getApplication());
        mBroadcastManager.registerReceiver(mChatReceiver, new IntentFilter(CommonField.RECEIVER_CHAT));
        mAdapter = new ChatAdapter();
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mIntent = new Intent(CommonField.RECEIVER_CHAT);
    }

    private void initData() {
        String userId = getIntent().getStringExtra(FROM_ID);
        if (isEmpty(userId)) {
            Timber.e("userId is null!!!");
            finish();
        }
        mContact = Contact.query(userId);
        getSupportActionBar().setTitle(mContact.user_name());
        List<Chat> chats = Chat.byFrom(userId);
        mAdapter.setData(chats);
        mAdapter.notifyDataSetChanged();
        mList.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @OnClick(R.id.send)
    public void send() {
        org.jivesoftware.smack.chat.Chat chat = XMPPUtil.getChat(mContact.user_id());
        String text = "";
        try {
            text = mContent.getText().toString();
            chat.sendMessage(text);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e);
            Toast.makeText(getApplicationContext(), R.string.error_send, Toast.LENGTH_SHORT).show();
            return;
        }
        mContent.setText("");
        chat.getThreadID();
        ImmutableChat chat1 = ImmutableChat.builder()
                .from_id(mContact.user_id())
                .content(text)
                .holder(true)
                .time(new Date().getTime()).build();
        mAdapter.addData(chat1);
        mList.scrollToPosition(mAdapter.getItemCount() - 1);
        ThreadUtil.runONWorkThread(() -> {
            Chat.insert(chat1);
        });
        mIntent.putExtra(INTENT_FORM, chat1.from_id());
        mIntent.putExtra(INTENT_CONTENT, chat1.content());
        mIntent.putExtra(INTENT_TIME, chat1.time());
        mIntent.putExtra(INTENT_HOLDER, HOLDER_ME);
        mBroadcastManager.sendBroadcast(mIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadcastManager.unregisterReceiver(mChatReceiver);
    }

    private BroadcastReceiver mChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(CommonField.INTENT_FORM) && intent.hasExtra(CommonField.INTENT_CONTENT)) {
                String from = intent.getStringExtra(CommonField.INTENT_FORM);
                String content = intent.getStringExtra(CommonField.INTENT_CONTENT);
                int holder = intent.getIntExtra(INTENT_HOLDER, 0);
                mList.scrollToPosition(mAdapter.getItemCount() - 1);
                if (holder != HOLDER_OTHER){
                    return;
                }
                long time = intent.getLongExtra(CommonField.INTENT_TIME, -1);
                if (time == -1) {
                    time = new Date().getTime();
                }
                ImmutableChat chat1 = ImmutableChat.builder()
                        .from_id(from)
                        .content(content)
                        .holder(false)
                        .time(time).build();
                mAdapter.addData(chat1);
            }
        }
    };
}
