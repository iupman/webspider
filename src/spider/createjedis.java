package spider;

import redis.clients.jedis.Jedis;

//运用单例模式去完成对jedis的实例化，采用饿汉式
public class createjedis {

	private static Jedis jedis =  new Jedis("localhost");
	
	private createjedis(){
		
	}
	
	public static Jedis getjedis(){
		return jedis;
	}
	
}
