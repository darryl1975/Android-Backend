package sg.edu.nus.iss.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Application entry point and single-Activity host for the Bottom Navigation pattern.
 *
 * Architecture overview
 * ─────────────────────
 * This app uses a single-Activity architecture: MainActivity owns the window
 * and navigation chrome (the BottomNavigationView), while each screen's UI and
 * logic lives in a Fragment. This avoids the overhead of creating and destroying
 * entire Activity windows when the user switches tabs.
 *
 * Layout: activity_main.xml
 *   ┌─────────────────────────┐
 *   │  FrameLayout            │  ← fragmentContainer: swap area for Fragments
 *   │  (fills remaining space)│
 *   ├─────────────────────────┤
 *   │  BottomNavigationView   │  ← fixed at the bottom
 *   └─────────────────────────┘
 *
 * AppCompatActivity provides backwards-compatible access to the Fragment
 * back-stack manager (supportFragmentManager) and Material theming support.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the Activity is first created. At this point the window
     * exists but is not yet visible to the user.
     *
     * [savedInstanceState] is non-null when the Activity is being recreated
     * after a configuration change (e.g. screen rotation) or a process death.
     * The Fragment manager automatically restores the previously shown Fragment
     * in that case, so we skip the initial fragment load to avoid stacking
     * a duplicate on top of the restored one.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate activity_main.xml and set it as the content view.
        // This creates the FrameLayout container and BottomNavigationView.
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Only load the default fragment on a fresh start.
        // When savedInstanceState != null the system has already restored
        // the fragment back-stack, so adding another fragment would cause
        // the create form to appear twice.
        if (savedInstanceState == null) {
            loadFragment(CreateTodoFragment())
        }

        /**
         * setOnItemSelectedListener fires whenever the user taps a tab,
         * including the currently selected one. The lambda must return true
         * to indicate the selection was handled and the tab should be
         * visually highlighted; returning false leaves the previous tab selected.
         */
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_create_todo -> { loadFragment(CreateTodoFragment()); true }
                R.id.nav_todo_list   -> { loadFragment(TodoListFragment()); true }
                else -> false
            }
        }
    }

    /**
     * Swaps the Fragment displayed inside [R.id.fragmentContainer].
     *
     * FragmentTransaction.replace() removes whatever Fragment is currently
     * in the container and adds the new one. This means each tab switch
     * destroys the old Fragment and creates a fresh one, which guarantees
     * the Todo List always shows up-to-date data when the user returns to it.
     *
     * commit() schedules the transaction on the main thread's message queue;
     * it is not executed immediately but is safe to call from onCreate().
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
