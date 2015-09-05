/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.template.material.menu.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yzxIM.IMManager;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.SingleChat;
import com.yzxIM.listener.IConversationListener;
import com.yzxIM.listener.MessageListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import xyz.template.material.menu.R;
import xyz.template.material.menu.provider.ScheduleContract;
import xyz.template.material.menu.ui.widget.CollectionView;
import xyz.template.material.menu.ui.widget.CollectionViewCallbacks;
import xyz.template.material.menu.ui.widget.chat.Message;
import xyz.template.material.menu.ui.widget.chat.MessageAdapter;
import xyz.template.material.menu.ui.widget.chat.MessageInputToolBox;
import xyz.template.material.menu.ui.widget.chat.OnOperationListener;
import xyz.template.material.menu.ui.widget.chat.Option;
import xyz.template.material.menu.utils.FileUtils;
import xyz.template.material.menu.utils.PrefUtils;
import xyz.template.material.menu.utils.UIUtils;

import static xyz.template.material.menu.utils.LogUtils.LOGD;
import static xyz.template.material.menu.utils.LogUtils.LOGE;
import static xyz.template.material.menu.utils.LogUtils.makeLogTag;

public class TalkDetailFragment extends Fragment implements MessageListener {
    private static final String TAG = makeLogTag(TalkDetailFragment.class);

    private static final int HERO_GROUP_ID = 1337;

    private MessageInputToolBox box;
    private ListView listView;
    private MessageAdapter adapter;
    private String toId = "";
    private String myId = "";

    public static TalkDetailFragment newInstance(String toid) {
        TalkDetailFragment fragment = new TalkDetailFragment();
        Bundle args = new Bundle();
        args.putString("toid", toid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            toId = getArguments().getString("toid", "18667141169");
        }
        IMManager.getInstance(getActivity()).setSendMsgListener(this);
        myId = PrefUtils.getUserInfo(getActivity()).getPhone();
        LOGE(TAG, "toId="+toId+",myId="+myId);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hashtags, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initMessageInputToolBox(view);
        listView = (ListView) view.findViewById(R.id.messageListview);
        adapter = new MessageAdapter(getActivity(), new ArrayList<Message>());
        listView.setAdapter(adapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hide();
                return false;
            }
        });
