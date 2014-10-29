public class NoName {
for (byte b : message) {
    System.out.println(Integer.toBinaryString(0x100 + b).substring(1));
}

}