/*
 * @author: Ashish Khatkar, Pulkit Manocha, Anindya Srivastava, Masood Pathan
 */
/*
 * This is version 1
 */
import java.io.*;
public class Version1 
{
	
	
	
	
	
	
	
	
	
	
	
	public static void func(int a, int b)
	{
		if(a<b)
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