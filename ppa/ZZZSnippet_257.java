
public class Teste {
	
    public static void main(String[] args) throws ParseException {
    	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String dateStr = "2013-02-28";
        System.out.println(sdf.parse(dateStr));
    }
}
