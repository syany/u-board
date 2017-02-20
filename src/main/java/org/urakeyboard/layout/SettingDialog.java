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

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.urakeyboard.shape.UraKeyboard;
import org.urakeyboard.sound.NoteProgram;
import org.urakeyboard.sound.UraMidiDevice;
import org.urakeyboard.sound.UraReceiver;
import org.urakeyboard.util.UraApplicationUtils;
import org.urakeyboard.util.UraLayoutUtils;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * 動的鍵群レイアウトクラス。<br>
 *
 * @since 2016/12/20
 * @author syany
 */
public class SettingDialog extends VBox {
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /** 選択可能なMIDIデバイスリストや、Receiverを開く */
    protected final UraMidiDevice midiDevice;
    /** 鍵リスト */
    protected final List<UraKeyboard> keyList;

    protected final UraReceiver commonUraReceiver;
    @FXML
    private Label synthesizerLabel;
    @FXML
    private Spinner<Integer> fromSpinner;
    @FXML
    private Spinner<Integer> toSpinner;
    @FXML
    private Spinner<Integer> chanelSpinner;
    @FXML
    private Button keySetButton;
    @FXML
    protected VBox noteSetPanels;

    /**
     * コンストラクタ。
     * @param scene シーン
     * @param midiDevice オリジナルMIDIデバイス管理オブジェクト
     * @param receiver レシーバ
     */
    public SettingDialog(Scene scene, UraMidiDevice midiDevice, List<UraKeyboard> keyList, UraReceiver commonUraReceiver) {
        this.midiDevice = midiDevice;
        this.keyList = keyList;
        this.commonUraReceiver = commonUraReceiver;
        if (scene == null) {
            scene = new Scene(this);
        }
        UraLayoutUtils.layoutLoad(this, scene);
        Stage secondStage = new Stage();

        init(scene, secondStage);

        secondStage.showAndWait();
    }

//    protected <Integer> void commitEditorText(Spinner<Integer> spinner) {
//        if (!spinner.isEditable()) return;
//        String text = spinner.getEditor().getText();
//        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
//        if (valueFactory != null) {
//            valueFactory.setValue(spinner.getEditor().getText());
//            StringConverter<T> converter = valueFactory.getConverter();
//            if (converter != null) {
//                T value = converter.fromString(text);
//                valueFactory.setValue(value);
//            }
//        }
//    }

