package MultiChatRoom;

import sun.reflect.generics.scope.Scope;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiThreadServer {

    private static Map<String,Socket> clientMap =
            new ConcurrentHashMap<>();

    //采用内部类来实现服务器与客户端的实际交互
    private static class ExecuteRealClient implements Runnable{
        private Socket client;

        public ExecuteRealClient(Socket client) {
            this.client = client;
        }

        public void toAllMessage(String message){
            try {
                PrintStream out = new PrintStream(client.getOutputStream());
                int num = clientMap.size();
                for(int i = 0;i < num;++i){
                    for(Socket tmpClient : clientMap.values()){
                        if(!(tmpClient.equals(client))){
                            out.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
                Scanner in = new Scanner(client.getInputStream());
                String str = null;
                while(true){

                    if(in.hasNextLine()){
                        str = in.nextLine();
                        //识别windows下的换行符，将\r替换为""
                        Pattern pattern = Pattern.compile("\r");
                        Matcher matcher = pattern.matcher(str);
                        matcher.replaceAll("");
                    }
                    //注册
                    if(str.startsWith("userName")){
                        String userName = str.split("\\:")[1];
                        clientMap.put(userName,client);
                        System.out.println(userName + "上线啦！");
                        System.out.println("当前共有" + clientMap.size() + "人在线");
                    }

                    //群聊:G
                    if(str.startsWith("G")){
                        String message = str.split("\\:")[1];
                        System.out.println(message);
                        toAllMessage(message);
                    }

                    //私聊:P

                    //用户退出:bye

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {

        //创建有20个线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        //建立基站
        try {
            ServerSocket server = new ServerSocket(6666);
            for(int i = 0;i < 20;i++){
                System.out.println("等待客户端连接");
                Socket client = server.accept();
                System.out.println("有新的客户端连接，端口号为" + client.getPort());
                //每当用户连接，新建线程进行处理
                executorService.submit(new ExecuteRealClient(client));

            }

            executorService.shutdown();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
