/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.search.api.entities;

import org.jetbrains.annotations.NotNull;

public final class BreadCrumb {
    @NotNull
    private Integer level;
    @NotNull
    private Integer frames;
    @NotNull
    private String typeName;
    @NotNull
    private Boolean passThrough;
    @NotNull
    private QualifiedCall entry;
    @NotNull
    private ComponentReference component;

    @NotNull
    public Integer getLevel() {
        return level;
    }

    @NotNull
    public Integer getFrames() {
        return frames;
    }

    @NotNull
    public String getTypeName() {
        return typeName;
    }

    @NotNull
    public Boolean getPassThrough() {
        return passThrough;
    }

    @NotNull
    public QualifiedCall getEntry() {
        return entry;
    }

    @NotNull
    public ComponentReference getComponent() {
        return component;
    }
}