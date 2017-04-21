package com.sergon146.mobilization17.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.activity.ChooseLanguageActivity;
import com.sergon146.mobilization17.presenter.TranslateFragmentPresenter;
import com.sergon146.mobilization17.presenter.impl.TranslateFragmentPresenterImpl;
import com.sergon146.mobilization17.util.Const;

import java.util.Locale;

public class TranslateFragment extends Fragment implements TextToSpeech.OnInitListener {

    private TranslateFragmentPresenter presenter;
    private LinearLayout translateLayout;

    private EditText sourceEditText;
    private TextView targetTranslate;

    private TextToSpeech tts;

    public static TranslateFragment newInstance(int page) {
        TranslateFragment translateFragment = new TranslateFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Const.ARGUMENT_PAGE_NUMBER, page);
        translateFragment.setArguments(arguments);
        return translateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        translateLayout = (LinearLayout) inflater.inflate(R.layout.fragment_translate, container, false);
        presenter = new TranslateFragmentPresenterImpl(this);

        sourceEditText = (EditText) translateLayout.findViewById(R.id.source_edit_text);
        sourceEditText.addTextChangedListener(presenter.getSourceTextWatcher());
        targetTranslate = (TextView) translateLayout.findViewById(R.id.target_translate);

        if (sourceEditText.getText().toString().isEmpty()) {
            hideButtons();
        } else {
            showButtons();
        }

        tts = new TextToSpeech(getContext(), this);


        updateSourceBar(presenter.getSourceName());
        updateTargetBar(presenter.getTargetName());

        initButtons();
        initBarButton();
        setHasOptionsMenu(true);

        return translateLayout;
    }

    private void initButtons() {
        ImageView clear = (ImageView) translateLayout.findViewById(R.id.clear_text_view);
        clear.setOnClickListener(view -> {
            setSourceText("");
            setTargetText("");
            hideButtons();
            hideTranslateProgress();
        });

        ImageView sourceSpeech = (ImageView) translateLayout.findViewById(R.id.source_speech_view);
        sourceSpeech.setOnClickListener(view -> {
            animateScale(view);
            speakSourceOut();
        });

        ImageView targetSpeech = (ImageView) translateLayout.findViewById(R.id.target_speech_view);
        targetSpeech.setOnClickListener(view -> {
            animateScale(view);
            speakTargetOut();
        });

        ImageView favourite = (ImageView) translateLayout.findViewById(R.id.favourite);
        favourite.setOnClickListener(view -> {
            favourite.setImageResource(presenter.getImageAndUpdateFavourite());
        });
    }

    private void speakSourceOut() {
        String sourceText = sourceEditText.getText().toString();
        tts.setLanguage(new Locale(presenter.getSourceCode()));
        tts.speak(sourceText, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void speakTargetOut() {
        String targetText = targetTranslate.getText().toString();
        tts.setLanguage(new Locale(presenter.getTargetCode()));
        tts.speak(targetText, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    private void initBarButton() {
        TextView sourceLang = (TextView) translateLayout.findViewById(R.id.sourceLangView);
        sourceLang.setOnClickListener(getLangClickListener(Const.SOURCE));

        ImageView swap = (ImageView) translateLayout.findViewById(R.id.swap);
        swap.setOnClickListener(getSwapListener());

        TextView targetLang = (TextView) translateLayout.findViewById(R.id.targetLangView);
        targetLang.setOnClickListener(getLangClickListener(Const.TARGET));
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

    private View.OnClickListener getSwapListener() {
        return v -> {
            swapLanguage();
            sourceEditText.setText(targetTranslate.getText());
            setTargetText("");
        };
    }

    private void swapLanguage() {
        ImageView swapView = (ImageView) translateLayout.findViewById(R.id.swap);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        swapView.startAnimation(animation);
        presenter.swapLanguages();
    }

    public void updateSourceBar(String sourceText) {
        TextView sourceView = (TextView) translateLayout.findViewById(R.id.sourceLangView);
        sourceView.setText(sourceText);
    }

    public void updateTargetBar(String targetText) {
        TextView targetView = (TextView) translateLayout.findViewById(R.id.targetLangView);
        targetView.setText(targetText);
    }

    public String getSourceLanguageText() {
        return ((TextView) translateLayout.findViewById(R.id.sourceLangView)).getText().toString();
    }

    public String getTargetLanguageText() {
        return ((TextView) translateLayout.findViewById(R.id.targetLangView)).getText().toString();
    }

    public void setTargetText(String text) {
        targetTranslate.setText(text);
    }

    public String getSourceText() {
        return sourceEditText.getText().toString();
    }

    public void setSourceText(String text) {
        sourceEditText.setText(text);
    }

    public void showTranslateProgress() {
        hideButtons();
        ProgressBar progressBar = (ProgressBar) translateLayout.findViewById(R.id.translateProgressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideTranslateProgress() {
        showButtons();
        ProgressBar progressBar = (ProgressBar) translateLayout.findViewById(R.id.translateProgressBar);
        progressBar.setVisibility(View.GONE);
    }

    public void hideButtons() {
        LinearLayout buttonLayout = (LinearLayout) translateLayout.findViewById(R.id.buttonLayout);
        buttonLayout.setVisibility(View.INVISIBLE);
    }

    public void showButtons() {
        LinearLayout buttonLayout = (LinearLayout) translateLayout.findViewById(R.id.buttonLayout);
        buttonLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideTranslateProgress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (tts != null) {
            tts.shutdown();
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

    public void animateScale(View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_scale);
        view.startAnimation(animation);
    }

    public LinearLayout getTranslateLayout() {
        return translateLayout;
    }

    //// TODO: 19.04.2017 broadcast, repo, subscr, db backend, gen means, data changed, revert api query
    // memory leaks
}
