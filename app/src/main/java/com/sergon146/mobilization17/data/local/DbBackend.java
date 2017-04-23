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

    //проверяем имеются ли сохранённые названия языков
    boolean isEmptyLocaleLanguageList(String localeCode) {
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
    }

    //записываем в БД загруженный список языков
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
            Log.e("DB", "Error inserting languages: " + e);
        } finally {
            db.endTransaction();
        }
    }

    //получаем сохранёный ранее список языков
    List<Language> getCashedLangs(String localeCode) {
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
    }

    //id языка по его ISO коду
    private int getLangId(String langCode) {
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
    }

    //id локали
    private int getLocaleId(String localeCode) {
        return getLangId(localeCode);
    }

    //при появлении нового языка записываем его код в базу
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
            Log.i("DB", "Insert new language code: " + langCode);
        } catch (Exception e) {
            Log.e("DB", "Error when inserting: " + e);
        } finally {
            db.endTransaction();
        }
    }

    //ISO код языка по его id
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
            return "";
        }
    }

    //язык текста для перевода
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
            return "";
        }
    }

    //язык текста перевода
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
            return "";
        }
    }

    //сохраняем новый язык текста
    void setSourceLang(String sourceCode) {
        db = dbHelper.getWritableDatabase();
        unSourcesAll();

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_SOURCE, 1);

        String where = LangsTbl.COLUMN_CODE + " = ?";
        String[] whereArgs = new String[]{sourceCode};
        db.update(TABLE_LANGS, values, where, whereArgs);
    }

    //снимаем пометку языка текста для всех языков, у которых она есть
    private void unSourcesAll() {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_SOURCE, 0);

        String where = LangsTbl.COLUMN_IS_SOURCE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        db.update(TABLE_LANGS, values, where, whereArgs);
    }

    //сохраняем язык перевода
    void setTargetLang(String targetCode) {
        db = dbHelper.getWritableDatabase();
        unTargetAll();

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_TARGET, 1);

        String where = LangsTbl.COLUMN_CODE + " = ?";
        String[] whereArgs = new String[]{targetCode};
        db.update(TABLE_LANGS, values, where, whereArgs);
    }

    //снимаем отметку языка перевода для всех языков, у которых она есть
    private void unTargetAll() {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LangsTbl.COLUMN_IS_TARGET, 0);

        String where = LangsTbl.COLUMN_IS_TARGET + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        db.update(TABLE_LANGS, values, where, whereArgs);
    }

    //код языка текста
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
            return -1;
        }
    }

    //код языка перевода
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
            return -1;
        }
    }

    //код языка текста
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
            return "";
        }
    }

    //код языка перевода
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
            return "";
        }
    }

    //сохраняем новый перевод, если его нет в базе
    void saveTranslate(Translate translate) {
        if (isTranslateContains(translate)) {
            Log.i("DB", "Already exist in db: " + translate.getSourceText());
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
            Log.i("DB", "Saving translation: " + translate.getSourceText());
        } catch (Exception e) {
            Log.e("DB", "Already exist in db: " + translate.getSourceText());
        } finally {
            db.endTransaction();
        }
    }

    //проверяем имеется ли текущий перевод в базе
    boolean isTranslateContains(Translate translate) {
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
    }

    //получаем ранее сохранённый перевод
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
                Log.w(Const.LOG_TAG, "Error while mapper to Word.class " + translate.getWordJson());
            }

            cursor.close();
            return translate;
        } catch (Exception e) {
            return translate;
        }
    }

    //очищаем всю историю
    void clearHistory() {
        db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TRANSLATE, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    //получаем всю историю
    List<Translate> getHistory() {
        return searchInHistory("");
    }

    //поиск по истории
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
            return Collections.emptyList();
        }
    }

    //добавляем перевод в избранное
    void updateTranslateFavourite(Translate translate) {
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
        Log.i(Const.LOG_TAG, "Set as favourite " + translate.getSourceText());
    }

    //получаем все избарнные переводы
    List<Translate> getFavourites() {
        return searchInFavourite("");
    }

    //поиск по избранному
    List<Translate> searchInFavourite(String searchText) {
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
        try {
            return translateList;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    //очищаем список избранного
    void clearFavourites() {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TranslateTbl.COLUMN_IS_FAVOURITE, 0);

        String where = TranslateTbl.COLUMN_IS_FAVOURITE + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(1)};
        db.update(TABLE_TRANSLATE, values, where, whereArgs);
    }

    //удаляем перевод
    public void deleteTranslate(Translate translate) {
        db = dbHelper.getReadableDatabase();
        String where = TranslateTbl.COLUMN_SOURCE_TEXT + " LIKE ? AND " +
                TranslateTbl.COLUMN_SOURCE_LANG + " = ? AND " +
                TranslateTbl.COLUMN_TARGET_LANG + " = ?";
        String[] whereArgs = new String[]{
                translate.getSourceText(),
                String.valueOf(getLangId(translate.getSourceLangCode())),
                String.valueOf(getLangId(translate.getTargetLangCode()))};

        db.beginTransaction();
        try {
            db.delete(TABLE_TRANSLATE, where, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
