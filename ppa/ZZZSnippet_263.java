public class IdentityHashMapTest{
    public static void main(String args[]) {
        Map<String, String> m = new IdentityHashMap<String, String>();
        m.put("John", "Doe");
        m.put("John", "Paul");
        System.out.println(m.size());
    }
}
