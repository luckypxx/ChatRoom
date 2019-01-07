package MultiChatRoom;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


class ReadFromServer implements Runnable {
    private Socket client;

    public ReadFromServer(Socket client) {
        this.client = client;
    }
    @Override
    public void run() {
        try {
            Scanner in = new Scanner(client.getInputStream());
            while (true) {
                if (client.isClosed()) {
                    System.out.println("客户端已退出");
                    in.close();
                    break;
                }
                if (in.hasNext()) {
                    System.out.println("从服务器发来的信息为:"+in.nextLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SendMsgToServer implements Runnable {
    private Socket client;

    public SendMsgToServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        try {
            PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
            while (true) {
                System.out.println("请输入要发送的内容");
                String strToServer = "";
                if (scanner.hasNext()) {
                   strToServer = scanner.nextLine();
                   out.println(strToServer);
                   if (strToServer.endsWith("byebye")) {
                       System.out.println("客户端退出");
                       scanner.close();
                       out.close();
                       client.close();
                       break;
                   }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class MultiThreadClient {
    public static void main(String[] args) throws Exception{
        Socket client = new Socket("127.0.0.1",6666);
        Thread readThread = new Thread(new ReadFromServer(client));
        Thread sendThread = new Thread(new SendMsgToServer(client));
        readThread.start();
        sendThread.start();
    }
}

