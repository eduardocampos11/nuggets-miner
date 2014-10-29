public class NoName {
private int evaluateWord(String sval) {
    if (sval.equals("program"))
        return 1;
    else if (sval.equals("begin"))
        return 2;
    else if (sval.equals("end"))
        return 3;
    else if (sval.equals("int"))
        return 4;
    else if (sval.equals("if"))
        return 5;
    else
        System.exit(0);

}