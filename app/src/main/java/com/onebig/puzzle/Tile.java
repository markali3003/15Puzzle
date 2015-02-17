package com.onebig.puzzle;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class Tile {

    private static Paint sPaintText;                    // Paint для рисования текста
    private static Paint sPaintPath;                    // Paint для рисования фона плитка

    private GameView mRootView;

    private Path mShape;                                // путь для рисования фона плитки
    private RectF mRectShape;                            // используется для определения границ плитки и создания пути
    private Rect mRectBounds;                           // используется для определения границ текста и т.д.

    private int mData;                                   // отображаемое число на плитке
    private int mIndex;                                  // индекс плитки в общем массиве
    private float mCanvasX = 0.0f;                       // позиция плитки
    private float mCanvasY = 0.0f;                       // на поле (canvas)

    private Animation mAnimation = new Animation();

    public Tile(GameView root, int d, int i) {
        this.mRootView = root;
        this.mData = d;
        this.mIndex = i;

        if (sPaintText == null) {
            sPaintText = new Paint();
            sPaintText.setTypeface(Settings.typeface);
            sPaintText.setTextAlign(Paint.Align.CENTER);
            sPaintText.setAntiAlias(Settings.antiAlias);
        }

        if (sPaintPath == null) {
            sPaintPath = new Paint();
            sPaintPath.setAntiAlias(Settings.antiAlias);
        }

        mRectShape = new RectF();
        mRectBounds = new Rect();
        mShape = new Path();

        mCanvasX = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * (mIndex % Settings.gameWidth);
        mCanvasY = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * (mIndex / Settings.gameWidth);

        mShape.addRoundRect(
                new RectF(
                        mCanvasX,
                        mCanvasY,
                        mCanvasX + Constraints.tileWidth,
                        mCanvasY + Constraints.tileHeight
                ),
                Constraints.tileCornerRadius,
                Constraints.tileCornerRadius,
                Path.Direction.CW
        );
    }

    public void draw(Canvas canvas) {

        // задержка анимации (в кадрах)
        if (mAnimation.delay > 0) {
            mAnimation.delay--;
            return;
        }

        mCanvasX = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * (mIndex % Settings.gameWidth);
        mCanvasY = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * (mIndex / Settings.gameWidth);

        if (mAnimation.isPlaying()) {
            mShape = mAnimation.getTransformPath(mShape);
        }

        sPaintPath.setColor(Colors.getTileColor());
        canvas.drawPath(mShape, sPaintPath);

        if (!mRootView.paused) {
            mRectShape.inset(-Constraints.spacing / 2.0f, -Constraints.spacing / 2.0f);
            String text = Integer.toString(mData);
            mShape.computeBounds(mRectShape, true);
            sPaintText.setTextSize(mAnimation.getScale() * Constraints.tileFontSize);
            sPaintText.getTextBounds(text, 0, text.length(), mRectBounds);
            sPaintText.setColor(Colors.getTileTextColor());
            canvas.drawText(Integer.toString(mData), mRectShape.centerX(), mRectShape.centerY() - mRectBounds.centerY(), sPaintText);
        }
    }

    // возвращает индекс данного спрайта в общем массиве
    public int getIndex() {
        return mIndex;
    }

    // для отслеживания событий onClick
    public boolean isCollision(float x2, float y2) {
        return mRectShape.contains(x2, y2);
    }

    public boolean onClick() {
        if (mAnimation.isPlaying()) {
            return false;
        }

        int x = mIndex % Settings.gameWidth;
        int y = mIndex / Settings.gameWidth;

        int newIndex = Game.move(x, y);

        if (mIndex != newIndex) {
            mIndex = newIndex;

            if (Settings.animationEnabled) {
                x = mIndex % Settings.gameWidth;
                y = mIndex / Settings.gameWidth;

                mAnimation.dx = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * x - mCanvasX;
                mAnimation.dy = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * y - mCanvasY;
                mAnimation.type = Animation.TRANSLATE;
                mAnimation.frames = Settings.tileAnimFrames;
            } else {
                mCanvasX = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * (mIndex % Settings.gameWidth);
                mCanvasY = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * (mIndex / Settings.gameWidth);

                mShape.reset();
                mShape.addRoundRect(
                        new RectF(
                                mCanvasX,
                                mCanvasY,
                                mCanvasX + Constraints.tileWidth,
                                mCanvasY + Constraints.tileHeight
                        ),
                        Constraints.tileCornerRadius,
                        Constraints.tileCornerRadius,
                        Path.Direction.CW
                );
            } // if

            return true;

        } // if mIndex

        return false;
    }

    public void setAnimation(int type, int delay) {
        if (Settings.animationEnabled) {
            mAnimation.delay = delay;
            mAnimation.type = type;
            mAnimation.frames = Settings.tileAnimFrames;
        }
    }

    //
    public class Animation {

        public static final int STATIC = 0;             // статическая (без анимации)
        public static final int SCALE = 1;              // увеличение
        public static final int TRANSLATE = 2;          // перемещение

        public int type;                                // тип анимации
        public int frames;                              // отсавшееся кол-во кадров
        public int delay;                               // задержка анимации (в кадрах)
        public float dx;                                // перемещение по x
        public float dy;                                // перемещение по y

        public Animation() {
            this.type = STATIC;
            this.frames = 0;
            this.delay = 0;
        }

        // производит преобразование фигуры исходя из выбранного типа анимации
        public Path getTransformPath(Path p) {
            float ds, ds2, tx, ty;
            Matrix m;

            switch (type) {
                case SCALE:
                    m = new Matrix();
                    ds = (float) Tools.easeOut(frames, 0.0f, 1.0f, Settings.tileAnimFrames);
                    tx = (1 - ds) * (mCanvasX + Constraints.tileWidth / 2.0f);
                    ty = (1 - ds) * (mCanvasY + Constraints.tileHeight / 2.0f);
                    m.postScale(ds, ds);
                    m.postTranslate(tx, ty);
                    p.reset();
                    p.addRoundRect(new RectF(mCanvasX, mCanvasY, mCanvasX + Constraints.tileWidth, mCanvasY
                                    + Constraints.tileHeight), Constraints.tileCornerRadius, Constraints.tileCornerRadius,
                            Path.Direction.CW);
                    p.transform(m);
                    break; // SCALE

                case TRANSLATE:
                    m = new Matrix();
                    ds = (float) Tools.easeOut(frames, 0, 1.0f, Settings.tileAnimFrames);
                    ds2 = (float) Tools.easeOut(Math.min(frames + 1, Settings.tileAnimFrames), 0.0f, 1.0f,
                            Settings.tileAnimFrames);
                    ds = ds - ds2;
                    tx = ds * dx;
                    ty = ds * dy;
                    m.postTranslate(tx, ty);
                    p.transform(m);
                    break; // TRANSLATE

            }

            if (frames > 0) {
                frames--;
            } else {
                type = STATIC;
            }

            return p;
        }

        public float getScale() {
            float ds = 1.0f;
            if (isPlaying() && type == SCALE) {
                ds = (float) Tools.easeOut(frames, 0.0f, 1.0f, Settings.tileAnimFrames);
            }
            return ds;
        }

        public boolean isPlaying() {
            return frames > 0;
        }
    } // Animation

}