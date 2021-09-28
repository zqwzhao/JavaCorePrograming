package exercise1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author : [zqwzh]
 * @version : [v1.0]
 * @createTime : [2021-09-21 10:14]
 */
public class TCPClient {
    public void connect() {
        String quit = "bye";
        final String DEFAULT_SERVER_HOST = "127.0.0.1";
        final int DEFAULT_SERVER_PORT=8189;
        Socket socket = null;

        try {
            //创建socket
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);
            //创建IO流
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //等待用户输入信息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = consoleReader.readLine();

                //发送信息给服务器
                writer.write(input + "\n");
                writer.flush();

                String msg = reader.readLine();
                System.out.println(msg);

                if (input.toLowerCase().equals(quit)) {
                    break;
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket!=null) {
                try {
                    System.out.println("关闭连接");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
