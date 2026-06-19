package sg.edu.nus.iss.todoapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sg.edu.nus.iss.todoapp.network.CreateTodoRequest
import sg.edu.nus.iss.todoapp.network.TodoApiService
import sg.edu.nus.iss.todoapp.network.TodoItem

/**
 * Fragment that presents the Create Todo form and posts the data to the API.
 *
 * Fragment lifecycle relevant to this screen
 * ───────────────────────────────────────────
 * onCreateView()  – inflate the XML layout and return its root View.
 *                   View references must NOT be stored here because the
 *                   view hierarchy is not yet fully attached to the window.
 * onViewCreated() – the view is ready; safe to call findViewById() and
 *                   attach listeners.
 * onDestroyView() – called when the Fragment is replaced by another one.
 *                   The Fragment instance may survive (it stays on the
 *                   back-stack), but its view is destroyed to free memory.
 *
 * View references are declared with lateinit var rather than nullable types
 * because they are guaranteed to be initialised in onViewCreated() before
 * any other method that uses them is called.
 */
class CreateTodoFragment : Fragment() {

    // TextInputLayout wraps the EditText to provide the floating label,
    // error messages, and character counter from the Material Design spec.
    private lateinit var tilTask: TextInputLayout
    private lateinit var etTask: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var tvStatus: TextView

    /**
     * Lazy initialisation: the Retrofit service is created the first time
     * [api] is accessed, not at Fragment construction time. This is safe
     * because [api] is only accessed after the Fragment is attached and
     * a user interaction occurs (button tap), by which point the Fragment
     * is fully initialised.
     */
    private val api by lazy { TodoApiService.create() }

    /**
     * Inflates fragment_create_todo.xml into a View hierarchy and returns
     * the root view to the Fragment manager, which attaches it to the
     * FrameLayout container in MainActivity.
     *
     * [attachToRoot] is false because the Fragment manager handles
     * attachment itself; passing true would cause an IllegalStateException.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_create_todo, container, false)

    /**
     * Called immediately after onCreateView(). The [view] parameter is the
     * inflated root returned above. All view-binding and listener setup
     * belongs here, not in onCreateView().
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tilTask = view.findViewById(R.id.tilTask)
        etTask = view.findViewById(R.id.etTask)
        etDescription = view.findViewById(R.id.etDescription)
        btnSave = view.findViewById(R.id.btnSave)
        tvStatus = view.findViewById(R.id.tvStatus)

        btnSave.setOnClickListener { submitTodo() }
    }

    /**
     * Validates the form and sends a POST /api/Todo request.
     *
     * Validation is intentionally done on the client before the network call
     * to give the user instant feedback without waiting for a round-trip.
     */
    private fun submitTodo() {
        // trim() strips leading/trailing whitespace so a blank space does not
        // pass as a valid task name.
        val task = etTask.text?.toString()?.trim() ?: ""
        // ifEmpty { null } converts an empty string to null so the API receives
        // a proper JSON null rather than an empty string "".
        val description = etDescription.text?.toString()?.trim()?.ifEmpty { null }

        if (task.isEmpty()) {
            // TextInputLayout.error displays a red message below the field
            // and changes the outline colour to the error colour automatically.
            tilTask.error = "Task is required"
            return
        }
        // Clear any previous error once the field is valid.
        tilTask.error = null

        setLoading(true)

        /**
         * Retrofit's enqueue() executes the HTTP request on a background
         * thread managed by OkHttp's thread pool. The two Callback methods
         * are delivered back on the main (UI) thread, so it is safe to
         * update Views directly inside them without posting to a Handler.
         */
        api.createTodo(CreateTodoRequest(task = task, description = description))
            .enqueue(object : Callback<TodoItem> {
                override fun onResponse(call: Call<TodoItem>, response: Response<TodoItem>) {
                    // isAdded guards against the edge case where the user
                    // switches tabs before the network response arrives,
                    // causing the Fragment's view to be destroyed. Touching
                    // a detached Fragment's views would throw an exception.
                    if (!isAdded) return
                    setLoading(false)
                    if (response.isSuccessful) {
                        // HTTP 2xx: the item was persisted; show its server-assigned id.
                        showStatus("Saved! Task ID: ${response.body()?.id}", success = true)
                        clearForm()
                    } else {
                        // HTTP 4xx/5xx: surface the status code and message for debugging.
                        showStatus("Error ${response.code()}: ${response.message()}", success = false)
                    }
                }

                override fun onFailure(call: Call<TodoItem>, t: Throwable) {
                    // onFailure is called for network-level errors (no connection,
                    // timeout, DNS failure) rather than HTTP error status codes.
                    if (!isAdded) return
                    setLoading(false)
                    showStatus("Network error: ${t.message}", success = false)
                }
            })
    }

    /**
     * Disables the Save button and changes its label during the network call
     * to prevent duplicate submissions if the user taps repeatedly.
     */
    private fun setLoading(loading: Boolean) {
        btnSave.isEnabled = !loading
        btnSave.text = if (loading) "Saving..." else "Save"
    }

    /**
     * Displays a coloured status message below the Save button.
     * Green (#388E3C) for success, red (#D32F2F) for failure.
     * The view starts as GONE in XML and is made VISIBLE only when there is
     * a message to show, keeping the layout clean on first load.
     */
    private fun showStatus(message: String, success: Boolean) {
        tvStatus.text = message
        tvStatus.setTextColor(if (success) Color.parseColor("#388E3C") else Color.parseColor("#D32F2F"))
        tvStatus.visibility = View.VISIBLE
    }

    /** Resets the form fields after a successful save so the user can enter another task. */
    private fun clearForm() {
        etTask.text?.clear()
        etDescription.text?.clear()
    }
}
