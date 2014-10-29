public class NoName {
string ToMixedFraction(decimal x) 
{
    int whole = (int) x;
    int denominator = 64;
    int numerator = (int)( (x - whole) * denominator );

    if (numerator == 0) 
    {
        return whole.ToString();
    }
    while ( numerator % 2 == 0 ) // simplify fraction
    {
        numerator /= 2;
        denominator /=2;
    }
    return string.Format("{0} {1}/{2}", whole, numerator, denominator);
}

}