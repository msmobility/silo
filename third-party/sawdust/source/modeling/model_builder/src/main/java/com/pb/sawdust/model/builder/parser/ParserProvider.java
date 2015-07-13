package com.pb.sawdust.model.builder.parser;

import org.antlr.runtime.Parser;

/**
 * The {@code ParserProvider} ...
 *
 * @author crf <br/>
 *         Started 4/17/11 5:55 AM
 */
public interface ParserProvider {
    Parser getParser(String input);
}
