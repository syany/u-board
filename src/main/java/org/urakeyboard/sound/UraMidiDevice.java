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
 * $Id: UraMidiDevice.java$
 */
package org.urakeyboard.sound;

import static org.uranoplums.typical.collection.factory.UraListFactory.*;

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.urakeyboard.util.UraApplicationUtils;
import org.uranoplums.typical.lang.UraSerialDataObject;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * オリジナルMidiDeviceクラス。<br>
 * MIDIデバイス全般、チャネル数やチャネルごとのオリジナルReceiverの作成などを担当する。
 *
 * @since 2016/12/19
 * @author syany
 */
public class UraMidiDevice extends UraSerialDataObject {
    /**  */
    private static final long serialVersionUID = 2653103677898079055L;
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /** ドラムパート用チャネル（MIDI仕様） */
    protected static final int CHANEL_DLUM = 9;
    /**  */
    protected static final int DEFAULT_CHANRL_NUMBER;
    /**  */
    protected MidiDevice defaultSynthesizer = null;
    /**  */
    protected MidiDevice defaultSequencer = null;
    /**  */
    protected final List<MidiDevice> synthesizerList = newArrayList();
    /**  */
    protected final List<MidiDevice> sequencerList = newArrayList();

    protected final ShortMessage shortMessage = new ShortMessage();

    protected final List<NoteProgram> noteProgramList = newArrayList(DEFAULT_CHANRL_NUMBER);

