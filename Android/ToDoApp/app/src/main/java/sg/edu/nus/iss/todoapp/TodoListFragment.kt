package sg.edu.nus.iss.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sg.edu.nus.iss.todoapp.network.TodoApiService
import sg.edu.nus.iss.todoapp.network.TodoItem

/**
 * Fragment that fetches all active to-do items from the API and displays
 * them in a scrollable ListView.
 *
 * UI states
 * ─────────
 * This fragment manages four mutually exclusive UI states:
 *   Loading  – spinner visible, list hidden, Refresh button disabled
 *   Success  – list visible with data (or empty-state text if the list is empty)
 *   Empty    – dedicated message when the API returns an empty array
 *   Error    – error text when the HTTP call fails or returns a non-2xx code
 *
 * Each state is expressed by toggling View.VISIBLE / View.GONE on the
 * relevant views. Only one set of views is shown at a time.
 */
class TodoListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView   // shown when API returns []
    private lateinit var tvError: TextView   // shown on network/HTTP failure
    private lateinit var btnRefresh: Button

    /**
     * Lazy Retrofit service: constructed once on first use. Using lazy here
     * means the OkHttp client and Retrofit instance are not built until the
     * user actually reaches this tab, which keeps startup time low.
     */
    private val api by lazy { TodoApiService.create() }

    /**
     * Inflates fragment_todo_list.xml. The container FrameLayout in
     * MainActivity is passed as [container] so that inflate() can resolve
     * LayoutParams, but attachToRoot=false prevents premature attachment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_todo_list, container, false)

    /**
     * Binds view references and triggers the initial data load.
     * loadTodos() is called here (not in onCreateView) because it updates
     * the View hierarchy, which is only safe after the view tree is built.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listViewTodos)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        tvError = view.findViewById(R.id.tvError)
        btnRefresh = view.findViewById(R.id.btnRefresh)

        btnRefresh.setOnClickListener { loadTodos() }
        loadTodos()
    }

    /**
     * onResume() is called every time this Fragment becomes the active,
     * visible screen — including when the user switches back from the
     * Create Todo tab. Reloading here ensures a task saved on the
     * Create Todo tab appears in the list immediately without needing
     * a manual Refresh tap.
     *
     * Note: because MainActivity uses FragmentTransaction.replace(), the
     * Fragment is destroyed and recreated on each tab switch. This means
     * onViewCreated() and onResume() both fire on every visit, so
     * loadTodos() effectively runs twice on the first entry. The double
     * call is harmless and acceptable for this use case; a more optimised
     * approach would use ViewModel + LiveData to cache the result.
     */
    override fun onResume() {
        super.onResume()
        loadTodos()
    }

    /**
     * Issues GET /api/Todo and routes the response to the appropriate
     * UI-state method. Called on fresh load, on resume, and on Refresh tap.
     *
     * Retrofit's enqueue() runs the HTTP request on a background thread and
     * delivers the callback on the main thread, keeping the UI thread free.
     */
    private fun loadTodos() {
        showLoading()

        api.getTodos().enqueue(object : Callback<List<TodoItem>> {
            override fun onResponse(
                call: Call<List<TodoItem>>,
                response: Response<List<TodoItem>>
            ) {
                // isAdded check: the user may switch tabs before the response
                // arrives, which destroys this Fragment's view. Updating a
                // destroyed view throws an exception, so we exit early.
                if (!isAdded) return
                if (response.isSuccessful) {
                    // response.body() can be null even on a 200 if the server
                    // sends an empty body; elvis to emptyList() handles that safely.
                    showTodos(response.body() ?: emptyList())
                } else {
                    showError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<TodoItem>>, t: Throwable) {
                // Covers no internet, host unreachable, SSL errors, timeouts, etc.
                if (!isAdded) return
                showError("Network error: ${t.message}")
            }
        })
    }

    /**
     * Transitions to the Loading state: spinner on, everything else off.
     * The Refresh button is disabled so the user cannot stack multiple
     * concurrent requests while one is already in flight.
     */
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        listView.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        tvError.visibility = View.GONE
        btnRefresh.isEnabled = false
    }

    /**
     * Transitions to the Success state.
     *
     * If [todos] is empty the API returned [] (no items in the database),
     * so we show the empty-state message instead of a blank list.
     *
     * When items are present, a new [TodoAdapter] is set on the ListView.
     * The adapter is the bridge between the data list and the View rows:
     * ListView calls adapter.getView() for each visible row as the user scrolls.
     *
     * requireContext() is the Fragment equivalent of 'this' in an Activity.
     * It returns the host Activity's Context and throws if the Fragment is
     * detached, which is preferable to a silent null dereference.
     */
    private fun showTodos(todos: List<TodoItem>) {
        progressBar.visibility = View.GONE
        btnRefresh.isEnabled = true

        if (todos.isEmpty()) {
            listView.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
            tvError.visibility = View.GONE
        } else {
            listView.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
            tvError.visibility = View.GONE
            listView.adapter = TodoAdapter(requireContext(), todos)
        }
    }

    /**
     * Transitions to the Error state: shows the error message and re-enables
     * the Refresh button so the user can retry without restarting the app.
     */
    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        listView.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        tvError.text = message
        tvError.visibility = View.VISIBLE
        btnRefresh.isEnabled = true
    }
}
