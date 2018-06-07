import java.util.*;
import java.io.*;
import com.ibm.edms.od.*;

public class QueryFolder
{
    public static void main ( String argv[] )
    {
        ODUtil util=new ODUtil();
        util.connect("10.1.1.1","user","password");
        //folder, account, query
        ArrayList<ODQueryResult> results=util.queryFolder(argv[0],argv[1],argv[2]);
        System.out.println(results.size());
        util.disconnect();
    }
    
}