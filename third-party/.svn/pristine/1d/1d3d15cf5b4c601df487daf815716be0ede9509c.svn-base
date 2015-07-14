// $ANTLR 3.4 d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g 2012-01-26 07:06:58

package com.pb.sawdust.model.builder.parser.yoube;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class YoubeLexer extends Lexer {
    public static final int EOF=-1;
    public static final int ADD=4;
    public static final int AND=5;
    public static final int BadIdentifier=6;
    public static final int COMMA=7;
    public static final int DEC_NUMBER=8;
    public static final int DIV=9;
    public static final int EQ=10;
    public static final int ESCAPE_SEQUENCE=11;
    public static final int EXP=12;
    public static final int Exponent=13;
    public static final int FALSE=14;
    public static final int FALSE___=15;
    public static final int FUNCTION___=16;
    public static final int GT=17;
    public static final int GTEQ=18;
    public static final int IDENTIFIER=19;
    public static final int INT_NUMBER=20;
    public static final int IdentifierStart=21;
    public static final int IntegerNumber=22;
    public static final int LPAREN=23;
    public static final int LT=24;
    public static final int LTEQ=25;
    public static final int MOD=26;
    public static final int MULT=27;
    public static final int NEG_DEC___=28;
    public static final int NEG_INT___=29;
    public static final int NOT=30;
    public static final int NOTEQ=31;
    public static final int NonIntegerNumber=32;
    public static final int OR=33;
    public static final int POS_DEC___=34;
    public static final int POS_INT___=35;
    public static final int RPAREN=36;
    public static final int STRING=37;
    public static final int SUB=38;
    public static final int TRUE=39;
    public static final int TRUE___=40;
    public static final int WHITESPACE=41;
    public static final int Whitespace=42;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public YoubeLexer() {} 
    public YoubeLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public YoubeLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g"; }

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:56:8: ( '\\\"' ( options {greedy=false; } : ESCAPE_SEQUENCE |~ '\\\\' )* '\\\"' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:57:2: '\\\"' ( options {greedy=false; } : ESCAPE_SEQUENCE |~ '\\\\' )* '\\\"'
            {
            match('\"'); 

            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:58:3: ( options {greedy=false; } : ESCAPE_SEQUENCE |~ '\\\\' )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\"') ) {
                    alt1=3;
                }
                else if ( (LA1_0=='\\') ) {
                    alt1=1;
                }
                else if ( ((LA1_0 >= '\u0000' && LA1_0 <= '!')||(LA1_0 >= '#' && LA1_0 <= '[')||(LA1_0 >= ']' && LA1_0 <= '\uFFFF')) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:59:5: ESCAPE_SEQUENCE
            	    {
            	    mESCAPE_SEQUENCE(); 


            	    }
            	    break;
            	case 2 :
            	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:60:5: ~ '\\\\'
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:63:12: ( ( Whitespace )+ )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:63:14: ( Whitespace )+
            {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:63:14: ( Whitespace )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '\t' && LA2_0 <= '\n')||LA2_0=='\r'||LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:64:6: ( ( 't' | 'T' ) ( 'r' | 'R' ) ( 'u' | 'U' ) ( 'e' | 'E' ) )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:64:8: ( 't' | 'T' ) ( 'r' | 'R' ) ( 'u' | 'U' ) ( 'e' | 'E' )
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:65:7: ( ( 'f' | 'F' ) ( 'a' | 'A' ) ( 'l' | 'L' ) ( 's' | 'S' ) ( 'e' | 'E' ) )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:65:9: ( 'f' | 'F' ) ( 'a' | 'A' ) ( 'l' | 'L' ) ( 's' | 'S' ) ( 'e' | 'E' )
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "NOTEQ"
    public final void mNOTEQ() throws RecognitionException {
        try {
            int _type = NOTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:67:17: ( '<>' | '!=' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='<') ) {
                alt3=1;
            }
            else if ( (LA3_0=='!') ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:67:19: '<>'
                    {
                    match("<>"); 



                    }
                    break;
                case 2 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:67:26: '!='
                    {
                    match("!="); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOTEQ"

    // $ANTLR start "LTEQ"
    public final void mLTEQ() throws RecognitionException {
        try {
            int _type = LTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:68:17: ( '<=' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:68:19: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LTEQ"

    // $ANTLR start "GTEQ"
    public final void mGTEQ() throws RecognitionException {
        try {
            int _type = GTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:69:17: ( '>=' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:69:19: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GTEQ"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:70:17: ( '<' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:70:19: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:71:17: ( '>' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:71:19: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:72:8: ( '&&' | '&' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='&') ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1=='&') ) {
                    alt4=1;
                }
                else {
                    alt4=2;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:72:10: '&&'
                    {
                    match("&&"); 



                    }
                    break;
                case 2 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:72:17: '&'
                    {
                    match('&'); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:73:7: ( '||' | '|' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='|') ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1=='|') ) {
                    alt5=1;
                }
                else {
                    alt5=2;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:73:9: '||'
                    {
                    match("||"); 



                    }
                    break;
                case 2 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:73:16: '|'
                    {
                    match('|'); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:74:8: ( '!' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:74:10: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:75:17: ( '==' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:75:19: '=='
            {
            match("=="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "EXP"
    public final void mEXP() throws RecognitionException {
        try {
            int _type = EXP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:77:17: ( '^' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:77:19: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXP"

    // $ANTLR start "MULT"
    public final void mMULT() throws RecognitionException {
        try {
            int _type = MULT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:78:17: ( '*' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:78:19: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MULT"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:79:17: ( '/' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:79:19: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:80:17: ( '%' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:80:19: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "ADD"
    public final void mADD() throws RecognitionException {
        try {
            int _type = ADD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:81:17: ( '+' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:81:19: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ADD"

    // $ANTLR start "SUB"
    public final void mSUB() throws RecognitionException {
        try {
            int _type = SUB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:82:17: ( '-' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:82:19: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SUB"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:84:17: ( '(' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:84:19: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:85:17: ( ')' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:85:19: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:86:17: ( ',' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:86:19: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "ESCAPE_SEQUENCE"
    public final void mESCAPE_SEQUENCE() throws RecognitionException {
        try {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:90:17: ( '\\\\' 't' | '\\\\' 'n' | '\\\\' '\\\"' | '\\\\' '\\'' | '\\\\' '\\\\' )
            int alt6=5;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 't':
                    {
                    alt6=1;
                    }
                    break;
                case 'n':
                    {
                    alt6=2;
                    }
                    break;
                case '\"':
                    {
                    alt6=3;
                    }
                    break;
                case '\'':
                    {
                    alt6=4;
                    }
                    break;
                case '\\':
                    {
                    alt6=5;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }
            switch (alt6) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:90:19: '\\\\' 't'
                    {
                    match('\\'); 

                    match('t'); 

                    }
                    break;
                case 2 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:90:30: '\\\\' 'n'
                    {
                    match('\\'); 

                    match('n'); 

                    }
                    break;
                case 3 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:90:41: '\\\\' '\\\"'
                    {
                    match('\\'); 

                    match('\"'); 

                    }
                    break;
                case 4 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:90:53: '\\\\' '\\''
                    {
                    match('\\'); 

                    match('\''); 

                    }
                    break;
                case 5 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:90:65: '\\\\' '\\\\'
                    {
                    match('\\'); 

                    match('\\'); 

                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESCAPE_SEQUENCE"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:92:12: ( IdentifierStart (~ ( BadIdentifier ) )* )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:92:14: IdentifierStart (~ ( BadIdentifier ) )*
            {
            mIdentifierStart(); 


            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:92:30: (~ ( BadIdentifier ) )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0 >= '\u0000' && LA7_0 <= '\b')||(LA7_0 >= '\u000B' && LA7_0 <= '\f')||(LA7_0 >= '\u000E' && LA7_0 <= '\u001F')||(LA7_0 >= '#' && LA7_0 <= '$')||LA7_0=='.'||(LA7_0 >= '0' && LA7_0 <= ';')||LA7_0=='='||(LA7_0 >= '?' && LA7_0 <= ']')||(LA7_0 >= '_' && LA7_0 <= '{')||(LA7_0 >= '}' && LA7_0 <= '\uFFFF')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:92:30: ~ ( BadIdentifier )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\b')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\u001F')||(input.LA(1) >= '#' && input.LA(1) <= '$')||input.LA(1)=='.'||(input.LA(1) >= '0' && input.LA(1) <= ';')||input.LA(1)=='='||(input.LA(1) >= '?' && input.LA(1) <= ']')||(input.LA(1) >= '_' && input.LA(1) <= '{')||(input.LA(1) >= '}' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "IdentifierStart"
    public final void mIdentifierStart() throws RecognitionException {
        try {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:96:17: (~ ( '0' .. '9' | BadIdentifier ) )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:96:19: ~ ( '0' .. '9' | BadIdentifier )
            {
            if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\b')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\u001F')||(input.LA(1) >= '#' && input.LA(1) <= '$')||input.LA(1)=='.'||(input.LA(1) >= ':' && input.LA(1) <= ';')||input.LA(1)=='='||(input.LA(1) >= '?' && input.LA(1) <= ']')||(input.LA(1) >= '_' && input.LA(1) <= '{')||(input.LA(1) >= '}' && input.LA(1) <= '\uFFFF') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IdentifierStart"

    // $ANTLR start "BadIdentifier"
    public final void mBadIdentifier() throws RecognitionException {
        try {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:99:15: ( Whitespace | '\"' | '\\'' | SUB | ADD | MULT | DIV | LT | GT | EQ | MOD | EXP | LPAREN | RPAREN | '&' | '|' | NOT | COMMA )
            int alt8=18;
            switch ( input.LA(1) ) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
                {
                alt8=1;
                }
                break;
            case '\"':
                {
                alt8=2;
                }
                break;
            case '\'':
                {
                alt8=3;
                }
                break;
            case '-':
                {
                alt8=4;
                }
                break;
            case '+':
                {
                alt8=5;
                }
                break;
            case '*':
                {
                alt8=6;
                }
                break;
            case '/':
                {
                alt8=7;
                }
                break;
            case '<':
                {
                alt8=8;
                }
                break;
            case '>':
                {
                alt8=9;
                }
                break;
            case '=':
                {
                alt8=10;
                }
                break;
            case '%':
                {
                alt8=11;
                }
                break;
            case '^':
                {
                alt8=12;
                }
                break;
            case '(':
                {
                alt8=13;
                }
                break;
            case ')':
                {
                alt8=14;
                }
                break;
            case '&':
                {
                alt8=15;
                }
                break;
            case '|':
                {
                alt8=16;
                }
                break;
            case '!':
                {
                alt8=17;
                }
                break;
            case ',':
                {
                alt8=18;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:100:7: Whitespace
                    {
                    mWhitespace(); 


                    }
                    break;
                case 2 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:101:7: '\"'
                    {
                    match('\"'); 

                    }
                    break;
                case 3 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:102:7: '\\''
                    {
                    match('\''); 

                    }
                    break;
                case 4 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:103:7: SUB
                    {
                    mSUB(); 


                    }
                    break;
                case 5 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:104:7: ADD
                    {
                    mADD(); 


                    }
                    break;
                case 6 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:105:7: MULT
                    {
                    mMULT(); 


                    }
                    break;
                case 7 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:106:7: DIV
                    {
                    mDIV(); 


                    }
                    break;
                case 8 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:107:7: LT
                    {
                    mLT(); 


                    }
                    break;
                case 9 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:108:7: GT
                    {
                    mGT(); 


                    }
                    break;
                case 10 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:109:7: EQ
                    {
                    mEQ(); 


                    }
                    break;
                case 11 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:110:7: MOD
                    {
                    mMOD(); 


                    }
                    break;
                case 12 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:111:7: EXP
                    {
                    mEXP(); 


                    }
                    break;
                case 13 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:112:7: LPAREN
                    {
                    mLPAREN(); 


                    }
                    break;
                case 14 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:113:7: RPAREN
                    {
                    mRPAREN(); 


                    }
                    break;
                case 15 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:114:7: '&'
                    {
                    match('&'); 

                    }
                    break;
                case 16 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:115:7: '|'
                    {
                    match('|'); 

                    }
                    break;
                case 17 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:116:7: NOT
                    {
                    mNOT(); 


                    }
                    break;
                case 18 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:117:7: COMMA
                    {
                    mCOMMA(); 


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BadIdentifier"

    // $ANTLR start "INT_NUMBER"
    public final void mINT_NUMBER() throws RecognitionException {
        try {
            int _type = INT_NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:118:12: ( IntegerNumber )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:118:14: IntegerNumber
            {
            mIntegerNumber(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT_NUMBER"

    // $ANTLR start "DEC_NUMBER"
    public final void mDEC_NUMBER() throws RecognitionException {
        try {
            int _type = DEC_NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:119:12: ( NonIntegerNumber )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:119:14: NonIntegerNumber
            {
            mNonIntegerNumber(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DEC_NUMBER"

    // $ANTLR start "IntegerNumber"
    public final void mIntegerNumber() throws RecognitionException {
        try {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:127:15: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='0') ) {
                alt10=1;
            }
            else if ( ((LA10_0 >= '1' && LA10_0 <= '9')) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }
            switch (alt10) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:127:17: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:127:23: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 

                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:127:32: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0 >= '0' && LA9_0 <= '9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IntegerNumber"

    // $ANTLR start "NonIntegerNumber"
    public final void mNonIntegerNumber() throws RecognitionException {
        try {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:130:18: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent )
            int alt17=3;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:130:20: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )?
                    {
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:130:20: ( '0' .. '9' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0 >= '0' && LA11_0 <= '9')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    match('.'); 

                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:130:38: ( '0' .. '9' )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0 >= '0' && LA12_0 <= '9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:130:52: ( Exponent )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='E'||LA13_0=='e') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:130:52: Exponent
                            {
                            mExponent(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:131:16: '.' ( '0' .. '9' )+ ( Exponent )?
                    {
                    match('.'); 

                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:131:20: ( '0' .. '9' )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0 >= '0' && LA14_0 <= '9')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);


                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:131:35: ( Exponent )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0=='E'||LA15_0=='e') ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:131:35: Exponent
                            {
                            mExponent(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:132:20: ( '0' .. '9' )+ Exponent
                    {
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:132:20: ( '0' .. '9' )+
                    int cnt16=0;
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( ((LA16_0 >= '0' && LA16_0 <= '9')) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt16 >= 1 ) break loop16;
                                EarlyExitException eee =
                                    new EarlyExitException(16, input);
                                throw eee;
                        }
                        cnt16++;
                    } while (true);


                    mExponent(); 


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NonIntegerNumber"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:135:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:135:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:135:26: ( '+' | '-' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='+'||LA18_0=='-') ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:135:41: ( '0' .. '9' )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( ((LA19_0 >= '0' && LA19_0 <= '9')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Exponent"

    // $ANTLR start "Whitespace"
    public final void mWhitespace() throws RecognitionException {
        try {
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:138:12: ( ' ' | '\\n' | '\\t' | '\\r' )
            // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Whitespace"

    public void mTokens() throws RecognitionException {
        // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:8: ( STRING | WHITESPACE | TRUE | FALSE | NOTEQ | LTEQ | GTEQ | LT | GT | AND | OR | NOT | EQ | EXP | MULT | DIV | MOD | ADD | SUB | LPAREN | RPAREN | COMMA | IDENTIFIER | INT_NUMBER | DEC_NUMBER )
        int alt20=25;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:10: STRING
                {
                mSTRING(); 


                }
                break;
            case 2 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:17: WHITESPACE
                {
                mWHITESPACE(); 


                }
                break;
            case 3 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:28: TRUE
                {
                mTRUE(); 


                }
                break;
            case 4 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:33: FALSE
                {
                mFALSE(); 


                }
                break;
            case 5 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:39: NOTEQ
                {
                mNOTEQ(); 


                }
                break;
            case 6 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:45: LTEQ
                {
                mLTEQ(); 


                }
                break;
            case 7 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:50: GTEQ
                {
                mGTEQ(); 


                }
                break;
            case 8 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:55: LT
                {
                mLT(); 


                }
                break;
            case 9 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:58: GT
                {
                mGT(); 


                }
                break;
            case 10 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:61: AND
                {
                mAND(); 


                }
                break;
            case 11 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:65: OR
                {
                mOR(); 


                }
                break;
            case 12 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:68: NOT
                {
                mNOT(); 


                }
                break;
            case 13 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:72: EQ
                {
                mEQ(); 


                }
                break;
            case 14 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:75: EXP
                {
                mEXP(); 


                }
                break;
            case 15 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:79: MULT
                {
                mMULT(); 


                }
                break;
            case 16 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:84: DIV
                {
                mDIV(); 


                }
                break;
            case 17 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:88: MOD
                {
                mMOD(); 


                }
                break;
            case 18 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:92: ADD
                {
                mADD(); 


                }
                break;
            case 19 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:96: SUB
                {
                mSUB(); 


                }
                break;
            case 20 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:100: LPAREN
                {
                mLPAREN(); 


                }
                break;
            case 21 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:107: RPAREN
                {
                mRPAREN(); 


                }
                break;
            case 22 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:114: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 23 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:120: IDENTIFIER
                {
                mIDENTIFIER(); 


                }
                break;
            case 24 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:131: INT_NUMBER
                {
                mINT_NUMBER(); 


                }
                break;
            case 25 :
                // d:\\code\\work\\java\\ark\\sawdust\\core\\model_builder\\src\\main\\java\\com\\pb\\sawdust\\model\\builder\\parser\\Yoube.g:1:142: DEC_NUMBER
                {
                mDEC_NUMBER(); 


                }
                break;

        }

    }


    protected DFA17 dfa17 = new DFA17(this);
    protected DFA20 dfa20 = new DFA20(this);
    static final String DFA17_eotS =
        "\5\uffff";
    static final String DFA17_eofS =
        "\5\uffff";
    static final String DFA17_minS =
        "\2\56\3\uffff";
    static final String DFA17_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA17_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA17_specialS =
        "\5\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "130:1: fragment NonIntegerNumber : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent );";
        }
    }
    static final String DFA20_eotS =
        "\3\uffff\2\27\1\34\1\35\1\37\2\uffff\1\27\11\uffff\1\27\2\42\1\uffff"+
        "\2\27\6\uffff\1\47\1\27\2\uffff\1\42\2\27\1\uffff\1\27\1\54\2\27"+
        "\1\uffff\1\56\1\uffff";
    static final String DFA20_eofS =
        "\57\uffff";
    static final String DFA20_minS =
        "\1\0\2\uffff\1\122\1\101\3\75\2\uffff\1\75\11\uffff\1\60\2\56\1"+
        "\uffff\1\125\1\114\6\uffff\1\0\1\60\2\uffff\1\56\1\105\1\123\1\uffff"+
        "\1\53\1\0\1\105\1\60\1\uffff\1\0\1\uffff";
    static final String DFA20_maxS =
        "\1\uffff\2\uffff\1\162\1\141\1\76\2\75\2\uffff\1\75\11\uffff\1\71"+
        "\2\145\1\uffff\1\165\1\154\6\uffff\1\uffff\1\145\2\uffff\2\145\1"+
        "\163\1\uffff\1\71\1\uffff\1\145\1\71\1\uffff\1\uffff\1\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\1\1\2\5\uffff\1\12\1\13\1\uffff\1\16\1\17\1\20\1\21\1"+
        "\22\1\23\1\24\1\25\1\26\3\uffff\1\27\2\uffff\1\5\1\6\1\10\1\14\1"+
        "\7\1\11\2\uffff\1\30\1\31\3\uffff\1\15\4\uffff\1\3\1\uffff\1\4";
    static final String DFA20_specialS =
        "\1\1\37\uffff\1\0\10\uffff\1\3\3\uffff\1\2\1\uffff}>";
    static final String[] DFA20_transitionS = {
            "\11\27\2\2\2\27\1\2\22\27\1\2\1\6\1\1\2\27\1\16\1\10\1\uffff"+
            "\1\21\1\22\1\14\1\17\1\23\1\20\1\24\1\15\1\25\11\26\2\27\1\5"+
            "\1\12\1\7\7\27\1\4\15\27\1\3\11\27\1\13\7\27\1\4\15\27\1\3\7"+
            "\27\1\11\uff83\27",
            "",
            "",
            "\1\30\37\uffff\1\30",
            "\1\31\37\uffff\1\31",
            "\1\33\1\32",
            "\1\32",
            "\1\36",
            "",
            "",
            "\1\40",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\41",
            "\1\43\1\uffff\12\43\13\uffff\1\43\37\uffff\1\43",
            "\1\43\1\uffff\12\44\13\uffff\1\43\37\uffff\1\43",
            "",
            "\1\45\37\uffff\1\45",
            "\1\46\37\uffff\1\46",
            "",
            "",
            "",
            "",
            "",
            "",
            "\11\27\2\uffff\2\27\1\uffff\22\27\3\uffff\2\27\11\uffff\1\27"+
            "\1\uffff\14\27\1\uffff\1\27\1\uffff\37\27\1\uffff\35\27\1\uffff"+
            "\uff83\27",
            "\12\41\13\uffff\1\50\37\uffff\1\50",
            "",
            "",
            "\1\43\1\uffff\12\44\13\uffff\1\43\37\uffff\1\43",
            "\1\51\37\uffff\1\51",
            "\1\52\37\uffff\1\52",
            "",
            "\1\43\1\uffff\1\43\2\uffff\12\53",
            "\11\27\2\uffff\2\27\1\uffff\22\27\3\uffff\2\27\11\uffff\1\27"+
            "\1\uffff\14\27\1\uffff\1\27\1\uffff\37\27\1\uffff\35\27\1\uffff"+
            "\uff83\27",
            "\1\55\37\uffff\1\55",
            "\12\53",
            "",
            "\11\27\2\uffff\2\27\1\uffff\22\27\3\uffff\2\27\11\uffff\1\27"+
            "\1\uffff\14\27\1\uffff\1\27\1\uffff\37\27\1\uffff\35\27\1\uffff"+
            "\uff83\27",
            ""
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( STRING | WHITESPACE | TRUE | FALSE | NOTEQ | LTEQ | GTEQ | LT | GT | AND | OR | NOT | EQ | EXP | MULT | DIV | MOD | ADD | SUB | LPAREN | RPAREN | COMMA | IDENTIFIER | INT_NUMBER | DEC_NUMBER );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA20_32 = input.LA(1);

                        s = -1;
                        if ( ((LA20_32 >= '\u0000' && LA20_32 <= '\b')||(LA20_32 >= '\u000B' && LA20_32 <= '\f')||(LA20_32 >= '\u000E' && LA20_32 <= '\u001F')||(LA20_32 >= '#' && LA20_32 <= '$')||LA20_32=='.'||(LA20_32 >= '0' && LA20_32 <= ';')||LA20_32=='='||(LA20_32 >= '?' && LA20_32 <= ']')||(LA20_32 >= '_' && LA20_32 <= '{')||(LA20_32 >= '}' && LA20_32 <= '\uFFFF')) ) {s = 23;}

                        else s = 39;

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA20_0 = input.LA(1);

                        s = -1;
                        if ( (LA20_0=='\"') ) {s = 1;}

                        else if ( ((LA20_0 >= '\t' && LA20_0 <= '\n')||LA20_0=='\r'||LA20_0==' ') ) {s = 2;}

                        else if ( (LA20_0=='T'||LA20_0=='t') ) {s = 3;}

                        else if ( (LA20_0=='F'||LA20_0=='f') ) {s = 4;}

                        else if ( (LA20_0=='<') ) {s = 5;}

                        else if ( (LA20_0=='!') ) {s = 6;}

                        else if ( (LA20_0=='>') ) {s = 7;}

                        else if ( (LA20_0=='&') ) {s = 8;}

                        else if ( (LA20_0=='|') ) {s = 9;}

                        else if ( (LA20_0=='=') ) {s = 10;}

                        else if ( (LA20_0=='^') ) {s = 11;}

                        else if ( (LA20_0=='*') ) {s = 12;}

                        else if ( (LA20_0=='/') ) {s = 13;}

                        else if ( (LA20_0=='%') ) {s = 14;}

                        else if ( (LA20_0=='+') ) {s = 15;}

                        else if ( (LA20_0=='-') ) {s = 16;}

                        else if ( (LA20_0=='(') ) {s = 17;}

                        else if ( (LA20_0==')') ) {s = 18;}

                        else if ( (LA20_0==',') ) {s = 19;}

                        else if ( (LA20_0=='.') ) {s = 20;}

                        else if ( (LA20_0=='0') ) {s = 21;}

                        else if ( ((LA20_0 >= '1' && LA20_0 <= '9')) ) {s = 22;}

                        else if ( ((LA20_0 >= '\u0000' && LA20_0 <= '\b')||(LA20_0 >= '\u000B' && LA20_0 <= '\f')||(LA20_0 >= '\u000E' && LA20_0 <= '\u001F')||(LA20_0 >= '#' && LA20_0 <= '$')||(LA20_0 >= ':' && LA20_0 <= ';')||(LA20_0 >= '?' && LA20_0 <= 'E')||(LA20_0 >= 'G' && LA20_0 <= 'S')||(LA20_0 >= 'U' && LA20_0 <= ']')||(LA20_0 >= '_' && LA20_0 <= 'e')||(LA20_0 >= 'g' && LA20_0 <= 's')||(LA20_0 >= 'u' && LA20_0 <= '{')||(LA20_0 >= '}' && LA20_0 <= '\uFFFF')) ) {s = 23;}

                        if ( s>=0 ) return s;
                        break;

                    case 2 : 
                        int LA20_45 = input.LA(1);

                        s = -1;
                        if ( ((LA20_45 >= '\u0000' && LA20_45 <= '\b')||(LA20_45 >= '\u000B' && LA20_45 <= '\f')||(LA20_45 >= '\u000E' && LA20_45 <= '\u001F')||(LA20_45 >= '#' && LA20_45 <= '$')||LA20_45=='.'||(LA20_45 >= '0' && LA20_45 <= ';')||LA20_45=='='||(LA20_45 >= '?' && LA20_45 <= ']')||(LA20_45 >= '_' && LA20_45 <= '{')||(LA20_45 >= '}' && LA20_45 <= '\uFFFF')) ) {s = 23;}

                        else s = 46;

                        if ( s>=0 ) return s;
                        break;

                    case 3 : 
                        int LA20_41 = input.LA(1);

                        s = -1;
                        if ( ((LA20_41 >= '\u0000' && LA20_41 <= '\b')||(LA20_41 >= '\u000B' && LA20_41 <= '\f')||(LA20_41 >= '\u000E' && LA20_41 <= '\u001F')||(LA20_41 >= '#' && LA20_41 <= '$')||LA20_41=='.'||(LA20_41 >= '0' && LA20_41 <= ';')||LA20_41=='='||(LA20_41 >= '?' && LA20_41 <= ']')||(LA20_41 >= '_' && LA20_41 <= '{')||(LA20_41 >= '}' && LA20_41 <= '\uFFFF')) ) {s = 23;}

                        else s = 44;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 20, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}