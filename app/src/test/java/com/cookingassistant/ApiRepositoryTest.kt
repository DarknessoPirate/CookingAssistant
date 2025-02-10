package com.cookingassistant
import com.cookingassistant.data.repositories.ApiRepository
import com.cookingassistant.data.DTO.*
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.Models.ApiErrorResponse
import com.cookingassistant.data.repositories.TokenRepository
import com.cookingassistant.data.network.AuthInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.buffer
import okio.source
import java.io.File

class ApiRepositoryTest {
    private val defaultUsername = "Darknesso5"
    private val defaultEmail = "testemail5@email.com"
    private val defaultPassword = "Test123"
    private val defaultNewPassword = "Test1234"
    private val defaultRecipeId = 34
    private val defaultFavouriteRecipeId = 800
    private var defaultReviewId = 1
    private var token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IjgiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiRGFya25lc3NvNSIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL2VtYWlsYWRkcmVzcyI6InRlc3RlbWFpbDVAZW1haWwuY29tIiwiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy9yb2xlIjoiVXNlciIsImV4cCI6MTczMjQ2NjM2OCwiaXNzIjoiaHR0cDovL2Nvb2tpbmdhc3Npc3RhbnQuY29tIiwiYXVkIjoiaHR0cDovL2Nvb2tpbmdhc3Npc3RhbnQuY29tIn0.upDtm_EqQo-Gx_df2SmBUK0a8_0K23wAyks4yuoFMmo"
    private lateinit var apiRepository: ApiRepository
    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setUp(){
        tokenRepository = mockk()

        every { tokenRepository.getToken() } returns token

        // Build the OkHttpClient with interceptors (including logging and auth)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(tokenRepository)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Build Retrofit with the OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:5080/api/") // Replace with actual base URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiRepository = retrofit.create(ApiRepository::class.java)
    }


    private fun convertListToMultipart(key: String, parts: List<String?>): List<MultipartBody.Part> {
        return parts.mapIndexed { index, value ->
            MultipartBody.Part.createFormData(key, null, RequestBody.create("text/plain".toMediaTypeOrNull(), value ?: ""))
        }
    }

    // Helper function to prepare a file as MultipartBody.Part for image upload
    private fun prepareFilePart(partName: String, filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    // ################################################################################
    // ################################## TESTS #######################################
    // ################################################################################

    // BadRequest + (username/email taken)
    // 204 NoContent +

    @Test
    fun `test register`() = runBlocking {
        val registerRequest = RegisterRequest(
            defaultUsername,
            defaultEmail,
            defaultPassword
        )
        val response = apiRepository.register(registerRequest)
        if (response.isSuccessful) {
            println("Success: ${response.body().toString()}")
        } else {
            val errorJson = response.errorBody()?.string()
            if (errorJson != null) {
                val apiError = Gson().fromJson(errorJson, ApiErrorResponse::class.java)

                // Display the errors
                apiError.errors.forEach { field, messages ->
                    messages.forEach { message ->
                        println("$field: $message")
                    }
                }
            }
        }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `test logIn`() = runBlocking {
        val logInRequest = LoginRequest(
            defaultEmail,
            defaultPassword
        )
        val response = apiRepository.logIn(logInRequest)
        if (response.isSuccessful) {
            println("Success: ${response.body().toString()}")
            token = response.body()?.token ?: token
            println("New token: $token")
        } else {
            val errorJson = response.errorBody()?.string()
            if (errorJson != null) {
                val apiError = Gson().fromJson(errorJson, ApiErrorResponse::class.java)

                // Display the errors
                apiError.errors.forEach { field, messages ->
                    messages.forEach { message ->
                        println("$field: $message")
                    }
                }
            }
        }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `test changePassword`() = runBlocking {
        val passwordChangeDTO = UserPasswordChangeDTO(
            defaultPassword,
            defaultNewPassword,
            defaultNewPassword
        )

        val response = apiRepository.changePassword(passwordChangeDTO)

        assertTrue(response.isSuccessful)
    }
    // GIT
    // Ok +
    @Test
    fun `test getAllIngredientsList`() = runBlocking {
        val response = apiRepository.getAllIngredientsList()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.isNotEmpty())
    }

    @Test
    fun `test getAllUnitsList`() = runBlocking {
        val response = apiRepository.getAllUnitList()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.isNotEmpty())
    }

    @Test
    fun `test getAllOccasionsList`() = runBlocking {
        val response = apiRepository.getAllOccasionsList()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.isNotEmpty())
    }

