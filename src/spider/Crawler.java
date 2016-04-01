package spider;

import java.io.FileInputStream;



import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;





import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;

import org.apache.commons.logging.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;

import redis.clients.jedis.Jedis;


public class Crawler {
	public static String context;
	private Set<String> links = null;
	private String Url;
	private CloseableHttpClient httpclient = null;
	private Jedis jediss;
	public Crawler(){
		httpclient = HttpClients.createDefault();
	}
	public void GetMethod(String url) throws IOException{
		
		
		
		
		this.Url = url;
		InputStream input = null;		
		try{
			HttpGet httpget = new HttpGet(Url);
			CloseableHttpResponse responce = httpclient.execute(httpget);
			int status = responce.getStatusLine().getStatusCode();
			if(status>=200 && status < 300 ){
				//从响应中获取消息实体包括css js等一切东西。
				HttpEntity entity = responce.getEntity();
				//得到文件名
				String filename = getFileNameByUrl(url)+".txt";
				
			//可以读取源码的代码。。。。。
			//context = EntityUtils.toString(entity);
			//以流的形式获取文件内容，而不是直接从网上获取内容
			input = entity.getContent();
			FileDownLoader filedown = new FileDownLoader(filename);
			//往文件名里面写入input的内容，即entity的内容。
			filedown.writeIn(input);
			System.out.println("状态码为："+responce.getStatusLine().toString());
			
			StringBuffer bf = new StringBuffer();
			byte[]b = new byte[1024];
			InputStream error = new FileInputStream(filename);
			while(error.read(b)!=-1){
				bf.append(new String(b));
			}
			context = bf.toString();
			HtmlFileParser.ImageLoad(bf.toString());
			
			responce.close();
			filedown.close();
			error.close();
			
			}else if(status>=400){
				//不存在这个网络，而不是没有，
				System.out.println("访问的网站有问题:"+url);
				jediss.sadd("wrongPage", url);
				  Logger log = Logger.getLogger(Crawler.class);
				  log.debug("url:"+url+"status:"+status);
			  
				//responce.close();
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
		
		
	}
	public String getFileNameByUrl(String url){
		//先获取除http://以外的东西，因为从7开始
		url = url.substring(7);
		//然后用正则表达式去掉  :  / 这两种字符
		url = url.replaceAll("[\\?/:*|<>\"]", "");
		return url;
	}
	
	
	
	
	//这里可以控制次数，然后就做一个安静的美男子，
	public void CrawlWithSeeds(Set<String> seeds) throws IOException{
		Iterator<String> it = seeds.iterator();
		while(it.hasNext()){
			String s = it.next();
			UrlList.addUnvisitedUrl(s);
		}
		
		while(!UrlList.unVisitedIsEmpty() && UrlList.getVisitedUrlNum()<=3000){
			//removefirst，为的是BFS
			String visitUrl = (String)UrlList.unVisitedUrlDeQueue();
			//utf8
			visitUrl = UrlUtility.Encode(visitUrl);
			//把网址的形式做正规 也就是改成   &  和去掉最后的  /
			visitUrl = UrlUtility.Normalizer(visitUrl);
			if(visitUrl == null || visitUrl.length()>30)
				continue;
			System.out.println(visitUrl);
			
			
			//在这里用getmethod(),也就是进行下载这个链接里面的内容
			GetMethod(visitUrl);
			//ok则加入这个链接
			UrlList.addVisitedUrl(visitUrl);
			//去进行解析并且得到这个<a>
			Set<String> link = HtmlParser.extracLinks(visitUrl);
			//把遍历的<a>放入
			for(String seed : link){
				UrlList.addUnvisitedUrl(seed);
			}
		}
	}
	//crawling与前一个方法的后半段有啥区别。
	public void Crawling(String url) throws IOException{
		//这个相当于头结点的感觉，即有了第一批数据
		jediss=createjedis.getjedis();
		GetMethod(url);
		UrlList.addVisitedUrl(url);
		links = HtmlParser.extracLinks(url);
		for(String seed : links){
			UrlList.addUnvisitedUrl(seed);
		}		
		
		//这个是控制次数，如果控制他就是乖乖女，不控制就有些
		//CrawlWithSeeds(links);
		
		CrawlWithMul();
	}
	
	
	
	String url;
	long startTime = System.currentTimeMillis();
	//进行线程安全的加减数值,从0开始

	//线程池
	ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	public void CrawlWithMul(){
	
		
		try{
			//采用BFS的算法：即一层一层，第一层完了，把第一层从前到后一次向他的后部访问，然后removefirst,完了再做，递归。一个一个不停地往后面加，即队列，然后不停地去头
			if((url = UrlList.unVisitedUrlDeQueue())!= null){
				//线程池里面存放线程
				//去解析这个
				executor.execute(new MutCrawler(url));//numberofthreads.get()为获取当前值
				CrawlWithMul();
			}
		}finally{
			executor.shutdown();
		}
		long useTime = System.currentTimeMillis()-startTime;
		System.out.println("UseTIME="+useTime);
	}
	
}

class MutCrawler implements Runnable{
	private String url;
	public MutCrawler(String url){
		this.url = url;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			//在这里进行递归解析
			Crawler cl = new Crawler();
			cl.GetMethod(url);
			UrlList.addVisitedUrl(url);
			Set<String> link = HtmlParser.extracLinks(url);
			for(String seed : link){
				UrlList.addUnvisitedUrl(seed);
			}
			HtmlFileParser.ImageLoad(Crawler.context);
		} catch (IOException | ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}