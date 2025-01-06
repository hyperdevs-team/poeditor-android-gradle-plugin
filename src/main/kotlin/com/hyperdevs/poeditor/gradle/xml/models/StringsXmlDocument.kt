/*
 * Copyright 2025 HyperDevs
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

package com.hyperdevs.poeditor.gradle.xml.models

/**
 * Class that models a strings.xml file.
 *
 * It contains a series of resources (string resources, plurals resources and string array resources) that follow the
 * schema defined at https://developer.android.com/guide/topics/resources/string-resource
 */
data class StringsXmlDocument(
    val resources: List<StringsXmlResource>
) {
    /**
     * List of string resources that the document contains.
     */
    val strings: List<StringsXmlResource.StringElement> by lazy {
        resources.filterIsInstance<StringsXmlResource.StringElement>()
    }

    /**
     * List of plural resources that the document contains.
     */
    val plurals: List<StringsXmlResource.PluralsElement> by lazy {
        resources.filterIsInstance<StringsXmlResource.PluralsElement>()
    }

    /**
     * List of string array resources that the document contains.
     */
    val stringArrays: List<StringsXmlResource.StringArrayElement> by lazy {
        resources.filterIsInstance<StringsXmlResource.StringArrayElement>()
    }

    /**
     * Filters the resources in the document by a given regex string, returning a new document with the filtered
     * resources.
     */
    fun filter(regex: String) = copy(
        resources = resources.filter { Regex(regex).matches(it.name) }
    )

    /**
     * Filters out the resources in the document by a given regex string, returning a new document without the filtered
     * resources.
     */
    fun filterNot(regex: String) = copy(
        resources = resources.filterNot { Regex(regex).matches(it.name) }
    )

    /**
     * Maps the resources in the document by a given predicate, returning a new document with the updated resources.
     */
    fun map(fn: (StringsXmlResource) -> StringsXmlResource) = copy(
        resources = resources.map(fn)
    )

    /**
     * Maps the resources in the document by a given predicate, returning a new document with the updated resources.
     * It filters out the null values returned by the predicate.
     */
    fun mapNotNull(fn: (StringsXmlResource) -> StringsXmlResource?) = copy(
        resources = resources.mapNotNull(fn)
    )
}

/**
 * Class that provides string resources to an Android app.
 */
sealed class StringsXmlResource(open val name: String, open val comments: List<String>) {

    /**
     * Class that models a string resource.
     */
    data class StringElement(
        override val name: String,
        val value: String,
        override val comments: List<String> = emptyList(),
        val translatable: Boolean = true
    ) : StringsXmlResource(name, comments)

    /**
     * Class that models a plurals resource.
     */
    data class PluralsElement(
        override val name: String,
        val items: List<Item> = emptyList(),
        override val comments: List<String> = emptyList()
    ) : StringsXmlResource(name, comments) {

        /**
         * Class that models a plural item.
         */
        data class Item(
            val quantity: Quantity,
            val value: String,
            val translatable: Boolean = true
        )

        /**
         * Enum class that represents the quantity type of a plural item.
         *
         * You can check the Android documentation for more information about the quantity types:
         * https://developer.android.com/guide/topics/resources/string-resource#Plurals
         */
        enum class Quantity(val value: String) {
            ZERO("zero"),
            ONE("one"),
            TWO("two"),
            FEW("few"),
            MANY("many"),
            OTHER("other");

            companion object {
                /**
                 * Returns a quantity type from a string.
                 */
                fun from(s: String): Quantity = when (s) {
                    ZERO.value -> ZERO
                    ONE.value -> ONE
                    TWO.value -> TWO
                    FEW.value -> FEW
                    MANY.value -> MANY
                    OTHER.value -> OTHER
                    else -> throw RuntimeException("Invalid quantity: $s")
                }
            }
        }
    }

    /**
     * Class that models a string array resource.
     */
    data class StringArrayElement(
        override val name: String,
        val items: List<Item> = emptyList(),
        override val comments: List<String> = emptyList()
    ) : StringsXmlResource(name, comments) {

        /**
         * Class that models a string array item.
         */
        data class Item(
            val value: String,
            val translatable: Boolean = true
        )
    }

    /**
     * Updates the name of the resource, returning a new resource with the updated name.
     */
    fun updateName(newName: String) = when (this) {
        is StringElement -> {
            StringElement(
                name = newName,
                value = this.value,
                comments = this.comments,
                translatable = this.translatable
            )
        }

        is PluralsElement -> {
            PluralsElement(
                name = newName,
                items = this.items,
                comments = this.comments
            )
        }

        is StringArrayElement -> {
            StringArrayElement(
                name = newName,
                items = this.items,
                comments = this.comments
            )
        }
    }

    /**
     * Updates the translatable attribute of the resource, returning a new resource with the updated
     * translatable attribute.
     */
    fun updateTranslatable(translatable: Boolean) = when (this) {
        is StringElement -> {
            this.copy(
                translatable = translatable
            )
        }

        is PluralsElement -> {
            this.copy(
                items = this.items.map { item ->
                    item.copy(
                        translatable = translatable
                    )
                }
            )
        }

        is StringArrayElement -> {
            this.copy(
                items = this.items.map { item ->
                    item.copy(
                        translatable = translatable
                    )
                }
            )
        }
    }

    /**
     * Updates the values of the resource, returning a new resource with the updated values.
     */
    fun updateValues(fn: (String) -> String) = when (this) {
        is StringElement -> {
            this.copy(
                value = fn(this.value)
            )
        }

        is PluralsElement -> {
            this.copy(
                items = this.items.map { item ->
                    item.copy(
                        value = fn(item.value)
                    )
                }
            )
        }

        is StringArrayElement -> {
            this.copy(
                items = this.items.map { item ->
                    item.copy(
                        value = fn(item.value)
                    )
                }
            )
        }
    }
}
