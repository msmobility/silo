package com.pb.sawdust.model.builder.parser.yoube;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import org.antlr.Tool;

import java.io.IOException;
import java.nio.file.*;

/**
 * The {@code ParserTool} ...
 *
 * @author crf <br/>
 *         Started 4/12/11 12:33 PM
 */
public class YoubeParserTool {
    public static void main(String ... args) {
        String baseDir = "d:/code/work/java/ark/sawdust";
        String grammarBase = "Yoube";
        String grammarPackage = YoubeParserTool.class.getCanonicalName().replace(".YoubeParserTool","").replace("","/");
        Path srcDir = FileSystems.getDefault().getPath(baseDir,"core/model_builder/src/main/java/",grammarPackage);

        Path tempDirectory;
        try {
            tempDirectory= Files.createTempDirectory("temp_yoube");
            FileUtil.deleteDirOnExit(tempDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        new Tool(new String[] {"-o",tempDirectory.toString(),srcDir.resolve(grammarBase + ".g").toString()}).process();
        String parserName = grammarBase + "Parser.java";
        String lexerName = grammarBase + "Lexer.java";
        Path parserPath = tempDirectory.resolve(parserName);
        Path lexerPath = tempDirectory.resolve(lexerName);
        if (Files.exists(parserPath) && Files.exists(lexerPath)) {
            try {
                Files.copy(parserPath,srcDir.resolve(parserName),StandardCopyOption.REPLACE_EXISTING);
                Files.copy(lexerPath,srcDir.resolve(lexerName),StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }
}
