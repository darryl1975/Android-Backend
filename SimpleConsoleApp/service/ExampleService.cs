public class ExampleService : IExampleService
{
    public string performMultiplication(int a, int b)
    {
        return $"The result of multiplying {a} and {b} is: {a * b}";
    }

    public string performAddition(int a, int b)
    {
        return $"The result of adding {a} and {b} is: {a + b}";
    }

    public string performSubtraction(int a, int b)
    {
        return $"The result of subtracting {b} from {a} is: {a - b}";
    }
}