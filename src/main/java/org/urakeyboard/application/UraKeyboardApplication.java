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
 * $Id: UraKeyboardApplication.java$
 */
package org.urakeyboard.application;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.sound.midi.Receiver;

import org.urakeyboard.layout.KeyboardMain;
import org.urakeyboard.layout.Notes;
import org.urakeyboard.sound.UraMidiDevice;
import org.urakeyboard.util.UraApplicationUtils;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * UraKeyboardApplicationクラス。<br>
 *
 * @since 2016/12/19
 * @author syany
 */
public final class UraKeyboardApplication extends Application {
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();

    /** 選択可能なMIDIデバイスリストや、Receiverを開く */
    final UraMidiDevice midiDevice = new UraMidiDevice();
//    /** キーボードのキーリスト */
//    protected final List<UraWhiteKey> whiteKeyList = newArrayList();
//    protected final List<UraBlackKey> blackKeyList = newArrayList();
    /** レシーバ */
    Receiver receiver;
    /* (非 Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception {
        // デフォルト定数
        final int SYNTHESIZER_IDX = Integer.parseInt(UraApplicationUtils.APP_RESOURCE.getResourceValue("synthesizerIdx"));
        final int SEQUENCER_IDX = Integer.parseInt(UraApplicationUtils.APP_RESOURCE.getResourceValue("synthesizerIdx"));

        // レシーバを開く
        receiver = midiDevice.openReciver(SYNTHESIZER_IDX, SEQUENCER_IDX);

        // 指定の音階分キーボードを作成
//        keyInit();

        // レイアウト設定
        Scene scene = null;
//        final Notes notes = new Notes(scene, whiteKeyList, blackKeyList);
//        KeyboardMain keyboardMain = new KeyboardMain(scene, whiteKeyList, blackKeyList);
        final Notes notes = new Notes(scene, midiDevice, receiver);
        KeyboardMain keyboardMain = new KeyboardMain(scene, notes.getWhiteKeyList(), notes.getBlackKeyList());
        for(final Node childNode : keyboardMain.getChildren()) {
            if ("KeyArea".equals(childNode.getId())) {
                ((HBox) childNode).getChildren().add(notes);
                LOG.log("TRC note point(x:{}, y:{})", notes.getLayoutX(), notes.getLayoutY());
            }
        }

        // 対象シーンの表示
        stage.setScene(keyboardMain.getScene());
//        setFilter(stage);
        stage.show();
    }

    protected void setFilter(Stage stage) {
        stage.addEventFilter(EventType.ROOT, e -> System.out.println(e));
    }

//    public void keyInit() {
//        final int FIRST_NOTE = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("firstNote"));
//        final int FIRST_COUNT = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceString("noteCount"));
//        final int END_NOTE = FIRST_NOTE + FIRST_COUNT;
//        final double WHITE_KEY_WIDTH = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("whiteKey").get("width"));
//        final double BLACK_KEY_WIDTH = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("blackKey").get("width"));
//        final double BLACK_KEY_OFFSET = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) / 2;
//        final double WHITE_KEY_HEIGHT = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("whiteKey").get("height"));
//        final double BLACK_KEY_HEIGHT = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("blackKey").get("height"));
//
//        // 指定の音階分キーボードを作成
//        for (int note = FIRST_NOTE, wIdx = 0; note < END_NOTE; note++) {
//            final UraReceiver uraReceiver = midiDevice.createUraReceiver(receiver, 0);
//            if (UraKeyboardUtils.isBlack(note)) {
//                final double w = (wIdx - 1) * WHITE_KEY_WIDTH;
//                final double x = (w + WHITE_KEY_WIDTH / 2) + BLACK_KEY_OFFSET;
//                UraBlackKey blackKey = new UraBlackKey(uraReceiver)
//                .x(x).y(0).width(BLACK_KEY_WIDTH).height(BLACK_KEY_HEIGHT).note(note);
//                blackKeyList.add(blackKey);
//            } else {
//                final double x = wIdx * WHITE_KEY_WIDTH;
//                final UraWhiteKey whiteKey = new UraWhiteKey(uraReceiver)
//                    .x(x).y(0).width(WHITE_KEY_WIDTH).height(WHITE_KEY_HEIGHT).note(note);
//                whiteKeyList.add(whiteKey);
//                wIdx++;
//            }
//        }
//    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
