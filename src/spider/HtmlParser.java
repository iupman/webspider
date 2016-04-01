package spider;

import java.util.HashSet;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlParser {
	public static Set<String> extracLinks(String url){
		//����һ�����<a>��hashset
		Set<String> links = new HashSet<String>();
		try{
			Parser parser = new Parser(url);
			parser.setEncoding("utf-8");
			
			//����filter������ɸѡ������getnameByid...�ȵȵȵ�
			NodeFilter frameFilter = new NodeFilter() {
				
				@Override
				public boolean accept(Node node) {
					// TODO Auto-generated method stub
					//
					if(node.getText().startsWith("frame src=")){
						return true;
					}
					return false;
				}
			};
			//link����<a>��ǩ
			OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class),frameFilter);
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for(int i = 0;i< list.size();i++){
				Node tag = list.elementAt(i);
				if(tag instanceof LinkTag){
					LinkTag link = (LinkTag)tag;
					String linkUrl = link.getLink();
					System.out.println(linkUrl.toString());
					links.add(linkUrl);
				}else{
					//�������ܵ�url
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if(end == -1)
						end = frame.indexOf(">");
					String frameUrl = frame.substring(5,end -1);
					links.add(frameUrl);
				}
			}
		}catch(ParserException e){
			e.printStackTrace();
		}
		return links;
	}
}
