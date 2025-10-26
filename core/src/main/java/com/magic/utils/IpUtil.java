package com.magic.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class IpUtil {
	
	/** 本地ip（静态缓存） */
	private static String localIp = null;
	
	/** 外网ip（静态缓存） */
	private static String outterIp = null;
	
	/**
	 * 返回本地ip地址（内网ip）
	 * @return
	 */
	public static String getLocalIp() {
		if(localIp != null) return localIp;
		try {
			localIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e.getMessage());
		}
		return localIp;
	}
	
	/**
	 * 返回本机外网ip地址（linux: 读取hostinfo信息）
	 * @return
	 */
	public static String getOutterIp() {
		if(outterIp != null) return outterIp;
		try {
			String path = "/home/dspeak/yyms/hostinfo"; // display host info under Linux
			File ipFile = new File(path);
			if(!ipFile.exists()){
				log.warn("ip util getoutter ip failed, use localip replace");
				outterIp = getLocalIp();
			}else {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				JSONObject json = JSONUtil.parseObj(sb.toString());
				JSONArray ips = json.getJSONArray("ips");
				for (Object obj : ips.toArray()) {
					String ip = (String) ((JSONObject) obj).get("ip");
					if (ip != null) {
						outterIp = ip;
						break;
					}
				}
			}
		} catch (Exception e) {
			// 读取 hostinfo 文件失败后直接返回本地ip地址
			if(outterIp == null) {
				outterIp = getLocalIp();
			}
			log.error(e.getMessage());
		}
		return outterIp;
	}

}
