package SocketAndServerSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author : [zqwzh]
 * @version : [v1.0]
 * @createTime : [2021-09-20 12:56]
 */
public class Server {
    public static void main(String[] args) {
        String quit = "quit";
        final int DEFAULT_SERVER_PORT=8189;
        ServerSocket serverSocket = null;

        //绑定监听端口
        try {
            serverSocket = new ServerSocket(DEFAULT_SERVER_PORT);
            System.out.println("启动服务器，监听端口"+ DEFAULT_SERVER_PORT);

            while(true) {
                //等待客户端处理
                Socket socket = serverSocket.accept();
                System.out.println("获取客户端对象成功");
                //System.out.println("客户端[" + socket.getPort() +"]已经连接");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String msg = null;
                while((msg= bufferedReader.readLine())!=null) {
                    //读取客户端发送的信息
                    System.out.println("客户端[" + socket.getPort() + "]:" + msg);
                    //回复客户端
                    bufferedWriter.write("服务器：" + msg + "\n");
                    bufferedWriter.flush();

                    //查看客户是否退出
                    if (quit.equals(msg)) {
                        System.out.println("客户端[" + socket.getPort() + "]已断开链接");
                        break;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
