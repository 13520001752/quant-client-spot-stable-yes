package com.magic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class test1 {


    public static void main(String[] args) {

    }


    @Test
    public void testFun1(){

        String a = "burger.letters.com - - [01/Jul/1995:00:00:12 -0400] \"GET /shuttle/countdown/video/livevideo.GIF HTTP/1.0\" 200 0";
        parseLogLine(a);


    }

    private static String parseLogLine(String logLine) {
        if (logLine == null || logLine.isBlank()) {
            return null;
        }

        int    requestStart   = logLine.indexOf("\"");
        int    requestEnd     = logLine.lastIndexOf("\"");
        String request        = logLine.substring(requestStart, requestEnd);
        String requestParts[] = request.split("//");

        String afterRequest        = logLine.substring(requestEnd + 1);
        String afterRequestParts[] = afterRequest.split(" ");
        String statusCode          = afterRequestParts[0];

        if (!"200".equals(statusCode)) {
            return null;
        }

        String fileName = requestParts[requestParts.length - 1];
        log.info("ssss:{}", fileName);
        return fileName;
    }

}
