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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.sound.midi.Receiver;

import org.urakeyboard.shape.UraBlackKey;
import org.urakeyboard.shape.UraKeyboard;
import org.urakeyboard.shape.UraOnTouchMovedListener;
import org.urakeyboard.shape.UraOnTouchPressedListener;
import org.urakeyboard.shape.UraOnTouchReleasedListener;
import org.urakeyboard.shape.UraWhiteKey;
import org.urakeyboard.sound.UraMidiDevice;
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
    final UraMidiDevice midiDevice = new UraMidiDevice();
    /**  */
    protected final List<UraWhiteKey> whiteKeyList;
    /**  */
    protected final List<UraBlackKey> blackKeyList;
    /** 押下中（発音中）の鍵盤オブジェクトマップ */
    protected final ConcurrentMap<UraKeyboard, Boolean> noteOnKeyMap = newConcurrentHashMap(10, FACTOR.NONE);
    /** X座標を元にした鍵盤オブジェクトの並び替えルール（バイナリ検索用） */
    protected final Comparator<UraKeyboard> xComparator = new Comparator<UraKeyboard>() {
        @Override
        public int compare(UraKeyboard source, UraKeyboard target) {
            double xFirst = source.sceneX();
            double xLast = xFirst + source.width();
            if (xFirst > target.sceneX()) {
                return 1;
            } else if (xLast <= target.sceneX()) {
                return -1;
            }
            return 0;
        }
    };
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

        final Notes notes = new Notes(scene, midiDevice, receiver);
        this.whiteKeyList = notes.getWhiteKeyList();
        this.blackKeyList = notes.getBlackKeyList();
        Collections.sort(whiteKeyList, xComparator);
        Collections.sort(blackKeyList, xComparator);
        this.initLayout(notes);

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
     * @param notes
     */
    protected void initLayout(final Notes notes) {
        for(final Node childNode : this.getChildren()) {
            if ("KeyArea".equals(childNode.getId())) {
                ((HBox) childNode).getChildren().add(notes);
                LOG.log("TRC note point(x:{}, y:{})", notes.getLayoutX(), notes.getLayoutY());
            }
        }

    }

    /**
     * 対象を押下、離した際に実行されるイベントリスナ
     * @see org.urakeyboard.shape.UraOnTouchMovedListener#onTouchMovedListen(javafx.scene.input.TouchEvent, javafx.scene.Node)
     */
    @Override
    public void onTouchMovedListen(TouchEvent touchEvent, Node node) {
        try {
            if (!touchEvent.getEventType().equals(TouchEvent.TOUCH_MOVED)) {
                // MOVEDイベント以外は処理しない
                return;
            }
            double blackKeyBorder = 0.0D;
            if (this.blackKeyList.size() > 0) {
                final UraKeyboard bk = this.blackKeyList.get(0);
                blackKeyBorder = bk.sceneY() + bk.height();
            }
            final double touchX = touchEvent.getTouchPoint().getX();
            final double touchY = touchEvent.getTouchPoint().getY();
            final UraKeyboard seacher = new UraKeyboard(null).x(touchX);
            UraKeyboard uraKeyboard = null;

            if (touchY <= blackKeyBorder) {
                // 黒鍵を最優先で位置情報から対象の鍵を取得する
                int offset = Collections.binarySearch(this.blackKeyList, seacher, xComparator);
                if (offset < 0) {
                    // 黒鍵でなければ白鍵から探す
                    offset = Collections.binarySearch(this.whiteKeyList, seacher, xComparator);
                    if (offset >= 0) {
                        uraKeyboard = this.whiteKeyList.get(offset);
                    } else {
                        // 左端か右端のキーを選択する処理
                        uraKeyboard = getOutOfRangeWhiteKey(touchX);
                    }
                } else {
                    uraKeyboard = this.blackKeyList.get(offset);
                }
            } else {
                // 高さが黒鍵の位置でない場合は、白鍵で取得し直す。
                int offset = Collections.binarySearch(this.whiteKeyList, seacher, xComparator);
                if (offset >= 0) {
                    uraKeyboard = this.whiteKeyList.get(offset);
                } else {
                    // 左端か右端のキーを選択する処理
                    uraKeyboard = getOutOfRangeWhiteKey(touchX);
                }
            }
            if (uraKeyboard == null){
                return;
            }
            final boolean isHover = uraKeyboard.isSceneHover(touchX, touchY);
            final boolean isNoteOn = uraKeyboard.isNoteOn();

            if (!isNoteOn) {
                if (isHover) {
                    LOG.log("DBG Move The event already Hover({}), ({}).", touchEvent, uraKeyboard);
                    this.noteOn(uraKeyboard);
                    noteOnKeyMap.putIfAbsent(uraKeyboard, Boolean.TRUE);
                    /*
                     * 移動直前に押下していた鍵を戻す
                     */
                    if (uraKeyboard.isBlackKey()) {
                        // 黒鍵を押下した場合、黒鍵の隣は必ず白鍵
                        int offset = Collections.binarySearch(this.whiteKeyList, seacher, xComparator);
                        if (offset >= 0) {
                            UraKeyboard targetWhiteKey = this.whiteKeyList.get(offset);
                            if (targetWhiteKey.isNoteOn()) {
                                this.noteOff(targetWhiteKey);
                                noteOnKeyMap.remove(targetWhiteKey);
                            }
                        }
                    } else{
                        // 白鍵を押下した場合、白黒どちらも有りうるので、全押下中の鍵から検索
                        for (Iterator<UraKeyboard> noteOnIte = noteOnKeyMap.keySet().iterator(); noteOnIte.hasNext();) {
                            final UraKeyboard noteOnKey = noteOnIte.next();
                            if (uraKeyboard.equals(noteOnKey)) {
                                // 押下直後の鍵は無視
                                continue;
                            }
                            boolean isNoteOnKeyHover = false;
                            if (noteOnKey.isBlackKey()) {
                                // 黒
                                for (final TouchPoint subTp : touchEvent.getTouchPoints()) {
                                    if (isNoteOnKeyHover = noteOnKey.isSceneHover(subTp.getX(), subTp.getY())) {
                                        // 全てのタッチ中のイベントの内、一つでも座標上にあれば、まだ押下中のまま
                                        break;
                                    }
                                }
                            } else {
                                // 白
                                for (final TouchPoint subTp : touchEvent.getTouchPoints()) {
                                    final UraKeyboard subSeacher = new UraKeyboard(null).x(subTp.getX());
                                    if (subTp.getY() <= blackKeyBorder) {
                                        // 自身が白鍵であるのに対し黒鍵の上のポイントであるならばタッチ範囲ではない（Note off対象）
                                        int subOffset = Collections.binarySearch(this.blackKeyList, subSeacher, xComparator);
                                        if (subOffset > 0) {
                                            LOG.log("TRC This Point hover Black key!!");
                                            continue;
                                        }
                                    }
                                    if (isNoteOnKeyHover = noteOnKey.isSceneHover(subTp.getX(), subTp.getY())) {
                                        // 全てのタッチ中のイベントの内、一つでも座標上にあれば、まだ押下中のまま
                                        break;
                                    }
                                }
                            }
                            if (isNoteOnKeyHover) {
                                continue;
                            }
                            LOG.log("DBG (SUB Point) Move&Released The event did not Hover({}), ({}).", touchEvent, uraKeyboard);
                            this.noteOff(noteOnKey);
                            noteOnIte.remove();
                        }
                    }
                }
            } else {
                if (!isHover) {
                    // 対象座標から外れた場合は音を止め、鍵を戻す
                    LOG.log("DBG Move&Released The event did not Hover({}), ({}).", touchEvent, uraKeyboard);
                    this.noteOff(uraKeyboard);
                    noteOnKeyMap.remove(uraKeyboard);
                }
            }
        } finally {
            touchEvent.consume();
        }
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraOnTouchReleasedListener#onTouchReleasedListen(javafx.scene.input.TouchEvent, javafx.scene.Node)
     */
    @Override
    public void onTouchReleasedListen(TouchEvent touchEvent, Node node) {
        try {
            double blackKeyBorder = 0.0D;
            if (this.blackKeyList.size() > 0) {
                final UraKeyboard bk = this.blackKeyList.get(0);
                blackKeyBorder = bk.sceneY() + bk.height();
            }
            final double touchX = touchEvent.getTouchPoint().getX();
            final double touchY = touchEvent.getTouchPoint().getY();
            final UraKeyboard seacher = new UraKeyboard(null).x(touchX);
            UraKeyboard uraKeyboard = null;
            if (touchY <= blackKeyBorder) {
                // 黒鍵を最優先で位置情報から対象の鍵を取得する
                int offset = Collections.binarySearch(this.blackKeyList, seacher, xComparator);
                if (offset < 0) {
                    // 黒鍵でなければ白鍵から探す
                    offset = Collections.binarySearch(this.whiteKeyList, seacher, xComparator);
                    if (offset >= 0) {
                        uraKeyboard = this.whiteKeyList.get(offset);
                    } else {
                        // 左端か右端のキーを選択する処理
                        uraKeyboard = getOutOfRangeWhiteKey(touchX);
                    }
                } else {
                    uraKeyboard = this.blackKeyList.get(offset);
                }
            } else {
                // 高さが黒鍵の位置でない場合は、白鍵で取得し直す。
                int offset = Collections.binarySearch(this.whiteKeyList, seacher, xComparator);
                if (offset >= 0) {
                    uraKeyboard = this.whiteKeyList.get(offset);
                } else {
                    // 左端か右端のキーを選択する処理
                    uraKeyboard = getOutOfRangeWhiteKey(touchX);
                }
            }
            if (uraKeyboard == null){
                return;
            }
            final boolean isNoteOn = uraKeyboard.isNoteOn();
            if (isNoteOn) {
                LOG.log("DBG Released The event did not Hover({}), ({}).", touchEvent, uraKeyboard);
                this.noteOff(uraKeyboard);
                noteOnKeyMap.remove(uraKeyboard);
            }
        } finally {
            touchEvent.consume();
        }
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraOnTouchPressedListener#onTouchPressedListen(javafx.scene.input.TouchEvent, javafx.scene.Node)
     */
    @Override
    public void onTouchPressedListen(TouchEvent touchEvent, Node node) {
        try {
            double blackKeyBorder = 0.0D;
            if (this.blackKeyList.size() > 0) {
                final UraKeyboard bk = this.blackKeyList.get(0);
                blackKeyBorder = bk.sceneY() + bk.height();
            }
            final double touchX = touchEvent.getTouchPoint().getX();
            final double touchY = touchEvent.getTouchPoint().getY();
            final UraKeyboard seacher = new UraKeyboard(null).x(touchX);
            UraKeyboard uraKeyboard = null;

            if (touchY <= blackKeyBorder) {
                // 黒鍵を最優先で位置情報から対象の鍵を取得する
                int offset = Collections.binarySearch(this.blackKeyList, seacher, xComparator);
                if (offset < 0) {
                    // 黒鍵でなければ白鍵から探す
                    offset = Collections.binarySearch(this.whiteKeyList, seacher, xComparator);
                    if (offset >= 0) {
                        uraKeyboard = this.whiteKeyList.get(offset);
                    } else {
                        // どの位置にも該当しない場合は何もせず終了
                        return;
                    }
                } else {
                    uraKeyboard = this.blackKeyList.get(offset);
                }
            } else {
                // 高さが黒鍵の位置でない場合は、白鍵で取得し直す。
                int offset = Collections.binarySearch(this.whiteKeyList, seacher, xComparator);
                if (offset >= 0) {
                    uraKeyboard = this.whiteKeyList.get(offset);
                } else {
                  // どの位置にも該当しない場合は何もせず終了
                  return;
                }
            }
            final boolean isNoteOn = uraKeyboard.isNoteOn();
            if (!isNoteOn) {
                LOG.log("DBG Pressed The event already Hover({}), ({}).", touchEvent, uraKeyboard);
                this.noteOn(uraKeyboard);
                noteOnKeyMap.putIfAbsent(uraKeyboard, Boolean.TRUE);
            }
        } finally {
            touchEvent.consume();
        }
    }

    /**
     * 対象ポイント（x座標）から端（右端、左端）の白鍵キーオブジェクトを返却する。
     * @param touchX
     * @return
     */
    protected UraKeyboard getOutOfRangeWhiteKey(final double touchX) {
        if (this.whiteKeyList.size() <= 0) {
            // 白鍵がない場合はnullを返却（※あってはいけない）
            return null;
        }
        final UraKeyboard leftWkey = this.whiteKeyList.get(0);
        if (touchX > leftWkey.sceneX()) {
            // 鍵オブジェクト群の中にある場合は右端を返却
            return this.whiteKeyList.get(this.whiteKeyList.size() - 1);
        }
        return leftWkey;
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
}
