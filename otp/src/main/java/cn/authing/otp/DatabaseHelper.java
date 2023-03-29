package cn.authing.otp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "authing";
    private static final String TABLE_OTP = "otp";
    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_APPLICATION = "application";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_ALGORITHM = "algorithm";
    private static final String KEY_DIGITS = "digits";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_ISSUER = "issuer";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_OTP + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PATH + " TEXT,"
                + KEY_ACCOUNT + " TEXT,"
                + KEY_APPLICATION + " TEXT,"
                + KEY_SECRET + " TEXT,"
                + KEY_ALGORITHM + " TEXT,"
                + KEY_DIGITS + " INT,"
                + KEY_INTERVAL + " INT,"
                + KEY_ISSUER + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addOTP(TOTPEntity totp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, totp.getPath());
        values.put(KEY_ACCOUNT, totp.getAccount());
        values.put(KEY_APPLICATION, totp.getApplication());
        values.put(KEY_SECRET, totp.getSecret());
        values.put(KEY_ALGORITHM, totp.getAlgorithm());
        values.put(KEY_DIGITS, totp.getDigits());
        values.put(KEY_INTERVAL, totp.getPeriod());
        values.put(KEY_ISSUER, totp.getIssuer());

        db.insert(TABLE_OTP, null, values);
        db.close();
    }

    public void updateOTP(TOTPEntity totp){
        SQLiteDatabase database = this.getWritableDatabase();
        if(database.isOpen()){
            ContentValues values =new ContentValues();
            values.put(KEY_PATH, totp.getPath());
            values.put(KEY_ACCOUNT, totp.getAccount());
            values.put(KEY_APPLICATION, totp.getApplication());
            values.put(KEY_SECRET, totp.getSecret());
            values.put(KEY_ALGORITHM, totp.getAlgorithm());
            values.put(KEY_DIGITS, totp.getDigits());
            values.put(KEY_INTERVAL, totp.getPeriod());
            values.put(KEY_ISSUER, totp.getIssuer());
            database.update(TABLE_OTP, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(totp.getUuid()) });
        }
        database.close();
    }


    public TOTPEntity getOTP(String key) {
        TOTPEntity contact = null;
        String selectQuery = "SELECT  * FROM " + TABLE_OTP + " WHERE " + KEY_PATH + " == ? ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{key});

        if (cursor.moveToFirst()) {
            do {
                contact = new TOTPEntity();
                contact.setUuid(Integer.parseInt(cursor.getString(0)));
                contact.setPath(cursor.getString(1));
                contact.setAccount(cursor.getString(2));
                contact.setApplication(cursor.getString(3));
                contact.setSecret(cursor.getString(4));
                contact.setAlgorithm(cursor.getString(5));
                contact.setDigits(cursor.getInt(6));
                contact.setPeriod(cursor.getInt(7));
                contact.setIssuer(cursor.getString(8));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contact;
    }

    public List<TOTPEntity> getOTPs() {
        List<TOTPEntity> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_OTP;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TOTPEntity contact = new TOTPEntity();
                contact.setUuid(Integer.parseInt(cursor.getString(0)));
                contact.setPath(cursor.getString(1));
                contact.setAccount(cursor.getString(2));
                contact.setApplication(cursor.getString(3));
                contact.setSecret(cursor.getString(4));
                contact.setAlgorithm(cursor.getString(5));
                contact.setDigits(cursor.getInt(6));
                contact.setPeriod(cursor.getInt(7));
                contact.setIssuer(cursor.getString(8));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteOTP(TOTPEntity totp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OTP, KEY_ID + " = ?",
                new String[] { String.valueOf(totp.getUuid()) });
        db.close();
    }

    public void deleteOTP(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OTP, KEY_PATH + " = ?",
                new String[] { path });
        db.close();
    }
}
