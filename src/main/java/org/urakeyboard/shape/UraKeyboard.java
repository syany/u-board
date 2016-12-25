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

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.TouchEvent;
import javafx.scene.paint.Paint;

import org.urakeyboard.sound.UraReceiver;


/**
 * UraKeyboardクラス。<br>
 *
 * @since 2016/12/13
 * @author syany
 */
public class UraKeyboard extends UraRectangle implements UraOnTouchPressedListener, UraOnTouchReleasedListener {
    /** チャネル、楽器情報を含めたレシーバ */
    private UraReceiver uraReceiver;
    /** 音階(C:60) */
    private int note = 60;
    /** 音量 */
    private int velocity= 120;

    private Parent parentNode;

    private boolean noteOn = false;
    /**
     * @param uraReceiver チャネル、楽器情報を含めたレシーバ
     */
    public UraKeyboard(final UraReceiver uraReceiver) {
        super();
        this.uraReceiver = uraReceiver;
        final UraKeyboard self = this;
        this.setOnTouchPressed(touchEvent -> {
            this.onTouchPressedListen(touchEvent, self);
        });
        this.setOnTouchReleased(touchEvent -> {
            this.onTouchReleasedListen(touchEvent, self);
        });
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
     * @return velocity を返却します
     */
    public final int velocity() {
        return velocity;
    }

    /**
     * @return parentNode を返却します
     */
    public final Parent parentNode() {
        return parentNode;
    }

    /**
     * @return noteOn を返却します
     */
    public final boolean isNoteOn() {
        return noteOn;
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
     * @param velocity velocity  を設定します
     */
    public UraKeyboard velocity(int velocity) {
        this.velocity = velocity;
        return this;
    }

    /**
     * @param parentNode parentNode  を設定します
     */
    public UraKeyboard parentNode(Parent parentNode) {
        this.parentNode = parentNode;
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

    @Override
    public void onTouchReleasedListen(TouchEvent touchEvent, Node node) {
        try {
            final UraKeyboard uraKeyboard = UraKeyboard.class.cast(node);
            if (!uraKeyboard.isNoteOn()) {
                return;
            }
            setNoteOff();
            uraKeyboard.setPressed(false);
        } finally {
            touchEvent.consume();
        }
    }

    @Override
    public void onTouchPressedListen(TouchEvent touchEvent, Node node) {
        try {
            final UraKeyboard uraKeyboard = UraKeyboard.class.cast(node);
            if (node.isPressed() || uraKeyboard.isNoteOn()) {
                return;
            }
            setNoteOn();
            uraKeyboard.setPressed(true);
        } finally {
            touchEvent.consume();
        }
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
     */
    public synchronized final void setNoteOn() {
        this.uraReceiver().noteOn(note, velocity);
        this.noteOn = true;
    }

    /**
     * @param noteOn noteOn  を設定します
     */
    public synchronized final void setNoteOff() {
        this.uraReceiver().noteOff(note, velocity);
        this.noteOn = false;
    }
}
