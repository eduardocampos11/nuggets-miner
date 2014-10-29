public class DateTester {

    public static void main(String[] args) throws ParseException {
        String dateString = "2011-02-28";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        System.out.println(dateFormat.parse(dateString));
    }
}
