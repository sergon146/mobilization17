package com.sergon146.mobilization17.translate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.activity.ChooseLanguageActivity;
import com.sergon146.mobilization17.util.Const;

import java.util.List;
import java.util.Locale;

public class TrFragment extends Fragment implements TranslateContract.View, TextToSpeech.OnInitListener {
    private TranslateContract.Presenter mPresenter;

    private TextToSpeech tts;

    private TextView tvSourceLang;
    private ImageView ivSwap;
    private TextView tvTargetLang;

    private EditText etSource;
    private ImageView ivClear;
    private ImageView ivSourceSpeechOut;
    private ImageView ivSourceSpeechIn;

    private TextView tvConnectionError;
    private TextView tvTarget;
    private LinearLayout llMeanInner;

    private LinearLayout llButtons;

    private ImageView ivTargetSpeechOut;
    private ImageView ivFavourite;

    private ProgressBar progressBar;


    public TrFragment() {
    }

    public static TrFragment newInstance() {
        return new TrFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        tts = new TextToSpeech(getContext(), this);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    private void initViews(View rootView) {
        tvSourceLang = (TextView) rootView.findViewById(R.id.sourceLangView);
        tvSourceLang.setOnClickListener(getLangClickListener(Const.SOURCE));
        mPresenter.setSourceLang();

        ivSwap = (ImageView) rootView.findViewById(R.id.swap);
        ivSwap.setOnClickListener(v -> swapLanguage());

        tvTargetLang = (TextView) rootView.findViewById(R.id.targetLangView);
        tvTargetLang.setOnClickListener(getLangClickListener(Const.TARGET));
        mPresenter.setTargetLang();

        etSource = (EditText) rootView.findViewById(R.id.source_edit_text);
        etSource.addTextChangedListener(new TextWatcher() {
            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    hideSourceSpeechOut();
                    setTargetText("");
                } else {
                    showSourceSpeechOut();
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new CountDownTimer(600, 1) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            mPresenter.loadTranslate(s.toString());
                        }
                    }.start();
                }
            }

        });

        ivClear = (ImageView) rootView.findViewById(R.id.clear_text_view);
        ivClear.setOnClickListener(v -> clearAll());

        ivSourceSpeechOut = (ImageView) rootView.findViewById(R.id.source_speech_view);
        ivSourceSpeechOut.setOnClickListener(v -> speakSourceOut());

        ivSourceSpeechIn = (ImageView) rootView.findViewById(R.id.source_micro_view);
        ivSourceSpeechIn.setOnClickListener(null); // TODO: 21.04.2017

        tvConnectionError = (TextView) rootView.findViewById(R.id.offline_message);

        tvTarget = (TextView) rootView.findViewById(R.id.target_translate);

        llMeanInner = (LinearLayout) rootView.findViewById(R.id.mean_inner_container);

        llButtons = (LinearLayout) rootView.findViewById(R.id.buttonLayout);

        ivTargetSpeechOut = (ImageView) rootView.findViewById(R.id.target_speech_view);
        ivTargetSpeechOut.setOnClickListener(v -> speakTargetOut());

        ivFavourite = (ImageView) rootView.findViewById(R.id.favourite);
        ivFavourite.setOnClickListener(v -> mPresenter.setFavourite());

        progressBar = (ProgressBar) rootView.findViewById(R.id.translateProgressBar);
    }

    private View.OnClickListener getLangClickListener(final String langType) {
        return v -> {
            Intent intent = new Intent(getActivity(), ChooseLanguageActivity.class);
            switch (langType) {
                case Const.SOURCE:
                    intent.setAction(Const.SOURCE);
                    startActivityForResult(intent, Const.REQUEST_CODE_SOURCE);
                    break;
                case Const.TARGET:
                    intent.setAction(Const.TARGET);
                    startActivityForResult(intent, Const.REQUEST_CODE_TARGET);
                    break;
            }
        };
    }

    @Override
    public void setPresenter(TranslateContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setSourceLang(String sourceLang) {
        tvSourceLang.setText(sourceLang);
    }

    @Override
    public void setTargetLang(String targetLang) {
        tvTargetLang.setText(targetLang);
    }

    @Override
    public void swapLanguage() {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        ivSwap.startAnimation(rotate);
        setSourceText(tvTarget.getText().toString());
        setTargetText("");
        hideMean();
        mPresenter.swapLanguage();
    }

    @Override
    public void setSourceText(String sourceText) {
        etSource.setText(sourceText);
    }

    @Override
    public void hideTargetText() {
        tvTarget.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showTargetText() {
        tvTarget.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSourceSpeechOut() {
        ivSourceSpeechOut.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSourceSpeechOut() {
        ivSourceSpeechOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTargetText(String targetText) {
        tvTarget.setText(targetText);
    }

    @Override
    public void hideButtons() {
        llButtons.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showButtons() {
        llButtons.setVisibility(View.VISIBLE);
    }

    @Override
    public void speakSourceOut() {
        mPresenter.speakSourceOut(etSource.getText().toString(), tts);
    }

    @Override
    public void speakTargetOut() {
        mPresenter.speakTargetOut(tvTarget.getText().toString(), tts);
    }

    @Override
    public void hideMean() {
        llMeanInner.removeAllViews();
    }

    @Override
    public void setMean(List<View> layoutList) {
        llMeanInner.removeAllViews();
        for (View vg : layoutList) {
            llMeanInner.addView(vg);
        }
    }

    @Override
    public void hideConnectionError() {

    }

    @Override
    public void showConnectionError() {

    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void toastError() {
        Toast.makeText(getContext(), getString(R.string.translate_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeFavourite(boolean isFavourite) {
        if (isFavourite) {
            ivFavourite.setImageResource(R.drawable.ic_active_favourite);
        } else {
            ivFavourite.setImageResource(R.drawable.ic_inactive_favourite);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    private void clearAll() {
        setSourceText("");
        hideMean();
        hideTargetText();
        hideButtons();
        hideProgress();
        hideSourceSpeechOut();
    }
}
