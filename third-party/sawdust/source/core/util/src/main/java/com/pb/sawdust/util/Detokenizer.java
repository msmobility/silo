package com.pb.sawdust.util;

import com.pb.sawdust.io.TextFile;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Detokenizer} class provides very simple detokenizing (find key -> replace with value) operations on strings
 * and files. It includes a main method which allows it to detokenize a file from a command line call.
 *
 * @author crf
 *         Started 11/23/11 10:37 AM
 */
public class Detokenizer {
    private final Map<String,String> tokenMap;

    public Detokenizer(Map<String,String> tokenMap) {
        this.tokenMap = new HashMap<>(tokenMap);
    }

    public String detokenize(String input) {
        String output = input;
        for (String key : tokenMap.keySet())
            output = output.replace(key,tokenMap.get(key));
        return output;
    }

    public void detokenizeFile(String file, String outFile) {
        TextFile tf = new TextFile(outFile,true);
        for (String line : new TextFile(file))
            tf.writeLine(detokenize(line));
        tf.close();
    }

    private static String getUsage() {
        String usage = "Detokenizer.java - detokenize a text file";
        usage += "\nusage: java ... com.pb.sawdust.util.Detokenizer input_file output_file key_value_pairs";
        usage += "\nwhere:";
        usage += "\n\tinput_file = file to detokenize";
        usage += "\n\toutput_file = file to write out";
        usage += "\n\tkey_value_pairs = tokens and replacements in the following form:";
        usage += "\n\t\ta:A,b:B,c:C...";
        usage += "\n\t\twhere a is token1, A is replacement1, etc.";
        usage += "\n\t\tto include ':' or ',' in either token or replacement, use";
        usage += "\n\t\tdoubles instead: '::' or ',,'";
        return usage;
    }

    public static void main(String ... args) {
        if (args.length != 3) {
            System.out.println(getUsage());
            System.exit(0);
        }
        String inputFile = args[0];
        String outputFile = args[1];
        String tokenMap = args[2];

        String doubleColon = "__&double_colon&__";
        String doubleComma = "__&double_comma&__";
        tokenMap = tokenMap.replace("::",doubleColon).replace(",,",doubleComma);
        Map<String,String> tkMap = new HashMap<>();
        for (String tokenMapElement : tokenMap.split(",")) {
            int split = tokenMapElement.indexOf(':');
            if (split == -1)
                throw new IllegalArgumentException("Invalid token-replacement pair (no colon): " + tokenMapElement);
            tkMap.put(tokenMapElement.substring(0,split-1).replace(doubleColon,"::").replace(doubleComma,",,"),
                      tokenMapElement.substring(split+1).replace(doubleColon,"::").replace(doubleComma,",,"));
        }

        new Detokenizer(tkMap).detokenizeFile(inputFile,outputFile);
    }
}
