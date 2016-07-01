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
package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

public final class BreadCrumb {
    @NotNull
    public Integer level;
    @NotNull
    public Integer frames;
    @NotNull
    public String typeName;
    @NotNull
    public Boolean passThrough;
    @NotNull
    public QualifiedCall entry;
    @NotNull
    public ComponentReference component;

    public BreadCrumb(@NotNull final BreadCrumb rhs) {
        this.level = rhs.level;
        this.frames = rhs.frames;
        this.typeName = rhs.typeName;
        this.passThrough = rhs.passThrough;
        this.entry = new QualifiedCall(rhs.entry);
        if (component instanceof ApplicationComponentReference) this.component = new ApplicationComponentReference((ApplicationComponentReference) rhs.component);
        else if (component instanceof DefaultComponentReference) this.component = new DefaultComponentReference((DefaultComponentReference) rhs.component);
        else if (component instanceof LibraryComponentReference) this.component = new LibraryComponentReference((LibraryComponentReference) rhs.component);
        else if (component instanceof VendorComponentReference) this.component = new VendorComponentReference((VendorComponentReference) rhs.component);
    }
}
