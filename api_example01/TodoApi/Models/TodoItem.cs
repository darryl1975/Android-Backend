using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace TodoApi.Models;

[Table("to_do")]
public class TodoItem
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Column("id")]
    public int Id { get; set; }

    [Column("deleted")]
    public bool Deleted { get; set; } = false;

    [Column("description")]
    [MaxLength(255)]
    public string? Description { get; set; }

    [Column("task")]
    [MaxLength(255)]
    public string? Task { get; set; }
}