    protected void init(final Scene scene, final Stage secondStage) {

        secondStage.setScene(scene);
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setTitle("キー／チャネル設定");
        secondStage.initStyle(StageStyle.DECORATED);

        // Spinnerの最大/最小
        IntegerSpinnerValueFactory fromSvf = IntegerSpinnerValueFactory.class.cast(fromSpinner.getValueFactory());
        fromSvf.setMin(1);
        fromSvf.setMax(keyList.size());
        fromSpinner.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            String focusedText = fromSpinner.getEditor().getText();
            LOG.log("DBG from Spinner setText[{}]", focusedText);
            fromSvf.setValue(Integer.valueOf(focusedText));
        });

        IntegerSpinnerValueFactory toSvf = IntegerSpinnerValueFactory.class.cast(toSpinner.getValueFactory());
        toSvf.setMin(1);
        toSvf.setMax(keyList.size());
        toSvf.setValue(keyList.size());
        toSpinner.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            String focusedText = toSpinner.getEditor().getText();
            LOG.log("DBG to Spinner setText[{}]", focusedText);
            toSvf.setValue(Integer.valueOf(focusedText));
        });

        final List<String> programList =  UraApplicationUtils.MESSAGE.getResourceList("programList");
        final List<NoteProgram> noteProgramList = this.midiDevice.getNoteProgramList();

        IntegerSpinnerValueFactory chanelSvf = IntegerSpinnerValueFactory.class.cast(chanelSpinner.getValueFactory());
        chanelSvf.setMin(1);
        chanelSvf.setMax(noteProgramList.size());
        chanelSpinner.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            String focusedText = chanelSpinner.getEditor().getText();
            LOG.log("DBG Chanel Spinner setText[{}]", focusedText);
            chanelSvf.setValue(Integer.valueOf(focusedText));
        });

        // チャネル分チャネルリスト
        for (final NoteProgram noteProgram : noteProgramList) {
            final ProgramItem programItem = new ProgramItem(scene);
            final int chanelIdx = noteProgram.getChanel() + 1;
            programItem.setChanelLabel(chanelIdx);
            noteSetPanels.getChildren().add(programItem);
            if (chanelIdx == 10) {
                final ComboBox<String> programSelect = programItem.getProgramSelect();
                final String fixedString = "パーカッション";
                programSelect.getItems().add(fixedString);
                programSelect.getSelectionModel().select(fixedString);
                programSelect.setDisable(true);
                final Spinner<Integer> programSpinner =  programItem.getProgramSpinner();
                IntegerSpinnerValueFactory programSvf = IntegerSpinnerValueFactory.class.cast(programSpinner.getValueFactory());
                programSvf.setValue(0);
                programSpinner.setDisable(true);
            } else {
                final ComboBox<String> programSelect = programItem.getProgramSelect();
                final String firstValue = programList.get(noteProgram.getProgram());
                programSelect.getSelectionModel().select(firstValue);

                final Spinner<Integer> programSpinner =  programItem.getProgramSpinner();
//                int thisValue = programSpinner.getValue();
//                programSpinner.increment(noteProgram.getProgram() + 1 - thisValue);

                IntegerSpinnerValueFactory programSvf = IntegerSpinnerValueFactory.class.cast(programSpinner.getValueFactory());
                programSvf.setValue(noteProgram.getProgram() + 1);
                programSvf.setMin(1);
                programSpinner.focusedProperty().addListener((s, ov, nv) -> {
                    if (nv) return;
                    String focusedText = programSpinner.getEditor().getText();
                    LOG.log("DBG Program Spinner setText[{}]", focusedText);
                    programSvf.setValue(Integer.valueOf(focusedText));
                });
            }
        }
    }

    @FXML
    public void chanelSelectExec(ActionEvent actionEvent) {
        LOG.log("INF chanelSelectExec!! [{}]", actionEvent);
        int fromKey = this.fromSpinner.getValue() - 1;
        int endKey = this.toSpinner.getValue() - 1;
        try {
            List<UraKeyboard> targetList = keyList.subList(fromKey, endKey);
            int targetCanel = this.chanelSpinner.getValue() - 1;
            for (final UraKeyboard targetUraKeyboard : targetList) {
                final NoteProgram np = this.midiDevice.getNoteProgram(targetCanel);
                targetUraKeyboard.uraReceiver().setNoteProgram(np);
                LOG.log("DBG CHANGE CHANEL [{}][{}][{}]", targetCanel, np.getProgram(), targetUraKeyboard);
            }
        } catch(IndexOutOfBoundsException ex) {
            LOG.log("WRN from or to key index Exception [{}]", ex);
        } catch(IllegalArgumentException ex) {
            LOG.log("WRN from or to key index illegal Exception [{}]", ex);
        }
    }
    @FXML
    public void programSelectExec(ActionEvent actionEvent) {
        int idx = 0;
        final List<NoteProgram> noteProgramList = this.midiDevice.getNoteProgramList();
        for (final Node node : noteSetPanels.getChildren()) {
            final ProgramItem programItem = ProgramItem.class.cast(node);
            final Spinner<Integer> programSpinner =  programItem.getProgramSpinner();
            final NoteProgram noteProgram = noteProgramList.get(idx++);
            int spinnerValue = programSpinner.getValue() - 1;
            if (noteProgram.getProgram() != spinnerValue) {
                LOG.log("DBG Change Program {} -> {}", noteProgram.getProgram(), spinnerValue);
                noteProgram.setProgram(spinnerValue);
                commonUraReceiver.setNoteProgram(noteProgram);
            }
        }
    }
}
