/*
 * Copyright (C) 2017 The ORT Project Authors (see <https://github.com/oss-review-toolkit/ort/blob/main/NOTICE>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.utils.common

import org.springframework.util.AntPathMatcher

/**
 * A class to determine whether paths are matched by globs. It can either be used via its static
 * [match][FileMatcher.Companion.matches] functions, or (in case different paths have to be matched against the same
 * patterns in the same way over and over again) by instantiating it with fixed [patterns], optionally ignoring case.
 */
class FileMatcher(
    /**
     * The collection of [glob patterns][1] to consider for matching.
     *
     * [1]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
     */
    val patterns: Collection<String>,

    /**
     * Toggle the case-sensitivity of the matching.
     */
    ignoreCase: Boolean = false
) {
    companion object {
        private val matchCaseInsensitive = AntPathMatcher().apply { setCaseSensitive(false) }::match
        private val matchCaseSensitive = AntPathMatcher().apply { setCaseSensitive(true) }::match

        /**
         * Return true if [path] is matched by [pattern], false otherwise. The [path] must use '/' as separators, if it
         * contains any.
         */
        fun matches(pattern: String, path: String, ignoreCase: Boolean = false) =
            if (ignoreCase) matchCaseInsensitive(pattern, path) else matchCaseSensitive(pattern, path)

        /**
         * Return true if [path] is matched by any of [patterns], false otherwise. The [path] must use '/' as
         * separators, if it contains any.
         */
        fun matches(patterns: Collection<String>, path: String, ignoreCase: Boolean = false): Boolean {
            // Only decide once for all patterns which function to call.
            val match = if (ignoreCase) matchCaseInsensitive else matchCaseSensitive

            return patterns.any { pattern -> match(pattern, path) }
        }
    }

    constructor(vararg patterns: String, ignoreCase: Boolean = false) : this(patterns.asList(), ignoreCase)

    private val match = if (ignoreCase) matchCaseInsensitive else matchCaseSensitive

    /**
     * Return true if [path] is matched by any of [patterns], false otherwise. The [path] must use '/' as separators,
     * if it contains any.
     */
    fun matches(path: String): Boolean = patterns.any { pattern -> match(pattern, path) }
}
