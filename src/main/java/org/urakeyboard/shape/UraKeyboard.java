/*
 * Copyright 2013-2016 the Uranoplums Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * $Id: UraKeyboard.java$
 */
package org.urakeyboard.shape;

import javafx.scene.paint.Paint;

import org.urakeyboard.sound.UraReceiver;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * UraKeyboardクラス。<br>
 *
 * @since 2016/12/13
 * @author syany
 */
public class UraKeyboard extends UraRectangle {
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /** チャネル、楽器情報を含めたレシーバ */
    private UraReceiver uraReceiver;
    /** 音階(C:60) */
    private int note = 60;
    /** 音がなった状態であればtrue */
    private boolean noteOn = false;
    /** 黒鍵であればtrue */
    protected boolean blackKey = false;

    /**
     * @param uraReceiver チャネル、楽器情報を含めたレシーバ
     */
    public UraKeyboard(final UraReceiver uraReceiver) {
        super();
        this.uraReceiver = uraReceiver;
    }

    /**
     * @return uraReceiver を返却します
     */
    public final UraReceiver uraReceiver() {
        return uraReceiver;
    }

    /**
     * @return note を返却します
     */
    public final int note() {
        return note;
    }

    /**
     * @return noteOn を返却します
     */
    public final boolean isNoteOn() {
        return noteOn;
    }

    /**
     * @return blackKey を返却します
     */
    public final boolean isBlackKey() {
        return blackKey;
    }
    /**
     * @param uraReceiver uraReceiver  を設定します
     */
    public UraKeyboard uraReceiver(UraReceiver uraReceiver) {
        this.uraReceiver = uraReceiver;
        return this;
    }

    /**
     * @param note note  を設定します
     */
    public UraKeyboard note(int note) {
        this.note = note;
        return this;
    }

    /**
     * @param noteOn noteOn  を設定します
     */
    public UraKeyboard noteOn(boolean noteOn) {
        this.noteOn = noteOn;
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#x(double)
     */
    @Override
    public UraKeyboard x(double x) {
        super.x(x);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#y(double)
     */
    @Override
    public UraKeyboard y(double y) {
        super.y(y);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#width(double)
     */
    @Override
    public UraKeyboard width(double width) {
        super.width(width);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#height(double)
     */
    @Override
    public UraKeyboard height(double height) {
        super.height(height);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#paint(javafx.scene.paint.Paint)
     */
    @Override
    public UraKeyboard paint(Paint paint) {
        super.paint(paint);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#styleClassClear()
     */
    @Override
    public UraKeyboard styleClassClear() {
        super.styleClassClear();
        return this;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public boolean isHover(final double x, final double y) {
        return this.getX() <= x && (this.getX() + this.getWidth()) > x &&
                this.getY() <= y && (this.getY() + this.getHeight()) > y;
    }
    /**
     * @param x
     * @param y
     * @return
     */
    public boolean isSceneHover(final double x, final double y) {
        return this.sceneX() <= x && (this.sceneX() + this.getWidth()) > x &&
                this.sceneY() <= y && (this.sceneY() + this.getHeight()) > y;
    }
    /**
     * 音を鳴らしている際の鍵表示
     */
    public synchronized void noteOnView() {}
    /**
     * 音を止めている際の鍵表示
     */
    public synchronized void noteOffView() {}
}
