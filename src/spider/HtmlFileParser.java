package spider;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlFileParser {
	private static int n=0;
	private String fileContext = null;
	public HtmlFileParser(String in){
		this.fileContext = in;
	}
	public static void ImageLoad(String ct) throws ParserException, MalformedURLException, IOException{
		Parser parser = new Parser();
		parser.setInputHTML(ct);
		  NodeList imageTags=parser.parse(new NodeClassFilter(ImageTag.class  ));  
	        for(int i=0;i<imageTags.size();i++){  
	            ImageTag it=(ImageTag) imageTags.elementAt(i);  
	            String imageUrl=it.getImageURL();
	            //把间接地址imageURL补全成完整的样子
	            imageUrl = UrlUtility.Refine("http://www.m.sohu.com//", imageUrl);
	            System.out.println("imageurl:"+imageUrl.toString());
	            File out = new File(n+".gif");
	            n++;
	            BufferedImage buffer = ImageIO.read(new URL(imageUrl));
	            ImageIO.write(buffer, "gif", out);
	            System.out.println(buffer);
	        }
	}
}
