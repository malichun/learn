import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class demo {

    public static void main(String[] args) {

        try {
            String host = "open.soyoung.com";
            String path = "/union/api/feeds/get";
            String url = "https://"+host+path;

            String ak = "fd6eCXnpzxzZH1Io ";
            String sk = "jAfehTCwHAQIcvg4nxO4trVulHzjigzy";
//            String body = "{\"lng\":\"0.12\",\"device\":{\"connection_type\":1,\"oaid\":\"afe5fffb-afa6-1eb1-f7b0-fddffc8f9cba\",\"os\":0,\"imei\":\"865786042356384\",\"androidid\":\"af22d3fa0b7a8992\",\"ua\":\"Mozilla\\\\/5.0 (Linux; Android 7.0; KNT-AL10 Build\\\\/HUAWEIKNT-AL10; wv) AppleWebKit\\\\/537.36 (KHTML, like Gecko) Version\\\\/4.0 Chrome\\\\/59.0.3071.125 Mobile Safari\\\\/537.36\",\"osv\":\"7.0\",\"device_id\":\"865786042356384\",\"mac\":\"F4:60:E2:7B:5F:70\"},\"bu\":{\"media_type\":1,\"pack_name\":\"com.huanxi.toutiao\",\"posid\":\"a11a3x\",\"media_id\":\"51\",\"union_id\":\"28\"},\"api_version\":\"1.4\",\"request_id\":\"s01.10A71616176224326.RRrZbqijijQU.bEfrzrEA\",\"token\":\"01b657babf4a3f436990366293dc749e\",\"ip\":\"114.93.38.222\",\"lat\":\"0.45\"}";//请求内容
            String body = "123456";
            System.out.println("=============body=================");
            System.out.println(body);
            System.out.println("=============body=================");

            String date = getDate();
            String digest = Base64Utils.encodeToString(getSHA256(body));
            System.out.println("=============digest=================");
            System.out.println(digest);
            System.out.println("=============digest=================");


            Map<String, String> headers = new HashMap<String,String>(5);
            headers.put("Host", host);
            headers.put("Date", date);
            headers.put("Content-Type", "application/raw");
            headers.put("Digest", "SHA-256=" + digest);

            System.out.println(digest);

            String strToSign = "host: " + host + "\ndate: " + date + "\nPOST " + path + " HTTP/1.1\ndigest: SHA-256=" + digest;
            System.out.println("==========");
            System.out.println(strToSign);
            System.out.println("==========");
            String signature = Base64Utils.encodeToString(HMACSHA256(strToSign, sk));
            String strToHeader = "host date request-line digest";
            String authorization = "hmac username=\"" + ak + "\", algorithm=\"hmac-sha256\", headers=\"" + strToHeader + "\", signature=\"" + signature + "\"";

            System.out.println(authorization);
            headers.put("Authorization", authorization);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            try {
                URI uri = new URI("https", null, host, 443, path, "", null);

                HttpPost post = new HttpPost(uri);
                StringEntity httpEntity = new StringEntity(body);
                post.setEntity(httpEntity);

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String mapKey = entry.getKey();
                    String mapValue = entry.getValue();
                    post.setHeader(mapKey, mapValue);
                }

                HttpResponse response = httpClient.execute(post);

                if (response.getStatusLine().getStatusCode() == 200) {

                    HttpEntity entity1 = response.getEntity();
                    String result = EntityUtils.toString(entity1);
                    System.out.println("result:" + result);
                } else {
                    System.out.println(response.getStatusLine().getStatusCode());
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                httpClient.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 获取GMT时间
     * @return
     */
    private static String getDate() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        String date = sdf.format(cd.getTime());
//        return date;
        return "Fri, 19 Mar 2021 10:50:24 GMT";
    }

    /**
     * 利用java原生的类实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    private static byte[] getSHA256(String str){
        MessageDigest messageDigest;
        byte[] encodeStr = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    public static byte[] HMACSHA256(String data, String key) throws Exception {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");

        sha256_HMAC.init(secret_key);

        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        return array;
    }
}
