package com.sergon146.mobilization17.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergon146.mobilization17.pojo.Language;
import com.sergon146.mobilization17.pojo.Translate;
import com.sergon146.mobilization17.pojo.mapper.WordMapper;
import com.sergon146.mobilization17.util.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DbBackend implements DbContract {
    private final DbHelper dbHelper;
    private SQLiteDatabase db;

    DbBackend(Context context) {
        dbHelper = new DbHelper(context);
    }

    boolean isEmptyLocaleLanguageList(String localeCode) {
        try {
            db = dbHelper.getReadableDatabase();
            String[] columns = new String[]{ID};
            String where = LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(getLocaleId(localeCode))};

            Cursor cursor = db.query(TABLE_LANGS_NAME, columns, where, whereArgs, null, null, null);

            if (cursor.moveToNext()) {
                cursor.close();
                db.close();
                return false;
            } else {
                cursor.close();
                db.close();
                return true;
            }
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "on empty lang list: " + e);
            return true;
        }
    }

    void insertLanguages(String localeCode, List<Language> languages) {
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
            Log.e(Const.LOG_DB, "Error inserting languages: " + e);
        } finally {
            db.endTransaction();
        }
    }

    List<Language> getCashedLangs(String localeCode) {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{
                    ID,
                    LangsNameTbl.COLUMN_LANG_CODE,
                    LangsNameTbl.COLUMN_NAME};

            String where = LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(getLocaleId(localeCode))};

            String orderBy = LangsNameTbl.COLUMN_NAME + " ASC";

            Cursor cursor = db.query(
                    TABLE_LANGS_NAME,
                    columns,
                    where,
                    whereArgs,
                    null, null, orderBy);

            List<Language> languages = new ArrayList<>();
            while (cursor.moveToNext()) {
                Language lang = new Language();
                lang.setId(cursor.getInt(0));
                lang.setCode(getLangCodeById(cursor.getInt(1)));
                lang.setName(cursor.getString(2));
                languages.add(lang);
            }
            cursor.close();

            return languages;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get cashed langs: " + e);
            return Collections.emptyList();
        }
    }

    private int getLangId(String langCode) {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{ID};
            String where = LangsTbl.COLUMN_CODE + " LIKE ?";
            String[] whereArgs = new String[]{langCode};
            Cursor cursor = db.query(TABLE_LANGS, columns, where, whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                cursor.close();
                return id;
            } else {
                cursor.close();
                insertLangCode(langCode);
                return getLangId(langCode);
            }
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get lang id: " + e);
            return 0;
        }
    }

    private int getLocaleId(String localeCode) {
        return getLangId(localeCode);
    }

    private void insertLangCode(String langCode) {
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put(LangsTbl.COLUMN_CODE, langCode);
            cv.put(LangsTbl.COLUMN_IS_SOURCE, 0);
            cv.put(LangsTbl.COLUMN_IS_TARGET, 0);
            db.insert(TABLE_LANGS, null, cv);
            db.setTransactionSuccessful();
            Log.i(Const.LOG_DB, "Insert new language code: " + langCode);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Error when inserting lang code: " + e);
        } finally {
            db.endTransaction();
        }
    }

    private String getLangCodeById(int id) {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{LangsTbl.COLUMN_CODE};
            String where = ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(id)};
            Cursor cursor = db.query(TABLE_LANGS, columns, where, whereArgs, null, null, null);

            cursor.moveToFirst();

            String code = cursor.getString(0);
            cursor.close();
            return code;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get lang code by id: " + e);
            return "";
        }
    }

    String getSourceName(String localeCode) {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{LangsNameTbl.COLUMN_NAME};
            String where = LangsNameTbl.COLUMN_LANG_CODE + " = ? AND " + LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(getSourceLangId()), String.valueOf(getLocaleId(localeCode))};
            Cursor cursor = db.query(TABLE_LANGS_NAME, columns, where, whereArgs, null, null, null);

            cursor.moveToFirst();
            String name = cursor.getString(0);
            cursor.close();
            return name;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get source name: " + e);
            return "";
        }
    }

    String getTargetName(String localeCode) {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{LangsNameTbl.COLUMN_NAME};
            String where = LangsNameTbl.COLUMN_LANG_CODE + " = ? AND " + LangsNameTbl.COLUMN_LOCALE_CODE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(getTargetLangId()), String.valueOf(getLocaleId(localeCode))};
            Cursor cursor = db.query(TABLE_LANGS_NAME, columns, where, whereArgs, null, null, null);

            cursor.moveToFirst();
            String name = cursor.getString(0);
            cursor.close();
            return name;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get target name: " + e);
            return "";
        }
    }

    void setSourceLang(String sourceCode) {
        try {
            db = dbHelper.getWritableDatabase();
            unSourcesAll();

            ContentValues values = new ContentValues();
            values.put(LangsTbl.COLUMN_IS_SOURCE, 1);

            String where = LangsTbl.COLUMN_CODE + " = ?";
            String[] whereArgs = new String[]{sourceCode};
            db.update(TABLE_LANGS, values, where, whereArgs);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Set source lang: " + e);
        }
    }

    private void unSourcesAll() {
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(LangsTbl.COLUMN_IS_SOURCE, 0);

            String where = LangsTbl.COLUMN_IS_SOURCE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(1)};
            db.update(TABLE_LANGS, values, where, whereArgs);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Unsource all: " + e);
        }
    }

    void setTargetLang(String targetCode) {
        try {
            db = dbHelper.getWritableDatabase();
            unTargetAll();

            ContentValues values = new ContentValues();
            values.put(LangsTbl.COLUMN_IS_TARGET, 1);

            String where = LangsTbl.COLUMN_CODE + " = ?";
            String[] whereArgs = new String[]{targetCode};
            db.update(TABLE_LANGS, values, where, whereArgs);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Set target lang: " + e);
        }
    }

    private void unTargetAll() {
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(LangsTbl.COLUMN_IS_TARGET, 0);

            String where = LangsTbl.COLUMN_IS_TARGET + " = ?";
            String[] whereArgs = new String[]{String.valueOf(1)};
            db.update(TABLE_LANGS, values, where, whereArgs);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Untarget all: " + e);
        }
    }

    private int getSourceLangId() {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{ID};
            String where = LangsTbl.COLUMN_IS_SOURCE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(1)};
            Cursor cursor = db.query(TABLE_LANGS, columns, where, whereArgs, null, null, null);

            cursor.moveToFirst();
            return cursor.getInt(0);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get source lang id: " + e);
            return -1;
        }
    }

    private int getTargetLangId() {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{ID};
            String where = LangsTbl.COLUMN_IS_TARGET + " = ?";
            String[] whereArgs = new String[]{String.valueOf(1)};
            Cursor cursor = db.query(TABLE_LANGS, columns, where, whereArgs, null, null, null);

            cursor.moveToFirst();
            return cursor.getInt(0);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get target lang id: " + e);
            return -1;
        }
    }

    String getSourceCode() {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{LangsTbl.COLUMN_CODE};
            String where = LangsTbl.COLUMN_IS_SOURCE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(1)};
            Cursor cursor = db.query(TABLE_LANGS, columns, where, whereArgs, null, null, null);
            cursor.moveToFirst();

            String code = cursor.getString(0);
            cursor.close();

            return code;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get source code: " + e);
            return "";
        }
    }

    String getTargetCode() {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{LangsTbl.COLUMN_CODE};
            String where = LangsTbl.COLUMN_IS_TARGET + " = ?";
            String[] whereArgs = new String[]{String.valueOf(1)};
            Cursor cursor = db.query(TABLE_LANGS, columns, where, whereArgs, null, null, null);
            cursor.moveToFirst();

            return cursor.getString(0);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get target code: " + e);
            return "";
        }
    }

    void saveTranslate(Translate translate) {
        if (isTranslateContains(translate)) {
            Log.i(Const.LOG_DB, "Already exist in db: " + translate.getSourceText());
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
            Log.i(Const.LOG_DB, "Saving translation: " + translate.getSourceText());
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Save translate: " + translate.getSourceText());
        } finally {
            db.endTransaction();
        }
    }

    boolean isTranslateContains(Translate translate) {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{ID};
            String where = "(" + TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ? OR " +
                    TranslateTbl.COLUMN_TARGET_TEXT + " LIKE ?) AND " +
                    TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND " +
                    TranslateTbl.COLUMN_TARGET_LANG + " = ?";
            String[] whereArgs = new String[]{translate.getSourceText(),
                    translate.getTargetText(),
                    String.valueOf(getLangId(translate.getSourceLangCode())),
                    String.valueOf(getLangId(translate.getTargetLangCode()))};
            Cursor cursor = db.query(TABLE_TRANSLATE, columns, where, whereArgs, null, null, null);


            boolean b = cursor.moveToFirst();
            cursor.close();
            return b;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Is translate contains: " + e);
            return false;
        }
    }

    Translate getTranslate(Translate translate) {
        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{
                    TranslateTbl.COLUMN_TARGET_TEXT,
                    TranslateTbl.COLUMN_IS_FAVOURITE,
                    TranslateTbl.COLUMN_WORD_JSON};

            String where = "(" + TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ? OR " +
                    TranslateTbl.COLUMN_TARGET_TEXT + " LIKE ?) AND " +
                    TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND " +
                    TranslateTbl.COLUMN_TARGET_LANG + " = ?";

            String[] whereArgs = new String[]{translate.getSourceText(),
                    translate.getTargetText(),
                    String.valueOf(getLangId(translate.getSourceLangCode())),
                    String.valueOf(getLangId(translate.getTargetLangCode()))};

            Cursor cursor = db.query(TABLE_TRANSLATE, columns, where, whereArgs, null, null, null);
            cursor.moveToNext();

            translate.setTargetText(cursor.getString(0));
            translate.setFavourite(cursor.getInt(1) == 1);
            translate.setWordJson(cursor.getString(2));

            try {
                ObjectMapper mapper = new ObjectMapper();
                translate.setWordMapper(mapper.readValue(translate.getWordJson(), WordMapper.class));
            } catch (IOException e) {
                Log.w(Const.LOG_DB, "Error while mapper to Word.class " + translate.getWordJson());
            }

            cursor.close();
            return translate;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Get translate: " + e);
            return translate;
        }
    }

    void clearHistory() {
        db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TRANSLATE, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Clear history: " + e);
        } finally {
            db.endTransaction();
        }
    }

    List<Translate> getHistory() {
        return searchInHistory("");
    }

    List<Translate> searchInHistory(String searchText) {
        try {
            searchText = "%" + searchText + "%";
            List<Translate> translateList = new ArrayList<>();

            db = dbHelper.getReadableDatabase();

            String[] columns = new String[]{
                    TranslateTbl.COLUMN_SOURCE_TEXT,
                    TranslateTbl.COLUMN_TARGET_TEXT,
                    TranslateTbl.COLUMN_SOURCE_LANG,
                    TranslateTbl.COLUMN_TARGET_LANG,
                    TranslateTbl.COLUMN_IS_FAVOURITE,
                    TranslateTbl.COLUMN_WORD_JSON};

            String where = TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ? OR " +
                    TranslateTbl.COLUMN_TARGET_TEXT + " LIKE ?";

            String[] whereArgs = new String[]{searchText, searchText};
            String orderBy = ID + " DESC";

            Cursor cursor = db.query(TABLE_TRANSLATE, columns, where, whereArgs, null, null, orderBy);

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
            cursor.close();
            return translateList;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Search in history: " + e);
            return Collections.emptyList();
        }
    }

    void updateTranslateFavourite(Translate translate) {
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(TranslateTbl.COLUMN_IS_FAVOURITE, translate.isFavourite() ? 1 : 0);

            String where = TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND "
                    + TranslateTbl.COLUMN_TARGET_LANG + " LIKE ? AND "
                    + TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ?";
            String[] whereArgs = new String[]{
                    String.valueOf(getLangId(translate.getSourceLangCode())),
                    String.valueOf(getLangId(translate.getTargetLangCode())),
                    translate.getSourceText()};
            db.update(TABLE_TRANSLATE, values, where, whereArgs);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Update favourite: " + e);
        }
    }

    List<Translate> getFavourites() {
        return searchInFavourite("");
    }

    List<Translate> searchInFavourite(String searchText) {
        try {
            searchText = "%" + searchText + "%";
            List<Translate> translateList = new ArrayList<>();

            db = dbHelper.getReadableDatabase();

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
            String orderBy = ID + " DESC";

            Cursor cursor = db.query(TABLE_TRANSLATE, columns, where, whereArgs, null, null, orderBy);

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

            cursor.close();
            return translateList;
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Search in favourite: " + e);
            return Collections.emptyList();
        }
    }

    void clearFavourites() {
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(TranslateTbl.COLUMN_IS_FAVOURITE, 0);

            String where = TranslateTbl.COLUMN_IS_FAVOURITE + " = ? ";
            String[] whereArgs = new String[]{String.valueOf(1)};
            db.update(TABLE_TRANSLATE, values, where, whereArgs);
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "Clear favourite: " + e);
        }
    }

    public void deleteTranslate(Translate translate) {
        try {
            db.beginTransaction();
            db = dbHelper.getReadableDatabase();
            String where = TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ? AND " +
                    TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND " +
                    TranslateTbl.COLUMN_TARGET_LANG + " = ?";
            String[] whereArgs = new String[]{
                    translate.getSourceText(),
                    String.valueOf(getLangId(translate.getSourceLangCode())),
                    String.valueOf(getLangId(translate.getTargetLangCode()))};

            db.delete(TABLE_TRANSLATE, where, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(Const.LOG_DB, "delete translate: " + e);
        } finally {
            db.endTransaction();
        }
    }
}
