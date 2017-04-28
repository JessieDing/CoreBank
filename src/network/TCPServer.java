package network;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dell on 2017/4/27.
 */
public class TCPServer {
    public static void main(String[] args) throws Exception {
        short port = 9999;
        ServerSocket server = new ServerSocket(port); // 创建ServerSocket,并且绑定9999端口
        System.out.println("Server start, port binding:" + port);

        while (true) {
            Socket s = server.accept(); // 接收客户端连接 网络I/O终端*
            System.out.println("a client connect!" + s);

            // 创建工作线程，进行收发数据
            Worker wk = new Worker();
            wk.setClient(s);   // *

            Thread tWork = new Thread(wk);
            System.out.println("Create new thread: " + tWork.getId());
            tWork.start();
        }
    }
}




