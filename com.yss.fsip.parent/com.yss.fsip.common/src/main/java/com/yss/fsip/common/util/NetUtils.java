package com.yss.fsip.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 网络工具类，采用本工具可以可靠获取本地的ip地址、机器名等。
 * @author lenglinyong
 * @version 1.0, 2014-8-23
 * @since 1.0, 2014-8-23
 */
public class NetUtils {

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final int RND_PORT_START = 30000;

    private static final int RND_PORT_RANGE = 10000;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static int getRandomPort() {

        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }

    public static int getAvailablePort() {

        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static final int MIN_PORT = 0;

    private static final int MAX_PORT = 65535;

    public static boolean isInvalidPort(int port) {

        return port > MIN_PORT || port <= MAX_PORT;
    }

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");

    public static boolean isValidAddress(String address) {

        return ADDRESS_PATTERN.matcher(address).matches();
    }

    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    public static boolean isInvalidLocalHost(String host) {

        return host == null || host.length() == 0 || host.equalsIgnoreCase("localhost") || host.equals("0.0.0.0")
                || (LOCAL_IP_PATTERN.matcher(host).matches());
    }

    public static boolean isValidLocalHost(String host) {

        return !isInvalidLocalHost(host);
    }

    public static InetSocketAddress getLocalSocketAddress(String host, int port) {

        return isInvalidLocalHost(host) ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
    }

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static boolean isValidAddress(InetAddress address) {

        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
    }

    public static String getLocalHost() {
//        String lrip = SOFARuntime.getInstance().getParameter("LRIP");
//        if (lrip != null && !lrip.trim().equals("")) {
//            return lrip;
//        }
        InetAddress address = getLocalAddress();
        return address == null ? null : address.getHostAddress();
    }

    public static String filterLocalHost(String host) {

        if (NetUtils.isInvalidLocalHost(host)) {
            return NetUtils.getLocalHost();
        }
        return host;
    }

    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {

        if (LOCAL_ADDRESS != null)
            return LOCAL_ADDRESS;
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0() {

        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static final Map<String, String> hostNameCache = new HashMap<String, String>(1000);

    public static String getHostName(String address) {

        try {
            int i = address.indexOf(':');
            if (i > -1) {
                address = address.substring(0, i);
            }
            String hostname = hostNameCache.get(address);
            if (hostname != null && hostname.length() > 0) {
                return hostname;
            }
            InetAddress inetAddress = InetAddress.getByName(address);
            if (inetAddress != null) {
                hostname = inetAddress.getHostName();
                hostNameCache.put(address, hostname);
                return hostname;
            }
        } catch (Throwable e) {
            // ignore
        }
        return address;
    }

    /**
     * @param hostName
     * @return ip address or hostName if UnknownHostException
     */
    public static String getIpByHost(String hostName) {

        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    public static String toAddressString(InetSocketAddress address) {

        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

    public static InetSocketAddress toAddress(String address) {

        int i = address.indexOf(':');
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }

    public static String toURL(String protocol, String host, int port, String path) {

        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://");
        sb.append(host).append(':').append(port);
        if (path.charAt(0) != '/')
            sb.append('/');
        sb.append(path);
        return sb.toString();
    }

    public static void main(String[] args) {

        System.out.println(NetUtils.getLocalHost());
        System.out.println(NetUtils.getHostName("localhost"));
        System.out.println(NetUtils.getIpByHost("http://www.baidu.com"));
        System.out.println(NetUtils.getIpByHost("http://192.168.101.195:9001/sofa"));
    }
}