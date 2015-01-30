package jaci.openrio.toast.core.webui;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class ThreadWebClient extends Thread {

    HashMap<String, Object> requestInfo;

    Socket socket;
    private static final int BUFFER_SIZE = 32768;

    public ThreadWebClient(Socket clientSocket) {
        this.socket = clientSocket;
        this.setName("Proxy Thread");
        this.start();
    }

    @Override
    public void run() {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader /*you should want a bad bitch like*/ dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            HashMap<String, String> headers = new HashMap<String, String>();
            requestInfo = new HashMap<String, Object>();

            String in;
            int count = 0;
            String total = "";
            while ((in = dis.readLine()) != null && !in.equals("")) {
                parseRequests(headers, requestInfo, in, count);
                total += in + "\n";
                count++;
            }

            HashMap<String, String> postQueries = new HashMap<String, String>();
            HashMap<String, String> getQueries = new HashMap<String, String>();

            if (requestInfo.get("Method").equals("POST")) {
                int length = Integer.parseInt(headers.get("Content-Length"));
                String query = "";
                for (int i = 0; i < length; i++) {
                    query += (char)dis.read();
                }

                String[] querySplit = query.split("&");
                for (String qu : querySplit) {
                    String[] split = qu.split("=");
                    postQueries.put(split[0], split.length == 1 ? "" : split[1]);
                }
            }

            String url = (String) requestInfo.get("URL");

            int indexQuery = url.indexOf("?");
            String[] q = null;
            if (indexQuery != -1) {
                q = url.substring(indexQuery + 1).split("&");
                url = url.substring(0, indexQuery);
            }

            if (q != null) {
                for (String qu : q) {
                    String[] split = qu.split("=");
                    getQueries.put(split[0], split.length == 1 ? "" : split[1]);
                }
            }

            if (url.endsWith("/")) {
                url += "index.html";
            } else if (!url.matches(".*\\..*")) {
                dos.writeBytes("HTTP/1.1 301 Moved Permanently \n");
                dos.writeBytes("Location: http://" + headers.get("Host") + url + "/\n");
                url += "/index.html";
            }

            for (WebHandler handler : WebRegistry.handlers)
                if (handler.handleURL(url))
                    url = handler.handle(url, postQueries, getQueries, headers);

            InputStream is = null;
            try {
                is = ClassLoader.getSystemResourceAsStream(WebRegistry.ASSETS_ROOT + url);
            } catch (NullPointerException e) {
                is = ClassLoader.getSystemResourceAsStream(WebRegistry.ASSETS_ROOT + "/notfound.html");
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            byte by[] = new byte[BUFFER_SIZE];
            int index = is.read(by, 0, BUFFER_SIZE);
            while (index != -1) {
                dos.write(by, 0, index);
                index = is.read(by, 0, BUFFER_SIZE);
            }
            dos.flush();

            rd.close();
            dos.close();
            dis.close();

            if (socket != null) {
                socket.close();
            }

        } catch (Exception e) {
        }
    }

    public static void parseRequests(HashMap<String, String> headers, HashMap<String, Object> requestInfo, String request, int count) {
        if (request == null)
            return;

        String[] parsed = request.split(" ");
        if (parsed.length == 0) return;

        if (parsed[0].startsWith("Accept-Encoding")) return;

        if (count == 0) {
            requestInfo.put("Method", parsed[0]);
            requestInfo.put("URL", parsed[1]);
            requestInfo.put("Protocol", parsed[2]);
        } else {
            try {
                String req = request.replace(parsed[0], "");
                if (req.startsWith(" "))
                    req = req.substring(1);

                headers.put(parsed[0].substring(0, parsed[0].lastIndexOf(':')), req);
            } catch (Exception e) {
            }
        }

    }

}