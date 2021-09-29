import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by John.Ma on 2021/9/7 0007 20:57
 */
public class Test6 {
    public static void main(String[] args) {


        String someData= "HTTP/1.1 200 OK " + "Server: nginx Date: Tue, 07 Sep 2021 11:25:17 GMT"
            + "Content-Type: application/json; charset=utf-8 Content-Length: 123 Connection: keep-alive"
            + "Set-Cookie: usid=GUeya3CGMz9lnMe1qjOq6JbFJQvPqhFx; path=/"
            + "Set-Cookie: X-Token=vBrTmDZKTmIUjlF-j_vC6U9wYhSL28aD6U_VBL7nrytKjIZehez_BQ4vPJQsAqWG; path=/"
            + "Set-Cookie: usid=GUeya3CGMz9lnMe1qjOq6JbFJQvPqhFx; path=/"
            + "Cache-Control: no-cache, no-store, must-revalidate Pragma: no-cache Expires: 0"
            + "Cache-Control: no-cache Link: </!/config.json>; as=json; rel=preload; X-Frame-Options: SAMEORIGIN";


        Pattern pattern = Pattern.compile("(?<=usid=)[^;]*");

        Matcher m = pattern.matcher(someData);


        if (m.find( )) {

            System.out.println("Found value: " + m.group(0) );
//            System.out.println("Found value: " + m.group(1) );
//            System.out.println("Found value: " + m.group(2) );
//            System.out.println("Found value: " + m.group(3) );
        } else {
            System.out.println("NO MATCH");
        }


    }
}
