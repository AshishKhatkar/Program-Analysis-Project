/*
 * @author: Ashish Khatkar, Pulkit Manocha, Anindya Srivastava, Masood Pathan
 */
/*
 * This is version 1
 */
import java.io.*;
public class Test1 
{
	public static void func(int a, int b)
	{
		if(a<b)
			System.out.println(b/a);
		else
			System.out.println(a/b);
	}
	public static void main(String[] args)
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String str=null;
		String str1=null;
		try
		{
			str=br.readLine();
			str1=br.readLine();
		}
		catch(IOException e)
		{
			return;
		}
		int a=Integer.parseInt(str);
		int b=Integer.parseInt(str1);
		func(a,b);
	}
}
