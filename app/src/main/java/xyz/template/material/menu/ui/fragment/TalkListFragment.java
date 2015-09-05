package xyz.template.material.menu.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.listener.IConversationListener;

import java.util.List;

import xyz.template.material.menu.Config;
import xyz.template.material.menu.R;
import xyz.template.material.menu.adapter.TalkListAdapter;
import xyz.template.material.menu.model.TagMetadata;
import xyz.template.material.menu.ui.widget.CollectionViewCallbacks;
import xyz.template.material.menu.utils.UIUtils;

import static xyz.template.material.menu.utils.LogUtils.LOGD;
import static xyz.template.material.menu.utils.LogUtils.LOGW;
import static xyz.template.material.menu.utils.LogUtils.makeLogTag;
import static xyz.template.material.menu.utils.UIUtils.buildStyledSnippet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TalkListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TalkListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TalkListFragment extends Fragment implements AdapterView.OnItemClickListener, IConversationListener{
    private static final String TAG = makeLogTag(TalkListFragment.class);
    private TalkListAdapter adapter;

    private ListView listView;

    /**
     * View with information shown on empty contact list.
     */
    private View infoView;

    /**
     * Image view with connected icon.
     */
    private View connectedView;

    /**
     * Image view with disconnected icon.
     */
    private View disconnectedView;

    /**
     * View with help text.
     */
    private TextView textView;

    /**
     * Button to apply help text.
     */
    private Button buttonView;

    /**
     * Animation for disconnected view.
     */
    private Animation animation;

    private List<ConversationInfo> conversationInfoList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TalkListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TalkListFragment newInstance(String param1, String param2) {
        TalkListFragment fragment = new TalkListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TalkListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        IMManager.getInstance(getActivity()).setConversationListener(this);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateConversation(ConversationInfo conversationInfo) {
        conversationInfoList.add(conversationInfo);
    }

    @Override
    public void onDeleteConversation(ConversationInfo conversationInfo) {

    }

    @Override
    public void onUpdateConversation(ConversationInfo conversationInfo) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_talk_list, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
        listView.setItemsCanFocus(true);
        registerForContextMenu(listView);
        conversationInfoList = IMManager.getInstance(getActivity()).getConversationList();
        LOGD(TAG, "conversationInfoList = " + conversationInfoList);
        adapter = new TalkListAdapter(getActivity(), conversationInfoList);
        listView.setAdapter(adapter);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.connection);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
