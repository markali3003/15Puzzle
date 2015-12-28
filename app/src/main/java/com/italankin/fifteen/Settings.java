package com.italankin.fifteen;

import android.content.SharedPreferences;
import android.graphics.Typeface;

public class Settings {

    public static final int MIN_GAME_WIDTH = 3;
    public static final int MIN_GAME_HEIGHT = 3;
    public static final int MAX_GAME_WIDTH = 8;
    public static final int MAX_GAME_HEIGHT = 8;
    public static final int COLOR_MODES = 2;
    public static final int GAME_MODES = 2;

    public static final String KEY_GAME_WIDTH = "puzzle_width";
    public static final String KEY_GAME_HEIGHT = "puzzle_height";
    public static final String KEY_GAME_ARRAY = "puzzle_prev";
    public static final String KEY_GAME_MOVES = "puzzle_prev_moves";
    public static final String KEY_GAME_TIME = "puzzle_prev_time";
    public static final String KEY_GAME_SAVE = "savegame";
    public static final String KEY_GAME_TILE_COLOR = "tile_color";
    public static final String KEY_GAME_BG_COLOR = "bg_color";
    public static final String KEY_GAME_MODE = "mode";
    public static final String KEY_GAME_BF = "blind";
    public static final String KEY_GAME_ANTI_ALIAS = "antialias";
    public static final String KEY_GAME_ANIMATION = "animation";

    /**
     * ширина игры
     */
    public static int gameWidth = 4;
    /**
     * высота игры
     */
    public static int gameHeight = 4;
    /**
     * сложный режим
     */
    public static boolean hardmode = false;
    /**
     * сохранение игр между сессиями
     */
    public static boolean saveGame = true;
    /**
     * анимации
     */
    public static boolean animations = true;
    /**
     * сглаживание
     */
    public static boolean antiAlias = true;
    /**
     * количество кадров для анимирования плиток
     */
    public static long tileAnimFrames = BuildConfig.FPS / 2;
    /**
     * кол-во кадров для анимирования элементов интерфейса
     */
    public static long screenAnimFrames = BuildConfig.FPS / 3;
    /**
     * цвет плиток
     */
    public static int tileColor = 0;
    /**
     * цветовая тема приложения
     */
    public static int colorMode = 0;
    /**
     * текущий режим игры
     */
    public static int gameMode = Game.MODE_CLASSIC;
    /**
     * Typeface текста
     */
    public static Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

    /**
     * хранилище настроек приложения
     */
    public static SharedPreferences prefs;

    /**
     * Чтение настроек приложения
     */
    public static void load() {
        gameWidth = prefs.getInt(KEY_GAME_WIDTH, gameWidth);
        gameHeight = prefs.getInt(KEY_GAME_HEIGHT, gameHeight);
        saveGame = prefs.getBoolean(KEY_GAME_SAVE, saveGame);
        tileColor = prefs.getInt(KEY_GAME_TILE_COLOR, tileColor);
        colorMode = prefs.getInt(KEY_GAME_BG_COLOR, colorMode);
        gameMode = prefs.getInt(KEY_GAME_MODE, gameMode);
        antiAlias = prefs.getBoolean(KEY_GAME_ANTI_ALIAS, antiAlias);
        animations = prefs.getBoolean(KEY_GAME_ANIMATION, animations);
        hardmode = prefs.getBoolean(KEY_GAME_BF, hardmode);
    }

    /**
     * Запись настроек приложения
     */
    public static void save() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_GAME_WIDTH, gameWidth);
        editor.putInt(KEY_GAME_HEIGHT, gameHeight);
        if (!Game.isSolved() && saveGame) {
            String string_array = Game.getGridStr();
            editor.putString(KEY_GAME_ARRAY, string_array);
            editor.putInt(KEY_GAME_MOVES, Game.getMoves());
            editor.putLong(KEY_GAME_TIME, Game.getTime());
        } else {
            editor.remove(KEY_GAME_ARRAY);
            editor.remove(KEY_GAME_MOVES);
            editor.remove(KEY_GAME_TIME);
        }
        editor.putBoolean(KEY_GAME_SAVE, saveGame);
        editor.putInt(KEY_GAME_TILE_COLOR, tileColor);
        editor.putInt(KEY_GAME_BG_COLOR, colorMode);
        editor.putInt(KEY_GAME_MODE, gameMode);
        editor.putBoolean(KEY_GAME_ANTI_ALIAS, antiAlias);
        editor.putBoolean(KEY_GAME_ANIMATION, animations);
        editor.putBoolean(KEY_GAME_BF, hardmode);
        editor.commit();
    }

}