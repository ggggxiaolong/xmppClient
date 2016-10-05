package com.github.ggggxiaolong.xmpp.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.datasource.model.Contact;
import com.github.ggggxiaolong.xmpp.utils.CommonField;
import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;

import java.util.List;

import timber.log.Timber;

public final class ContactFragment extends Fragment {

    private RecyclerView mList;
    private LocalBroadcastManager mBroadcastManager;
    private ContactAdapter mAdapter;
    private List<Contact> mContacts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mList = (RecyclerView) inflater.inflate(R.layout.fragment_contact, container, false);
        initData(container.getContext());
        return mList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBroadcastManager = ObjectHolder.getBroadcastManager();
        mBroadcastManager.registerReceiver(mContactReceiver, new IntentFilter(CommonField.RECEIVER_CONTACT));
    }

    private void initData(Context ctx) {
        mContacts = Contact.selectAll();
        Timber.i("contact size: %s", mContacts.size());
        mAdapter = new ContactAdapter();
        mAdapter.setData(mContacts);
        mAdapter.setOnItemClickListener(mItemClickListener);
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(new LinearLayoutManager(ctx));
    }

    @Override
    public void onDetach() {
        mBroadcastManager.unregisterReceiver(mContactReceiver);
        super.onDetach();
    }

    private BroadcastReceiver mContactReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            List<Contact> contacts = Contact.selectAll();
            mAdapter.setData(contacts);
            mAdapter.notifyDataSetChanged();
        }
    };

    private Listener.OnItemClickListener mItemClickListener = new Listener.OnItemClickListener() {
        @Override
        public void onClick(View view, int position) {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.FROM_ID, mContacts.get(position).user_id());
            view.getContext().startActivity(intent);
        }
    };
}
