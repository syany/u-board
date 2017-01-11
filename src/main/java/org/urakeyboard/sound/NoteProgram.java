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
 * $Id: NoteProgram.java$
 */
package org.urakeyboard.sound;

import org.uranoplums.typical.lang.UraDataObject;


/**
 * NoteProgramクラス。<br>
 *
 * @since 2016/10/07
 * @author syany
 */
public class NoteProgram extends UraDataObject {
    /** チャネル */
    private int chanel;
    /** 音色（プログラム） */
    private int program;
    /** 音量 */
    private int velocity= 120;
    /**
     * @param chanel
     */
    public NoteProgram(int chanel) {
        this(chanel, 0);
    }
    /**
     * @param chanel
     * @param program
     */
    public NoteProgram(int chanel, int program) {
        super();
        this.chanel = chanel;
        this.program = program;
    }

    /**
     * @param chanel
     * @param program
     * @param velocity
     */
    public NoteProgram(int chanel, int program, int velocity) {
        super();
        this.chanel = chanel;
        this.program = program;
        this.velocity = velocity;
    }
    /**
     * @return chanel を返却します
     */
    public final int getChanel() {
        return chanel;
    }

    /**
     * @return program を返却します
     */
    public final int getProgram() {
        return program;
    }

    /**
     * @return velocity を返却します
     */
    public final int getVelocity() {
        return velocity;
    }

    /**
     * @param program program  を設定します
     */
    public final NoteProgram setProgram(int program) {
        this.program = program;
        return this;
    }

    /**
     * @param velocity velocity  を設定します
     */
    public final void setVelocity(int velocity) {
        this.velocity = velocity;
    }
}
