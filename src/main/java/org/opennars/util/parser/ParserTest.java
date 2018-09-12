package org.opennars.util.parser;

// temporary for testing correct functionality while prototyping the parser
public class ParserTest {
    public static void main(String[] args) {
        NarseseLexer lexer = new NarseseLexer();
        NarseseParser parser = new NarseseParser();

        parser.lexer = lexer;

        lexer.setSource("<a-->b>");

        parser.parse();
    }
}