//        initListView(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSendMsgRespone(ChatMessage chatMessage) {
//        boolean isSUccess = IMManager.getInstance(getActivity()).sendmessage(msg);
//        handleUpdate(chatMessage.getContent(), isSUccess, Message.MSG_TYPE_TEXT,
//                myId, "avatar", toId, "avatar", true, isSUccess);
        LOGE(TAG, "onSendMsgRespone chatMessage="+chatMessage);
    }

    @Override
    public void onReceiveMessage(List list) {
        LOGE(TAG, "onReceiveMessage list="+list);
        for (Object object : list) {
            final ChatMessage chatMessage = (ChatMessage) object;
            final Message reMessage = new Message(getmsgType(chatMessage.getMsgType()), 1, toId, "avatar", myId, "avatar",
                    chatMessage.getContent(),
                    false, true, new Date()
            );

            adapter.getData().add(reMessage);
            listView.setSelection(listView.getBottom());
        }

    }

    @Override
    public void onDownloadAttachedProgress(String s, String s1, int i, int i1) {

    }

    private int getmsgType(MSGTYPE msgtype) {
        switch (msgtype) {
            case MSG_DATA_TEXT:
                return  Message.MSG_TYPE_TEXT;
            case MSG_DATA_IMAGE:
                return  Message.MSG_TYPE_PHOTO;
            case MSG_DATA_VOICE:
                return  Message.MSG_TYPE_VOICE;
            case MSG_DATA_VIDEO:
                return  Message.MSG_TYPE_VIDEO;
            case MSG_DATA_SYSTEM:
                return  Message.MSG_TYPE_SYSTEM;
            default:return Message.MSG_TYPE_TEXT;
        }
    }

    private void handleUpdate(String content, boolean success, int msgType, String fromUserName,
                              String fromUserAvatar, String toUserName, String toUserAvatar,
                              Boolean isSend, Boolean sendSucces) {
        Message message = new Message(msgType, success ? Message.MSG_STATE_SUCCESS : Message.MSG_STATE_FAIL,
                fromUserName, fromUserAvatar, toUserName, toUserAvatar, content, isSend, sendSucces, new Date());
        adapter.getData().add(message);
        listView.setSelection(listView.getBottom());
    }

    @SuppressLint("ShowToast")
    private void initMessageInputToolBox(View rootView){
        box = (MessageInputToolBox) rootView.findViewById(R.id.messageInputToolBox);
        box.setOnOperationListener(new OnOperationListener() {
            @Override
            public void send(String content) {
                ChatMessage msg = new SingleChat();
                msg.setTargetId(toId);
                msg.setSenderId(myId);
                msg.setMsgType(MSGTYPE.MSG_DATA_TEXT);//设置消息类型为文本
                msg.setContent(content); //设置消息内容
                boolean isSUccess = IMManager.getInstance(getActivity()).sendmessage(msg);
                handleUpdate(content, isSUccess, Message.MSG_TYPE_TEXT,
                        myId, "avatar", toId, "avatar", true, isSUccess);
            }

            @Override
            public void selectedFace(String content) {

                String path = FileUtils.rootDirPath + "/" + content;
                LOGD(TAG, "selectedFace path="+path);
                FileUtils.saveBitmapToFile(BitmapFactory.decodeResource(getResources(),
                                getResources().getIdentifier(content, "drawable", getActivity().getPackageName())),
                        content);
                ChatMessage msg = new SingleChat();
                msg.setTargetId(toId);
                msg.setSenderId(myId);
                msg.setMsgType(MSGTYPE.MSG_DATA_IMAGE);//设置消息类型为文本
                msg.setContent(path); //设置缩量图片路径
                msg.setPath(path); //设置图片路径
                boolean isSUccess = IMManager.getInstance(getActivity()).sendmessage(msg);
                handleUpdate(content, isSUccess, Message.MSG_TYPE_FACE,
                        myId, "avatar", toId, "avatar", true, isSUccess);
            }


            @Override
            public void selectedFuncation(int index) {
                switch (index) {
                    case 0:
                        //do some thing
                        break;
                    case 1:
                        //do some thing
                        break;

                    default:
                        break;
                }
                Toast.makeText(getActivity(), "Do some thing here, index :" +index, Toast.LENGTH_LONG).show();

            }

        });

        ArrayList<String> faceNameList = new ArrayList<String>();
        for(int x = 1; x <= 10; x++){
            faceNameList.add("big"+x);
        }
        for(int x = 1; x <= 10; x++){
            faceNameList.add("big"+x);
        }

        ArrayList<String> faceNameList1 = new ArrayList<String>();
        for(int x = 1; x <= 7; x++){
            faceNameList1.add("cig"+x);
        }


        ArrayList<String> faceNameList2 = new ArrayList<String>();
        for(int x = 1; x <= 24; x++){
            faceNameList2.add("dig"+x);
        }

        Map<Integer, ArrayList<String>> faceData = new HashMap<Integer, ArrayList<String>>();
        faceData.put(R.drawable.em_cate_magic, faceNameList2);
        faceData.put(R.drawable.em_cate_rib, faceNameList1);
        faceData.put(R.drawable.em_cate_duck, faceNameList);
        box.setFaceData(faceData);


        List<Option> functionData = new ArrayList<Option>();
        for(int x = 0; x < 5; x++){
            Option takePhotoOption = new Option(getActivity(), "Take", R.drawable.take_photo);
            Option galleryOption = new Option(getActivity(), "Gallery", R.drawable.gallery);
            functionData.add(galleryOption);
            functionData.add(takePhotoOption);
        }
        box.setFunctionData(functionData);
    }



    private void initListView(View rootView){
        //create Data
        Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Hi", false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
        Message message1 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Hello World", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24)* 8));
        Message message2 = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "device_2014_08_21_215311", false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 7));
        Message message3 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Haha", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 7));
        Message message4 = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "big3", false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 7));
        Message message5 = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "big2", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 6));
        Message message6 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_FAIL, "Tom", "avatar", "Jerry", "avatar", "test send fail", true, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 6));
        Message message7 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SENDING, "Tom", "avatar", "Jerry", "avatar", "test sending", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 6));

        List<Message> messages = new ArrayList<Message>();
        messages.add(message);
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);
        messages.add(message5);
        messages.add(message6);
        messages.add(message7);

    }


    private void createReplayMsg(Message message){

        final Message reMessage = new Message(message.getType(), 1, "Tom", "avatar", "Jerry", "avatar",
                message.getType() == 0 ? "Re:" + message.getContent() : message.getContent(),
                false, true, new Date()
        );
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * (new Random().nextInt(3) +1));
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            adapter.getData().add(reMessage);
                            listView.setSelection(listView.getBottom());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
