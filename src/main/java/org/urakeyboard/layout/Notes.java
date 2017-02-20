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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javax.sound.midi.Receiver;

import org.urakeyboard.shape.UraBlackKey;
import org.urakeyboard.shape.UraKeyboard;
import org.urakeyboard.shape.UraWhiteKey;
import org.urakeyboard.sound.UraMidiDevice;
import org.urakeyboard.sound.UraReceiver;
import org.urakeyboard.util.UraApplicationUtils;
import org.urakeyboard.util.UraKeyboardUtils;
import org.urakeyboard.util.UraLayoutUtils;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * 動的鍵群レイアウトクラス。<br>
 *
 * @since 2016/12/20
 * @author syany
 */
public class Notes extends AnchorPane {
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /** 最左鍵音階 */
    protected int firstNote;
    /** 鍵数 */
    protected int noteCount;
    /** 白鍵リスト */
    protected final List<UraWhiteKey> whiteKeyList;
    /** 黒鍵リスト */
    protected final List<UraBlackKey> blackKeyList;
    /** 鍵リスト */
    protected final List<UraKeyboard> keyList;
    /** 鍵ラベルリスト */
    protected final List<Label> keyLabelList;
    /**
     * コンストラクタ。
     * @param scene シーン
     * @param midiDevice オリジナルMIDIデバイス管理オブジェクト
     * @param receiver レシーバ
     */
    public Notes(Scene scene, final UraMidiDevice midiDevice, final Receiver receiver) {
        if (scene == null) {
            scene = new Scene(this);
        }
        UraLayoutUtils.layoutLoad(this, scene);
        firstNote = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("firstNote"));
        noteCount = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("noteCount"));
        whiteKeyList = newArrayList((noteCount * 8) / 13);
        blackKeyList = newArrayList((noteCount * 5) / 13);
        keyList = newArrayList(noteCount);
        keyLabelList = newArrayList(noteCount);
        init(midiDevice, receiver);
    }
    /**
     * 鍵盤の初期化。位置、幅のデフォルトを取得し子ノードへ追加する。
     * @param midiDevice
     * @param receiver
     */
    public void init(final UraMidiDevice midiDevice, final Receiver receiver) {
//        final int FIRST_NOTE = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("firstNote"));
//        final int FIRST_COUNT = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("noteCount"));
        if (UraKeyboardUtils.isBlack(this.firstNote)) {
            LOG.log("WRN first keys note[{}] is black key.", firstNote);
        }
        final int END_NOTE = firstNote + noteCount;
        final double WHITE_KEY_WIDTH = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("whiteKey").get("width"));
        final double BLACK_KEY_WIDTH = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("blackKey").get("width"));
        final double BLACK_KEY_OFFSET = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) / 2;
        final double WHITE_KEY_HEIGHT = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("whiteKey").get("height"));
        final double BLACK_KEY_HEIGHT = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("blackKey").get("height"));

        final double WHITE_KEY_Y = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("whiteKey").get("y"));
        final double BLACK_KEY_Y = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("blackKey").get("y"));

        // 指定の音階分キーボードを作成
        for (int note = firstNote, wIdx = 0; note < END_NOTE; note++) {
            final UraReceiver uraReceiver = midiDevice.createUraReceiver(receiver, 0);
            if (UraKeyboardUtils.isBlack(note)) {
                final double w = (wIdx - 1) * WHITE_KEY_WIDTH;
                final double x = (w + WHITE_KEY_WIDTH / 2) + BLACK_KEY_OFFSET;
                UraBlackKey blackKey = new UraBlackKey(uraReceiver)
                .x(x).y(BLACK_KEY_Y).width(BLACK_KEY_WIDTH).height(BLACK_KEY_HEIGHT).note(note);
                blackKeyList.add(blackKey);
                keyList.add(blackKey);
            } else {
                final double x = wIdx * WHITE_KEY_WIDTH;
                final UraWhiteKey whiteKey = new UraWhiteKey(uraReceiver)
                    .x(x).y(WHITE_KEY_Y).width(WHITE_KEY_WIDTH).height(WHITE_KEY_HEIGHT).note(note);
                whiteKeyList.add(whiteKey);
                keyList.add(whiteKey);
                wIdx++;
            }
        }

        ObservableList<Node> nodes = this.getChildren();

        UraKeyboard currenyKey = null;
        for (int idx = 0; idx < keyList.size(); idx++) {
            final boolean preBlack = (currenyKey != null && currenyKey.isBlackKey());

            final Label keyLabel = new Label();
            if (preBlack) {
                double preWidth = currenyKey.getWidth() / 2;
                currenyKey = keyList.get(idx);
                keyLabel.setLayoutX(currenyKey.x()+preWidth);
                keyLabel.setText(String.valueOf(currenyKey.uraReceiver().getChanel()));
            } else {
                currenyKey = keyList.get(idx);
                keyLabel.setLayoutX(currenyKey.x());
                keyLabel.setText(String.valueOf(currenyKey.uraReceiver().getChanel()));
            }
            nodes.add(keyLabel);
        }
        // 鍵群レイアウトに追加する
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

    /**
     * @return keyList を返却します
     */
    public final List<UraKeyboard> getKeyList() {
        return keyList;
    }
}
