package Adaptive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class FileHandler {

	private FileHandler(){}
	
	static String readFile(String url, boolean withLineWrap){
		StringBuilder result = new StringBuilder();
		try {
			FileReader fr = new FileReader(url);
			BufferedReader br = new BufferedReader(fr);
			String temp ;
			
			if ( withLineWrap ) {
				while ( ( temp=br.readLine() ) !=null ){
					result.append(temp).append("\n");
				}
			} 
			else {
				while ( ( temp=br.readLine() ) !=null ){
					result.append(temp);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	
	static void writeFile(String url, String text, boolean withLineWrap){
		try {
			FileWriter fw = new FileWriter(url);
			BufferedWriter bw = new BufferedWriter(fw);
			char[] str = text.toCharArray();
			
			for ( int i=0; i<str.length; i++ ) {
				if ( withLineWrap && i%100==0 && i!=0 )
					bw.append('\n');
				bw.append(str[i]);
			}
			bw.close();
			fw.close();
			
			System.out.println("Writing file completed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void writeSymbolToFile(ArrayList<Symbol> symbolList) {
		try {
			FileWriter fw = new FileWriter("data/symboltable.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			for ( Symbol s: symbolList ){

				if ( s.letter.equals("\n") ) {
					bw.write( "lineWrapper" + " " + s.probability + " " + s.low + " " + s.high );
				}
				else if ( s.letter.equals(" ") ){
					bw.write( "space" + " " + s.probability + " " + s.low + " " + s.high );
				} else {
					bw.write( s.letter + " " + s.probability + " " + s.low + " " + s.high );
				}
				bw.newLine();
			}
			bw.close();
			fw.close();
			
			System.out.println("Writing file completed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
