/*
 * Copyright 2024 Daniel Gredler
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
 * Integer utility class.
 */
public final class Integers {

    private Integers() {
        // utility class
    }

    /**
     * Normalizes rotation degree values to 0, 90, 180, or 270 degrees.
     *
     * @param rotation the rotation to normalize
     * @return the normalized rotation (0, 90, 180, or 270)
     * @throws IllegalArgumentException if rotation is not a multiple of 90 degrees
     */
    public static int normalizeRotation(int rotation) {
        int normalized = ((rotation % 360) + 360) % 360;
        if (normalized % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90 degrees");
        }
        return normalized;
    }
}
