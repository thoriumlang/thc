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

@lexer::header {
import org.thoriumlang.compiler.antlr4.LexerConfiguration;
import org.thoriumlang.compiler.antlr4.DefaultLexerConfiguration;
}
@lexer::members {
private LexerConfiguration configuration = new DefaultLexerConfiguration();
public ThoriumLexer(CharStream input, LexerConfiguration configuration) {
    this(input);
    this.configuration = configuration;
}

private boolean keepAllTokens() {
    return configuration.keepAllTokens();
}
}

CLASS : 'class' ;
TYPE : 'type' ;
USE : 'use' ;
PRIVATE : 'private' ;
PUBLIC : 'public' ;
NAMESPACE : 'namespace' ;
VAL : 'val' ;
VAR : 'var' ;
TRUE : 'true' ;
FALSE : 'false' ;
NONE : 'none' ;
RETURN : 'return' ;
THIS : 'this' ;

NUMBER : ( [0-9][0-9_]* | [0-9][0-9_]*'.'[0-9][0-9_]* ) ( [eE] [+-]? [0-9][0-9_]* )? ;
STRING : '"' ( ~[\\"\r\n] | '\\"' )* '"' ;
IDENTIFIER : [a-zA-Z_][0-9a-zA-Z_]* ;

// keepAllTokens must be set to true to make the lexer compatible with org.antlr:antlr4-intellij-adaptor
// we need to keep all chars so that it can match the position withing the source file
// see https://github.com/antlr/antlr4-intellij-adaptor/wiki/Getting-started
// see https://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/implementing_lexer.html
fragment Whitespaces  : [ \t\r\n\f]+ ;
WS                    : {keepAllTokens()}?  Whitespaces   -> channel(HIDDEN) ;
SKIP_WS               : {!keepAllTokens()}? Whitespaces   -> skip ;

fragment DocComment   : '/**' .*? ( '*/' | EOF ) ;
DOC_COMMENT           : {keepAllTokens()}?  DocComment    -> channel(HIDDEN) ;
SKIP_DOC_COMMENT      : {!keepAllTokens()}? DocComment    -> skip ;

fragment BlockComment : '/*' .*? ( '*/' | EOF ) ;
BLOCK_COMMENT         : {keepAllTokens()}?  BlockComment  -> channel(HIDDEN) ;
SKIP_BLOCK_COMMENT    : {!keepAllTokens()}? BlockComment  -> skip ;

fragment LineComment  : '//' ~[\r\n]* ;
LINE_COMMENT          : {keepAllTokens()}?  LineComment   -> channel(HIDDEN) ;
SKIP_LINE_COMMENT     : {!keepAllTokens()}? LineComment   -> skip ;

root
    : use* ( typeDef | classDef ) EOF
    ;

fqIdentifier
    : IDENTIFIER ( '.' IDENTIFIER )*
    ;
use
    : USE baseFqIdentifier=fqIdentifier ';'
    | USE baseFqIdentifier=fqIdentifier '.' star='*' ';'
    | USE baseFqIdentifier=fqIdentifier '.' '{' useAs ( ',' useAs )* '}' ';'
    | USE useAs ';'
    ;
useAs
    : fqIdentifier ( ':' alias=IDENTIFIER )?
    ;

typeDef
    : visibility=( NAMESPACE | PUBLIC )? TYPE IDENTIFIER ( '[' typeParameter ']' )? implementsSpec? '{' ( methodSignature ';' )* '}'
    ;

typeParameter
    : IDENTIFIER ( ',' IDENTIFIER )*
    ;
typeArguments
    : typeSpec ( ',' typeSpec )*
    ;

implementsSpec
    : ':' typeSpec
    ;

classDef
    : visibility=( NAMESPACE | PUBLIC )? CLASS IDENTIFIER ( '[' typeParameter ']' )? implementsSpec? '{' ( attributeDef | methodDef )* '}'
    ;

attributeDef
    : VAR name=IDENTIFIER ':' typeSpec ( '=' value )? ';'
    | VAL? name=IDENTIFIER ( ':' typeSpec )? ( '=' value )? ';'
    ;

typeSpec
    : typeSpecSimple
    | '(' ( typeSpecSimple | typeSpecOptional | typeSpecUnion | typeSpecIntersection  | typeSpecFunction | typeSpec ) ')'
    | typeSpecOptional
    | typeSpecUnion
    | typeSpecIntersection
    | typeSpecFunction
    ;
typeSpecSimple
    : fqIdentifier ( '[' typeArguments ']' )?
    ;
typeSpecOptional
    : typeSpecSimple '?'
    | '(' ( typeSpecSimple | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' '?'
    ;
typeSpecUnion
    :       ( typeSpecSimple | typeSpecOptional | '(' ( typeSpecSimple | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' )
      ( '&' ( typeSpecSimple | typeSpecOptional | '(' ( typeSpecSimple | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' ) )+
    ;
typeSpecIntersection
    :       ( typeSpecSimple | typeSpecOptional | '(' ( typeSpecSimple | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' )
      ( '|' ( typeSpecSimple | typeSpecOptional | '(' ( typeSpecSimple | typeSpecOptional | typeSpecUnion | typeSpecIntersection | typeSpec ) ')' ) )+
    ;
typeSpecFunction
    : '(' typeArguments? ')' ':' returnType=typeSpec
    ;

methodSignature
    : visibility=( NAMESPACE | PUBLIC )? name=IDENTIFIER
      ( '[' typeParameter ']' )?
      '(' ( methodParameter ( ',' methodParameter )* )? ')'
      ':' returnType=typeSpec
    ;
methodParameter
    : IDENTIFIER ':' typeSpec
    ;

methodDef
    : visibility=( PRIVATE | NAMESPACE | PUBLIC )? name=IDENTIFIER
      ( '[' typeParameter ']' )?
      '(' ( methodParameter ( ',' methodParameter )* )? ')'
      ( ':' returnType=typeSpec )?
      '{' statement* '}'
    ;

statement
    : RETURN value ';'
    | value ';'
    ;

value
    : '(' value ')'
    | indirectValue
    | directValue
    | assignmentValue
    | functionValue
    ;
assignmentValue
    : indirectValue '.' identifier=IDENTIFIER '=' value
    | identifier=IDENTIFIER '=' value
    | VAL valName=IDENTIFIER ( ':' typeSpec )? '=' value
    | VAR varName=IDENTIFIER ( ':' typeSpec )? ( '=' value )?
    ;
directValue
    : NUMBER
    | STRING
    | TRUE | FALSE
    | NONE
    ;
indirectValue
    : THIS
    | IDENTIFIER
    | methodName=IDENTIFIER ( '[' typeArguments ']' )? '(' methodArguments? ')'
    | indirectValue ( '.' | '?.' ) IDENTIFIER
    | indirectValue ( '.' | '?.' ) methodName=IDENTIFIER ( '[' typeArguments ']' )? '(' methodArguments? ')'
    | directValue ( '.' | '?.' ) IDENTIFIER
    | directValue ( '.' | '?.' ) methodName=IDENTIFIER ( '[' typeArguments ']' )? '(' methodArguments? ')'
    ;
functionValue
    : ( '[' typeParameter ']' )?
      '(' ( methodParameter ( ',' methodParameter )* )? ')'
      ( ':' typeSpec )?
      ( '=>' value | '=>' '{' statement* '}' )
    ;

methodArguments
    : value ( ',' value )*
    ;

// make sure we consume all tokens to make org.antlr:antlr4-intellij-adaptor happy
// see explanation next to the whitespaces / comments lexer rules
ERRCHAR : . {keepAllTokens()}? -> channel(HIDDEN) ;