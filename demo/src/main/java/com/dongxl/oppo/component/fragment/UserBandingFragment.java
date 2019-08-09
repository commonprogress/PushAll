package com.dongxl.oppo.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.jpushdemo.R;

/**
 * <p>Title:${Title} </p>
 * <p>Description: PushInfoFragment</p>
 * <p>Copyright (c) 2016 www.oppo.com Inc. All rights reserved.</p>
 * <p>Company: OPPO</p>
 *
 * @author QuWanxin
 * @version 1.0
 * @date 2017/7/27
 */

public class UserBandingFragment extends BaseFragment {

    private EditText mEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_banding, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditText = getView().findViewById(R.id.et_alias_or_topics);
    }

    public String getInputText() {
        return mEditText.getText().toString();
    }
}
