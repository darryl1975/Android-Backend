package sg.edu.nus.iss.todoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sg.edu.nus.iss.todoapp.network.TodoItem

/**
 * Custom ArrayAdapter that binds a list of [TodoItem] objects to
 * the item_todo.xml row layout for display inside a ListView.
 *
 * Why extend ArrayAdapter instead of BaseAdapter?
 * ArrayAdapter already manages the underlying data list, provides
 * getCount(), getItem(), and getItemId() for free, so we only need
 * to override getView() to control how each row looks.
 *
 * The second constructor argument (0) is the resource ID for a simple
 * row layout, which we pass as 0 because we supply our own layout
 * inside getView() rather than using the default TextView row.
 */
class TodoAdapter(context: Context, items: List<TodoItem>) :
    ArrayAdapter<TodoItem>(context, 0, items) {

    /**
     * LayoutInflater converts an XML layout file into a live View hierarchy.
     * Caching it here at construction time avoids calling LayoutInflater.from()
     * on every getView() call, which would be wasteful when the list scrolls.
     */
    private val inflater = LayoutInflater.from(context)

    /**
     * Called by the ListView for each visible row. The ListView recycles
     * off-screen rows and passes them back as [convertView] to avoid
     * inflating new Views on every scroll event (which is expensive).
     *
     * @param position    Index of the data item in the adapter's list.
     * @param convertView A recycled row View to reuse, or null if none is available.
     * @param parent      The ListView this view will be attached to (needed by inflate()).
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            // No recycled view available: inflate a fresh row from XML and
            // create a ViewHolder to cache the findViewById() lookups.
            // false = do not attach to parent yet; the ListView does that itself.
            view = inflater.inflate(R.layout.item_todo, parent, false)
            holder = ViewHolder(
                tvId = view.findViewById(R.id.tvItemId),
                tvTask = view.findViewById(R.id.tvItemTask),
                tvDescription = view.findViewById(R.id.tvItemDescription)
            )
            // Store the holder in the view's tag so it can be retrieved
            // on the next scroll without calling findViewById() again.
            view.tag = holder
        } else {
            // Reuse the recycled view and retrieve its cached ViewHolder.
            // This is the ViewHolder pattern: trading a small memory cost
            // (one object per row) for significantly faster scrolling.
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // Retrieve the data item at this position from the adapter's list.
        // getItem() can return null if the list is modified concurrently,
        // so the null check exits early to avoid a NullPointerException.
        val item = getItem(position) ?: return view

        holder.tvId.text = item.id.toString()
        holder.tvTask.text = item.task ?: "(no task)"

        // Description is optional: show it only when present to keep
        // rows compact for tasks that don't have a description.
        val desc = item.description
        if (!desc.isNullOrBlank()) {
            holder.tvDescription.text = desc
            holder.tvDescription.visibility = View.VISIBLE
        } else {
            // GONE removes the view from layout completely (no empty space),
            // unlike INVISIBLE which hides the view but still occupies space.
            holder.tvDescription.visibility = View.GONE
        }

        return view
    }

    /**
     * ViewHolder pattern: holds direct references to the child views of a
     * single row so that getView() does not call findViewById() on every
     * scroll event. ListView's view recycling means getView() is called
     * very frequently; removing repeated tree traversals is a key
     * performance optimisation for smooth 60 fps scrolling.
     *
     * Declared as a private data class because it is an implementation
     * detail of this adapter and needs no logic of its own.
     */
    private data class ViewHolder(
        val tvId: TextView,
        val tvTask: TextView,
        val tvDescription: TextView
    )
}
