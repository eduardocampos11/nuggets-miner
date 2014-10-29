public class NoName {
public static void main(String args[]) {
    Scanner sc = new Scanner(System.in);
    Pattern newlineOrSpace = Pattern.compile(System
            .getProperty("line.separator")
            + "|\\s");
    sc.useDelimiter(newlineOrSpace);
    System.out.print("Enter a, b, c: ");
    double a = sc.nextDouble();
    double b = sc.nextDouble();
    double c = sc.nextDouble();
    // System.out.format("a = %f, b = %f, c = %f", a, b, c);

    double root1;
    double root2;
    double discriminant;
    discriminant = Math.sqrt(b * b - 4 * a * c);
    if (discriminant > 0) {
        System.out.println("There are no real roots ");
    } else {
        root1 = (-b + discriminant) / (2 * a);
        root2 = (-b - discriminant) / (2 * a);
        System.out.println("The roots are " + root1 + " and " + root2);
    }

}