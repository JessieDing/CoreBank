import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
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
            Socket s = server.accept(); // 接收客户端连接 网络I/O终端!!!!!!!
            System.out.println("a client connect!" + s);

            // 创建工作线程，进行收发数据
            Worker wk = new Worker();
            wk.setClient(s);   //  !!!!!!

            Thread tWork = new Thread(wk);
            System.out.println("Create new thread: " + tWork.getId());
            tWork.start();
        }
    }

    public static void readXML(String filePath) {
        SAXReader saxReader = new SAXReader();
        File file = new File(filePath);
        Document document = null;
        try {
            document = saxReader.read(file);
            Element root = document.getParent();
        }catch (DocumentException e){
            e.printStackTrace();
        }

    }

}

/*
*读取xml文档
public static void testReadXml(String filePath) {
        SAXReader reader = new SAXReader();
        File f = new File(filePath);
        Document doc = null;
        try {
            doc = reader.read(f);
            Element root = doc.getRootElement();  //获取文档根元素
            List<Element> funds = root.elements("fund"); //取得所有name为acct子节点
            for (Element e : funds) {
                System.out.println(e.element("fundId").getName() + ", " //元素名称
                        + e.element("fundId").getText());  //元素值
                System.out.println(e.element("custName").getName() + ", "
                        + e.element("custName").getText());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
* */



class Worker implements Runnable {
    Socket client;

    public void setClient(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            InputStreamReader inputStreamReader = new InputStreamReader(dis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while (true) {
                if (client.isClosed())
                    break;
                //tmp = dis.readChar();
                String strMsg = dis.readUTF();

                //XiaoTian Tech Co. Ltd,62234500000001,3000.00,Lisa,5223450000000
                System.out.println("strMsg:" + strMsg);
                String[] msgArr = strMsg.split(",");

                String payerName = msgArr[0]; // 接收从客户端发来的数据
                String payerAcct = msgArr[1];
                double payAmt = Double.parseDouble(msgArr[2]);
                String payeeName = msgArr[3];
                String payeeAcct = msgArr[4];


                dos.writeUTF("Your message[" + strMsg + "] is received."); // 向客户端回消息
                dos.flush();

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getStackTrace();
        }
    }
}
