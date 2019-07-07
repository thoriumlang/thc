/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thoriumlang.compiler;

import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.Root;
import org.thoriumlang.compiler.output.th.Walker;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        new Compiler().compile();
    }

    private void compile() throws IOException {
        Root r = new AST(Compiler.class.getResourceAsStream("/org/thoriumlang/compiler/examples/type.th")).root();
        System.out.println(r);

        Walker walker = new Walker(r);
        System.out.println(walker.walk());
    }
}
