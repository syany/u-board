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
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

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
public class ProgramItem extends HBox {
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    @FXML
    protected Label chanelNumLabel;
    /** シーケンサプルダウン */
    @FXML
    protected ComboBox<String> programSelect;
    @FXML
    protected Spinner<Integer> programSpinner;
//    protected final List<String> programList;
    /**
     * コンストラクタ。
     * @param scene シーン
     */
    public ProgramItem(Scene scene) {
        if (scene == null) {
            scene = new Scene(this);
        }
        UraLayoutUtils.layoutLoad(this, scene);
        final List<String> programList =  UraApplicationUtils.MESSAGE.getResourceList("programList");
        programSelect.getItems().addAll(programList);
    }

    /**
     * @param actionEvent
     */
    @FXML
    public void programSelectAction(ActionEvent actionEvent) {
        final String programValue = programSelect.getValue();
        List<String> programList = programSelect.getItems();
        int programIdx = programList.indexOf(programValue);
        LOG.log("DBG on select [{}][{}]",programIdx, programValue);
//        programSpinner.setUserData(programIdx);
        int thisValue = programSpinner.getValue();
        programSpinner.increment(programIdx + 1 - thisValue);
    }

    public final ProgramItem setChanelLabel(int chanel) {
        String labelStr = String.format("CH %02d:", chanel);
        chanelNumLabel.setText(labelStr);
        return this;
    }

    /**
     * @return chanelNumLabel を返却します
     */
    public final Label getChanelNumLabel() {
        return chanelNumLabel;
    }


    /**
     * @return programSelect を返却します
     */
    public final ComboBox<String> getProgramSelect() {
        return programSelect;
    }


    /**
     * @return programSpinner を返却します
     */
    public final Spinner<Integer> getProgramSpinner() {
        return programSpinner;
    }
}
