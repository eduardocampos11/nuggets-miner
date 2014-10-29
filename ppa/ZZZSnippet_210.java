public class HelloWorld {
    public static void hello(){
        int a=0xabcd;
        int b=0xaaaa;
        int c=a-b;
        String s=Integer.toHexString(c);
        System.out.println(s);
    }

}