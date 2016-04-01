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
				//����Ӧ�л�ȡ��Ϣʵ�����css js��һ�ж�����
				HttpEntity entity = responce.getEntity();
				//�õ��ļ���
				String filename = getFileNameByUrl(url)+".txt";
				
			//���Զ�ȡԴ��Ĵ��롣��������
			//context = EntityUtils.toString(entity);
			//��������ʽ��ȡ�ļ����ݣ�������ֱ�Ӵ����ϻ�ȡ����
			input = entity.getContent();
			FileDownLoader filedown = new FileDownLoader(filename);
			//���ļ�������д��input�����ݣ���entity�����ݡ�
			filedown.writeIn(input);
			System.out.println("״̬��Ϊ��"+responce.getStatusLine().toString());
			
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
				//������������磬������û�У�
				System.out.println("���ʵ���վ������:"+url);
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
		//�Ȼ�ȡ��http://����Ķ�������Ϊ��7��ʼ
		url = url.substring(7);
		//Ȼ����������ʽȥ��  :  / �������ַ�
		url = url.replaceAll("[\\?/:*|<>\"]", "");
		return url;
	}
	
	
	
	
	//������Կ��ƴ�����Ȼ�����һ�������������ӣ�
	public void CrawlWithSeeds(Set<String> seeds) throws IOException{
		Iterator<String> it = seeds.iterator();
		while(it.hasNext()){
			String s = it.next();
			UrlList.addUnvisitedUrl(s);
		}
		
		while(!UrlList.unVisitedIsEmpty() && UrlList.getVisitedUrlNum()<=3000){
			//removefirst��Ϊ����BFS
			String visitUrl = (String)UrlList.unVisitedUrlDeQueue();
			//utf8
			visitUrl = UrlUtility.Encode(visitUrl);
			//����ַ����ʽ������ Ҳ���Ǹĳ�   &  ��ȥ������  /
			visitUrl = UrlUtility.Normalizer(visitUrl);
			if(visitUrl == null || visitUrl.length()>30)
				continue;
			System.out.println(visitUrl);
			
			
			//��������getmethod(),Ҳ���ǽ�����������������������
			GetMethod(visitUrl);
			//ok������������
			UrlList.addVisitedUrl(visitUrl);
			//ȥ���н������ҵõ����<a>
			Set<String> link = HtmlParser.extracLinks(visitUrl);
			//�ѱ�����<a>����
			for(String seed : link){
				UrlList.addUnvisitedUrl(seed);
			}
		}
	}
	//crawling��ǰһ�������ĺ�����ɶ����
	public void Crawling(String url) throws IOException{
		//����൱��ͷ���ĸо��������˵�һ������
		jediss=createjedis.getjedis();
		GetMethod(url);
		UrlList.addVisitedUrl(url);
		links = HtmlParser.extracLinks(url);
		for(String seed : links){
			UrlList.addUnvisitedUrl(seed);
		}		
		
		//����ǿ��ƴ�����������������ǹԹ�Ů�������ƾ���Щ
		//CrawlWithSeeds(links);
		
		CrawlWithMul();
	}
	
	
	
	String url;
	long startTime = System.currentTimeMillis();
	//�����̰߳�ȫ�ļӼ���ֵ,��0��ʼ

	//�̳߳�
	ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	public void CrawlWithMul(){
	
		
		try{
			//����BFS���㷨����һ��һ�㣬��һ�����ˣ��ѵ�һ���ǰ����һ�������ĺ󲿷��ʣ�Ȼ��removefirst,�����������ݹ顣һ��һ����ͣ��������ӣ������У�Ȼ��ͣ��ȥͷ
			if((url = UrlList.unVisitedUrlDeQueue())!= null){
				//�̳߳��������߳�
				//ȥ�������
				executor.execute(new MutCrawler(url));//numberofthreads.get()Ϊ��ȡ��ǰֵ
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
			//��������еݹ����
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