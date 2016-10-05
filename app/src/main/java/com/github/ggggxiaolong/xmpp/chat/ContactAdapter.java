package com.github.ggggxiaolong.xmpp.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.datasource.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrTan
 * date: 10/4/16
 */

final class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> mData = new ArrayList<>();
    private Listener.OnItemClickListener mOnItemClickListener;

    public void setData(@NonNull List<Contact> data) {
        mData.clear();
        mData.addAll(data);
    }

    void setOnItemClickListener(Listener.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_contact, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mData.get(position);
        holder.icon.setImageResource(R.drawable.avatar);
        holder.title.setText(contact.user_name());
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
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.avatar);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

}
