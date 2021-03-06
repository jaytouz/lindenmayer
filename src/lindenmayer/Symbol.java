/*
 * Copyright 2020 Mikl&oacute;s Cs&#369;r&ouml;s.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @title Devoir 1 - IFT2015 - Hiv2020
 * @author Louis-Vincent Poellhuber (p1234802 - 20161115)
 * @author Jérémie Tousignant (p1038501 - TOUJ14059307)
 */
package lindenmayer;

/**
 * Symbol in an L-system's alphabet.
 *
 * @author Mikl&oacute;s Cs&#369;r&ouml;s
 */
public class Symbol {
    char sym;
    String action;

    public Symbol(char sym) { this.sym = sym;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "sym=" + sym +
                ", action='" + action + '\'' +
                '}';
    }
}