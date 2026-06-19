using System.ComponentModel.DataAnnotations;

namespace TodoApi.DTOs;

public class CreateTodoItemDto
{
    [MaxLength(255)]
    public string? Description { get; set; }

    [Required]
    [MaxLength(255)]
    public string Task { get; set; } = string.Empty;
}

public class UpdateTodoItemDto
{
    [MaxLength(255)]
    public string? Description { get; set; }

    [Required]
    [MaxLength(255)]
    public string Task { get; set; } = string.Empty;

    public bool Deleted { get; set; }
}
