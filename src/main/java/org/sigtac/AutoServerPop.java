package org.sigtac;

import org.apache.commons.cli.*;
import org.apache.log4j.*;
import org.sigtac.battlemetrics.BattleMetricsServerStatus;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.sigtac.battlemetrics.model.BattleMetricsServer;
import sun.net.util.IPAddressUtil;

import static org.sigtac.ServerConnectionManager.*;

public class AutoServerPop
{
    private static Logger logger = Logger.getLogger(AutoServerPop.class);
    private final Integer delay;
    private final Integer min_threshold;
    private final Integer max_threshold;
    private Integer port;
    private Integer connectPort;
    private final String ip;
    private final String nameFilter;

    private final BattleMetricsServerStatus serverStatus;

    public AutoServerPop(Integer delay, Integer min_threshold, Integer max_threshold, Integer port, Integer connectPort, String ip, String nameFilter) {
        this.delay = delay;
        this.min_threshold = min_threshold;
        this.max_threshold = max_threshold;
        this.serverStatus = new BattleMetricsServerStatus();
        this.port = port;
        this.connectPort = connectPort;
        this.ip = ip;
        this.nameFilter = nameFilter;
    }

    private void start(){
        do {
            try {
                Optional<BattleMetricsServer> server = serverStatus.getConnectionInfo(nameFilter, port, ip);

                server.ifPresent( (serv) -> {
                    String name = serv.getAttributes().getName();
                    String ip = serv.getAttributes().getIp();
                    String connectPortStr = serv.getAttributes().getPortQuery().toString();
                    if (connectPort != null ) {
                        connectPortStr = port.toString();
                    }
                    String addr = ip + ":" + connectPortStr;
                    Integer players = serv.getAttributes().getPlayers();
                    Integer maxPlayers =serv.getAttributes().getMaxPlayers();
                    logger.info("Found server: " + name +
                            " ip: " + addr +
                            " players: " + players + "/" + maxPlayers);

                    // Server is not populated enough, connect if you haven't already!
                    if(players < min_threshold &&
                     findProcessIdByName("Squad.exe").equals("Unknown")) {
                        logger.info("Current server pop below desired ("+min_threshold+ " desired, "+players+" found).  Connecting... ");
                        connectToServer(ip, connectPortStr);
                    } else if (players > max_threshold &&
                            !findProcessIdByName("Squad.exe").equals("Unknown")) {
                        logger.info("Nice!  Server pop is healthy, bowing out to make room");
                        ProcessKillResult result = killProcessByName("Squad.exe");
                        if(!result.isSuccess()) {
                            logger.error("Error exiting squad! " + result.getDetail());
                        }
                    }
                });

                Thread.sleep(delay * 1000);
            } catch (NoSuchElementException nse) {
                logger.info("No server found!  Make sure it's up, hoss", nse);
            } catch(IOException ioe) {
                logger.error("Error communicating with the battlemetrics API", ioe);
            } catch (Exception e) {
                logger.error("Uncaught exception, something broke", e);
            }
        }
        while (true);
    }

