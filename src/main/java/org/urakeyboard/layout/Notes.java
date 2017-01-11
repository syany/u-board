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
 * $Id: Notes.java$
 */
package org.urakeyboard.layout;

import static org.uranoplums.typical.collection.factory.UraListFactory.*;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import javax.sound.midi.Receiver;

import org.urakeyboard.shape.UraBlackKey;
import org.urakeyboard.shape.UraWhiteKey;
import org.urakeyboard.sound.UraMidiDevice;
import org.urakeyboard.sound.UraReceiver;
import org.urakeyboard.util.UraApplicationUtils;
import org.urakeyboard.util.UraKeyboardUtils;
import org.urakeyboard.util.UraLayoutUtils;


/**
 * 動的キーレイアウトクラス。<br>
 *
 * @since 2016/12/20
 * @author syany
 */
public class Notes extends AnchorPane {

//    protected final List<UraKeyboard> keyboardList = newArrayList();
//
//    /** 選択可能なMIDIデバイスリストや、Receiverを開く */
//    final UraMidiDevice midiDevice = new UraMidiDevice();
    /** キーボードのキーリスト */
    protected final List<UraWhiteKey> whiteKeyList = newArrayList();
    protected final List<UraBlackKey> blackKeyList = newArrayList();
    /** レシーバ */
//    Receiver receiver;
    /**
     * コンストラクタ
     */
    public Notes(Scene scene, final List<UraWhiteKey> whiteKeyList, final List<UraBlackKey> blackKeyList) {
        if (scene == null) {
            scene = new Scene(this);
        }
        UraLayoutUtils.layoutLoad(this, scene);
        init(whiteKeyList, blackKeyList);
    }

    public Notes(Scene scene, final UraMidiDevice midiDevice, final Receiver receiver) {
        if (scene == null) {
            scene = new Scene(this);
        }
        UraLayoutUtils.layoutLoad(this, scene);
//        init(whiteKeyList, blackKeyList);
        keyInit(midiDevice, receiver);
    }

    protected void init(final List<UraWhiteKey> whiteKeyList, final List<UraBlackKey> blackKeyList) {
        // add child keyboard shape
        ObservableList<Node> nodes = this.getChildren();
        for (final UraWhiteKey wKey : whiteKeyList) {
            nodes.add(wKey);
        }

        for (final UraBlackKey bKey : blackKeyList) {
            nodes.add(bKey);
        }
    }

    public void keyInit(final UraMidiDevice midiDevice, final Receiver receiver) {
        final int FIRST_NOTE = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("firstNote"));
        final int FIRST_COUNT = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("noteCount"));
        final int END_NOTE = FIRST_NOTE + FIRST_COUNT;
        final double WHITE_KEY_WIDTH = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("whiteKey").get("width"));
        final double BLACK_KEY_WIDTH = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("blackKey").get("width"));
        final double BLACK_KEY_OFFSET = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) / 2;
        final double WHITE_KEY_HEIGHT = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("whiteKey").get("height"));
        final double BLACK_KEY_HEIGHT = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("blackKey").get("height"));

        // 指定の音階分キーボードを作成
        for (int note = FIRST_NOTE, wIdx = 0; note < END_NOTE; note++) {
            final UraReceiver uraReceiver = midiDevice.createUraReceiver(receiver, 0);
            if (UraKeyboardUtils.isBlack(note)) {
                final double w = (wIdx - 1) * WHITE_KEY_WIDTH;
                final double x = (w + WHITE_KEY_WIDTH / 2) + BLACK_KEY_OFFSET;
                UraBlackKey blackKey = new UraBlackKey(uraReceiver)
                .x(x).y(0).width(BLACK_KEY_WIDTH).height(BLACK_KEY_HEIGHT).note(note);
                blackKeyList.add(blackKey);
            } else {
                final double x = wIdx * WHITE_KEY_WIDTH;
                final UraWhiteKey whiteKey = new UraWhiteKey(uraReceiver)
                    .x(x).y(0).width(WHITE_KEY_WIDTH).height(WHITE_KEY_HEIGHT).note(note);
                whiteKeyList.add(whiteKey);
                wIdx++;
            }
        }

        // 鍵群レイアウトに追加する
        ObservableList<Node> nodes = this.getChildren();
        for (final UraWhiteKey wKey : whiteKeyList) {
            nodes.add(wKey);
        }

        for (final UraBlackKey bKey : blackKeyList) {
            nodes.add(bKey);
        }
    }


    /**
     * @return whiteKeyList を返却します
     */
    public final List<UraWhiteKey> getWhiteKeyList() {
        return whiteKeyList;
    }



    /**
     * @return blackKeyList を返却します
     */
    public final List<UraBlackKey> getBlackKeyList() {
        return blackKeyList;
    }

}
