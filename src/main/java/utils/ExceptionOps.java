package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionOps {
    public static String print(Throwable t){
        //No need to close this since StringWriter.close is a noop
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
