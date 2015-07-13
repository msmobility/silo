package com.pb.sawdust.model.builder;

import com.pb.sawdust.model.builder.parser.yoube.YoubeLexer;
import com.pb.sawdust.model.builder.parser.yoube.YoubeParser;
import com.pb.sawdust.model.builder.spec.VariableSpec;
import com.pb.sawdust.model.models.provider.DataProvider;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Parser;
import org.antlr.runtime.tree.CommonTree;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code CalculationDataProviderBuilder} ...
 *
 * @author crf <br/>
 *         Started 4/12/11 11:52 AM
 */
public class CalculationDataProviderBuilder implements DataProviderBuilder {
//    private final List<VariableSpec> variableSpecs;
    private final AntlrParserProvider parserProvider;

    public static interface AntlrParserProvider {
        Parser getParser(String formula);
    }

    public CalculationDataProviderBuilder(List<VariableSpec> variableSpecs, AntlrParserProvider parserProvider) {
        this.parserProvider = parserProvider;
    }

    public CalculationDataProviderBuilder(List<VariableSpec> variableSpecs) {
        this(variableSpecs, new AntlrParserProvider() {
            @Override
            public Parser getParser(String formula) {
                return  new YoubeParser(new CommonTokenStream(new YoubeLexer(new ANTLRStringStream(formula))));
            }
        });
    }

    @Override
    public DataProvider getProvider() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void parseFormula(String formula) {
        try {
            //YoubeParser.prog_return result = ;
//            System.out.println(parser.prog().getTree());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//      GreetingsLexer lexer = new GreetingsLexer(new ANTLRReaderStream(input));
//      GreetingsParser parser = new GreetingsParser(new CommonTokenStream(lexer));
//      parser.helper = this;
//      parser.script() ;
//      if (hasErrors() ) throw new RuntimeException("it all went pear-shaped\n" + errorReport() ) ;
//    } catch (IOException e) {
//      throw new RuntimeException( e) ;
//    } catch (RecognitionException e) {
//      throw new RuntimeException( e) ;
//    }
//        CharStream cs = new ANTLRStringStream(formula);
//
    }

    public static void main(String ... args) {
        String formula = "3 * alpha(( 6 - 9 ) + abs(-5) * -90.8)";
//        formula = "((3-4*6)*9)+((5-7)*9-6)";
//        formula = "f*g(5,6,7)+y(9)-t()";
        YoubeLexer lexer = new YoubeLexer(new ANTLRStringStream(formula));
        YoubeParser parser = new YoubeParser(new CommonTokenStream(lexer));
        try {
            //YoubeParser.prog_return result = ;
            CommonTree t = ((CommonTree) parser.formula().getTree());
            System.out.println(t.toStringTree());
            System.out.println(toRpn(t));
            for (CommonTree tree : toRpn(t))
                System.out.println(tree.getText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTree(CommonTree t, int indent) {
        if ( t != null ) {
            StringBuffer sb = new StringBuffer(indent);
            for ( int i = 0; i < indent; i++ )
                sb = sb.append("   ");
            for ( int i = 0; i < t.getChildCount(); i++ ) {
                System.out.println(sb.toString() + "<" + t.getChild(i).toString() + ">");
                printTree((CommonTree)t.getChild(i), indent+1);
            }
        }
    }

    public static List<CommonTree> toRpn(CommonTree t) {
        List<CommonTree> rpn = new LinkedList<CommonTree>();
        if (t != null) {
            for (int i = 0; i < t.getChildCount(); i++)
                rpn.addAll(toRpn((CommonTree) t.getChild(i)));
            rpn.add(t);
        }
        return rpn;
    }


















}
