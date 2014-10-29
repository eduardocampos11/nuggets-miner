public class Teste {
	public static void main(String[] args) {
		byte[] array = { 10, 43, -32, -64 };
		for (byte b : array) {
			System.out.println(
			Integer.toBinaryString(0x100 + b).substring(1));
		}
	}
}
