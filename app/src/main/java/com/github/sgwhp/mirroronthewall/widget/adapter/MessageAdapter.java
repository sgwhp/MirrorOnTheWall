package com.github.sgwhp.mirroronthewall.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.sgwhp.mirroronthewall.R;
import com.github.sgwhp.mirroronthewall.model.Constant;
import com.github.sgwhp.mirroronthewall.util.LogUtil;

/**
 * Created by robust on 2015/9/27.
 */
public class MessageAdapter extends BaseAdapter {
    private String[] msgs = new String[Constant.MAX_MSG_NUM];
    private LayoutInflater inflater;
    private int start;
    private int end;
    private int count;

    public MessageAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tv = (TextView) view;
        if(tv == null){
            tv = (TextView) inflater.inflate(R.layout.msg_item, null);
        }
        LogUtil.d(msgs[(start + i) % msgs.length]);
        tv.setText(msgs[(start + i) % msgs.length]);
        return tv;
    }

    public void addMsg(String msg){
        if(count == msgs.length){
            start++;
        } else {
            count++;
        }
        msgs[end] = msg;
        end = (end + 1) % msgs.length;
    }

    public void clear(){
        count = 0;
        start = 0;
        end = 0;
    }
}
