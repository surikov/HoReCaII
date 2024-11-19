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

import java.io.IOException;

import reactive.ui.qr.Symbol;

/**
 * Renders symbols to some output format.
 */
public interface SymbolRenderer {

    /**
     * Renders the specified symbology.
     *
     * @param symbol the symbology to render
     * @throws IOException if there is an I/O error
     */
    void render(Symbol symbol) throws IOException;

}
