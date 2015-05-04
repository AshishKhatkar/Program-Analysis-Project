/*
 * @author: Ashish Khatkar, Pulkit Manocha, Anindya Srivastava, Masood Pathan
 */
/*
 * This is version 2
 */
package v2;
import datastructure.MVICFG;
import java.io.*;
public class Test1 
{
	public static void func(int a, int b) throws Exception
	{
            try
            {
		if(a==0)
			System.out.println(a/b);
		else if(a<b)
			System.out.println(b/a);
		else
			System.out.println(a/b);
            }
            catch(Exception e)
            {
                int line_no = -1;
                for(StackTraceElement s : e.getStackTrace())
                {
                    if(s.getMethodName().equals("func"))
                    {
                        line_no = s.getLineNumber();
                        break;
                    }
                }
                MVICFG mvicfg = new MVICFG();
                mvicfg.runTextDiff();
                mvicfg.printGraph(mvicfg.getGraph());
                mvicfg.matchingNode(line_no + " ver 2");
            }
	}
	public static void main(String[] args) throws Exception
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String str[]=br.readLine().split(" ");
		int a=Integer.parseInt(str[0]);
		int b=Integer.parseInt(str[1]);
		func(a,b);
	}
}
