/*
   Copyright 2013 Paul LeBeau, Cave Rock Software Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package reactive.ui.androidsvg.utils;

/*
 * Thrown by the CSS parser if a problem is found while parsing a CSS file.
 */

public class CSSParseException extends Exception
{
   public CSSParseException(String msg)
   {
      super(msg);
   }

   public CSSParseException(String msg, Exception cause)
   {
      super(msg, cause);
   }
}