    @Test
    fun `test getAllDifficultiesList`() = runBlocking {
        val response = apiRepository.getAllDifficultiesList()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.isNotEmpty())
    }

    @Test
    fun `test getAllCategoriesList`() = runBlocking {
        val response = apiRepository.getAllCategoriesList()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.isNotEmpty())
    }

    // 404 user NOT FOUND +
    // 400 BAD REQUEST ( SAME RECIPE ) +
    // Ok NoContent +
    @Test
    fun `test addRecipeToFavourites`() = runBlocking {
        val recipeId = defaultFavouriteRecipeId // Use an actual recipe ID
        val response = apiRepository.addRecipeToFavourites(recipeId)
        assertTrue(response.isSuccessful)
    }

    // 404 NOT FOUND ---  NOT WORKING
    // 200 Ok +
    @Test
    fun `test getFavouriteRecipes`() = runBlocking {
        val response = apiRepository.getFavouriteRecipes()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
    }

    @Test
    fun `test checkIfRecipeInFavourites`() = runBlocking {
        val response = apiRepository.checkIfRecipeInFavourites(defaultFavouriteRecipeId)
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
    }

    // 404 NOT FOUND recipe +
    // 400 BAD REQUEST + (recipe not in favorites)
    // 204 NO CONTENT +
    @Test
    fun `test removeRecipeFromFavourites`() = runBlocking{

        val response = apiRepository.removeRecipeFromFavourites(defaultFavouriteRecipeId)
        assertTrue(response.isSuccessful)
    }

    // 204 NO CONTENT +
    @Test
    fun `test addProfilePicture`() = runBlocking {
        val imageInputStream = javaClass.classLoader.getResourceAsStream("pomocy.jpg")
            ?: throw IllegalStateException("Image file not found in resources")

        // Create RequestBody directly from InputStream using Okio for multipart upload
        val imageRequestBody = imageInputStream.source().buffer().readByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("imageData", "pomocy.jpg", imageRequestBody)

        val response = apiRepository.addProfilePicture(imagePart)
        assertTrue(response.isSuccessful)
    }


    // GIT
    @Test
    fun `test getUserProfilePicture`() = runBlocking {
        val response = apiRepository.getUserProfilePicture()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
    }

    // GIT
    @Test
    fun `test postRecipe`() = runBlocking {
        // Define test RecipePostDTO
        val recipe = RecipePostDTO(
            name = "Vegan Tacos",
            description = "Tacos with plant-based ingredients",
            imageData = null,
            serves = 4,
            difficultyId = 1,
            timeInMinutes = 20,
            categoryId = 14,
            ingredientNames = listOf("Tortillas", "Black Beans", "Avocado", "Salsa"),
            ingredientQuantities = listOf("4", "150", "1", "50"),
            ingredientUnits = listOf("pcs", "g", "pcs", "g"),
            occasionId = 6,
            caloricity = 350,
            steps = listOf("Prepare Ingredients", "Assemble Tacos", "Serve")
        )

        // Load the image from resources as InputStream
        val imageInputStream = javaClass.classLoader.getResourceAsStream("polsl.jpg")
            ?: throw IllegalStateException("Image file not found in resources")

        // Create RequestBody directly from InputStream using Okio for multipart upload
        val imageRequestBody = imageInputStream.source().buffer().readByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("ImageData", "polsl.jpg", imageRequestBody)

        // Prepare text fields as RequestBody
        val namePart = recipe.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = recipe.description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val servesPart = recipe.serves.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val difficultyIdPart = recipe.difficultyId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val timeInMinutesPart = recipe.timeInMinutes.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryIdPart = recipe.categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        // Convert lists to multipart with appropriate form key
        val ingredientNames = convertListToMultipart("IngredientNames", recipe.ingredientNames)
        val ingredientQuantities = convertListToMultipart("IngredientQuantities", recipe.ingredientQuantities)
        val ingredientUnits = convertListToMultipart("IngredientUnits", recipe.ingredientUnits)
        val occasionIdPart = recipe.occasionId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val caloricityPart = recipe.caloricity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val steps = convertListToMultipart("Steps", recipe.steps)



        // Call the API
        val response = apiRepository.postRecipe(
            namePart,
            descriptionPart,
            servesPart,
            difficultyIdPart,
            timeInMinutesPart,
            categoryIdPart,
            occasionIdPart,
            caloricityPart,
            ingredientNames,
            ingredientQuantities,
            ingredientUnits,
            steps,
            imagePart
        )


        assertTrue(response.isSuccessful)
    }

    // 200 OK +
    // 403 FORBIDDEN +
    @Test
    fun `test modifyRecipe`() = runBlocking {
        val recipe = RecipePostDTO(
            name = "Test Recipe",
            description = "This is a test recipe",
            imageData = null,
            serves = 2,
            difficultyId = 1,
            timeInMinutes = 20,
            categoryId = 14,
            ingredientNames = listOf("Tomato", "Onion"),
            ingredientQuantities = listOf("2", "1"),
            ingredientUnits = listOf("pcs", "pcs"),
            occasionId = 1,
            caloricity = 330,
            steps = listOf("ChopVegetables", "Cook"),
        )

        // Load the image from resources as InputStream
        val imageInputStream = javaClass.classLoader.getResourceAsStream("polsl.jpg")
            ?: throw IllegalStateException("Image file not found in resources")

        // Create RequestBody directly from InputStream using Okio for multipart upload
        val imageRequestBody = imageInputStream.source().buffer().readByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("ImageData", "polsl.jpg", imageRequestBody)

        // Prepare text fields as RequestBody
        val namePart = recipe.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = recipe.description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val servesPart = recipe.serves.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val difficultyIdPart = recipe.difficultyId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val timeInMinutesPart = recipe.timeInMinutes.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryIdPart = recipe.categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        // Convert lists to multipart with appropriate form key
        val ingredientNames = convertListToMultipart("IngredientNames", recipe.ingredientNames)
        val ingredientQuantities = convertListToMultipart("IngredientQuantities", recipe.ingredientQuantities)
        val ingredientUnits = convertListToMultipart("IngredientUnits", recipe.ingredientUnits)
        val occasionIdPart = recipe.occasionId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val caloricityPart = recipe.caloricity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val steps = convertListToMultipart("Steps", recipe.steps)

        // Call the API
        val response = apiRepository.modifyRecipe(
            1029,
            namePart,
            descriptionPart,
            servesPart,
            difficultyIdPart,
            timeInMinutesPart,
            categoryIdPart,
            occasionIdPart,
            caloricityPart,
            ingredientNames,
            ingredientQuantities,
            ingredientUnits,
            steps,
            imagePart
        )

        assertTrue(response.isSuccessful)
    }

    // GIT
    @Test
    fun `test getRecipeDetails`() = runBlocking {
        val recipeId = 1020 // Replace with an actual recipe ID
        val response = apiRepository.getRecipeDetails(recipeId)
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals(recipeId, response.body()?.id)
    }

    // Forbidden GIT
    // Delete own GIT
    @Test
    fun `test deleteRecipe`() = runBlocking {
        val recipeId = 1030 // Replace with an actual recipe ID to delete
        val response = apiRepository.deleteRecipe(recipeId)
        assertTrue(response.isSuccessful)
    }

    // GIT
    @Test
    fun `test getAllRecipes`() = runBlocking {

        val rq = RecipeQuery(
            SearchPhrase = "soup", // phrase or null
            Ingredients = listOf("carrot","chicken"), // list or null
            SortBy = null, // can skip if null ( i'll skip the next one) //  "Ratings", "TimeInMinutes", "Difficulty", "VoteCount", "Caloricity or null
            // SortDirection is here ( skipped because null ) // "Ascending" or "Descending" or null
            Difficulty = "easy", // // "easy", "medium", "hard"
            Category = null // // get categories from getAllCategoriesList()
            // Occasion skipped because null // get occasions from getAllOccasionsList()
            // PageNumber 1 by default ( skipped )
            // PageSize 10 by default (skipped)
        )

        val response = apiRepository.getAllRecipes(
            SearchPhrase = rq.SearchPhrase,
            IngredientsSearch = rq.Ingredients,
            SortBy = rq.SortBy,
            SortDirection = rq.SortDirection,
            FilterByDifficulty = rq.Difficulty,
            FilterByCategoryName = rq.Category,
            FilterByOccasion = rq.Occasion,
            PageNumber = rq.PageNumber,
            PageSize = rq.PageSize
        )
        assert(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.items.isNotEmpty())
    }

    // GIT
    @Test
    fun `test getAllRecipeNames`() = runBlocking {
        val response = apiRepository.getAllRecipeNames()
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.isNotEmpty())
    }

    // GIT
    @Test
    fun `test getRecipeImage`() = runBlocking {
        val recipeId = 900 // Replace with an actual recipe ID with an image
        val response = apiRepository.getRecipeImage(recipeId)
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
    }

    // Forbidden GIT
    // BadRequest GIT
    // NOT FOUND GIT
    // Post GIT
    @Test
    fun `test postReview`() = runBlocking {
        val recipeId = 901
        val review = ReviewPostDTO(value = 5, description = "Great recipeeee!")
        val response = apiRepository.postReview(recipeId, review)
        assertTrue(response.isSuccessful)
    }
    // FORBIDDEN NIE MA
    // NOT FOUND GIT
    // Modify GIT
    @Test
    fun `test modifyReview`() = runBlocking {
        val recipeId = 901
        val updatedReview = ReviewPostDTO(value = 4, description = "Jednak nie tak great")
        val response = apiRepository.modifyReview(recipeId, updatedReview)
        assertTrue(response.isSuccessful)
    }

    // Date Goes to string for now,
    // TODO : fix that later
    @Test
    fun `test getMyReview`() = runBlocking {
        val recipeId = 900 // Replace with an actual recipe ID
        val response = apiRepository.getMyReview(recipeId)
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
    }

    // NOT FOUND Git
    // GIT
    @Test
    fun `test getAllReviews`() = runBlocking {
        val recipeId = 900
        val response = apiRepository.getAllReviews(recipeId)
        //assertEquals(404,response.code())
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assert(response.body()!!.isNotEmpty())
    }

    // NOT FOUND GIT
    // Ok GIT
    @Test
    fun `test getReviewImage`() = runBlocking {
        val reviewId = 3
        val response = apiRepository.getReviewImage(reviewId)
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
    }

    // BadRequest GIT
    // NotFound GIT
    // DELETE GIT
    @Test
    fun `test deleteReview`() = runBlocking {
        val recipeId = 900
        val response = apiRepository.deleteReview(recipeId)
        assertTrue(response.isSuccessful)
        //assertTrue(response.isSuccessful)
    }

    @Test
    fun `test deleteAccount`() = runBlocking {
        val userDeleteRequest = UserDeleteRequest(defaultPassword)

        val response = apiRepository.deleteUser(userDeleteRequest)

        assertTrue(response.isSuccessful)
    }



}
