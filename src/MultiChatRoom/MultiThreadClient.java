package MultiChatRoom;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;



//class ReadFromServer implements Runnable{
//    private Socket client;
//
//    public ReadFromServer(Socket client) {
//        this.client = client;
//    }
//
//    @Override
//    public void run() {
//        try {
//            //获取服务端输入流，获得服务器发来的信息
//            Scanner scanner = new Scanner(client.getInputStream());
//            while(true){
//                if(scanner.hasNextLine()){
//                    System.out.println(scanner.nextLine());
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
//
//class WriteToServer implements Runnable{
//    private Socket client;
//
//    public WriteToServer(Socket client) {
//        this.client = client;
//    }
//
//    @Override
//    public void run(){
//        //获取键盘输入流，获得用户从键盘输入的信息
//        Scanner scanner = new Scanner(System.in);
//        String string = null;
//        PrintStream out = null;
//        try {
//            out = new PrintStream(client.getOutputStream(),true,"UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        while(true){
//            System.out.println("请输入你要发送的信息");
//            if(scanner.hasNextLine()){
//                string = scanner.nextLine();
//                out.println(string);
//            }
//
//            if(string.contains("bye")){
//                System.out.println("不聊了");
//                scanner.close();
//                out.close();
//                try {
//                    client.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
//
//public class MultiThreadClient {
//    public static void main(String[] args) {
//        try {
//            Socket client = new Socket("127.0.0.1",6666);
//            Thread readThread = new Thread(new ReadFromServer(client));
//            Thread writeThread = new Thread(new WriteToServer(client));
//
//            readThread.start();
//            writeThread.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
