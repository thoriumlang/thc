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

USE : 'use' ;

PRIVATE : 'private' ;
PUBLIC : 'public' ;
NAMESPACE : 'namespace' ;

VAL : 'val' ;
VAR : 'var' ;

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

fqIdentifier
    : IDENTIFIER ( '.' IDENTIFIER )*
    ;
use
    : USE fqIdentifier ';'
    | USE fqIdentifier '.' '*' ';'
    | USE fqIdentifier ':' IDENTIFIER ';'
    | USE fqIdentifier '.' '{' fqIdentifier ( ':' IDENTIFIER )? ( ',' fqIdentifier ( ':' IDENTIFIER )? )? '}' ';'
    ;

typeDef
    : TYPE IDENTIFIER '{' ( methodSignature ';' )* '}'
    ;

classDef
    : CLASS IDENTIFIER '{' '}'
    ;

typeSpec
    : IDENTIFIER
    | '(' ( IDENTIFIER | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')'
    | typeSpecOptional
    | typeSpecUnion
    | typeSpecIntersection
    ;
typeSpecOptional
    : IDENTIFIER '?'
    | '(' ( IDENTIFIER | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' '?'
    ;
typeSpecUnion
    :       ( IDENTIFIER | typeSpecOptional | '(' ( IDENTIFIER | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' )
      ( '&' ( IDENTIFIER | typeSpecOptional | '(' ( IDENTIFIER | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' ) )+
    ;
typeSpecIntersection
    :       ( IDENTIFIER | typeSpecOptional | '(' ( IDENTIFIER | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' )
      ( '|' ( IDENTIFIER | typeSpecOptional | '(' ( IDENTIFIER | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' ) )+
    ;

methodSignature
    : visibility=( PRIVATE | NAMESPACE | PUBLIC )? name=IDENTIFIER '(' ( IDENTIFIER ':' typeSpec ( ',' IDENTIFIER ':' typeSpec )* )? ')' ':' returnType=typeSpec
    ;

constOrVarDef
    : VAL? IDENTIFIER ( ':' typeSpec )? '=' expression ';'
    | VAR IDENTIFIER ( ':' typeSpec )? ( '=' expression )? ';'
    ;

expression
    : NUMBER | STRING | BOOLEAN | NONE | IDENTIFIER
    | '(' expression ')'
    | expression ( '.' | '?.' ) IDENTIFIER '(' ')'
    ;