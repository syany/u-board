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
 * $Id: UraRectangle.java$
 */
package org.urakeyboard.shape;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


/**
 * uranoplumsオリジナル長方形クラス。<br>
 *
 * @since 2016/12/12
 * @author syany
 */
public class UraRectangle extends Rectangle implements Cloneable {

    /**
     *
     */
    public UraRectangle() {
        super(0, 0, 0, 0);
    }
    /**
     * ローカル座標空間からそのシーンの座標空間に点を変換。
     * @return
     */
    public final Point2D getSceneOffset() {
        return this.localToScene(0.0d, 0.0d);
    }
    /**
     * シーンの座標空間に点にX座標
     * @return
     */
    public double sceneX() {
        return this.x() + getSceneOffset().getX();
    }
    /**
     * シーンの座標空間にY座標変換
     * @return
     */
    public double sceneY() {
        return this.y() + getSceneOffset().getY();
    }

    public UraRectangle x(final double x) {
        this.setX(x);
        return this;
    }

    public final double x() {
        return this.getX();
    }

    public UraRectangle y(final double y) {
        this.setY(y);
        return this;
    }

    public final double y() {
        return this.getY();
    }

    public UraRectangle width(final double width) {
        this.setWidth(width);
        return this;
    }

    public final double width() {
        return this.getWidth();
    }

    public UraRectangle height(final double height) {
        this.setHeight(height);
        return this;
    }

    public final double height() {
        return this.getHeight();
    }

    public UraRectangle paint(final Paint paint) {
        this.setFill(paint);
        return this;
    }

    public final Paint paint() {
        return this.getFill();
    }

    public final List<String> styleClassList() {
        return this.getStyleClass();
    }

    public UraRectangle styleClassClear() {
        this.getStyleClass().clear();
        return this;
    }
    /**
     * このオブジェクトのクローン(Shallow copy)を作成し、返却します。
     *
     * @return シャローコピーをした自オブジェクト
     *  (非 Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public UraRectangle clone() {
        try {
            return (UraRectangle) super.clone();
        } catch (CloneNotSupportedException e) {
            // * 必ず入らない例外
            throw new AssertionError();
        }
    }
}
