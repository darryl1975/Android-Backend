using Microsoft.Extensions.DependencyInjection;

var services = new ServiceCollection();

services.AddSingleton<IExampleService, ExampleService>();

var serviceProvider = services.BuildServiceProvider();

// See https://aka.ms/new-console-template for more information
Console.WriteLine("Hello, World! MY first .NET project");

Console.WriteLine("Input your name: ");
string name = Console.ReadLine();
Console.WriteLine($"Hello, {name}!");

string additionResult = serviceProvider.GetService<IExampleService>()?.performAddition(5, 3);

string subtractionResult = serviceProvider.GetService<IExampleService>()?.performSubtraction(10, 4);
string multiplicationResult = serviceProvider.GetService<IExampleService>()?.performMultiplication(6, 7);

Console.WriteLine($"Addition Result: {additionResult}");
Console.WriteLine($"Subtraction Result: {subtractionResult}");
Console.WriteLine($"Multiplication Result: {multiplicationResult}");

