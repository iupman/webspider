package spider;

import java.io.IOException;

import org.htmlparser.util.ParserException;

public class Start {
	public static void main(String[] args) throws IOException, ParserException{
		Crawler crewler = new Crawler();
		crewler.Crawling("http://www.m.sohu.com/");
	}
}
