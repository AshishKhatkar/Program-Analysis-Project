/* Version3 */

import java.io.*;

public class DivideByZero {
	
	public static void func(int a, int b)
	{
		if(a==0)
			System.out.println(a/b);
		else if(b==0)
			System.out.println(b/a);
		else if(a<b)
			System.out.println(b/a);
		else
			System.out.println(a/b);
	}
	public static void main(String[] args) throws IOException
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String str[]=br.readLine().split(" ");
		int a=Integer.parseInt(str[0]);
		int b=Integer.parseInt(str[1]);
		func(a,b);
	}
}
