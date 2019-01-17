package org.loser.cache.concurrent;

import com.loserico.cache.redis.RedissonUtils;
import com.loserico.cache.redis.collection.ConcurrentMap;

public class MapTest4 {

	public static void main(String[] args) {
		MapTest4 mapTest4 = new MapTest4();
		String studentName = mapTest4.getStudent(100L);
		System.out.println(studentName);
	}
	
	public String getStudent(Long studentId) {
		ConcurrentMap<Long, String> map = RedissonUtils.concurrentMap("students");
		String studentName = map.get(studentId);
		System.out.println(studentName);
		map.offline();
		System.out.println("再次获取studentName: " + map.get(studentId));
		return studentName;
	}
}
