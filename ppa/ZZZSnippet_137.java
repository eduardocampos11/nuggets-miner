public class NoName {
byte[] message = { 1, 2, 3, -128 };
for (byte b : message) {
    System.out.println(Integer.toBinaryString(0x100 + b).substring(1));
}

}