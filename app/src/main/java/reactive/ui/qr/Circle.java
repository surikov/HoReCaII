/*
 * Copyright 2014-2018 Daniel Gredler
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

import java.util.Objects;

/**
 * A circle.
 *
 * @author Daniel Gredler
 */
public final class Circle {

    public final double centreX;
    public final double centreY;
    public final double radius;

    public Circle(double centreX, double centreY, double radius) {
        this.centreX = centreX;
        this.centreY = centreY;
        this.radius = radius;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Circle)) {
            return false;
        }
        Circle c = (Circle) other;
        return centreX == c.centreX && centreY == c.centreY && radius == c.radius;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(centreX, centreY, radius);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Circle[centreX=" + centreX + ", centreY=" + centreY + ", radius=" + radius + "]";
    }
}
