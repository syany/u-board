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
 * $Id: UraWhiteKey.java$
 */
package org.urakeyboard.shape;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.TouchEvent;
import javafx.scene.paint.Paint;

import org.urakeyboard.sound.UraReceiver;


/**
 * UraWhiteKeyクラス。<br>
 *
 * @since 2016/12/16
 * @author syany
 */
public class UraWhiteKey extends UraKeyboard {
    protected static final String CSS_KEY_BASE = "keyBase";
    protected static final String CSS_KEY_OFF = "whiteKey";
    protected static final String CSS_KEY_ON = "whiteKeyOn";
    /**
     * @param receiver
     * @param shortMessage
     * @param noteProgram
     */
    public UraWhiteKey(final UraReceiver uraReceiver) {
        super(uraReceiver);
        this.styleClassList().add(CSS_KEY_BASE);
        this.styleClassList().add(CSS_KEY_OFF);
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#onTouchReleasedListen(javafx.scene.input.TouchEvent, javafx.scene.Node)
     */
    @Override
    public void onTouchReleasedListen(TouchEvent touchEvent, Node node) {
        super.onTouchReleasedListen(touchEvent, node);
        node.getStyleClass().clear();
        node.getStyleClass().add(UraWhiteKey.CSS_KEY_BASE);
        node.getStyleClass().add(UraWhiteKey.CSS_KEY_OFF);
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#onTouchPressedListen(javafx.scene.input.TouchEvent, javafx.scene.Node)
     */
    @Override
    public void onTouchPressedListen(TouchEvent touchEvent, Node node) {
        super.onTouchPressedListen(touchEvent, node);
        node.getStyleClass().clear();
        node.getStyleClass().add(UraWhiteKey.CSS_KEY_BASE);
        node.getStyleClass().add(UraWhiteKey.CSS_KEY_ON);
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#note(int)
     */
    @Override
    public UraWhiteKey note(int note) {
        super.note(note);
        return this;
    }
    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#velocity(int)
     */
    @Override
    public UraWhiteKey velocity(int velocity) {
        super.velocity(velocity);
        return this;
    }
    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#parentNode(javafx.scene.Parent)
     */
    @Override
    public UraWhiteKey parentNode(Parent parentNode) {
        super.parentNode(parentNode);
        return this;
    }
    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#noteOn(boolean)
     */
    @Override
    public UraWhiteKey noteOn(boolean noteOn) {
        super.noteOn(noteOn);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraKeyboard#uraReceiver(org.urakeyboard.sound.UraReceiver)
     */
    @Override
    public UraWhiteKey uraReceiver(UraReceiver uraReceiver) {
        super.uraReceiver(uraReceiver);
        return this;
    }

    /* (非 Javadoc)
     * @see org.UraWhiteKey.shape.UraWhiteKey#x(double)
     */
    @Override
    public UraWhiteKey x(double x) {
        super.x(x);
        return this;
    }

    /* (非 Javadoc)
     * @see org.UraWhiteKey.shape.UraWhiteKey#y(double)
     */
    @Override
    public UraWhiteKey y(double y) {
        super.y(y);
        return this;
    }

    /* (非 Javadoc)
     * @see org.UraWhiteKey.shape.UraWhiteKey#width(double)
     */
    @Override
    public UraWhiteKey width(double width) {
        super.width(width);
        return this;
    }

    /* (非 Javadoc)
     * @see org.UraWhiteKey.shape.UraWhiteKey#height(double)
     */
    @Override
    public UraWhiteKey height(double height) {
        super.height(height);
        return this;
    }

    /* (非 Javadoc)
     * @see org.UraWhiteKey.shape.UraWhiteKey#paint(javafx.scene.paint.Paint)
     */
    @Override
    public UraWhiteKey paint(Paint paint) {
        super.paint(paint);
        return this;
    }

    /* (非 Javadoc)
     * @see org.UraWhiteKey.shape.UraWhiteKey#styleClassClear()
     */
    @Override
    public UraWhiteKey styleClassClear() {
        super.styleClassClear();
        return this;
    }
}
