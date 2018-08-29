/*
 * Copyright 2018 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.base.form;

import java.awt.*;

public class FormColors {
    public final Color[] normalBorder;
    public final Color[] focusBorder;
    public final Color[] errorBorder;
    public final Color[] background;
    public final Color[] text;
    public final Color[] error;
    public final Color[] fieldName;
    public final Color[] placeholder;

    public FormColors(Color[] normalBorder,
                      Color[] focusBorder,
                      Color[] errorBorder,
                      Color[] background,
                      Color[] text,
                      Color[] error,
                      Color[] fieldName,
                      Color[] placeholder) {
        this.normalBorder = normalBorder;
        this.focusBorder = focusBorder;
        this.errorBorder = errorBorder;
        this.background = background;
        this.text = text;
        this.error = error;
        this.fieldName = fieldName;
        this.placeholder = placeholder;
    }
}
