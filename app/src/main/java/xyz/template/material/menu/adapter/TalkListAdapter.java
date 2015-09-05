package xyz.template.material.menu.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzxIM.data.db.ConversationInfo;
import com.yzxtcp.tools.StringUtils;

import java.util.List;

import xyz.template.material.menu.R;
import xyz.template.material.menu.model.LoginData;
import xyz.template.material.menu.utils.UIUtils;


public class TalkListAdapter extends BaseAdapter {
    final Context context;
    private final int elevation;
    private int[] accountMainColors;
    private List<ConversationInfo> data;

    private LayoutInflater inflater;


    public TalkListAdapter(Context context, List<ConversationInfo> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        accountMainColors = context.getResources().getIntArray(R.array.account_action_bar);
        elevation = context.getResources().getDimensionPixelSize(R.dimen.contact_elevation);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ConversationInfo conversationInfo = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            viewHolder = new ViewHolder(convertView);
            viewHolder.statusIconSeparator.setVisibility(View.INVISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                convertView.setElevation(elevation);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int colorLevel = ((int) (Math.random() * 1000)) % accountMainColors.length;
        viewHolder.color.setImageDrawable(new ColorDrawable(accountMainColors[colorLevel]));
        viewHolder.color.setVisibility(View.VISIBLE);

        viewHolder.avatar.setVisibility(View.VISIBLE);

        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClick(conversationInfo);
            }
        });

        viewHolder.name.setText(conversationInfo.getConversationTitle());
        String statusText = "在线";

        viewHolder.outgoingMessageIndicator.setVisibility(View.GONE);

        viewHolder.smallRightText.setVisibility(View.GONE);
        viewHolder.smallRightIcon.setVisibility(View.GONE);

        convertView.setBackgroundColor(context.getResources().getColor(R.color.contact_list_active_chat_background));

        if (!statusText.isEmpty()) {
            viewHolder.smallRightText.setText("在线");
            viewHolder.smallRightText.setVisibility(View.VISIBLE);

            viewHolder.outgoingMessageIndicator.setText("你: ");
            viewHolder.outgoingMessageIndicator.setVisibility(View.VISIBLE);
            viewHolder.outgoingMessageIndicator.setTextColor(accountMainColors[colorLevel]);

            viewHolder.smallRightIcon.setVisibility(View.VISIBLE);
            viewHolder.smallRightIcon.setImageLevel(((int) (Math.random() * 1000)) % 2);
            viewHolder.largeClientIcon.setVisibility(View.GONE);
        } else {
            viewHolder.largeClientIcon.setVisibility(View.VISIBLE);
            viewHolder.largeClientIcon.setImageLevel(((int) (Math.random() * 1000)) % 2);
        }

        if (statusText.isEmpty()) {
            viewHolder.secondLineMessage.setVisibility(View.GONE);
        } else {
            viewHolder.secondLineMessage.setVisibility(View.VISIBLE);
            viewHolder.secondLineMessage.setText(statusText.trim());
        }

        viewHolder.statusIcon.setImageLevel(((int) (Math.random() * 1000)) % 8);


        return convertView;
    }

    private void onAvatarClick(ConversationInfo conversationInfo) {

    }

    class ViewHolder {

        final ImageView color;
        final ImageView avatar;
        final TextView name;
        final TextView outgoingMessageIndicator;
        final TextView secondLineMessage;
        final TextView smallRightText;
        final ImageView smallRightIcon;
        final ImageView largeClientIcon;
        final View statusIconSeparator;
        final ImageView statusIcon;
        final ImageView offlineShadow;

        public ViewHolder(View view) {
            color = (ImageView) view.findViewById(R.id.account_color_indicator);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            name = (TextView) view.findViewById(R.id.contact_list_item_name);
            outgoingMessageIndicator = (TextView) view.findViewById(R.id.outgoing_message_indicator);
            secondLineMessage = (TextView) view.findViewById(R.id.second_line_message);
            smallRightIcon = (ImageView) view.findViewById(R.id.small_right_icon);
            smallRightText = (TextView) view.findViewById(R.id.small_right_text);
            largeClientIcon = (ImageView) view.findViewById(R.id.client_icon_large);
            statusIconSeparator = view.findViewById(R.id.status_icon_separator);
            statusIcon = (ImageView) view.findViewById(R.id.contact_list_item_status_icon);
            offlineShadow = (ImageView) view.findViewById(R.id.offline_shadow);
        }
    }
}