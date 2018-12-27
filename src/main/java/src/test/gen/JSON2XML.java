package src.test.gen;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class XMLEmitter extends JSONBaseListener{
    public ParseTreeProperty<String> xml = new ParseTreeProperty<String>();
    String getXML(ParseTree ctx){
        return xml.get(ctx);
    }

    void setXML(ParseTree ctx,String s){
        xml.put(ctx, s);
    }

    public void exitAtom(JSONParser.AtomContext ctx) {
        setXML(ctx, ctx.getText());
    }

    public void exitArrayValue(JSONParser.ArrayValueContext ctx) {
        setXML(ctx,getXML(ctx.array()));
    }

    public void exitString(JSONParser.StringContext ctx) {
        setXML(ctx,ctx.getText().replaceAll("\"", ""));
    }

    public void exitObjectValue(JSONParser.ObjectValueContext ctx) {
        setXML(ctx,getXML(ctx.object()));
    }

    public void exitPair(JSONParser.PairContext ctx) {
        String tag = ctx.STRING().getText().replace("\"", "");
        JSONParser.ValueContext vctx = ctx.value();
        String x = String.format("<%s>%s<%s>\n",tag,getXML(vctx),tag);
        setXML(ctx,x);
    }

    public void exitAnObject(JSONParser.AnObjectContext ctx) {
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        for(JSONParser.PairContext pctx : ctx.pair()){
            buf.append(getXML(pctx));
        }
        setXML(ctx,buf.toString());
    }

    public void exitEmptyObject(JSONParser.EmptyObjectContext ctx) {
        setXML(ctx,"");
    }

    public void exitArrayOfValues(JSONParser.ArrayOfValuesContext ctx) {
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        for(JSONParser.ValueContext vctx : ctx.value()){
            buf.append("<element>")
                    .append(getXML(vctx))
                    .append("<element>")
                    .append("\n");
        }
        setXML(ctx,buf.toString());
    }

    public void exitEmptyArray(JSONParser.EmptyArrayContext ctx) {
        setXML(ctx,"");
    }

    public void exitJson(JSONParser.JsonContext ctx) {
        setXML(ctx,getXML(ctx.getChild(0)));
    }
}

public class JSON2XML {
    public static void main(String[] args) throws IOException {
        String path = "F:\\编译原理\\jason\\src\\test\\java\\test.json";
        CharStream inputStream = CharStreams.fromFileName(path);
        JSONLexer lexer = new JSONLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(tokenStream);
        ParseTreeWalker walker = new ParseTreeWalker();
        XMLEmitter xml = new XMLEmitter();
        ParseTree json = parser.json();
        walker.walk(xml,json);
        System.out.println(xml.xml.get(json));
    }
}
/*
注意的是，在一些文法后面用”#”号定义了一个名称，就会在用于访问生成的抽象语法树AST的访问器中生成该方法，用于访问当这个规约被满足时候的那个树节点。
 */
