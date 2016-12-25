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
 * $Id: UraSoundReceive.java$
 */
package org.urakeyboard.shape;

import javafx.scene.Node;
import javafx.scene.input.TouchEvent;
import javafx.scene.paint.Paint;

import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;


/**
 * UraSoundReceiveクラス。<br>
 *
 * @since 2016/12/12
 * @author syany
 */
public class UraSoundReceive extends UraRectangle implements UraOnTouchPressedListener, UraOnTouchReleasedListener {
    /** MIDIレシーバ */
    private Receiver receiver;
    /** レシーバへ送信するメッセージインスタンス */
    private ShortMessage shortMessage;
    /**
     * @param receiver MIDIレシーバ
     * @param shortMessage レシーバへ送信するメッセージインスタンス
     */
    public UraSoundReceive(Receiver receiver, ShortMessage shortMessage) {
        super();
        this.receiver = receiver;
        this.shortMessage = shortMessage;
        this.setOnTouchPressed(touchEvent -> {
            this.onTouchPressedListen(touchEvent, this);
        });
        this.setOnTouchReleased(touchEvent -> {
            this.onTouchReleasedListen(touchEvent, this);
        });
    }

    /**
     * @return receiver を返却します
     */
    public final Receiver receiver() {
        return receiver;
    }

    /**
     * @return shortMessage を返却します
     */
    public final ShortMessage shortMessage() {
        return shortMessage;
    }

    /**
     * @param receiver receiver  を設定します
     */
    public UraSoundReceive receiver(final Receiver receiver) {
        this.receiver = receiver;
        return this;
    }

    /**
     * @param shortMessage shortMessage  を設定します
     */
    public UraSoundReceive shortMessage(final ShortMessage shortMessage) {
        this.shortMessage = shortMessage;
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraRectangle#x(double)
     */
    @Override
    public UraSoundReceive x(double x) {
        super.x(x);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraRectangle#y(double)
     */
    @Override
    public UraSoundReceive y(double y) {
        super.y(y);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraRectangle#width(double)
     */
    @Override
    public UraSoundReceive width(double width) {
        super.width(width);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraRectangle#height(double)
     */
    @Override
    public UraSoundReceive height(double height) {
        super.height(height);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraRectangle#paint(javafx.scene.paint.Paint)
     */
    @Override
    public UraSoundReceive paint(Paint paint) {
        super.paint(paint);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraRectangle#styleClassClear()
     */
    @Override
    public UraSoundReceive styleClassClear() {
        super.styleClassClear();
        return this;
    }

    @Override
    public void onTouchReleasedListen(TouchEvent touchEvent, Node node) {}

    @Override
    public void onTouchPressedListen(TouchEvent touchEvent, Node node) {}


}
