package com.pcm.automailsender.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.pcm.automailsender.HighlightActivity;
import com.pcm.automailsender.R;
import com.pcm.automailsender.model.MainReaderModel;

public class HomeFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;

    private CheckBox checkContact;
    private TextView contactTV;
    private TextView contactResultTV;

    private CheckBox checkScore;
    private TextView scoreTV;
    private TextView scoreResultTV;

    private Button sendSms;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        checkContact = root.findViewById(R.id.check_read_contact);
        contactTV = root.findViewById(R.id.read_contact);
        contactResultTV = root.findViewById(R.id.contact_preview);

        checkScore = root.findViewById(R.id.check_read_score);
        scoreTV = root.findViewById(R.id.read_score);
        scoreResultTV = root.findViewById(R.id.score_preview);

        sendSms = root.findViewById(R.id.send_sms);


        if (homeViewModel.getCheckChoose() == null || homeViewModel.getCheckChoose().getValue() == null) {
            checkContact.setChecked(false);
            checkScore.setChecked(true);
        } else if (homeViewModel.getCheckChoose().getValue() == HomeViewModel.CURRENT_READ_CONTACT) {
            checkContact.setChecked(true);
            checkScore.setChecked(false);
        } else if (homeViewModel.getCheckChoose().getValue() == HomeViewModel.CURRENT_READ_SCORE) {
            checkContact.setChecked(false);
            checkScore.setChecked(true);
        };

        homeViewModel.getContactButtonTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                contactTV.setText(s);
            }
        });
        homeViewModel.getContactResultText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                contactResultTV.setText(s);
            }
        });
        homeViewModel.getScoreButtonTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                scoreTV.setText(s);
            }
        });
        homeViewModel.getScoreResultText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                scoreResultTV.setText(s);
            }
        });
        homeViewModel.getSendSmsButtonTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                sendSms.setText(s);
            }
        });
        homeViewModel.getCanSendSms().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                sendSms.setClickable(aBoolean);
            }
        });
        homeViewModel.getCheckChoose().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer checked) {
                if (checked == null) {
                    checked = HomeViewModel.CURRENT_READ_SCORE;
                }
                if (checked == HomeViewModel.CURRENT_READ_CONTACT) {
                    checkContact.setChecked(true);
                    checkScore.setChecked(false);
                } else if (checked == HomeViewModel.CURRENT_READ_SCORE) {
                    checkContact.setChecked(false);
                    checkScore.setChecked(true);
                }
            }
        });

        contactTV.setOnClickListener(this);
        scoreTV.setOnClickListener(this);
        sendSms.setOnClickListener(this);
        sendSms.setClickable(false);

        checkContact.setOnCheckedChangeListener(this);
        checkScore.setOnCheckedChangeListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        if (v == contactTV) {
//            homeViewModel.onReadContactClick();
        } else if (v == scoreTV) {
//            homeViewModel.onReadScoreClick();
        } else if (v == sendSms) {
            homeViewModel.onSendSmsClick(v.getContext());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkContact) {
            homeViewModel.setCheckContact(isChecked ? HomeViewModel.CURRENT_READ_CONTACT : HomeViewModel.CURRENT_READ_SCORE);
        } else if (buttonView == checkScore) {
            homeViewModel.setCheckContact(isChecked ? HomeViewModel.CURRENT_READ_SCORE : HomeViewModel.CURRENT_READ_CONTACT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        MainReaderModel.getInstance().readData(getContext(), homeViewModel);
    }
}