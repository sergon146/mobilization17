package com.sergon146.mobilization17.pojo;

import java.util.List;
import java.util.Map;

public class Langs {
    private List<String> dirs;
    private Map<String, String> langs;

    public Langs() {
    }

    public List<String> getLangCodeList() {
        return dirs;
    }

    public void setLangCodeList(List<String> langCodeList) {
        this.dirs = langCodeList;
    }

    public Map<String, String> getLangName() {
        return langs;
    }

    public void setLangName(Map<String, String> langName) {
        this.langs = langName;
    }

    public String getNameByCode(String code) {
        return langs.get(code);
    }
}
