package com.example.proyecto1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GameBD extends SQLiteOpenHelper {

    private static GameBD mDB;
    public  static final String DATABASE_NAME = "game.db";
    public  static final int DATABASE_VERSION = 1;


    private GameBD(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized GameBD getmDB(Context context){
        if (mDB == null){
            mDB = new GameBD(context);
        }
        return mDB;
    }

    public void createDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        String createTablaUsuario = "CREATE TABLE IF NOT EXISTS user (" +
                "username TEXT PRIMARY KEY ," +
                "password TEXT NOT NULL)";
        String createTablaAmigos = "CREATE TABLE IF NOT EXISTS friends (" +
                "id_pair INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "user1 TEXT NOT NULL," +
                "user2 TEXT NOT NULL,"+
                "pending INTEGER NOT NULL,"+
                "FOREIGN KEY (user1) REFERENCES user(username)," +
                "FOREIGN KEY (user2) REFERENCES user(username))";
        String createTablaCompeticion = "CREATE TABLE IF NOT EXISTS competition (" +
                "id_competition INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL,"+
                "init_date TEXT NOT NULL," +
                "end_date TEXT NOT NULL)";
        String createTablaParticipantes = "CREATE TABLE IF NOT EXISTS option (" +
                "id_option INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "competition INTEGER NOT NULL," +
                "name TEXT NOT NULL,"+
                "photho TEXT,"+
                "FOREIGN KEY (competition) REFERENCES competition(id_competition))";
        String createTablaRonda = "CREATE TABLE IF NOT EXISTS round (" +
                "id_round INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "competition INTEGER NOT NULL," +
                "n_round INTEGER NOT NULL," +
                "FOREIGN KEY (competition) REFERENCES competition(id_competition))";
        String createTablaEnfrentamiento = "CREATE TABLE IF NOT EXISTS clash (" +
                "id_clash INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "round INTEGER NOT NULL," +
                "option1 INTEGER NOT NULL," +
                "option2 INTEGER NOT NULL," +
                "date TEXT NOT NULL,"+
                "votes1 INTEGER DEFAULT 0 NOT NULL,"+
                "votes2 INTEGER DEFAULT 0 NOT NULL,"+
                "FOREIGN KEY (round) REFERENCES round(id_round)," +
                "FOREIGN KEY (option1) REFERENCES option(id_option),"+
                "FOREIGN KEY (option2) REFERENCES option(id_option))";
        String createTablaVoto = "CREATE TABLE IF NOT EXISTS vote (" +
                "id_vote INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "clash INTEGER NOT NULL," +
                "user INTEGER NOT NULL," +
                "vote INTEGER NOT NULL," +
                "FOREIGN KEY (clash) REFERENCES clash(id_match)," +
                "FOREIGN KEY (user) REFERENCES user(username))";
        String createTablaComentario = "CREATE TABLE IF NOT EXISTS comment (" +
                "id_comment INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "vote INTEGER NOT NULL," +
                "user_voting INTEGER NOT NULL," +
                "user_commenting INTEGER NOT NULL," +
                "comment TEXT NOT NULL," +
                "FOREIGN KEY (vote) REFERENCES voto(id_voto)," +
                "FOREIGN KEY (user_voting) REFERENCES user(username)," +
                "FOREIGN KEY (user_commenting) REFERENCES user(username))";
        db.execSQL(createTablaUsuario);
        db.execSQL(createTablaAmigos);
        db.execSQL(createTablaCompeticion);
        db.execSQL(createTablaParticipantes);
        db.execSQL(createTablaRonda);
        db.execSQL(createTablaEnfrentamiento);
        db.execSQL(createTablaVoto);
        db.execSQL(createTablaComentario);
        db.close();
    }
    public void prueba(BufferedReader buff){
        cargarTxt(buff);
        crearUsuario("Bosco", "Bosco");
        crearUsuario("Joel", "Joel");
        crearUsuario("Diego", "Diego");
        crearUsuario("Vicen", "Vicen");
        hacerAmigos("Bosco", "Joel");
        hacerAmigos("Joel", "Bosco");
        hacerAmigos("Bosco", "Diego");
        hacerAmigos("Diego", "Bosco");
        hacerAmigos("Bosco", "Vicen");
        hacerAmigos("Vicen", "Bosco");
        vota("Bosco", 1);
        vota("Diego", 2);
        vota("Vicen", 2);

    }
    public void cargarTxt(BufferedReader buff){
        try {
            String competition = buff.readLine();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String startDate = buff.readLine();
            Date date = sdf.parse(startDate);
            String option;
            ArrayList<String> LOptions = new ArrayList<>();
            while ((option=buff.readLine())!=null){
                LOptions.add(option);
            }
            buff.close();
            Calendar c = Calendar.getInstance();
            assert date != null;
            c.setTime(date);
            int nRounds = (int)(Math.log(LOptions.size())/Math.log(2));
            int competitionDays = (2^nRounds)+1;
            c.add(Calendar.DATE, competitionDays);
            String endDate = sdf.format(c.getTime());
            long idCompetition = addCompetition(competition, startDate, endDate);
            long idRound = addRound(idCompetition);
            Random random = new Random();
            Calendar d = Calendar.getInstance();
            Date dClash = date;
            while (LOptions.size()>=2){
                int i1 = random.nextInt(LOptions.size());
                String option1 = LOptions.get(i1);
                long idO1 = addOption(option1, idCompetition);
                LOptions.remove(i1);
                int i2 = random.nextInt(LOptions.size());
                String option2 = LOptions.get(i2);
                long idO2 = addOption(option2, idCompetition);
                LOptions.remove(i2);
                d.setTime(dClash);
                d.add(Calendar.DATE, 1);
                dClash.setTime(d.getTimeInMillis());
                String clashDate = sdf.format(d.getTime());
                addClash(idO1, idO2, idRound, clashDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public long addClash(long idO1, long idO2, long idRound, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("round", idRound);
        contentValues.put("option1", idO1);
        contentValues.put("option2", idO2);
        contentValues.put("date", date);
        return db.insert("clash", null, contentValues);
    }
    public long addOption(String option, long idCompetition){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("competition", idCompetition);
        contentValues.put("name", option);
        return db.insert("option", null, contentValues);
    }
    public long addCompetition(String competition, String startDate, String endDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", competition);
        contentValues.put("init_date", startDate);
        contentValues.put("end_date", endDate);
        return db.insert("competition", null, contentValues);
    }
    public long addRound(long competiton){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("competition", competiton);
        contentValues.put("n_round", 1);
        return db.insert("round", null, contentValues);
    }
    public void vota(String user, int option){
        String[] options = getTodaysOptions();
        String clash_id = options[0];
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = {clash_id};
        if (option==1){
            Cursor c = db.rawQuery("SELECT votes1 FROM clash WHERE id_clash = ?", params);
            c.moveToFirst();
            String[] paramsUpdate = {String.valueOf(c.getInt(0)+1), clash_id};
            c.close();
            Cursor cUpdate = db.rawQuery("UPDATE clash SET votes1=? WHERE id_clash = ?", paramsUpdate);
            cUpdate.close();
        }else {
            Cursor c = db.rawQuery("SELECT votes2 FROM clash WHERE id_clash = ?", params);
            c.moveToFirst();
            String[] paramsUpdate = {String.valueOf(c.getInt(0)+1), clash_id};
            c.close();
            Cursor cUpdate = db.rawQuery("UPDATE clash SET votes2=? WHERE id_clash = ?", paramsUpdate);
            cUpdate.close();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("clash", clash_id);
        contentValues.put("user", user);
        contentValues.put("vote", option);
        db.insert("vote", null, contentValues);
    }

    public void hacerAmigos(String user1, String user2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user1", user1);
        contentValues.put("user2", user2);
        contentValues.put("pending", 0);
        db.insert("friends", null, contentValues);
    }

    public void solicitarAmistad(String user1, String user2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user1", user1);
        contentValues.put("user2", user2);
        contentValues.put("pending", 1);
        db.insert("friends", null, contentValues);
    }

    public void aceptarSolicitud(String user1, String user2){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] paramsUpdate = {user2, user1};
        Cursor cUpdate = db.rawQuery("UPDATE friends SET pending=0 WHERE user1=? AND user2=?", paramsUpdate);
        cUpdate.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user1", user2);
        contentValues.put("user2", user1);
        contentValues.put("pending", 0);
        db.insert("friends", null, contentValues);
    }

    public void crearUsuario(String name, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", name);
        contentValues.put("password", password);
        db.insert("user", null, contentValues);
    }
    public String usuarioCorrecto(String name, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] params = {name};
        Cursor c = db.rawQuery("SELECT password FROM user WHERE username LIKE ?", params);
        boolean result = c.moveToFirst();
        if (!result){
            c.close();
            return "NO USER";
        } else{
            if (password.equals(c.getString(0))){
                c.close();
                return "CORRECT";
            } else {
                c.close();
                return "INCORRECT";
            }
        }
    }
    public Cursor getFriends(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] params = {user};
        Cursor c = db.rawQuery("SELECT user2 FROM friends WHERE user1 LIKE ? AND pending=0", params);
        c.moveToFirst();
        return c;
    }
    public Cursor getSolicitudes(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] params = {user};
        Cursor c = db.rawQuery("SELECT user1 FROM friends WHERE user2 LIKE ? AND pending=1", params);
        c.moveToFirst();
        return c;
    }

    public void eliminarSolicitud(String user, String friend){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = {user, friend};
        db.rawQuery("DELETE FROM friends WHERE user2=? AND user1=?", params);
    }
    public String[] getTodaysOptions(){
        SQLiteDatabase db = getReadableDatabase();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String[] params = {sdf.format(date)};
        String[] result = new String[3];
        Cursor c = db.rawQuery("SELECT id_clash FROM clash WHERE date=?", params);
        c.moveToFirst();
        result[0] = c.getString(0);
        c.close();
        Cursor c1 = db.rawQuery("SELECT name FROM option WHERE id_option = (SELECT option1 FROM clash WHERE date=?)", params);
        c1.moveToFirst();
        result[1] = c1.getString(0);
        c1.close();
        Cursor c2 = db.rawQuery("SELECT name FROM option WHERE id_option = (SELECT option2 FROM clash WHERE date=?)", params);
        c2.moveToFirst();
        result[2] = c2.getString(0);
        c2.close();
        return result;
    }

    public boolean haVotado(long clash, String user){
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(clash), user};
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM vote WHERE clash = ? AND user = ?", params);
        c.moveToFirst();
        if (c.getInt(0)==0){
            return false;
        } else {
            return true;
        }
    }
     public float getVotePercent(long clash){
        SQLiteDatabase db = getWritableDatabase();
        String[] params1 = {String.valueOf(clash), "1"};
        String[] params2 = {String.valueOf(clash), "2"};
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM vote WHERE clash = ? AND vote = ?", params1);
        c1.moveToFirst();
        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM vote WHERE clash = ? AND vote = ?", params2);
        c2.moveToFirst();
        float op1 = c1.getInt(0);
        float op2 = c2.getInt(0);
        if (op1==0 && op2!=0){
            return (0);
        } else if (op2==0 && op1!=0) {
            return (1);
        } else if (op1==0 && op2==0) {
            return (0.5f);
        }else {
            return op1 / (op1 + op2);
        }
    }

    public Cursor searchUser(String user){
        SQLiteDatabase db = getWritableDatabase();
        String[] param = {user};
        Cursor c = db.rawQuery("SELECT username FROM user WHERE  username= ?", param);
        return c;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
