package sg.edu.nus.iss.todoapp.network

import com.google.gson.annotations.SerializedName

/**
 * Request body sent to POST /api/Todo.
 *
 * Kotlin data classes are ideal DTOs: the compiler auto-generates equals(),
 * hashCode(), copy(), and toString(), so no boilerplate is needed.
 *
 * @SerializedName maps each Kotlin property to the exact JSON key the API
 * expects. Without it, Gson would use the property name as-is, which works
 * here but becomes fragile if the API ever uses snake_case or the property
 * is renamed during a refactor.
 *
 * [task] is non-nullable (String) because the API marks it [Required].
 * [description] is nullable (String?) because the API accepts null/missing values.
 */
data class CreateTodoRequest(
    @SerializedName("task") val task: String,
    @SerializedName("description") val description: String?
)

/**
 * Represents a single to-do item returned by the API (GET or POST response).
 *
 * Mirrors the server-side TodoItem model:
 *   id          – auto-incremented primary key assigned by the database
 *   task        – short title of the task (nullable to match the server schema)
 *   description – optional longer description
 *   deleted     – soft-delete flag; the API filters these out by default
 *
 * This same class is reused for both the GET list response and the POST
 * created-item response, which keeps the model layer DRY.
 */
data class TodoItem(
    @SerializedName("id") val id: Int,
    @SerializedName("task") val task: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("deleted") val deleted: Boolean
)
