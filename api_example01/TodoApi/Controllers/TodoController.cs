using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using TodoApi.Data;
using TodoApi.DTOs;
using TodoApi.Models;

namespace TodoApi.Controllers;

[ApiController]
[Route("api/[controller]")]
[Produces("application/json")]
public class TodoController(TodoDbContext db) : ControllerBase
{
    /// <summary>Get all to-do items.</summary>
    /// <param name="includeDeleted">When true, includes soft-deleted items.</param>
    [HttpGet]
    [ProducesResponseType<IEnumerable<TodoItem>>(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetAll([FromQuery] bool includeDeleted = false)
    {
        var query = db.TodoItems.AsQueryable();
        if (!includeDeleted)
            query = query.Where(t => !t.Deleted);

        return Ok(await query.ToListAsync());
    }

    /// <summary>Get a single to-do item by ID.</summary>
    [HttpGet("{id:int}")]
    [ProducesResponseType<TodoItem>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetById(int id)
    {
        var item = await db.TodoItems.FindAsync(id);
        return item is null ? NotFound() : Ok(item);
    }

    /// <summary>Create a new to-do item.</summary>
    [HttpPost]
    [ProducesResponseType<TodoItem>(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> Create([FromBody] CreateTodoItemDto dto)
    {
        var item = new TodoItem
        {
            Task = dto.Task,
            Description = dto.Description
        };

        db.TodoItems.Add(item);
        await db.SaveChangesAsync();

        return CreatedAtAction(nameof(GetById), new { id = item.Id }, item);
    }

    /// <summary>Update an existing to-do item.</summary>
    [HttpPut("{id:int}")]
    [ProducesResponseType<TodoItem>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Update(int id, [FromBody] UpdateTodoItemDto dto)
    {
        var item = await db.TodoItems.FindAsync(id);
        if (item is null) return NotFound();

        item.Task = dto.Task;
        item.Description = dto.Description;
        item.Deleted = dto.Deleted;

        await db.SaveChangesAsync();
        return Ok(item);
    }

    /// <summary>Soft-delete a to-do item (sets deleted = true).</summary>
    [HttpDelete("{id:int}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Delete(int id)
    {
        var item = await db.TodoItems.FindAsync(id);
        if (item is null) return NotFound();

        item.Deleted = true;
        await db.SaveChangesAsync();
        return NoContent();
    }
}
