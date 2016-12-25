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

import org.urakeyboard.shape.UraBlackKey;
import org.urakeyboard.shape.UraKeyboard;
import org.urakeyboard.shape.UraWhiteKey;
import org.urakeyboard.util.UraLayoutUtils;


/**
 * 動的キーレイアウトクラス。<br>
 *
 * @since 2016/12/20
 * @author syany
 */
public class Notes extends AnchorPane {

    protected final List<UraKeyboard> keyboardList = newArrayList();
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

    protected void init(final List<UraWhiteKey> whiteKeyList, final List<UraBlackKey> blackKeyList) {
        // add child keyboard shape
        ObservableList<Node> nodes = this.getChildren();
        for (final UraWhiteKey wKey : whiteKeyList) {
            wKey.parentNode(this);
            nodes.add(wKey);
        }

        for (final UraBlackKey bKey : blackKeyList) {
            bKey.parentNode(this);
            nodes.add(bKey);
        }
    }
}
