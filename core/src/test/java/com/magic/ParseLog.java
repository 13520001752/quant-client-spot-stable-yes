package com.magic;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ParseLog {

    // burger.letters.com - - [01/Jul/1995:00:00:12 -0400] "GET /shuttle/countdown/video/livevideo.GIF HTTP/1.0" 200 0
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

    public static void main(String[] args) throws IOException {

//        ConcurrentHashSet<String> hashSet = new ConcurrentHashSet<>();
//
//        try {
//            Scanner scanner = new Scanner(new File("hosts_access_log_00.txt"));
//            while (scanner.hasNextLine()) {
//            }
//            scanner.close();
//
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }


        parseLogLine ("burger.letters.com - - [01/Jul/1995:00:00:12 -0400] \"GET /shuttle/countdown/video/livevideo.GIF HTTP/1.0\" 200 0");

    }
}
