package spider;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class UrlList{
	//����һ������
	private static Set<String> visitedUrl = new HashSet<String>();
	//����һ������linkedlist
	private static MyQueue<String> unVisitedUrl = new MyQueue<String>();

	
	
	//֮����������ô��synchronized������Ϊǰ������иú����ܶ���������Ǹ����߳���
	public synchronized static MyQueue<String> getUnvisitedUrl(){
		return unVisitedUrl;
	}
	public synchronized static void  addVisitedUrl(String url){
		visitedUrl.add(url);
	}
	
	public synchronized static void removeVisitedUrl(String url){
		visitedUrl.remove(url);
	}
	public synchronized static String unVisitedUrlDeQueue(){
		return unVisitedUrl.deQueue();
	}
	public synchronized static void addUnvisitedUrl(String url){
		if(url != null && !url.trim().equals("") && !visitedUrl.contains(url) && !unVisitedUrl.contains(url)){
			unVisitedUrl.enQueue(url);
		}
	}
	public synchronized static int getVisitedUrlNum(){
		return visitedUrl.size();
	}
	public synchronized static boolean unVisitedIsEmpty(){
		return unVisitedUrl.empty();
	}
	
}
