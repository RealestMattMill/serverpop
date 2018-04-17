package org.sigtac;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConnectionManager {
    private static Logger logger = Logger.getLogger(ServerConnectionManager.class);
    public static void connectToServer(String ip, String port) {
        String url = "steam://connect/" + ip + ":" + port;
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (IOException e) {
            logger.error("Error attempting to launch game", e);
        }
    }

    public static ProcessKillResult killProcessByName(String processName) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "taskkill.exe /IM " + processName +" /F");
            BufferedReader successResult = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String successfulStatus = successResult.readLine();
            if(successfulStatus != null) {
                return new ProcessKillResult()
                        .withStatus(true)
                        .withDetail(successfulStatus);
            }
            successResult.close();

            BufferedReader errorResult = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String errStatus = (errorResult.readLine());
            if(errStatus != null ){
                return new ProcessKillResult()
                        .withStatus(false)
                        .withDetail(errStatus);
            }
            errorResult.close();
        } catch (IOException e) {
            logger.error("Error trying to kill game process", e);
            return new ProcessKillResult().withStatus(false).withDetail("Unknown status");
        }

        return new ProcessKillResult().withStatus(false).withDetail("Unknown status");
    }

    public static String findProcessIdByName(String processName) {
        try {
            String line;
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /fo csv /nh");
            BufferedReader processes = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = processes.readLine()) != null) {
                logger.trace(line);
                String[] fields = line.split(",");
                if (fields[0].substring(1, fields[0].length() - 1).equals(processName)) {
                    processes.close();
                    return fields[1];
                }
            }
            processes.close();
        } catch (IOException ioe) {
            logger.error("Error attempting to find status of game process", ioe);
            return "Unknown";
        }
        return "Unknown";
    }

    public static class ProcessKillResult {
        private String detail;
        private boolean success;

        public ProcessKillResult withDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public ProcessKillResult withStatus(boolean success) {
            this.success = success;
            return this;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
