/*
 * Copyright 2013-2017 the Uranoplums Foundation and the Others.
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
 * $Id: UraPitchBendRibon.java$
 */
package org.urakeyboard.shape;

import javafx.scene.input.TouchPoint;
import javafx.scene.paint.Paint;

import org.urakeyboard.sound.UraReceiver;
import org.uranoplums.typical.log.UraLoggerFactory;
import org.uranoplums.typical.log.UraStringCodeLog;


/**
 * UraPitchBendRibonクラス。<br>
 *
 * @since 2017/01/20
 * @author syany
 */
public class UraPitchBendRibon extends UraRectangle {
    /**  */
    protected static final UraStringCodeLog LOG = UraLoggerFactory.getUraStringCodeLog();
    /** チャネル、楽器情報を含めたレシーバ */
    final private UraReceiver uraReceiver;
    /** 現在のタッチ位置 */
    private TouchPoint currentTouchPoint = null;
    /** 押下中状態であればtrue */
    private boolean touching = false;

    /**
     *
     */
    public UraPitchBendRibon(final UraReceiver uraReceiver) {
        super();
        this.uraReceiver = uraReceiver;
    }

    /**
     * @return touching を返却します
     */
    public final boolean isTouching() {
        return touching;
    }
    /**
     * @return uraReceiver を返却します
     */
    public final UraReceiver getUraReceiver() {
        return uraReceiver;
    }
    /**
     * @return currentTouchPoint を返却します
     */
    public final TouchPoint getCurrentTouchPoint() {
        return currentTouchPoint;
    }

    /**
     * @param touching touching  を設定します
     */
    public UraPitchBendRibon touching(boolean touching) {
        this.touching = touching;
        return this;
    }
//    /**
//     * @param uraReceiver uraReceiver  を設定します
//     */
//    public UraPitchBendRibon uraReceiver(UraReceiver uraReceiver) {
//        this.uraReceiver = uraReceiver;
//        return this;
//    }
    /**
     * @param currentTouchPoint currentTouchPoint  を設定します
     */
    public UraPitchBendRibon currentTouchPoint(TouchPoint currentTouchPoint) {
        this.currentTouchPoint = currentTouchPoint;
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraPitchBendRibon#x(double)
     */
    @Override
    public UraPitchBendRibon x(double x) {
        super.x(x);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraPitchBendRibon#y(double)
     */
    @Override
    public UraPitchBendRibon y(double y) {
        super.y(y);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraPitchBendRibon#width(double)
     */
    @Override
    public UraPitchBendRibon width(double width) {
        super.width(width);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraPitchBendRibon#height(double)
     */
    @Override
    public UraPitchBendRibon height(double height) {
        super.height(height);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraPitchBendRibon#paint(javafx.scene.paint.Paint)
     */
    @Override
    public UraPitchBendRibon paint(Paint paint) {
        super.paint(paint);
        return this;
    }

    /* (非 Javadoc)
     * @see org.urakeyboard.shape.UraPitchBendRibon#styleClassClear()
     */
    @Override
    public UraPitchBendRibon styleClassClear() {
        super.styleClassClear();
        return this;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public boolean isHover(final double x, final double y) {
        return this.getX() <= x && (this.getX() + this.getWidth()) > x &&
                this.getY() <= y && (this.getY() + this.getHeight()) > y;
    }
    /**
     * @param x
     * @param y
     * @return
     */
    public boolean isSceneHover(final double x, final double y) {
        return this.sceneX() <= x && (this.sceneX() + this.getWidth()) > x &&
                this.sceneY() <= y && (this.sceneY() + this.getHeight()) > y;
    }
    /**
     * @param sourceTouchPoint
     */
    public void bendModule(final TouchPoint sourceTouchPoint) {
        if (currentTouchPoint == null) {
            this.currentTouchPoint = sourceTouchPoint;
            LOG.log("DBG bendModule init add TouchPoint={}", this.currentTouchPoint);
            return;
        }
        int yDiff = (int)(currentTouchPoint.getY() - sourceTouchPoint.getY());
        LOG.log("DBG bendModule pitchBend diff=[{}], cTouch Y={}, sTouch Y={}",
                yDiff, currentTouchPoint.getY(), sourceTouchPoint.getY());
        uraReceiver.pitchBend(yDiff);
        int xDiff = (int) Math.abs(currentTouchPoint.getX() - sourceTouchPoint.getX());
        LOG.log("DBG bendModule modulation diff=[{}], cTouch X={}, sTouch X={}",
                xDiff, currentTouchPoint.getX(), sourceTouchPoint.getX());
        uraReceiver.modulation(xDiff);
    }
    /**
     *
     */
    public void bendModuleClear() {
        LOG.log("DBG bendModule clear TouchPoint={}", this.currentTouchPoint);
        this.currentTouchPoint = null;
        uraReceiver.pitchBend(0);
        uraReceiver.modulation(0);
    }
}
