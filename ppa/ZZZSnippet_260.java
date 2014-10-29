import java.util.*;

public class BeerTaste {
    public static void main(String args[]) {
        Map<String, String> m = new IdentityHashMap<String, String>();
		m.put("beer", "good");
		m.put("beer", "bad");
		System.out.println(m.size());
    } 
}
