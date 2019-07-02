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
grammar Thorium;

CLASS
    : 'class'
    ;
TYPE
    : 'type'
    ;

PRIVATE : 'private' ;
PUBLIC : 'public' ;
NAMESPACE : 'namespace' ;

NUMBER
    : ( [0-9][0-9_]* | [0-9][0-9_]*'.' | '.'[0-9][0-9_]* | [0-9][0-9_]*'.'[0-9][0-9_]* ) ( [eE] [+-]? [0-9][0-9_]* )?
    ;
STRING
    : '"' ( ~[\\"\r\n] | '\\"' )* '"'
    ;
BOOLEAN
    : 'true' | 'false'
    ;
NONE
    : 'none'
    ;

IDENTIFIER
    : [a-zA-Z_][0-9a-zA-Z_]*
    ;

WS
    : [ \t\r\n\u000C]+ -> skip
    ;

root
    : typeDef
    | classDef
    ;

typeDef
    : TYPE IDENTIFIER '{' '}'
    ;

classDef
    : CLASS IDENTIFIER '{' '}'
    ;

typeSpec
    : IDENTIFIER
    ;

methodSignature
    : ( PRIVATE | NAMESPACE | PUBLIC )? IDENTIFIER '(' ( IDENTIFIER ':' typeSpec ( ',' IDENTIFIER ':' typeSpec )* )? ')' ':' typeSpec
    ;