package com.email.writer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"gemini.api.url=${GEMINI_URL: https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=}",
		"gemini.api.key=${GEMINI_KEY: AIzaSyDBDUNTogYQVfLBBQVG13UvmrsLTPr2NmU}"
})
class AiEmailExcSbApplicationTests {
	@Test
	void contextLoads() {
	}
}