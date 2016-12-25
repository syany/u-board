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
 * $Id: UraLayoutUtils.java$
 */
package org.urakeyboard.util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

import org.uranoplums.typical.exception.UraIORuntimeException;
import org.uranoplums.typical.util.UraClassUtils;
import org.uranoplums.typical.util.UraStringUtils;
import org.uranoplums.typical.util.UraUtils;


/**
 * UraLayoutUtilsクラス。<br>
 *
 * @since 2016/12/20
 * @author syany
 */
public class UraLayoutUtils extends UraUtils {

    /**
     * レイアウトをロードする。
     * @param node
     * @param scene
     */
    public static final void layoutLoad(final Node node, final Scene scene) {
        final String simpleClassName = node.getClass().getSimpleName();
        // fxmlではエディターが認識してくれないので都合上xmlにしてる
        final URL fxmlURL = getURL(node, simpleClassName + ".xml");
        final URL cssURL = getURL(node, simpleClassName + ".css");
        final FXMLLoader loader = new FXMLLoader(fxmlURL);
        loader.setRoot(node);
        loader.setController(node);
        try {
            loader.load();
            if (cssURL != null) {
                node.getScene().getStylesheets().add(cssURL.toExternalForm());
            }
        } catch (IOException e) {
            e.printStackTrace();
            String message = String.format("fxmlファイルのロードに失敗しました。 %s.xml", simpleClassName);
            throw new UraIORuntimeException(message, e);
        }
    }

    /**
     * 対象のリソースのURLを返却する。<br>
     * アプリケーション直下になければ、対象NODEのあるパッケージのURLパスで探します。
     * @param node
     * @param resourceName
     * @return リソースのURL
     */
    public static final URL getURL(final Node node, final String resourceName) {
        final Class<?> klass = node.getClass();
        final ClassLoader classLoader = UraClassUtils.getCurrentClassLoader(klass);
        // アプリケーションと同位置にある場合はそれを返す
        URL resourceURL = classLoader.getResource(resourceName);
        if (resourceURL == null) {
            // ない場合、同一パッケージ配下にあるかを検索
            final String packagePath = UraStringUtils.replaceChars(klass.getPackage().getName(), '.', '/');
            resourceURL = classLoader.getResource(packagePath + UraStringUtils.SLASH + resourceName);
        }
        return resourceURL;
    }
    /**
     *
     */
    private UraLayoutUtils() {
        super();
    }
}
