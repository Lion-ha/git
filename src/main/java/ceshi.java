package com.hadoop.ip;
import java.io.*;
import java.net.InetAddress;
import java.util.*;

public class IpUtils {

    /**
     * 查询ip对应地址
     *
     * @param ip
     * @return null:找不到; String[] 0:country 1:province 2:city 3: provider
     * @throws IOException
     */
    public static String[] getCity (
            HashMap< Long, String[]> ipMap, List< Long > binSearchMap, String ip){
        long numericIp = ip2UnsignedLong(ip);
        return get(ipMap, binSearchMap, numericIp);
    }

    /**
     * 查询ip对应地址
     *
     * @param ip
     * @return null:找不到; String[] 0:country 1:province 2:city 3: provider
     * @throws IOException
     */
    private static String[] get (HashMap < Long, String[]>ipMap, List < Long > binSearchMap,long ip){
        if (ip < 0L) {
            return null;
        }

        if (null == ipMap || ipMap.size() <= 0) {

            return null;
        }

        int offset = Collections.binarySearch(binSearchMap, ip);

        if (offset < 0) {
            offset = -offset - 2;
        }

        if (offset < 0) {
            return null;
        }
        return ipMap.get(binSearchMap.get(offset));
    }

    /**
     * ip to long ,按位移动
     *
     * @param ip
     * @return
     */
    public static long ip2UnsignedLong (String ip){
        long ret = -1L;
        if (null == ip) {
            return ret;
        }
        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] bytes = address.getAddress();
            long a, b, c, d;
            a = byte2int(bytes[0]);
            b = byte2int(bytes[1]);
            c = byte2int(bytes[2]);
            d = byte2int(bytes[3]);
            long result = (a << 24) | (b << 16) | (c << 8) | d;
            return result;

        } catch (Exception e) {
            return ret;
        } finally {
        }
    }

    private static int byte2int ( byte b){
        int l = b & 0x07f;
        if (b < 0) {
            l |= 0x80;
        }
        return l;
    }


    public String[] parseIp(String ip){
        InputStream input = this.getClass().getResourceAsStream("/ip.merge.txt");
        HashMap<Long,String[]> ipMap = new HashMap<Long,String[]>();
        List binSearchMap = new ArrayList();
        String separator = "\\|";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, "utf8"));
            while(bufferedReader.ready()){
                String line = bufferedReader.readLine().trim();
                String[] split = line.split(separator);
                if(split.length >= 6){
                    binSearchMap.add(ip2UnsignedLong(split[0]));
                    ipMap.put(ip2UnsignedLong(split[0]),new String[]{split[2],split[3],split[4],split[5],split[6]});
                }
            }
            String[] city = getCity(ipMap, binSearchMap, ip);
            return city;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[]{};

    }
    public static String  address(String ip) {
        IpUtils is = new IpUtils();
        String[] s = is.parseIp(ip);
        String address = "";
        for (int i = 0; i < s.length; i++) {
            address += s[i] + " ";
        }
        return address;
    }

    public static void main(String[] args) throws Exception {
        String ip = "220.248.12.40";
        IpUtils is = new IpUtils();
        String[] s = is.parseIp(ip);
        for (int i = 0; i < s.length; i++) {
            System.out.print(s[i] + "\t");
        }
    }
}
