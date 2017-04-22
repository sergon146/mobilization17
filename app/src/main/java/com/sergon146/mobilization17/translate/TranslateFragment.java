package com.sergon146.mobilization17.translate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.activity.ChooseLanguageActivity;
import com.sergon146.mobilization17.util.Const;
import com.sergon146.mobilization17.util.Util;
import com.sergon146.mobilization17.util.ViewUtil;

import java.util.List;
import java.util.Locale;

public class TranslateFragment extends Fragment implements TranslateContract.View, TextToSpeech.OnInitListener {
    private TranslateContract.Presenter mPresenter;

    private TextToSpeech tts;

    private TextView tvSourceLang;
    private ImageView ivSwap;
    private TextView tvTargetLang;

    private EditText etSource;
    private ImageView ivClear;
    private ImageView ivSourceSpeechOut;
    private ViewSwitcher vsSource;
    private ImageView ivSourceSpeechIn;

    private LinearLayout llOfflineMessage;
    private Button butRetry;

    private TextView tvTarget;
    private LinearLayout llMeanInner;

    private LinearLayout llButtons;

    private ImageView ivTargetSpeechOut;
    private ImageView ivFavourite;

    private ProgressBar pbTranslate;

    private ViewSwitcher vsTarget;

    public TranslateFragment() {
    }

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);

        tts = new TextToSpeech(getContext(), this);

        tts.setOnUtteranceProgressListener(getUtteranceListener());
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
        tts.shutdown();
    }

    private void initViews(View rootView) {
        tvSourceLang = (TextView) rootView.findViewById(R.id.source_lang_view);
        tvSourceLang.setOnClickListener(getLangClickListener(Const.SOURCE));

        ivSwap = (ImageView) rootView.findViewById(R.id.swap);
        ivSwap.setOnClickListener(v -> swapLanguage());

        tvTargetLang = (TextView) rootView.findViewById(R.id.target_lang_view);
        tvTargetLang.setOnClickListener(getLangClickListener(Const.TARGET));

        etSource = (EditText) rootView.findViewById(R.id.source_edit_text);
        etSource.addTextChangedListener(getSourceTextWatcher());

        ivClear = (ImageView) rootView.findViewById(R.id.clear_text_view);
        ivClear.setOnClickListener(v -> clearAll());

        ivSourceSpeechOut = (ImageView) rootView.findViewById(R.id.source_speech_view);
        ivSourceSpeechOut.setOnClickListener(v -> speakSourceOut());

        ivSourceSpeechIn = (ImageView) rootView.findViewById(R.id.source_micro_view);
        ivSourceSpeechIn.setOnClickListener(v -> startVoiceRecognitionActivity());

        llOfflineMessage = (LinearLayout) rootView.findViewById(R.id.offline_message);
        butRetry = (Button) rootView.findViewById(R.id.retry_button);
        butRetry.setOnClickListener(v -> mPresenter.loadTranslate(getSourceText()));

        tvTarget = (TextView) rootView.findViewById(R.id.target_translate);

        llMeanInner = (LinearLayout) rootView.findViewById(R.id.mean_inner_container);
        llButtons = (LinearLayout) rootView.findViewById(R.id.button_layout);

        ivTargetSpeechOut = (ImageView) rootView.findViewById(R.id.target_speech_view);
        ivTargetSpeechOut.setOnClickListener(v -> speakTargetOut());

        ivFavourite = (ImageView) rootView.findViewById(R.id.favourite);
        ivFavourite.setOnClickListener(v -> {
            ViewUtil.animateClick(getContext(), v, R.anim.click_scale);
            mPresenter.setFavourite();
        });

        pbTranslate = (ProgressBar) rootView.findViewById(R.id.translate_progress_bar);

        vsSource = (ViewSwitcher) rootView.findViewById(R.id.source_speech_switcher);
        vsTarget = (ViewSwitcher) rootView.findViewById(R.id.target_speech_switcher);
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
        ViewUtil.animateClick(getContext(), ivSwap, R.anim.rotate);
        setSourceText(tvTarget.getText().toString());
        hideTargetText();
        clearMeanLayout();
        mPresenter.swapLanguage();
    }

    @Override
    public void setSourceText(String sourceText) {
        etSource.setText(sourceText);
    }

    @Override
    public String getSourceText() {
        return etSource.getText().toString();
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
    public String getTargetText() {
        return tvTarget.getText().toString();
    }

    @Override
    public void hideSourceSpeechOut() {
        vsSource.setVisibility(View.GONE);
    }

    @Override
    public void showSourceSpeechOut() {
        vsSource.setVisibility(View.VISIBLE);
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
        vsSource.showNext();
        mPresenter.speakSourceOut(etSource.getText().toString(), tts);
    }

    @Override
    public void speakTargetOut() {
        vsTarget.showNext();
        mPresenter.speakTargetOut(tvTarget.getText().toString(), tts);
    }

    @Override
    public void clearMeanLayout() {
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
    public void hideOfflineMessage() {
        llOfflineMessage.setVisibility(View.GONE);
    }

    @Override
    public void showOfflineMessage() {
        llOfflineMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbTranslate.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        pbTranslate.setVisibility(View.VISIBLE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
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

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mPresenter.getSourceLangCode());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.recognition));
        startActivityForResult(intent, Const.REQUEST_CODE_RECOGNITION);
    }

    private UtteranceProgressListener getUtteranceListener() {
        return new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                swapView(utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                swapView(utteranceId);
            }

            private void swapView(String utteranceId) {
                getActivity().runOnUiThread(() -> {
                    switch (utteranceId) {
                        case Const.SOURCE_SPEECH:
                            vsSource.showPrevious();
                            break;
                        case Const.TARGET_SPEECH:
                            vsTarget.showPrevious();
                            break;
                    }
                });
            }
        };
    }

    private TextWatcher getSourceTextWatcher() {
        return new TextWatcher() {
            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = Util.trimAll(s.toString());
                if (text.isEmpty()) {
                    hideSourceSpeechOut();
                    hideTargetText();
                } else {
                    showSourceSpeechOut();
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new CountDownTimer(600, 1) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            mPresenter.loadTranslate(text);
                        }
                    }.start();
                }
            }
        };
    }

    private void clearAll() {
        setSourceText("");
        clearMeanLayout();
        hideTargetText();
        hideButtons();
        hideProgress();
        hideSourceSpeechOut();
    }
}
