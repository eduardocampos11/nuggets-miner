public class Puzzle23{
    void Puzzle23(){
        map1.put(String1, "1");
        map1.put(String2, "2");
    }

    private final NewMap map1 = new NewMap();
    private static final String String1 = new String("J2eeSig");
    private static final String String2 = new String("J2eeSig");

    public static void main(final String args[]){
        final Puzzle23 p22 = new Puzzle23();
        final Map<String, String> map2 = new HashMap();

        map2.put(String1, "1");
        map2.put(String2, "2");
        System.out.println(p22.map1.size() == map2.size() ? true : false);
        p22.map1.remove(new String(String1));
        map2.remove(new String(String2));
        System.out.println(p22.map1.size() == map2.size() ? true : false);
    }

    class NewMap extends IdentityHashMap<String, String>{
        public void put(final String... values){
            super.put(values[0], values[1]);
        }

        public int size(){
            return super.size() + 1 - 1 / 1 * 1;
        }
    }
}
