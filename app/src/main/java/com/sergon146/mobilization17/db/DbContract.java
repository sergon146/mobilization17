package com.sergon146.mobilization17.db;

interface DbContract {
    String DB_NAME = "mobilization.db";

    String ID = "rowid";

    String TABLE_LANGS = "langs";

    interface LangsTbl {
        String COLUMN_CODE = "code";
        String COLUMN_IS_SOURCE = "isSource";
        String COLUMN_IS_TARGET = "isTarget";
    }

    String TABLE_LANGS_NAME = "langs_name";

    interface LangsNameTbl {
        String COLUMN_LANG_CODE = "lang_code";
        String COLUMN_NAME = "name";
        String COLUMN_LOCALE_CODE = "locale_code";
    }

    String TABLE_TRANSLATE = "translate";

    interface TranslateTbl {
        String COLUMN_SOURCE_TEXT = "source_text";
        String COLUMN_TARGET_TEXT = "target_text";
        String COLUMN_SOURCE_LANG = "source_lang";
        String COLUMN_TARGET_LANG = "target_lang";
        String COLUMN_IS_FAVOURITE = "isFavourite";
        String COLUMN_WORD_JSON = "word_json";
    }
}
