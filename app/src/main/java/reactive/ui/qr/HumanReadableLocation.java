/*
 * Copyright 2015 Daniel Gredler
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

package reactive.ui.qr;

/**
 * The location of a bar code's human-readable text.
 */
public enum HumanReadableLocation {

    /** Display the human-readable text below the bar code. */
    BOTTOM,

    /** Display the human-readable text above the bar code. */
    TOP,

    /** Do not display the human-readable text. */
    NONE
}
