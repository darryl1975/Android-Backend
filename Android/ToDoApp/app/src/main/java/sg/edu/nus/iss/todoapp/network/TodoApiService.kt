package sg.edu.nus.iss.todoapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service interface that declares the HTTP endpoints of the Todo API.
 *
 * Retrofit reads these annotations at runtime and generates a concrete
 * implementation, so no manual HttpURLConnection or OkHttp request building
 * is needed. Each method corresponds to one API endpoint.
 */
interface TodoApiService {

    /**
     * GET /api/Todo
     *
     * Fetches all non-deleted to-do items by default. Passing includeDeleted=true
     * also returns soft-deleted records.
     *
     * @Query appends the parameter as a URL query string:
     *   http://10.0.2.2:5211/api/Todo?includeDeleted=false
     *
     * The return type Call<List<TodoItem>> lets us choose between synchronous
     * (.execute()) and asynchronous (.enqueue()) execution. We always use
     * .enqueue() on the main thread to avoid blocking the UI.
     */
    @GET("api/" +
            "")
    fun getTodos(@Query("includeDeleted") includeDeleted: Boolean = false): Call<List<TodoItem>>

    /**
     * POST /api/Todo
     *
     * Creates a new to-do item. @Body serialises the [CreateTodoRequest] object
     * to JSON and sets Content-Type: application/json automatically.
     * On success the API responds with HTTP 201 and returns the saved item
     * (including the server-assigned id).
     */
    @POST("api/Todo")
    fun createTodo(@Body request: CreateTodoRequest): Call<TodoItem>

    companion object {
        /**
         * The Android emulator runs inside a virtual machine. The address
         * 10.0.2.2 is a special alias that the emulator networking stack
         * routes to the host machine's loopback (127.0.0.1), so this reaches
         * the .NET API running on localhost:5211 of the developer's machine.
         *
         * On a physical device, replace this with the machine's LAN IP address.
         */
        private const val BASE_URL = "http://10.0.2.2:5211/"

        /**
         * Factory method that builds and returns a configured TodoApiService.
         *
         * Using a companion object factory keeps construction logic in one place
         * and avoids scattering Retrofit setup across multiple screens.
         *
         * Steps:
         * 1. HttpLoggingInterceptor – OkHttp interceptor that prints every
         *    request/response body to Logcat. Level.BODY is useful during
         *    development; set to NONE for production builds.
         *
         * 2. OkHttpClient – the underlying HTTP engine. Interceptors are
         *    middleware: they run on every request/response passing through
         *    the client, making logging, auth headers, and retries easy to add.
         *
         * 3. Retrofit.Builder – assembles the Retrofit instance:
         *    - baseUrl: the common prefix prepended to every @GET/@POST path
         *    - addConverterFactory(GsonConverterFactory): tells Retrofit to use
         *      Gson to serialise request bodies to JSON and deserialise response
         *      JSON back into Kotlin data classes automatically
         *
         * 4. .create(TodoApiService::class.java) – Retrofit generates a dynamic
         *    proxy that implements the interface at runtime.
         */
        fun create(): TodoApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoApiService::class.java)
        }
    }
}
