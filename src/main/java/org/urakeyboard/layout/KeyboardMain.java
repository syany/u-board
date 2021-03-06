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
 * $Id: KeyboardMain.java$
 */
package org.urakeyboard.layout;

import static org.uranoplums.typical.collection.factory.UraMapFactory.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;

import org.urakeyboard.shape.UraKeyboard;
import org.urakeyboard.shape.UraOnTouchMovedListener;
import org.urakeyboard.shape.UraOnTouchPressedListener;
import org.urakeyboard.shape.UraOnTouchReleasedListener;
import org.urakeyboard.shape.UraPitchBendRibon;
import org.urakeyboard.sound.UraMidiDevice;
import org.urakeyboard.sound.UraReceiver;
import org.urakeyboard.util.UraApplicationUtils;
import org.urakeyboard.util.UraLayoutUtils;
import org.uranoplums.typical.collection.factory.UraMapFactory.FACTOR;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;

/**
 * KeyboardMainクラス。<br>
 *
 * @since 2016/12/19
 * @author syany
 */
public class KeyboardMain extends VBox implements UraOnTouchMovedListener, UraOnTouchReleasedListener, UraOnTouchPressedListener {
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /** 選択可能なMIDIデバイスリストや、Receiverを開く */
    protected final UraMidiDevice midiDevice = new UraMidiDevice();
    /** 鍵リスト */
    protected final List<UraKeyboard> keyList;
    /** ピッチベンドリボンエリア */
    protected final UraPitchBendRibon pitchBandRibon;
    /** シンセプルダウン */
    @FXML
    protected ComboBox<MidiDevice> synthesizerSelect;
    /** シーケンサプルダウン */
    @FXML
    protected ComboBox<MidiDevice> sequencerSelect;
    /** デバイス設定ボタン */
    @FXML
    protected Button deviceButton;
    /** キー設定 */
    @FXML
    protected Button setKeyButton;
    @FXML
    protected VBox optionArea;
    @FXML
    protected HBox keyArea;
    /** 押下中（発音中）の鍵盤オブジェクトマップ */
    protected final ConcurrentMap<Integer, UraKeyboard> noteOnKeyCacheMap = newConcurrentHashMap(10, FACTOR.NONE);
    /** 押下中ピッチベンドリボンコントローラタッチID */
    protected int currentOnPitchBendTouchId = -1;
    protected final UraReceiver commonUraReceiver;

    /**
     * コンストラクタ
     * @param scene
     */
    public KeyboardMain(Scene scene) {
        if (scene == null) {
            scene = new Scene(this);
        }
        UraLayoutUtils.layoutLoad(this, scene);
        // シーケンス、シンセサイザのデフォルト取得
        final int SYNTHESIZER_IDX = Integer.parseInt(UraApplicationUtils.APP_RESOURCE.getResourceValue("synthesizerIdx"));
        final int SEQUENCER_IDX = Integer.parseInt(UraApplicationUtils.APP_RESOURCE.getResourceValue("synthesizerIdx"));
        // レシーバを開く
        final Receiver receiver = midiDevice.openReciver(SYNTHESIZER_IDX, SEQUENCER_IDX);
        LOG.log("DBG Open Receiver is [{}]", receiver);
        final Notes notes = new Notes(scene, midiDevice, receiver);
        this.keyList = notes.getKeyList();

        initSynthesizerSelect();
        initSequencerSelect();

        commonUraReceiver = midiDevice.createUraReceiver(receiver, 0);

        final double PITCH_B_WIDTH = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("pitchBendArea").get("width"));
        final double PITCH_B_HEIGHT = Double.class.cast(UraApplicationUtils.APP_RESOURCE.getResourceMap("pitchBendArea").get("height"));
        pitchBandRibon = new UraPitchBendRibon(commonUraReceiver).width(PITCH_B_WIDTH).height(PITCH_B_HEIGHT);
        pitchBandRibon.getStyleClass().add(String.class.cast(UraApplicationUtils.APP_RESOURCE
                .getResourceMap("pitchBendArea").get("css")));

        optionArea.getChildren().add(pitchBandRibon);
        keyArea.getChildren().add(notes);

