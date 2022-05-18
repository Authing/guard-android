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
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_ISSUER = "issuer";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_OTP + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ACCOUNT + " TEXT,"
                + KEY_SECRET + " TEXT,"
                + KEY_ISSUER + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    void addOTP(TOTPEntity totp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT, totp.getAccount());
        values.put(KEY_SECRET, totp.getSecret());
        values.put(KEY_ISSUER, totp.getIssuer());

        db.insert(TABLE_OTP, null, values);
        db.close();
    }

    List<TOTPEntity> getOTPs() {
        List<TOTPEntity> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_OTP;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TOTPEntity contact = new TOTPEntity();
                contact.setUuid(Integer.parseInt(cursor.getString(0)));
                contact.setAccount(cursor.getString(1));
                contact.setSecret(cursor.getString(2));
                contact.setIssuer(cursor.getString(3));
                list.add(contact);
            } while (cursor.moveToNext());
        }

        return list;
    }

    void deleteOTP(TOTPEntity totp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OTP, KEY_ID + " = ?",
                new String[] { String.valueOf(totp.getUuid()) });
        db.close();
    }
}
