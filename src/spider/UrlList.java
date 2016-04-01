package spider;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class UrlList{
	//这是一个集合
	private static Set<String> visitedUrl = new HashSet<String>();
	//这是一个链表linkedlist
	private static MyQueue<String> unVisitedUrl = new MyQueue<String>();

	
	
	//之所以用了这么多synchronized，是因为前面的是有该函数很多情况都是那个多线程里
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
