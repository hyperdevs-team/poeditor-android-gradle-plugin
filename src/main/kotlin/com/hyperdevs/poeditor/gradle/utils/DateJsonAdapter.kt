/*
 * Copyright 2021 HyperDevs
 *
 * Copyright 2020 BQ
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

package com.hyperdevs.poeditor.gradle.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Basic JSON adapter that converts from PoEditor date format to standard Date objects.
 */
class DateJsonAdapter : JsonAdapter<Date>() {
    companion object {
        private const val DATE_FORMAT = "YYYY-MM-dd HH:mm:ss"
        private val sdf = SimpleDateFormat(DATE_FORMAT)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): Date? {
        val string = reader.nextString()
        if (string == null || string.isBlank()) return null
        return sdf.parse(string)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Date?) {
        val string = sdf.format(value)
        writer.value(string)
    }
}