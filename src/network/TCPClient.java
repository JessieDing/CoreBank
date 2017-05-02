package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class TCPClient {

    public static void main(String[] args) throws Exception {
        Socket client = new Socket("127.0.0.1", 9999); //连接服务器
        System.out.println("Connected!");

        DataOutputStream dataout = new DataOutputStream(client.getOutputStream());
        DataInputStream dis = new DataInputStream(client.getInputStream());

        Thread.sleep(500);

        System.out.println("enter url:");

		/* 读取文件 */
        while (true) {
            Scanner scan = new Scanner(System.in);
            String url = scan.nextLine();// D:/KerlAcct.xml
            String xml = readXML(url);
            dataout.writeUTF(xml);
            dataout.flush();
            System.out.println("Successfully send message to server.");
            System.out.println("Reply from server: " + dis.readUTF());
        }
//        client.close();
    }

    private static String readXML(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
