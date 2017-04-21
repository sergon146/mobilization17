package com.sergon146.mobilization17.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DbBackend implements DbContract {
    private final DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbBackend(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void insertLanguages(String localeCode, List<Language> languages) {
        db = dbHelper.getWritableDatabase();
        int localeId = getLocaleId(localeCode);
        db.beginTransaction();
        try {
            for (Language language : languages) {
                ContentValues cv = new ContentValues();
                cv.put(LangsNameTbl.COLUMN_LANG_CODE, getLangId(language.getCode()));
                cv.put(LangsNameTbl.COLUMN_NAME, language.getName());
                cv.put(LangsNameTbl.COLUMN_LOCALE_CODE, localeId);
                db.insert(TABLE_LANGS_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB", "Error inserting languages: " + e);
        } finally {
            db.endTransaction();
        }
    }

    public int countLangs() {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS_NAME;
        String[] columns = new String[]{"COUNT(rowid)"};
        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public ArrayList<Language> getCashedLangs(String localeCode) {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS_NAME;
        String[] columns = new String[]{
                ID,
                LangsNameTbl.COLUMN_LANG_CODE,
                LangsNameTbl.COLUMN_NAME};
        String where = LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(getLocaleId(localeCode))};

        //select lang_code, name from langs_name where locale_code = 'locale_code'
        Cursor cursor = db.query(
                table,
                columns,
                where,
                whereArgs,
                null, null, null);
        int sourceId = getSourceLangId();
        int targetId = getTargetLangId();

        ArrayList<Language> languages = new ArrayList<>();
        while (cursor.moveToNext()) {
            Language lang = new Language();
            lang.setId(cursor.getInt(0));
            if (cursor.getInt(1) == sourceId) {
                lang.setSource(true);
            } else if (cursor.getInt(1) == targetId) {
                lang.setTarget(true);
            }
            lang.setCode(getLangCodeById(cursor.getInt(1)));
            lang.setName(cursor.getString(2));
            languages.add(lang);
        }
        cursor.close();

        languages = Util.sortLangs(languages);

        return languages;
    }

    private int getLangId(String langCode) {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS;
        String[] columns = new String[]{ID};
        String where = LangsTbl.COLUMN_CODE + " like ?";
        String[] whereArgs = new String[]{langCode};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            insertLangCode(langCode);
            return getLangId(langCode);
        }
    }

    private void insertLangCode(String langCode) {
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(LangsTbl.COLUMN_CODE, langCode);
            cv.put(LangsTbl.COLUMN_IS_SOURCE, 0);
            cv.put(LangsTbl.COLUMN_IS_TARGET, 0);
            db.insert(TABLE_LANGS, null, cv);
            db.setTransactionSuccessful();
            Log.i("DB", "Insert new language code: " + langCode);
        } catch (Exception e) {
            Log.e("DB", "Error when inserting: " + e);
        } finally {
            db.endTransaction();
        }
    }

    private int getLocaleId(String localeCode) {
        return getLangId(localeCode);
    }

    private String getLangCodeById(int id) {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS;
        String[] columns = new String[]{LangsTbl.COLUMN_CODE};
        String where = ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();

        return cursor.getString(0);
    }

    public boolean isEmptyLocaleLanguageList(String localeCode) {
        db = dbHelper.getReadableDatabase();
        String table = TABLE_LANGS_NAME;
        String[] columns = new String[]{ID};
        String where = LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(getLocaleId(localeCode))};

        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        if (cursor.moveToNext()) {
            cursor.close();
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public String getSourceName(String localeCode) {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS_NAME;
        String[] columns = new String[]{LangsNameTbl.COLUMN_NAME};
        String where = LangsNameTbl.COLUMN_LANG_CODE + " = ? AND " + LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(getSourceLangId()), String.valueOf(getLocaleId(localeCode))};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();

        return cursor.getString(0);
    }

    public String getTargetName(String localeCode) {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS_NAME;
        String[] columns = new String[]{LangsNameTbl.COLUMN_NAME};
        String where = LangsNameTbl.COLUMN_LANG_CODE + " = ? AND " + LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(getTargetLangId()), String.valueOf(getLocaleId(localeCode))};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();

        return cursor.getString(0);
    }

    public void updateSource(String sourceCode) {
        db = dbHelper.getWritableDatabase();
        unSourcesAll();

        String table = TABLE_LANGS;

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_SOURCE, 1);

        String where = LangsTbl.COLUMN_CODE + " = ?";
        String[] whereArgs = new String[]{sourceCode};
        db.update(table, values, where, whereArgs);
    }

    private void unSourcesAll() {
        db = dbHelper.getWritableDatabase();
        String table = TABLE_LANGS;

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_SOURCE, 0);

        String where = LangsTbl.COLUMN_IS_SOURCE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        db.update(table, values, where, whereArgs);
    }

    public void setTargetLang(String targetCode) {
        db = dbHelper.getWritableDatabase();
        unTargetAll();

        String table = TABLE_LANGS;

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_TARGET, 1);

        String where = LangsTbl.COLUMN_CODE + " = ?";
        String[] whereArgs = new String[]{targetCode};
        db.update(table, values, where, whereArgs);
    }

    private void unTargetAll() {
        db = dbHelper.getWritableDatabase();
        String table = TABLE_LANGS;

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_TARGET, 0);

        String where = LangsTbl.COLUMN_IS_TARGET + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        db.update(table, values, where, whereArgs);
    }

    private int getSourceLangId() {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS;
        String[] columns = new String[]{ID};
        String where = LangsTbl.COLUMN_IS_SOURCE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    private int getTargetLangId() {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS;
        String[] columns = new String[]{ID};
        String where = LangsTbl.COLUMN_IS_TARGET + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    public String getSourceCode() {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS;
        String[] columns = new String[]{LangsTbl.COLUMN_CODE};
        String where = LangsTbl.COLUMN_IS_SOURCE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);
        cursor.moveToFirst();

        return cursor.getString(0);
    }

    public String getTargetCode() {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_LANGS;
        String[] columns = new String[]{LangsTbl.COLUMN_CODE};
        String where = LangsTbl.COLUMN_IS_TARGET + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);
        cursor.moveToFirst();

        return cursor.getString(0);
    }

    public void saveTranslate(Translate translate) {
        if (isTranslateContains(translate)) {
            return;
        }

        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(TranslateTbl.COLUMN_SOURCE_TEXT, translate.getSourceText());
            cv.put(TranslateTbl.COLUMN_TARGET_TEXT, translate.getTargetText());
            cv.put(TranslateTbl.COLUMN_SOURCE_LANG, getLangId(translate.getSourceLangCode()));
            cv.put(TranslateTbl.COLUMN_TARGET_LANG, getLangId(translate.getTargetLangCode()));
            cv.put(TranslateTbl.COLUMN_IS_FAVOURITE, translate.isFavourite() ? 1 : 0);
            cv.put(TranslateTbl.COLUMN_WORD_JSON, translate.getWordJson());
            db.insert(TABLE_TRANSLATE, null, cv);
            db.setTransactionSuccessful();
            Log.i("DB", "Saving translation: " + translate.getTargetText());
        } catch (Exception e) {
            Log.e("DB", "Already exist in db: " + translate.getTargetText());
        } finally {
            db.endTransaction();
        }
    }

    public boolean isTranslateContains(Translate translate) {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_TRANSLATE;
        String[] columns = new String[]{ID};
        String where = TranslateTbl.COLUMN_SOURCE_TEXT + " = ? AND " +
                TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND " +
                TranslateTbl.COLUMN_TARGET_LANG + " = ?";
        String[] whereArgs = new String[]{translate.getSourceText(),
                String.valueOf(getLangId(translate.getSourceLangCode())),
                String.valueOf(getLangId(translate.getTargetLangCode()))};
        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        return cursor.moveToFirst();
    }

    public void updateTranslateFavourite(Translate translate) {
        db = dbHelper.getWritableDatabase();

        String table = TABLE_TRANSLATE;

        ContentValues values = new ContentValues();
        values.put(TranslateTbl.COLUMN_IS_FAVOURITE, translate.isFavourite() ? 1 : 0);

        String where = TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND "
                + TranslateTbl.COLUMN_TARGET_LANG + " = ? AND "
                + TranslateTbl.COLUMN_SOURCE_TEXT + "= ?";
        String[] whereArgs = new String[]{
                String.valueOf(getLangId(translate.getSourceLangCode())),
                String.valueOf(getLangId(translate.getTargetLangCode())),
                translate.getSourceText()};
        db.update(table, values, where, whereArgs);
    }

    public void deleteHistory() {
        db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TRANSLATE, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public List<Translate> getHistory() {
        return searchInHistory("");
    }

    public List<Translate> searchInHistory(String searchText) {
        searchText = "%" + searchText + "%";
        List<Translate> translateList = new ArrayList<>();

        db = dbHelper.getReadableDatabase();

        String table = TABLE_TRANSLATE;
        String[] columns = new String[]{
                TranslateTbl.COLUMN_SOURCE_TEXT,
                TranslateTbl.COLUMN_TARGET_TEXT,
                TranslateTbl.COLUMN_SOURCE_LANG,
                TranslateTbl.COLUMN_TARGET_LANG,
                TranslateTbl.COLUMN_IS_FAVOURITE,
                TranslateTbl.COLUMN_WORD_JSON};

        String where = TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ? OR " +
                TranslateTbl.COLUMN_TARGET_TEXT + " LIKE ?)";

        String[] whereArgs = new String[]{searchText, searchText};

        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        while (cursor.moveToNext()) {
            Translate translate = new Translate();
            translate.setSourceText(cursor.getString(0));
            translate.setTargetText(cursor.getString(1));
            translate.setSourceLangCode(getLangCodeById(cursor.getInt(2)));
            translate.setTargetLangCode(getLangCodeById(cursor.getInt(3)));
            translate.setFavourite(cursor.getInt(4) == 1);
            translate.setWordJson(cursor.getString(5));
            translateList.add(translate);
        }

        return translateList;
    }


    public List<Translate> getFavourites() {
        return searchInFavourite("");
    }


    public List<Translate> searchInFavourite(String searchText) {
        searchText = "%" + searchText + "%";
        List<Translate> translateList = new ArrayList<>();

        db = dbHelper.getReadableDatabase();

        String table = TABLE_TRANSLATE;
        String[] columns = new String[]{
                TranslateTbl.COLUMN_SOURCE_TEXT,
                TranslateTbl.COLUMN_TARGET_TEXT,
                TranslateTbl.COLUMN_SOURCE_LANG,
                TranslateTbl.COLUMN_TARGET_LANG,
                TranslateTbl.COLUMN_IS_FAVOURITE,
                TranslateTbl.COLUMN_WORD_JSON};

        String where =
                TranslateTbl.COLUMN_IS_FAVOURITE + " = ? AND (" +
                        TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ? OR " +
                        TranslateTbl.COLUMN_TARGET_TEXT + " LIKE ?)";

        String[] whereArgs = new String[]{String.valueOf(1), searchText, searchText};

        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);

        while (cursor.moveToNext()) {
            Translate translate = new Translate();
            translate.setSourceText(cursor.getString(0));
            translate.setTargetText(cursor.getString(1));
            translate.setSourceLangCode(getLangCodeById(cursor.getInt(2)));
            translate.setTargetLangCode(getLangCodeById(cursor.getInt(3)));
            translate.setFavourite(cursor.getInt(4) == 1);
            translate.setWordJson(cursor.getString(5));
            translateList.add(translate);
        }

        return translateList;
    }

    public void clearFavourites() {
        db = dbHelper.getWritableDatabase();

        String table = TABLE_TRANSLATE;

        ContentValues values = new ContentValues();
        values.put(TranslateTbl.COLUMN_IS_FAVOURITE, 0);

        String where = TranslateTbl.COLUMN_IS_FAVOURITE + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(1)};
        db.update(table, values, where, whereArgs);
    }

    public Translate getTranslateSentence(Translate translate) {
        db = dbHelper.getReadableDatabase();

        String table = TABLE_TRANSLATE;
        String[] columns = new String[]{
                TranslateTbl.COLUMN_TARGET_TEXT,
                TranslateTbl.COLUMN_IS_FAVOURITE,
                TranslateTbl.COLUMN_WORD_JSON};

        String where = TranslateTbl.COLUMN_SOURCE_TEXT + " = ? AND " +
                TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND " +
                TranslateTbl.COLUMN_TARGET_LANG + " = ?";

        String[] whereArgs = new String[]{translate.getSourceText(),
                String.valueOf(getLangId(translate.getSourceLangCode())),
                String.valueOf(getLangId(translate.getTargetLangCode()))};

        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null);
        cursor.moveToNext();

        translate.setTargetText(cursor.getString(0));
        translate.setFavourite(cursor.getInt(1) == 1);
        translate.setWordJson(cursor.getString(2));

        cursor.close();
        return translate;
    }
}
