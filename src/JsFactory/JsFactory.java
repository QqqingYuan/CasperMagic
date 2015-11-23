package JsFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JsAction.ActionChain;
import JsAction.ActionNode;
import JsAction.ClickLabel;
import JsAction.clickCrawler;

public class JsFactory {
	
	private  Logger logger = LoggerFactory.getLogger(getClass());
	
	//生成要运行的js文件
	public    boolean   CreateJs(String filename,List<String> content)
	{
		  try {  
	            FileWriter fileWriter = new FileWriter(filename);  
	            
	            for(String line:content)
	            {
	            	String s =line+"\n";
		            fileWriter.write(s);  
	            }
	            fileWriter.close(); // 关闭数据流  
	        } catch (IOException e) {  
	        	logger.error("Can't Create js file!");
	            e.printStackTrace();  
	            return false;
	        }  
		  logger.info("Create js file :"+filename+" success.");
		return false;
	}
	
	//追加
	public  boolean  AddJs(String filename,List<String> content)
	{
		 try {  
	            FileWriter fileWriter = new FileWriter(filename,true);  
	            
	            for(String line:content)
	            {
	            	String s =line+"\n";
		            fileWriter.write(s);  
	            }
	            fileWriter.close(); // 关闭数据流  
	        } catch (IOException e) {  
	        	logger.error("Can't add content to this file!");
	            e.printStackTrace();  
	            return false;
	        }  
		  logger.info("Add content to this file :"+filename+" success.");
		return false;
	}
	
	private  List<String>  getJsStart()
	{
		List<String>  content=new ArrayList<String>();
		content.add("var  casper= require('casper').create();");
		content.add("var  url=casper.cli.get(0);");
		content.add("var  charset=casper.cli.get('charset');");
		content.add("var  isaction=casper.cli.get('action');");
		content.add("phantom.outputEncoding=charset;");
        content.add("casper.start(url);");
        return content;
	}
	
	private  List<String>  getJsEnd()
	{
		List<String> content=new ArrayList<String>();
		content.add("casper.then(function() {");
		content.add("      this.echo(this.getPageContent());");
        content.add("});");
        content.add("casper.run();");
        
        return content;
	}
	
	public   List<String> getJsForm(String  selector,Map<String,String> attributes)
	{
		
		List<String> content=new ArrayList<String>();
		content.add("if(Boolean(isaction))");
		content.add("{");
		content.add("    casper.then(function() {");
		content.add("         this.fill('"+selector+"',{");
		for(Entry<String,String> one:attributes.entrySet())
		{
			content.add("                 '"+one.getKey()+"': '"+one.getValue()+"'");
		}
        content.add("         },true);");
        
        //check
        content.add("         this.waitFor(function check(){");
        content.add("             return this.evaluate(function(){");
        content.add("                  return document.querySelectorAll('#content_left').length > 0;");
        content.add("                          });");
        content.add("         });");
        
        content.add("    });");
        content.add("}");
        
        return content;
	}
	
	public  List<String> getJsClickLabel(ClickLabel label)
	{
		List<String> content=new ArrayList<String>();
		content.add("if(Boolean(isaction))");
		content.add("{");
		content.add("    casper.then(function() {");
		
		//clicklabel
		content.add("         this.clickLabel('"+label.getValue()+"','"+label.getType()+"');");
		
		content.add("    });");
	    content.add("}");
		return content;
	}
	
	public  List<String>  getJsClick(String eventElement)
	{
		List<String> content=new ArrayList<String>();
		content.add("if(Boolean(isaction))");
		content.add("{");
		content.add("    casper.then(function() {");
		
		//click
		content.add("         this.click('"+eventElement+"');");
		
		content.add("    });");
	    content.add("}");
		return content;
	}
	
	//根据ActionChain生成js文件
	public   boolean   CreateChainJs(ActionChain  chain,String jsname)
	{
		CreateJs(jsname,getJsStart());
		
		for(ActionNode node:chain.getChain())
		{
			switch(node.getNodetype())
			{
		     	case 2:
		     		AddJs(jsname,getJsForm(node.getDataElement(),node.getAttribute()));
		     		break;
		     	case 3:
		     		AddJs(jsname,getJsClickLabel(node.getLabel()));
		     		break;
		     	case 4:
		     		AddJs(jsname,getJsClick(node.getEventElement()));
		     		break;
			}
		}
		
		AddJs(jsname,getJsEnd());
		return true;
	}
	
	
	public static void main(String[] args)
	{
		JsFactory  factory=new JsFactory();
		ClickLabel  click=new ClickLabel();
		click.setValue("博客园");
		click.setType("a");
		System.out.println(factory.getJsClickLabel(click));
	}
	
	
}