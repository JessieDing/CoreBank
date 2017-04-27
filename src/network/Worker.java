package network;

import busi.model.KerlAcct;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Woody on 2017/4/27.
 */
class Worker implements Runnable {
    private Socket client;

    void setClient(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());

            while (true) {
                if (client.isClosed())
                    break;
                String message = dataInputStream.readUTF();

                System.out.println("strMsg:" + message);
                List<KerlAcct> kerlAccts = parseKerlAcctXML(message);
                dataOutputStream.writeUTF("Your message[" + message + "] is received."); // 向客户端回消息
                dataOutputStream.flush();

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getStackTrace();
        }
    }

    private static List<KerlAcct> parseKerlAcctXML(String xml) {
        SAXReader saxReader = new SAXReader();
        StringReader reader = new StringReader(xml);
        Document document;
        List<KerlAcct> results = new ArrayList<>();
        try {
            document = saxReader.read(reader);
            Element root = document.getRootElement();
            List<Element> kerlAccts = root.elements("KerlAcct");
            for (Element kerlAcct : kerlAccts) {
                String dbtrAcct = kerlAcct.element("DbtrAcct").getText();
                String dbtrName = kerlAcct.element("DbtrName").getText();
                String cdtrAcct = kerlAcct.element("CdtrAcct").getText();
                String cdtrName = kerlAcct.element("CdtrName").getText();
                String amt = kerlAcct.element("Amt").getText();
                String remark = kerlAcct.element("Remark").getText();
                KerlAcct newKerlAcct = new KerlAcct();
                newKerlAcct.setDbtrAcct(dbtrAcct);
                newKerlAcct.setDbtrName(dbtrName);
                newKerlAcct.setCdtrAcct(cdtrAcct);
                newKerlAcct.setCdtrName(cdtrName);
                newKerlAcct.setAmt(amt);
                newKerlAcct.setRemark(remark);
                results.add(newKerlAcct);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return results;
    }
}
