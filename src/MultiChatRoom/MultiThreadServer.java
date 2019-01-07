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
    private static Map<String, Socket> clientInfos = new ConcurrentHashMap<>();

    private static class ExecuteClientRequest implements Runnable {
        private Socket client;

        public ExecuteClientRequest(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                Scanner in = new Scanner(client.getInputStream());
                while (true) {
                    String strFromClient = "";
                    if (in.hasNext()) {
                        strFromClient = in.nextLine();
                        Pattern pattern = Pattern.compile("\r");
                        Matcher matcher = pattern.matcher(strFromClient);
                        strFromClient = matcher.replaceAll("");
                        // 注册流程
                        // userName:
                        if (strFromClient.startsWith("userName")) {
                            String userName = strFromClient.split("\\:")[1];
                            registerUser(userName,client);
                        }
                        // 群聊流程
                        // G:hello
                        if (strFromClient.startsWith("G:")) {
                            String groupMsg = strFromClient.split("\\:")[1];
                            groupChat(groupMsg);
                        }
                        // 私聊流程
                        // P:name msg
                        if (strFromClient.startsWith("P:")) {
                            String userName = strFromClient.split("\\:")[1].split("\\-")[0];
                            String privateMsg = strFromClient.split("\\:")[1].split("\\-")[1];
                            privateChat(userName,privateMsg);
                        }
                        // 退出
                        if (strFromClient.contains("byebye")) {
                            String userName = strFromClient.split("\\:")[0];
                            userOffLine(userName);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 注册流程
        private void registerUser(String userName,Socket socket) {
            clientInfos.put(userName,socket);
            System.out.println("用户"+userName+"注册成功!");
            int clientNumber = clientInfos.size();
            System.out.println("当前聊天室共"+clientNumber+"人");
            try {
                PrintStream out = new PrintStream(socket.getOutputStream(),true,"UTF-8");
                out.println("注册成功!");
                out.println("当前聊天室共"+clientNumber+"人");
                groupChat("用户"+userName+"已上线!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 群聊流程
        private void groupChat(String groupMsg) {
            // 取出当前所有用户的Socket信息，将消息遍历发送
            Set<Map.Entry<String,Socket>> clientEntry = clientInfos.entrySet();
            Iterator<Map.Entry<String,Socket>> iterator = clientEntry.iterator();
            while (iterator.hasNext()) {
                // 取出Socket
                Socket client = iterator.next().getValue();
                try {
                    PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
                    out.println("群聊信息为:"+groupMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 私聊流程
        private void privateChat(String userName,String privateMsg) {
            // 取出userName对应的Socket信息
            Socket client = clientInfos.get(userName);
            try {
                PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
                out.println("私聊信息为:"+privateMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 用户退出流程
        private void userOffLine(String userName) {
            clientInfos.remove(userName);
            System.out.println(userName+"已下线!");
            System.out.println("当前聊天室共"+clientInfos.size()+"人");
            groupChat(userName+"已下线!");
        }
    }

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(6666);
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("等待用户连接");
        for (int i = 0;i < 20;i++) {
            Socket client = serverSocket.accept();
            System.out.println("有新用户连接!端口号为:"+client.getPort());
            ExecuteClientRequest executeClientRequest = new ExecuteClientRequest(client);
            executorService.submit(executeClientRequest);
        }
        executorService.shutdown();
        serverSocket.close();
    }
}

