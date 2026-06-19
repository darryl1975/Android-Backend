using Microsoft.EntityFrameworkCore;
using TodoApi.Data;

var builder = WebApplication.CreateBuilder(args);

// MySQL connection from environment variables
var mysqlUrl = Environment.GetEnvironmentVariable("MYSQL_URL3")
    ?? throw new InvalidOperationException("MYSQL_URL environment variable is not set.");
var mysqlDb = "test"; // Database name is hardcoded for simplicity
var mysqlUsername = Environment.GetEnvironmentVariable("MYSQL_USERNAME")
    ?? throw new InvalidOperationException("MYSQL_USERNAME environment variable is not set.");
var mysqlPassword = Environment.GetEnvironmentVariable("MYSQL_PASSWORD")
    ?? throw new InvalidOperationException("MYSQL_PASSWORD environment variable is not set.");

var connectionString = $"Server={mysqlUrl};Database={mysqlDb};User={mysqlUsername};Password={mysqlPassword};";

builder.Services.AddDbContext<TodoDbContext>(options =>
    options.UseMySql(connectionString, new MySqlServerVersion(new Version(8, 0, 0))));

using (var scope = builder.Services.BuildServiceProvider().CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<TodoDbContext>();

    if (db.Database.CanConnect())
    {
        db.Database.Migrate();
        Console.WriteLine("Successfully connected to the MySQL database.");
    }
    else
    {
        // Console.WriteLine("Failed to connect to the MySQL database. Please check your connection settings.");

        db.Database.EnsureCreated();
        Console.WriteLine("Successfully created MySQL database.");
        return;
    }
}


builder.Services.AddControllers();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new()
    {
        Title = "Todo API",
        Version = "v1",
        Description = "A simple CRUD API for managing to-do items backed by MySQL."
    });

    // Include XML comments for endpoint summaries
    var xmlFile = $"{System.Reflection.Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    if (File.Exists(xmlPath))
        options.IncludeXmlComments(xmlPath);
});

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "Todo API v1");
    options.RoutePrefix = string.Empty; // Serve Swagger UI at the app root
});

app.UseHttpsRedirection();
app.MapControllers();

app.Run();
