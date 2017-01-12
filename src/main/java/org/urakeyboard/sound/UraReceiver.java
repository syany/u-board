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
 * $Id: UraReceiver.java$
 */
package org.urakeyboard.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.uranoplums.typical.lang.UraSerialDataObject;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * オリジナルReceiverクラス。<br>
 * メッセージ送信まで担当する。
 *
 * @since 2016/12/22
 * @author syany
 */
public class UraReceiver extends UraSerialDataObject {
    /**  */
    private static final long serialVersionUID = -2891559146843369060L;
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /**  */
    protected final ShortMessage shortMessage = new ShortMessage();
    /**  */
    protected Receiver receiver;
    /**  */
    protected NoteProgram noteProgram;
    /**
     *
     */
    public UraReceiver(final Receiver receiver, final NoteProgram noteProgram) {
        this.receiver = receiver;
        this.noteProgram = noteProgram;
    }

    /**
     * @param receiver receiver  を設定します
     */
    public final UraReceiver setReceiver(Receiver receiver) {
        this.receiver = receiver;
        return this;
    }

    /**
     * @param noteProgram noteProgram  を設定します
     */
    public final UraReceiver setNoteProgram(NoteProgram noteProgram) {
        this.noteProgram = noteProgram;
        return this;
    }
    /**
     * プログラム（音色）設定
     */
    public void changeProgram() {
        try {
            this.shortMessage.setMessage(
                    ShortMessage.PROGRAM_CHANGE,
                    this.noteProgram.getChanel(),
                    this.noteProgram.getProgram(),
                    0);
            this.receiver.send(this.shortMessage, 0);
        } catch (InvalidMidiDataException e) {
            LOG.log("ERR 意図しないエラーが発生しました。", e);
        }
    }
    /**
     * note on
     * @param note
     */
    public void noteOn(final int note) {
        try {
            this.shortMessage.setMessage(
                    ShortMessage.NOTE_ON,
                    this.noteProgram.getChanel(),
                    note,
                    this.noteProgram.getVelocity()
                    );
            this.receiver.send(this.shortMessage, 0);
        } catch (InvalidMidiDataException e) {
            LOG.log("ERR 意図しないエラーが発生しました。", e);
        }
    }
    /**
     * note off
     * @param note
     */
    public void noteOff(final int note) {
        try {
            this.shortMessage.setMessage(
                    ShortMessage.NOTE_OFF,
                    this.noteProgram.getChanel(),
                    note,
                    this.noteProgram.getVelocity()
                    );
            this.receiver.send(this.shortMessage, 0);
        } catch (InvalidMidiDataException e) {
            LOG.log("ERR 意図しないエラーが発生しました。", e);
        }
    }
}
