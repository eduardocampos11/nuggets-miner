public class NoName {
byte[] array = { 10, 43, -64};
for (byte b : array) {
System.out.println(Integer.toBinaryString(0x100 + b).substring(1));
}

}