package com.github.ggggxiaolong.xmpp.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.datasource.model.Chat;
import com.github.ggggxiaolong.xmpp.datasource.model.Contact;
import com.github.ggggxiaolong.xmpp.datasource.model.ImmutableChat;
import com.github.ggggxiaolong.xmpp.datasource.model.ImmutableSession;
import com.github.ggggxiaolong.xmpp.utils.Common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;


final class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private ArrayList<Chat.Session> mData = new ArrayList<>();
    private ArrayList<String> mMapping = new ArrayList<>();
    private Listener.OnItemClickListener mOnItemClickListener;

    public void setData(@NonNull List<Chat.Session> data) {
        mData.clear();
        mData.addAll(data);
        initMapping();
    }

    private void initMapping() {
        mMapping.clear();
        for (int i = 0; i < mData.size(); i++) {
            String fromId = mData.get(i).chat().from_id();
            mMapping.add(fromId);
            Timber.i("add fromId: %s", fromId);
        }
    }

    Chat.Session getItem(int position){
        return mData.get(position);
    }

    void update(final String from, String content, long time) {
        int position = mMapping.indexOf(from);
        Timber.i("add fromId: %s, position: %s", from, position);
        if (position < 0) {
            Timber.i("新会话");
            Contact contact = Contact.query(from);
            ImmutableChat chat = ImmutableChat.builder()
                    .from_id(from)
                    .content(content)
                    .holder(false)
                    .time(time).build();
            ImmutableSession session = ImmutableSession.builder().chat(chat).contact(contact).build();
            mData.add(0, session);
            initMapping();
            notifyItemChanged(0);
        } else {
            ImmutableSession session = (ImmutableSession) mData.get(position);
            ImmutableChat chat = (ImmutableChat) session.chat();
            chat = ImmutableChat.builder().from(chat).content(content).time(time).build();
            session = session.withChat(chat);
            mData.set(position, session);
            notifyItemChanged(position);
        }
    }

    void setOnItemClickListener(Listener.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_chat, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat.Session session = mData.get(position);
        holder.icon.setImageResource(R.drawable.avatar);
        holder.title.setText(session.contact().user_name());
        holder.subtitle.setText(session.chat().content());
        long time = session.chat().time();
        Timber.i("chat time： %d", time);
        Date date = new Date(time);
        if (Common.isToDay(date)) {
            String today = Common.getTime(date);
            Timber.i("today: %s", today);
            holder.tip.setText(today);
        } else if (Common.isTomorrow(date)) {
            Timber.i("tomorrow");
            holder.tip.setText(R.string.prompt_tomorrow);
        } else {
            String otherDay = Common.getDate(date);
            Timber.i("otherDay: %s", otherDay);
            holder.tip.setText(otherDay);
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(view -> mOnItemClickListener.onClick(holder.itemView, position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, subtitle, tip;

        ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.avatar);
            title = (TextView) itemView.findViewById(R.id.title);
            tip = (TextView) itemView.findViewById(R.id.tip);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }

}
