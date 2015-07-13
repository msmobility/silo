grammar Yoube;

options {
    output=AST;
    ASTLabelType=CommonTree; // type of $stat.tree ref etc...
}

tokens {
    POS_INT___;
    POS_DEC___;
    NEG_INT___;
    NEG_DEC___;
    FUNCTION___;
    TRUE___;
    FALSE___;
}

@header {
package com.pb.sawdust.model.builder.parser;
}

@lexer::header {
package com.pb.sawdust.model.builder.parser;
}



formula : expression;
expression : boolCombExpr;
boolCombExpr : boolExpr ((AND | OR)^ boolExpr)*;
boolExpr : sumExpr ((LT | LTEQ | GT | GTEQ | EQ | NOTEQ)^ sumExpr)*;
sumExpr : productExpr ((SUB | ADD)^ productExpr)*;
productExpr : expExpr ((DIV | MULT | MOD)^ expExpr)*;
expExpr : unaryOperation (EXP^ unaryOperation)*;
unaryOperation :
      NOT^ operand
    | ADD n=INT_NUMBER -> ^(POS_INT___ $n)
    | ADD n=DEC_NUMBER -> ^(POS_DEC___ $n)
    | SUB n=INT_NUMBER -> ^(NEG_INT___ $n)
    | SUB n=DEC_NUMBER -> ^(NEG_DEC___ $n)
    | operand;
operand : literal | functionExpr | IDENTIFIER | LPAREN! expression RPAREN!;
functionExpr : functionArgExpr | functionNoArgExpr;
//functionNoArgExpr : IDENTIFIER LPAREN! RPAREN!;
functionNoArgExpr : f=IDENTIFIER LPAREN RPAREN -> ^(FUNCTION___ $f);
functionArgExpr : f=IDENTIFIER LPAREN a=functionArgs RPAREN -> ^(FUNCTION___ $a $f);
functionArgs : expression commaArgs*;
commaArgs : COMMA! expression;
literal :
      n=INT_NUMBER -> ^(POS_INT___ $n)
    | n=DEC_NUMBER -> ^(POS_DEC___ $n)
    | STRING
    | n=TRUE -> ^(TRUE___ $n)
    | n=FALSE -> ^(FALSE___ $n);

STRING :
	'\"'
		( options {greedy=false;}
		: ESCAPE_SEQUENCE
		| ~'\\'
		)*
	'\"';
WHITESPACE : (Whitespace)+ {skip();};
TRUE : ('t'|'T')('r'|'R')('u'|'U')('e'|'E');
FALSE : ('f'|'F')('a'|'A')('l'|'L')('s'|'S')('e'|'E');

NOTEQ           : '<>' | '!=';
LTEQ            : '<=';
GTEQ            : '>=';
LT              : '<';
GT              : '>';
AND				: '&&' | '&';
OR				: '||' | '|';
NOT				: '!';
EQ              : '==';

EXP             : '^';
MULT            : '*';
DIV             : '/';
MOD             : '%';
ADD             : '+';
SUB             : '-';

LPAREN          : '(';
RPAREN          : ')';
COMMA           : ',';

fragment
ESCAPE_SEQUENCE : '\\' 't' | '\\' 'n' | '\\' '\"' | '\\' '\'' | '\\' '\\';


IDENTIFIER : IdentifierStart ~(BadIdentifier)*;

fragment
IdentifierStart : ~('0'..'9' | BadIdentifier);

fragment
BadIdentifier :
      Whitespace
    | '"'
    | '\''
    | SUB
    | ADD
    | MULT
    | DIV
    | LT
    | GT
    | EQ
    | MOD
    | EXP
    | LPAREN
    | RPAREN
    | '&'
    | '|'
    | NOT
    | COMMA;

INT_NUMBER : IntegerNumber;
DEC_NUMBER : NonIntegerNumber;

//NUMBER :
//      NonIntegerNumber
//    | IntegerNumber;

fragment
IntegerNumber : '0' | '1'..'9' ('0'..'9')*  ;

fragment
NonIntegerNumber : ('0' .. '9')+ '.' ('0' .. '9')* Exponent?
             | '.' ('0' .. '9' )+ Exponent?
             |     ('0' .. '9')+ Exponent;

fragment
Exponent : ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+;

fragment
Whitespace : ' ' | '\n' | '\t' | '\r';
