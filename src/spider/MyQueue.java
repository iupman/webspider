package spider;

import java.util.LinkedList;

public class MyQueue<T>{
	private LinkedList<T> queue = new LinkedList<T>();
	public void enQueue(T t){
		queue.add(t);
	}
	public T deQueue(){
		return queue.removeFirst();
	}
	public boolean isQueueEmpty(){
		return queue.isEmpty();
	}
	public boolean contains(T t){
		return queue.contains(t);
	}
	public boolean empty(){
		return queue.isEmpty();
	}
}
