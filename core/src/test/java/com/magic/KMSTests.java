package com.magic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

//@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public class KMSTests {
	@Value("${spring.redis.password}")
	private  String ps ;
}