        this.setOnTouchMoved(touchEvent -> {
            onTouchMovedListen(touchEvent, this);
        });
        this.setOnTouchReleased(touchEvent -> {
            onTouchReleasedListen(touchEvent, this);
        });
        this.setOnTouchPressed(touchEvent -> {
            onTouchPressedListen(touchEvent, this);
        });
    }

    /**
     * シンセサイザ選択の初期化
     */
    protected void initSynthesizerSelect() {
        this.synthesizerSelect.getItems().addAll(midiDevice.getSynthesizerList());
        this.synthesizerSelect.setValue(midiDevice.getDefaultSynthesizer());
        // ラムダ（Callback@FunctionalInterface）を使って超省略
        this.synthesizerSelect.setCellFactory(listView -> new ListCell<MidiDevice>() {
            /**
             * リストボックス表示を設定
             *  (非 Javadoc)
             * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
             */
            @Override
            protected void updateItem(MidiDevice midiDevice, boolean empty) {
                LOG.log("DBG updateItem Synthesizer dev={}, empty={}", midiDevice, empty);
                super.updateItem(midiDevice, empty);
                if (midiDevice != null && !empty) {
                    setText(midiDevice.getDeviceInfo().getName());
                }
            }
        });
        this.synthesizerSelect.setButtonCell(this.synthesizerSelect.getCellFactory().call(null));
    }
    /**
     * シーケンサー選択の初期化
     */
    protected void initSequencerSelect() {
        this.sequencerSelect.getItems().addAll(midiDevice.getSequencerList());
        this.sequencerSelect.setValue(midiDevice.getDefaultSequencer());
        // ラムダ（Callback@FunctionalInterface）を使って超省略
        this.sequencerSelect.setCellFactory(listView -> new ListCell<MidiDevice>() {
            /**
             * リストボックス表示を設定
             *  (非 Javadoc)
             * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
             */
            @Override
            protected void updateItem(MidiDevice midiDevice, boolean empty) {
                LOG.log("DBG updateItem Sequencer dev={}, empty={}", midiDevice, empty);
                super.updateItem(midiDevice, empty);
                if (midiDevice != null && !empty) {
                    setText(midiDevice.getDeviceInfo().getName());
                }
            }
        });
        this.sequencerSelect.setButtonCell(this.sequencerSelect.getCellFactory().call(null));
    }

    /**
     * @param actionEvent
     */
    @FXML
    public void deviceSelectExec(ActionEvent actionEvent) {
        this.deviceButton.setOnMouseClicked(mouseClicked -> {
            final Receiver newReceiver = midiDevice.openReciver();
            for (final UraKeyboard uraKeyboard: this.keyList) {
                uraKeyboard.uraReceiver().setReceiver(newReceiver);
            }
        });
    }
    /**
     * 設定ボタン押下時の動作
     * @param actionEvent
     */
    @FXML
    public void keySettingDialogOpen(ActionEvent actionEvent) {
        new SettingDialog(null, this.midiDevice, this.keyList, commonUraReceiver);
    }
    /**
     * 対象を押下、離した際に実行されるイベントリスナ
     * @see org.urakeyboard.shape.UraOnTouchMovedListener#onTouchMovedListen(javafx.scene.input.TouchEvent,
     *      javafx.scene.Node)
     */
    @Override
    public void onTouchMovedListen(TouchEvent touchEvent, Node node) {
        try {
            if (!touchEvent.getEventType().equals(TouchEvent.TOUCH_MOVED)) {
                // MOVEDイベント以外は処理しない
                return;
            }
            // 現在タッチしている鍵を取得
            final Node targetNode = touchEvent.getTouchPoint().getPickResult().getIntersectedNode();
            if (targetNode == null) {
                return;
            }
//          LOG.log("DBG Move Before [{}] getTouchPoint=({}), target=({})", touchEvent.getTouchPoint().getId(), touchEvent.getTouchPoint(), touchEvent.getTarget());

            final boolean IS_URAKEYBOARD_INSTANCE = UraKeyboard.class.isInstance(targetNode);
            final boolean IS_PITCHBEND_INSTANCE = UraPitchBendRibon.class.isInstance(targetNode);
            final TouchPoint touchPoint = touchEvent.getTouchPoint();
            final Integer CURRENT_TOUCH_ID = touchPoint.getId();
            if (IS_URAKEYBOARD_INSTANCE) {
                // ピッチベンドリボンコントローラ制御
                if (CURRENT_TOUCH_ID == this.currentOnPitchBendTouchId) {
                    // ピッチベンドの範囲から外れた
                    LOG.log("DBG This point out of range(PitchBendRibon) id[{}] (x:{}, y:{})",CURRENT_TOUCH_ID, touchPoint.getX(), touchPoint.getY());
                    this.pitchBendOff(CURRENT_TOUCH_ID);
                }
                // 鍵オブジェクト上であれば、鍵用Movedイベントリスナを実行
                this.touchMovedKeyboardListen(touchEvent, UraKeyboard.class.cast(targetNode));
            } else if (this.noteOnKeyCacheMap.size() > 0 && IS_PITCHBEND_INSTANCE) {
                // 鍵制御
                if (noteOnKeyCacheMap.containsKey(CURRENT_TOUCH_ID)) {
                    // 鍵オブジェクト上でなく、鍵キャシュマップ上にあるならばNote off処理を実行
                    final UraKeyboard uraKeyboard = noteOnKeyCacheMap.get(CURRENT_TOUCH_ID);
                    if (uraKeyboard != null) {
                        LOG.log("DBG This point out of range id[{}] (x:{}, y:{})",touchPoint.getId(), touchPoint.getX(), touchPoint.getY());
                        this.noteOff(touchPoint, uraKeyboard);
                    }
                }
                LOG.log("DBG Moved PitchBend touch=[{}]({})", touchPoint.getId(), touchPoint);
                this.pitchBendOn(touchPoint);
            } else {
                if (noteOnKeyCacheMap.size() > 0) {
                    // 鍵オブジェクト上でなく、鍵キャシュマップ上にあるならばNote off処理を実行
                    final UraKeyboard uraKeyboard = noteOnKeyCacheMap.get(CURRENT_TOUCH_ID);
                    if (uraKeyboard != null) {
                        LOG.log("DBG This point out of range id[{}] (x:{}, y:{})",touchPoint.getId(), touchPoint.getX(), touchPoint.getY());
                        this.noteOff(touchPoint, uraKeyboard);
                    }
                }
                if (noteOnKeyCacheMap.size() == 0) {
                    // 全ての鍵が押されていない
                    if (this.currentOnPitchBendTouchId > 0) {
                        LOG.log("DBG This point out of range(all keys) id[{}] (x:{}, y:{})",CURRENT_TOUCH_ID, touchPoint.getX(), touchPoint.getY());
                        this.pitchBendOff(CURRENT_TOUCH_ID);
                    }
                } else if (CURRENT_TOUCH_ID == this.currentOnPitchBendTouchId) {
                    // ピッチベンドの範囲から外れた
                    LOG.log("DBG This point out of range(PitchBendRibon) id[{}] (x:{}, y:{})",CURRENT_TOUCH_ID, touchPoint.getX(), touchPoint.getY());
                    this.pitchBendOff(CURRENT_TOUCH_ID);
                }
            }
        } finally {
            touchEvent.consume();
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see org.urakeyboard.shape.UraOnTouchReleasedListener#onTouchReleasedListen(javafx.scene.input.TouchEvent,
     * javafx.scene.Node)
     */
    @Override
    public void onTouchReleasedListen(TouchEvent touchEvent, Node node) {
        try {
            // 現在タッチしている鍵を取得
            final Node targetNode = touchEvent.getTouchPoint().getPickResult().getIntersectedNode();
            if (targetNode == null) {
                return;
            }

            LOG.log("DBG Released Before [{}] getTouchPoint=({}), target=({})", touchEvent.getTouchPoint().getId(), touchEvent.getTouchPoint(), touchEvent.getTarget());
            if (UraKeyboard.class.isInstance(targetNode)) {
                // 鍵オブジェクト上であれば、鍵用Releasedイベントリスナを実行
                this.touchReleasedKeyboardListen(touchEvent, UraKeyboard.class.cast(targetNode));
            } else if (UraPitchBendRibon.class.isInstance(targetNode)) {
                final TouchPoint tp = touchEvent.getTouchPoint();
                LOG.log("DBG Released PitchBend Clear touch=[{}]({})", tp.getId(), tp);
//                pitchBend.bendModuleClear();
                this.pitchBendOff(tp.getId());
            }
            //
            final TouchPoint touchPoint = touchEvent.getTouchPoint();
            final Integer CURRENT_TOUCH_ID = touchPoint.getId();
            if (noteOnKeyCacheMap.size() == 0) {
                // 全ての鍵が押されていない
                if (this.currentOnPitchBendTouchId > 0) {
                    LOG.log("DBG Released This point out of range(all keys) id[{}] (x:{}, y:{})",CURRENT_TOUCH_ID, touchPoint.getX(), touchPoint.getY());
                    this.pitchBendOff(CURRENT_TOUCH_ID);
                }
            }
            // 全てのキャッシュがなくなった場合のみ、全キーを検索し直し、音を止める。
            this.resetKeyboardNotes(touchEvent);
        } finally {
            touchEvent.consume();
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see org.urakeyboard.shape.UraOnTouchPressedListener#onTouchPressedListen(javafx.scene.input.TouchEvent,
     * javafx.scene.Node)
     */
    @Override
    public void onTouchPressedListen(TouchEvent touchEvent, Node node) {
        try {
            // 現在タッチしている鍵を取得
            final Node targetNode = touchEvent.getTouchPoint().getPickResult().getIntersectedNode();
            if (targetNode == null) {
                return;
            }

            LOG.log("DBG Pressed Before [{}] getTouchPoint=({}), target=({})", touchEvent.getTouchPoint().getId(), touchEvent.getTouchPoint(), touchEvent.getTarget());
            if (UraKeyboard.class.isInstance(targetNode)) {
                // 鍵オブジェクト上であれば、鍵用Pressedイベントリスナを実行
                this.touchPressedKeyboardListen(touchEvent, UraKeyboard.class.cast(targetNode));
            } else if (this.noteOnKeyCacheMap.size() > 0 && UraPitchBendRibon.class.isInstance(targetNode)) {
                final TouchPoint tp = touchEvent.getTouchPoint();
                LOG.log("DBG Pressed PitchBend touch=[{}]({})", tp.getId(), tp);
                this.pitchBendOn(tp);
            }
        } finally {
            touchEvent.consume();
        }
    }

    /**
     * 鍵向けMovedイベント用リスナ
     * @param touchEvent
     * @param uraKeyboard
     */
    protected void touchMovedKeyboardListen(final TouchEvent touchEvent, final UraKeyboard uraKeyboard) {
        final double touchX = touchEvent.getTouchPoint().getX();
        final double touchY = touchEvent.getTouchPoint().getY();

        synchronized (noteOnKeyCacheMap) {
            // 念のためキャッシュマップで同期
            Integer CURRENT_TOUCH_ID = touchEvent.getTouchPoint().getId();
            final UraKeyboard oldNoteOnKey = noteOnKeyCacheMap.get(CURRENT_TOUCH_ID);
            if (uraKeyboard.equals(oldNoteOnKey)) {
                // 現在選択中の鍵とキャッシュの鍵が一致した場合は何もしない。
                return;
            }

            final boolean IS_HOVER = uraKeyboard.isSceneHover(touchX, touchY);
            if (IS_HOVER) {
//                LOG.log("DBG Move xBefore [{}] getTouchPoint=({}), target=({})", touchEvent.getTouchPoint().getId(), touchEvent.getTouchPoint(), touchEvent.getTarget());
                if (oldNoteOnKey != null) {
                    // 現在もどこかの鍵上でキャッシュに鍵が見つかった場合、キャシュの鍵の音を止める。
                    this.noteOff(touchEvent.getTouchPoint(), oldNoteOnKey);
                }
                // 対象の鍵を鳴らす
                this.noteOn(touchEvent.getTouchPoint(), uraKeyboard);

            } else {
                // 対象座標から外れた場合は音を止める。
                LOG.log("DBG Move&Released The event did not Hover({}), ({}).", touchEvent, uraKeyboard);
                this.noteOff(touchEvent.getTouchPoint(), uraKeyboard);
            }
        }
    }
    /**
     * 鍵向けReleasedイベント用リスナ
     * @param touchEvent
     * @param uraKeyboard
     */
    protected void touchReleasedKeyboardListen(final TouchEvent touchEvent, final UraKeyboard uraKeyboard) {
        synchronized (noteOnKeyCacheMap) {
            // 対象の音を止める
            this.noteOff(touchEvent.getTouchPoint(), uraKeyboard);
        }
    }
    /**
     * 鍵向けPressedイベント用リスナ
     * @param touchEvent
     * @param uraKeyboard
     */
    protected void touchPressedKeyboardListen(final TouchEvent touchEvent, final UraKeyboard uraKeyboard) {
     // 対象の鍵を鳴らす
        this.noteOn(touchEvent.getTouchPoint(), uraKeyboard);
    }
    /**
     * 盤上の全ての鍵をリセットする。
     * @param touchEvent
     */
    protected void resetKeyboardNotes(final TouchEvent touchEvent) {
        if (noteOnKeyCacheMap.size() == 0) {
            final int CURRENT_TOUCH_ID = touchEvent.getTouchPoint().getId();
            for (final UraKeyboard sourceKey : this.keyList) {
                // 鍵
                if (sourceKey.isNoteOn()) {
                    //
                    boolean isOtherKeyNoteOn = false;
                    for (final TouchPoint touchPoint : touchEvent.getTouchPoints()) {
                        if (CURRENT_TOUCH_ID == touchPoint.getId()) {
                            continue;
                        }
                        if (isOtherKeyNoteOn = sourceKey.isSceneHover(touchPoint.getX(), touchPoint.getY())) {
                            break;
                        }
                    }
                    if (!isOtherKeyNoteOn) {
                        this.noteOff(sourceKey);
                    }
                }
            }
        }
    }
    /**
     * 対象の鍵を鳴らしていない場合は、鳴らしてキャッシュに入れる。
     * @param touchPoint
     * @param uraKeyboard 対象の鍵
     */
    protected void noteOn(final TouchPoint touchPoint, final UraKeyboard uraKeyboard) {
        if (!uraKeyboard.isNoteOn()) {
            LOG.log("DBG Pressed The event already Hover({}), ({}).", uraKeyboard, uraKeyboard);
            this.noteOn(uraKeyboard);
        }
        noteOnKeyCacheMap.putIfAbsent(touchPoint.getId(), uraKeyboard);
        LOG.log("DBG Pressed PUT({}), __CACHE__({}).", uraKeyboard, noteOnKeyCacheMap.entrySet());
    }
    /**
     * 対象鍵上に全てのタッチポイントがない場合は音を止める。キャッシュからも削除する。
     * @param touchPoint
     * @param uraKeyboard 対象の鍵
     */
    protected void noteOff(final TouchPoint touchPoint, final UraKeyboard uraKeyboard) {
        final Integer CURRENT_TOUCH_ID = touchPoint.getId();
        boolean isOtherKeyNoteOn = false;
        for (final Map.Entry<Integer, UraKeyboard> noteOnEntry : noteOnKeyCacheMap.entrySet()) {
            if (CURRENT_TOUCH_ID.equals(noteOnEntry.getKey())) {
                continue;
            }
            if (isOtherKeyNoteOn = uraKeyboard.equals(noteOnEntry.getValue())) {
                // 対象以外にも鍵上にタッチポイントが存在した場合は音を鳴らしたままにする。
                LOG.log("DBG XXX oldNoteOnKey in map(oldNoteOnKey=[{}] ({}), target=[{}] ({}))", CURRENT_TOUCH_ID, uraKeyboard, noteOnEntry.getKey(), noteOnEntry.getValue());
                break;
            }
        }
        if (!isOtherKeyNoteOn) {
            // 対象以外、対象の鍵の上にタッチポイントが見つからない場合は音を止める
            LOG.log("DBG Move&Pressed The event did not Hover({}), ({}).", touchPoint, uraKeyboard);
            this.noteOff(uraKeyboard);
        }
        noteOnKeyCacheMap.remove(CURRENT_TOUCH_ID);
        LOG.log("DBG Move DELETE({}), __CACHE__({}).", uraKeyboard, noteOnKeyCacheMap.entrySet());
    }

    /**
     * 対象キーの音を鳴らし、表示を押下中の表示にする。
     * @param uraKeyboard
     */
    protected synchronized void noteOn(UraKeyboard uraKeyboard) {
        uraKeyboard.uraReceiver().noteOn(uraKeyboard.note());
        uraKeyboard.noteOn(true);
        uraKeyboard.noteOnView();
    }

    /**
     * 対象のキーの音を消し、表示を押下していない表示にする。
     * @param uraKeyboard
     */
    protected synchronized void noteOff(UraKeyboard uraKeyboard) {
        uraKeyboard.uraReceiver().noteOff(uraKeyboard.note());
        uraKeyboard.noteOn(false);
        uraKeyboard.noteOffView();
    }
    /**
     * @param touchPoint
     */
    protected void pitchBendOn(final TouchPoint touchPoint) {
        final int CURRENT_TOUCH_ID = touchPoint.getId();
        if (this.currentOnPitchBendTouchId == -1) {
            this.currentOnPitchBendTouchId = CURRENT_TOUCH_ID;
        } else if (CURRENT_TOUCH_ID != this.currentOnPitchBendTouchId) {
            return;
        }
        LOG.log("DBG Pressed pitchBandRibon ID[{}].", CURRENT_TOUCH_ID);
        this.pitchBandRibon.bendModule(touchPoint);
    }
    /**
     * @param CURRENT_TOUCH_ID
     */
    protected void pitchBendOff(final int CURRENT_TOUCH_ID) {
        if (this.currentOnPitchBendTouchId == -1) {
            return;
        }
        LOG.log("DBG Move pitchBandRibon ID[{}].", CURRENT_TOUCH_ID);
        this.pitchBandRibon.clearbendModule();
        this.currentOnPitchBendTouchId = -1;
    }
}
