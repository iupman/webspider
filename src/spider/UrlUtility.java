package spider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtility {
	
	public static String Encode(String url){
		 String res = "";
		 //chartoarray ���ַ���תΪ�ַ�����
	        for(char c : url.toCharArray()) {
	        	//���c������Щ���߰���ģ���ôתutf8
	            if( !":/.?&#=".contains("" + c) ) {
	                try {
	                    res += URLEncoder.encode("" + c, "UTF-8");
	                } catch (UnsupportedEncodingException e) {
	                }
	            } else {
	                res += c;
	            }
	        }

	        return res;
	}
	
	public static String Normalizer(String url){
		//&amp�൱��and,�����xml����ų��ֵġ�
		url = url.replace("&amp;", "&");
		if(url.endsWith("/")){
			url = url.substring(0,url.length()-1);
		}
		return url;
	}
	
	//�Ѽ�ӵ�ַ��ȫ
	public static String Refine(String baseUrl,String relative){
		if(baseUrl == null || relative == null){
			return null;
		}
		final Url base = Parse(baseUrl),url = Parse(relative);
		if(base == null || url == null){
			return null;
		}
		if(url.scheme == null){
			url.scheme = base.scheme;
			if(url.host == null){
				url.host = base.host;
			}
		}
		if( url.path.startsWith("../") ) {
            String prefix = "";
            int idx = base.path.lastIndexOf('/');
            if( (idx = base.path.lastIndexOf('/', idx - 1)) > 0 ) prefix = base.path.substring(0, idx);
            url.path = prefix + url.path.substring(3);            
        }
                                                
        return Normalizer(url.ToUrl());
	}
	
	//��url���������������壬����ֵ
	//˼·�ǰ�����urlһ��һ�ν�ȡ����֮��ʣһ�㣬�ٽ�
	public static Url Parse(String link){
		int idx,endIndex;
		final Url url = new Url();
		
		if((idx = link.indexOf("#"))>=0){
			if(idx == 0) {
				return null;
			}else{
				//#֮ǰ
				link = link.substring(0,idx - 1);
			}
		}
		
		if( (idx = link.indexOf(":")) > 0 ) {
			//   :  ֮ǰ
            url.scheme = link.substring(0, idx).trim();
            if( IsLegalScheme(url.scheme) ) {
                link = link.substring(idx + 1);
            }
            else {
                return null;
            }
        }
        
        if( link.startsWith("//") ) {
            if( (endIndex = link.indexOf('/', 2)) > 0 ) {
                url.host = link.substring(2, endIndex).trim();
                link = link.substring(endIndex + 1);
            }
            else {
                url.host = link.substring(2).trim();
                link = null;
            }
        }
        
        if( link != null ) {
        	url.path = link.trim();
        }else     {
        	  url.path = "";
        }        
        return url;
	}
	private static boolean IsLegalScheme(String scheme){
		 if( scheme.equals("http") || scheme.equals("https") || scheme.equals("ftp") ){
			 return true;
		 }  else {
	        	return false;	
	        } 
	}
	
	
	private static class Url{
		
		
		//url:     scheme://host:port/path
		public String scheme;
		public String host;
		public String path;
		
		public Url(){}
		public String ToUrl(){
			String prefix = null;
			if(path.startsWith("/")) prefix = scheme+"://"+host;
			else prefix = scheme + "://" + host + "/";
			return prefix + path;
		}
	}
	
}
