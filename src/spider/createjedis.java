package spider;

import redis.clients.jedis.Jedis;

//���õ���ģʽȥ��ɶ�jedis��ʵ���������ö���ʽ
public class createjedis {

	private static Jedis jedis =  new Jedis("localhost");
	
	private createjedis(){
		
	}
	
	public static Jedis getjedis(){
		return jedis;
	}
	
}
