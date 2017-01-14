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
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.urakeyboard.layout.KeyboardMain;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * UraKeyboardメインアプリケーションクラス。<br>
 *
 * @since 2016/12/19
 * @author syany
 */
public final class UraKeyboardApplication extends Application {
    /** ロガー */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /* (非 Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception {
        // レイアウト設定
        Scene scene = null;
        KeyboardMain keyboardMain = new KeyboardMain(scene);

        // 対象シーンの表示
        stage.setScene(keyboardMain.getScene());
//        setFilter(stage);
        stage.show();
    }

    /**
     * デバッグ用、イベント出力
     * @param stage
     */
    protected void setFilter(Stage stage) {
        stage.addEventFilter(EventType.ROOT, e -> System.out.println(e));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
