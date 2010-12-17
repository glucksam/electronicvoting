package ElectronicVoting.Workshop.Tools;

import java.util.LinkedList;
import java.util.List;

public class Parser {
	/**
	 * parse strings by the Delimiter
	 * @param i_sInput - the string to parse
	 * @param i_sDelimiter -the delimiter 
	 * @return a list of all he part of the string spreded by the delimiter
	 */
	public static List <String> parseString(String i_sInput,String i_sDelimiter){
	 List<String> o_lParsedString=new LinkedList<String>();	
	 String [] temp=i_sInput.split(i_sDelimiter);
	 for(String s: temp){
		 o_lParsedString.add(s);
	 }
	 return o_lParsedString;
	}
}