    static {
        int chanelNumber = 10;
        try {
            chanelNumber = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceValue("defaultChanelNumber"));
        } catch (Exception e) {
            LOG.log("ERR デフォルトチャネル数（defaultChanelNumber）の取得に失敗。", e);
            chanelNumber = 10;
        } finally {
            DEFAULT_CHANRL_NUMBER = chanelNumber;
        }
    }
    /**
     * デフォルトコンストラクタ。
     */
    public UraMidiDevice() {
        super();
        refleshDeviceList();
        // init programs.
        int defaultProgram = 0;
        List<String> defaultProgramList = null;
        try {
            defaultProgram = Integer.valueOf(UraApplicationUtils.APP_RESOURCE.getResourceValue("defaultProgram"));
        } catch (Exception e) {
            LOG.log("ERR デフォルト楽器ID（defaultProgram）の取得に失敗。", e);
        }
        try {
            defaultProgramList = UraApplicationUtils.APP_RESOURCE.getResourceList("defaultProgramList");
        } catch (Exception e) {
            LOG.log("WRN デフォルト楽器IDリスト（defaultProgramList）の取得に失敗。", e);
        }
        if (defaultProgramList != null) {
            for (int idx = 0; idx < DEFAULT_CHANRL_NUMBER && idx < defaultProgramList.size(); idx++) {
                int program = Integer.valueOf(defaultProgramList.get(idx));
                final NoteProgram noteProgram = new NoteProgram(idx, program);
                LOG.log("DBG IDX[{}], default[{}]", idx, program);
                noteProgramList.add(noteProgram);
            }
        } else {
            for (int idx = 0; idx < DEFAULT_CHANRL_NUMBER; idx++) {
                final NoteProgram noteProgram = new NoteProgram(idx, defaultProgram);
                noteProgramList.add(noteProgram);
            }
        }
        for (final NoteProgram np : this.noteProgramList) {
            LOG.log("DBG SET Chanel[{}]default[{}]", np.getChanel(), np.getProgram());
        }
    }
    /**
     *
     */
    protected void refleshDeviceList() {
        synthesizerList.clear();
        sequencerList.clear();
        for (final MidiDevice.Info deviceInfo: MidiSystem.getMidiDeviceInfo()) {
            try {
                final MidiDevice midiDevice = MidiSystem.getMidiDevice(deviceInfo);
                LOG.log("DBG MidiSystem.getMidiDevice [{}] ({}), vendor={}", midiDevice.getDeviceInfo().getName()
                        ,midiDevice.getDeviceInfo().getDescription(), midiDevice.getDeviceInfo().getVendor());

                if (midiDevice instanceof Synthesizer) {
                    synthesizerList.add(midiDevice);
//                    Synthesizer syn = Synthesizer.class.cast(midiDevice);
//                    syn.getDeviceInfo().getName();
                }
                if (midiDevice instanceof Sequencer) {
                    sequencerList.add(midiDevice);
                }
//                if (midiDevice instanceof MidiOutDevice) {
//                    synthesizerList.add(midiDevice);
//
//                }
//                if (midiDevice instanceof MidiInDevice) {
//                    sequencerList.add(midiDevice);
//
//                }
            } catch (MidiUnavailableException e) {
                LOG.log("ERR 存在しないMIDIデバイスを選択しました。", e);
            }
        }
        if (LOG.isDebugEnabled()) {
            int idx = 0;
            for (final MidiDevice synthesizer : synthesizerList) {
                LOG.log("DBG SynthesizerList [{}] {} ({}), vendor={}", idx, synthesizer.getDeviceInfo().getName()
                        ,synthesizer.getDeviceInfo().getDescription(), synthesizer.getDeviceInfo().getVendor());
                idx++;
            }
            idx = 0;
            for (final MidiDevice sequencer : sequencerList) {
                LOG.log("DBG SequencerList [{}] {} ({}), vendor={}", idx, sequencer.getDeviceInfo().getName()
                        ,sequencer.getDeviceInfo().getDescription(), sequencer.getDeviceInfo().getVendor());
                idx++;
            }
        }
    }

    /**
     * @param sequencerIdx
     */
    protected void setDefaultSequencer(final int sequencerIdx) {
        if (sequencerIdx < 0) {
            try {
                this.defaultSequencer = MidiSystem.getSequencer();
            } catch (MidiUnavailableException e) {
                LOG.log("ERR 存在しないMIDIシーケンサを選択しました。", e);
            }
            return;
        }
        if (this.sequencerList.size() > 0) {
            this.defaultSequencer = this.sequencerList.get(sequencerIdx);
        }
        if (this.defaultSequencer == null) {
            try {
                this.defaultSequencer = MidiSystem.getSequencer();
            } catch (MidiUnavailableException e) {
                LOG.log("ERR 存在しないMIDIシーケンサを選択しました。", e);
            }
        }
    }

    /**
     * @param synthesizerIdx
     */
    protected void setDefaultSynthesizer(final int synthesizerIdx) {
        if (synthesizerIdx < 0) {
            try {
                this.defaultSynthesizer = MidiSystem.getSynthesizer();
            } catch (MidiUnavailableException e) {
                LOG.log("WRN 存在しないMIDIシンセサイザを選択しました。", e);
                this.defaultSynthesizer = null;
            }
        }
        if (this.sequencerList.size() > 0) {
            this.defaultSynthesizer = this.synthesizerList.get(synthesizerIdx);
        }
        if (this.defaultSynthesizer == null) {
            try {
                this.defaultSynthesizer = MidiSystem.getSynthesizer();
            } catch (MidiUnavailableException e) {
                LOG.log("ERR 存在しないMIDIシンセサイザを選択しました。", e);
                this.defaultSynthesizer = null;
            }
        }
    }
    /**
     * @param synthesizerIdx
     * @param sequencerIdx
     * @return
     */
    public Receiver openReciver(final int synthesizerIdx, final int sequencerIdx) {
        // 負値シンセサイザを指定した場合は、デフォルトレシーバを返却する。
        if (synthesizerIdx < 0) {
            try {
                return MidiSystem.getReceiver();
            } catch (MidiUnavailableException e) {
                LOG.log("WRN 存在しないMIDIデバイスを選択しました。", e);
                return null;
            }
        }

        setDefaultSynthesizer(synthesizerIdx);
        if (this.defaultSynthesizer == null) {
            return null;
        }

        setDefaultSequencer(sequencerIdx);
        if (this.defaultSequencer == null) {
            return null;
        }
        return openReciver();
    }
    /**
     * @return
     */
    public Receiver openReciver() {
        try {
            Transmitter transmitter = this.defaultSequencer.getTransmitter();
            Receiver receiver = this.defaultSynthesizer.getReceiver();
            LOG.log("DBG Open Receiver Synthesizer={}, Sequencer={}, Transmitter={}, Receiver={}",
                    this.defaultSynthesizer, this.defaultSequencer,
                    transmitter, receiver);
            if (!this.defaultSynthesizer.isOpen()) {
                LOG.log("DBG Open Receiver");
                this.defaultSynthesizer.open();
            }
            transmitter.setReceiver(receiver);
            return receiver;
        } catch (MidiUnavailableException e) {
            LOG.log("ERR 不正なMIDIレシーバを設定しました。", e);
            return null;
        }
    }

    /**
     * @return shortMessage を返却します
     */
    public final ShortMessage getShortMessage() {
        return shortMessage;
    }

    /**
     * @param receiver
     * @param chanelIndex
     * @return
     */
    public UraReceiver createUraReceiver(final Receiver receiver, final int chanelIndex) {
        final NoteProgram noteProgram = this.noteProgramList.get(chanelIndex);
        UraReceiver uraReceiver = new UraReceiver(receiver, noteProgram);
        uraReceiver.setChanelNumber(this.noteProgramList.size());
        return uraReceiver;
    }

    /**
     * @return synthesizerList を返却します
     */
    public final List<MidiDevice> getSynthesizerList() {
        return synthesizerList;
    }

    /**
     * @return sequencerList を返却します
     */
    public final List<MidiDevice> getSequencerList() {
        return sequencerList;
    }

    /**
     * @param defaultSynthesizer defaultSynthesizer  を設定します
     */
    public final void setDefaultSynthesizer(MidiDevice defaultSynthesizer) {
        this.defaultSynthesizer = defaultSynthesizer;
    }

    /**
     * @param defaultSequencer defaultSequencer  を設定します
     */
    public final void setDefaultSequencer(MidiDevice defaultSequencer) {
        this.defaultSequencer = defaultSequencer;
    }

    /**
     * @return defaultSynthesizer を返却します
     */
    public final MidiDevice getDefaultSynthesizer() {
        return defaultSynthesizer;
    }

    /**
     * @return defaultSequencer を返却します
     */
    public final MidiDevice getDefaultSequencer() {
        return defaultSequencer;
    }
    public final NoteProgram getNoteProgram(int chanelIndex) {
        for (final NoteProgram np : this.noteProgramList) {
            LOG.log("DBG SET Chanel[{}]default[{}]", np.getChanel(), np.getProgram());
        }
        return this.noteProgramList.get(chanelIndex);
    }
}
