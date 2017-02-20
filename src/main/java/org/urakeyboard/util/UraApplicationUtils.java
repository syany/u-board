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
 * $Id: UraApplicationUtils.java$
 */
package org.urakeyboard.util;

import org.uranoplums.typical.resource.UraJSONResource;
import org.uranoplums.typical.util.UraUtils;


/**
 * アプリケーション向け外部設定操作ユーティリティ。<br>
 *
 * @since 2016/12/20
 * @author syany
 */
public final class UraApplicationUtils extends UraUtils {

    public static final UraJSONResource APP_RESOURCE = new UraJSONResource(UraApplicationUtils.class.getPackage().getName() + ".application");
    public static final UraJSONResource MESSAGE = new UraJSONResource(UraApplicationUtils.class.getPackage().getName() + ".message");
    /**
     *
     */
    private UraApplicationUtils() {
        super();
    }

}
