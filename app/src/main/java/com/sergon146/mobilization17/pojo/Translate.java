package com.sergon146.mobilization17.pojo;

import com.sergon146.mobilization17.pojo.mapper.WordMapper;

public class Translate {
    private String sourceText;
    private String targetText = "";
    private String sourceLangCode = "";
    private String targetLangCode = "";
    private boolean isFavourite = false;
    private String wordJson = "";
    private WordMapper wordMapper;

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    public String getSourceLangCode() {
        return sourceLangCode;
    }

    public void setSourceLangCode(String sourceLangCode) {
        this.sourceLangCode = sourceLangCode;
    }

    public String getTargetLangCode() {
        return targetLangCode;
    }

    public void setTargetLangCode(String targetLangCode) {
        this.targetLangCode = targetLangCode;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getWordJson() {
        return wordJson;
    }

    public void setWordJson(String wordJson) {
        this.wordJson = wordJson;
    }

    public WordMapper getWordMapper() {
        return wordMapper;
    }

    public void setWordMapper(WordMapper wordMapper) {
        this.wordMapper = wordMapper;
    }
}
