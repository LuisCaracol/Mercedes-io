package biz.netcentric.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.slf4j.Logger;

public interface BaseHandler
{

    
    default public String expression(String html, ScriptEngine engine, Logger log) {
        Pattern el$expression = Pattern.compile("\\$\\{(\\w+)\\.?(\\w+)\\}");
        //String html ="<h1 title=\"${person.name}\">${person.name} ${person}</h1>";
        log.info("$Expression: "+html);
        Matcher action = el$expression.matcher(html);
        StringBuffer sb = new StringBuffer(html.length());
        while (action.find()) {
          String text = action.group(0);
          String unwrapped = text.substring(2, text.length()-1);
          //System.out.println(unwrapped);
          try {
            String eval = (String) engine.eval(unwrapped);
            action.appendReplacement(sb, Matcher.quoteReplacement(eval));
          }catch (ScriptException e) {e.printStackTrace();}
        }
        action.appendTail(sb);
        return sb.toString();
    }
}