    public static void main( String[] args ) {
        configLogger();

        Integer searchPort = null;
        Integer connectPort = null;
        Integer delay = 180;
        Integer min_threshold = 40;
        Integer max_threshold = 65;
        String ipAddr = "";
        String nameFilter = "ΣT";

        try {
            CommandLineParser parser = new DefaultParser();
            Options options = new Options();
            options.addOption("n", "name", true, "The name to filter for using string contains");
            options.addOption("p", "searchPort", true, "The port to search on");
            options.addOption("cp", "connectPort", true, "Override the port to connect on.  Default is the queryPort from battle metrics.");
            options.addOption("d", "delay", true, "The base delay to refresh in seconds.  Will randomize by +-1 minute.  Default 180");
            options.addOption("mn", "min", true, "Min threshold.  Will reconnect below this threshold if we have previously disconnected after hitting the max. Default 15, will randomize +- 5");
            options.addOption("mx", "max", true, "Max threshold.  Will initially connect if lower than this value.  Will disconnect after the threshold is met.  Default 65, Will randomize by +- 5");
            options.addOption("i", "ip", true, "The explicit IP to connect to");
            options.addOption("h","help", false, "Display help");

            CommandLine cmd = parser.parse(options, args);

            if(cmd.hasOption("h")){
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "help", options );
                System.exit(0);
            }
            if(cmd.hasOption("n")){
                nameFilter = cmd.getOptionValue("n");
            }
            if(cmd.hasOption("p")) {
                searchPort = Integer.parseInt(cmd.getOptionValue("p"));
            }
            if(cmd.hasOption("cp")) {
                connectPort = Integer.parseInt(cmd.getOptionValue("cp"));
            }
            if(cmd.hasOption("d")) {
                delay = Integer.parseInt(cmd.getOptionValue("d"));
            }
            if(cmd.hasOption("mn")) {
                min_threshold = Integer.parseInt(cmd.getOptionValue("mn"));
            }
            if(cmd.hasOption("mx")) {
                max_threshold = Integer.parseInt(cmd.getOptionValue("mx"));
            }
            if(cmd.hasOption("i")) {
                ipAddr = cmd.getOptionValue("i");
            }
        } catch (ParseException e) {
            logger.error(e);
        }

        delay = delay - 60 + new Double(Math.random() * 120.0).intValue();
        min_threshold = min_threshold - 5 + new Double(Math.random() * 10.0).intValue();
        max_threshold = max_threshold - 5 + new Double(Math.random() * 10.0).intValue();
        if(delay < 60) {
            logger.warn("Delay is a bit aggressive there.  Resetting to 120");
            delay = 120;
        }
        if(min_threshold < 0 || min_threshold > 80 ) {
            logger.warn("Min threshold value looks off, we have " + min_threshold + ".  Resetting to 15");
            min_threshold = 15;
        }
        if(max_threshold < 0 || max_threshold > 80) {
            logger.warn("Max threshold value looks off, we have " + max_threshold + ".  Resetting to 70");
            max_threshold = 70;
        }
        if(searchPort != null) {
            if (searchPort < 0 || searchPort > 65535) {
                logger.warn("Search port looks off, we have " + searchPort + ".  Resetting to null (won't filter searchPort)");
                searchPort = null;
            }
        }
        if(connectPort != null) {
            if (connectPort < 0 || connectPort > 65535) {
                logger.warn("Connect port looks off, we have " + connectPort + ".  Resetting to null (will connect on BM result port)");
                connectPort = null;
            }
        }
        if(!IPAddressUtil.isIPv4LiteralAddress(ipAddr)){
            logger.warn("IP address provided does not look valid.  Won't filter by IP");
            ipAddr = "";
        }

        startupBanner(searchPort, connectPort, delay, min_threshold, max_threshold, ipAddr, nameFilter);

        AutoServerPop pop = new AutoServerPop(delay, min_threshold, max_threshold, searchPort, connectPort, ipAddr, nameFilter);
        pop.start();
    }

    private static void startupBanner(Integer searchPort, Integer connectPort, Integer delay, Integer min_threshold, Integer max_threshold, String ipAddr, String nameFilter) {
        String banner = "=============================================================\n" +
                        "             A U T O  S E R V E R  P O P                     \n" +
                        "                 by matticusrex                              \n" +
                        "=============================================================";
        logger.info(banner);
        String startupString = "";
        startupString = startupString + "AutoServerPop starting.  Random status check interval is " + delay + " seconds.  ";
        startupString = startupString + "Min/Max threshold: " + min_threshold + "/" + max_threshold+".  ";
        if(connectPort != null ){
            startupString = startupString + "Connecting on port "+connectPort+".  ";
        }
        startupString = startupString + "Search params are: [Name: " + nameFilter + " || IP: " + ipAddr + " || Port: " + searchPort + "]";
        logger.info(startupString);
    }

    private static void configLogger() {
        ConsoleAppender appender = new ConsoleAppender(new PatternLayout("%d{HH:mm:ss} %p %c %x - %m%n"));
        BasicConfigurator.configure(appender);
    }
}
