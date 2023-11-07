/*
 * Copyright 2023 HyperDevs
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

package com.hyperdevs.poeditor.gradle.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException
import java.util.*

/**
 * Formats dates using [RFC 3339](https://www.ietf.org/rfc/rfc3339.txt), which is
 * formatted like `2015-09-26T18:23:50.250Z`. This adapter is null-safe. To use, add this as
 * an adapter for `Date.class` on your [Moshi.Builder][com.squareup.moshi.Moshi.Builder]:
 *
 * <pre> `Moshi moshi = new Moshi.Builder()
 * .add(Date.class, new Rfc3339DateJsonAdapter())
 * .build();
 * </pre>
 *
 * The adapter is basically a copy-paste of [Rfc3339DateJsonAdapter] but it also accepts empty date strings and
 * default to a given value. The default is a null date.
 */
class PoEditorDateJsonAdapter : JsonAdapter<Date?>() {
    private var defaultValue: Date? = null

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): Date? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull()
        }
        val string = reader.nextString()

        if (string.isBlank()) return defaultValue

        return Iso8601Utils.parse(string)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value == null) {
            writer.nullValue()
        } else {
            val string = Iso8601Utils.format(value)
            writer.value(string)
        }
    }

    /**
     * Adds default value to use if the date is null or empty.
     */
    fun withDefaultValue(date: Date): PoEditorDateJsonAdapter {
        this.defaultValue = date
        return this
    }
}
