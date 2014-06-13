package com.tomatozq.opengl.rubik;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.tomatozq.opengl.R;

public final class GuideFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";

    public static GuideFragment newInstance(String content) {
        GuideFragment fragment = new GuideFragment();

        fragment.mContent = content;

        return fragment;
    }

    private String mContent = "???";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
        View view = inflater.inflate(R.layout.guide_fragment_layout, null);
        if ("第二页".equals(mContent)) {
            view = inflater.inflate(R.layout.guide_move_face_fragment_layout, null);
        } else if ("第三页".equals(mContent)) {
            view = inflater.inflate(R.layout.guide_confirm_move_face_fragment_layout, null);
            Button btnSavePwd = (Button) view.findViewById(R.id.btn_OK);
            btnSavePwd.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(getActivity(), RobikCube2DActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
