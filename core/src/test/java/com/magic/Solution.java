package com.magic;

import ch.qos.logback.classic.Logger;
import cn.hutool.core.annotation.Link;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


@Slf4j
public class Solution {
    private static String parseLogLine(String logLine) {
        if (logLine == null || logLine.isBlank()) {
            return null;
        }

        int    requestStart = logLine.indexOf("\"");
        int    requestEnd   = logLine.lastIndexOf("\"");
        String request      = logLine.substring(requestStart, requestEnd);

        // "GET /shuttle/countdown/video/livevideo.GIF HTTP/1.0
        String requestParts[] = request.split(" ");
        String requestType    = requestParts[0];
        String requestPath    = requestParts[1];

        String afterRequest        = logLine.substring(requestEnd + 1).trim();
        String afterRequestParts[] = afterRequest.split(" ");
        String statusCode          = afterRequestParts[0];

        if (!"200".equals(statusCode)) {
            return null;
        }

        if ("GET".equals(requestType)) {
            return null;
        }

        String requestPathParts[] = requestPath.split("/");

        String fileName = requestPathParts[requestPathParts.length - 1];
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        return fileName;
    }

    public static int createFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            System.out.println("filename is invalid");
            return -1;
        }

        File file = new File(filePath);
        if (file.exists()) {
            return -1;
        }

        try {
            //在上级目录里创建文件
            file.createNewFile();
        } catch (IOException e) {
            System.out.printf("failed to create file: %s", filePath);
            return -1;
        }

        return 0;
    }

    public static void appendDataToFile (String filePath, String text) {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("append file error: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        String filename    = "./aaa.txt";
        String filenameNew = "gifs_" + filename;

//        int ret = createFile(filenameNew);
//        if (ret != 0) {
//            return;
//        }

        String test[] = {
                "burger.letters.com - - [01/Jul/1995:00:00:12 -0400] \"GET /shuttle/countdown/video/livevideo.GIF HTTP/1.0\" 200 0",
                "unicomp6.unicomp.net - - [01/Jul/1995:00:00:14 -0400] \"GET /shuttle/countdown/count.gif HTTP/1.0\" 200 40310",
                "d104.aa.net - - [01/Jul/1995:00:00:15 -0400] \"GET /shuttle/countdown/count.gif HTTP/1.0\" 200 40310",
                "unicomp6.unicomp.net - - [01/Jul/1995:00:00:14 -0400] \"GET /images/NASA-logosmall.gif HTTP/1.0\" 200 786",
                "unicomp6.unicomp.net - - [01/Jul/1995:00:00:14 -0400] \"GET /images/NASA-logosmall.gif HTTP/1.0\" 200 786",
                "unicomp6.unicomp.net - - [01/Jul/1995:00:00:14 -0400] \"GET /images/NASA-logosmall.gif HTTP/1.0\" 200 786",
                };

        String          f;
        HashSet<String> dataHashSet = new HashSet<>();
        for (String s : test) {
            f = parseLogLine(s);

//            log.info("f:{}", f)
            if (dataHashSet.contains(f)) {
                log.info("ignore f:{}", f);
                continue;
            }
            dataHashSet.add(f);

            appendDataToFile(filenameNew, f);
        }

        parseLogLine("burger.letters.com - - [01/Jul/1995:00:00:12 -0400] \"GET /shuttle/countdown/video/livevideo.GIF HTTP/1.0\" 200 0");


//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

//        int commandsCount = Integer.parseInt(bufferedReader.readLine().trim());

//        List<String> commands = IntStream.range(0, commandsCount).mapToObj(i -> {
//                    try {
//                        return bufferedReader.readLine();
//                    } catch (IOException ex) {
//                        throw new RuntimeException(ex);
//                    }
//                })
//                .collect(toList());
//
        //

//        List<String> commands = new LinkedList<>();
//
//        commands.add("clear");
//        commands.add("open firefox");
//        commands.add("close 4");
//        commands.add("open firefox");
//        commands.add("open curl");
//        commands.add("close 1");
//        commands.add( "clear");
//        commands.add( "open ps");
//        int commandsCount = commands.size();
//
//        List<String> result = Result.getOpenApplications(commands);
//        System.out.println(result);

//        bufferedWriter.write(
//                result.stream()
//                        .collect(joining("\n"))
//                        + "\n"
//        );
//
//        bufferedReader.close();
//        bufferedWriter.close();
    }
}
