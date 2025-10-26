package com.magic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@SpringBootTest
@ComponentScan(basePackages = "ap")
public class MultipleKafkaTests {

	@Test
	void sendMultipleTest() {
//		ListenableFuture<SendResult<String, String>> future= cubKafkaTemplate.send("magic_data_upload_user_register_test", 0, "key1", "message1");
//		try {
//			SendResult<String, String> result = future.get();
//			RecordMetadata metadata = result.getRecordMetadata();
//			System.out.println("message sent to " + metadata.topic() + ", partition " + metadata.partition() + ", offset " + metadata.offset());
//		}catch (Exception e) {
//			System.out.println("send message failed with " + e.getMessage());
//		}



		String msg = "{\n" +
				"        \"hh\": \"05\",\n" +
				"        \"symbol\": \"LRCUSDT\",\n" +
				"        \"start_at\": 1657264855,\n" +
				"        \"env\": \"dev\",\n" +
				"        \"op_time\": 1657264845000,\n" +
				"        \"time_trade\": 1657264855,\n" +
				"        \"act\": \"raw_competitor_depth_data_history\",\n" +
				"        \"product_type\": \"SPOT\",\n" +
				"        \"exchange\": \"BINANCE\",\n" +
				"        \"op_time\": 1657264845000,\n" +
				"        \"id\": \"1234567\"\n" +
				"}";

//		magicUpLoadProducer.sendSynMessage("magic_data_ul_bd_hive_tb_test",msg);
		//bigDataUpLoadProducer.sendAsynMessage("seer_raw_competitor_depth_data_history",msg);


	}





}
