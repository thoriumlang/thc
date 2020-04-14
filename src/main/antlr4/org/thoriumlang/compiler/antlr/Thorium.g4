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
WS : [ \t\r\n\u000C]+ -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~[\r\n]* -> channel(HIDDEN) ;
BLOCK_COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;

root
    : use* ( typeDef | classDef )
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

//ERRCHAR
//    : . -> channel(HIDDEN)
//    ;