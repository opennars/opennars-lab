package org.opennars.util.parser;

public class Token {
    public String contentString;
    public int contentOperation = 0; // set to 0 for internalerror
    public int contentNumber = 0;

    public int type = INTERNALERROR;
    public int line = 0;
    public int column = 0; // Spalte
    // public String Filename;

    /*
    final public String getRealString() {
        if( type == EnumType.OPERATION ) {
            return to!string(this.contentOperation);
        }
        else if( type == EnumType.IDENTIFIER ) {
            return this.contentString;
        }
        else if( type == EnumType.NUMBER ) {
            // TODO< catch exceptions >
            return to!string(contentNumber);
        }
        else if( type == EnumType.STRING ) {
            return contentString;
        }


        return "";
    }
     */

    public Token copy() {
        Token result = new Token();
        result.contentString = this.contentString;
        result.contentOperation = this.contentOperation;
        result.contentNumber = this.contentNumber;
        result.type = this.type;
        result.line = this.line;
        result.column = this.column;
        return result;
    }

    // types
    public static final int NUMBER = 0;
    public static final int IDENTIFIER = 1;
    public static final int KEYWORD = 2;       // example: if do end then
    public static final int OPERATION = 3;     // example: := > < >= <=

    public static final int ERROR = 4;         // if Lexer found an error
    public static final int INTERNALERROR = 5; // if token wasn't initialized by Lexer
    public static final int STRING = 6;        // "..."

    public static final int EOF = 7;           // end of file

}
