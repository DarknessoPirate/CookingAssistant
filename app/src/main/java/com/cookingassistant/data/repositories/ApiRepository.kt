package com.cookingassistant.data.repositories
import com.cookingassistant.data.DTO.CategoriesGetDTO
import com.cookingassistant.data.DTO.DifficultiesGetDTO
import com.cookingassistant.data.DTO.LoginRequest
import com.cookingassistant.data.DTO.LoginResponse
import com.cookingassistant.data.DTO.OccasionsGetDTO
import com.cookingassistant.data.DTO.RecipeGetDTO
import com.cookingassistant.data.DTO.RecipeNameDTO
import com.cookingassistant.data.DTO.RecipePageResponse
import com.cookingassistant.data.DTO.RegisterRequest
import com.cookingassistant.data.DTO.ReviewGetDTO
import com.cookingassistant.data.DTO.ReviewPostDTO
import com.cookingassistant.data.DTO.UserDeleteRequest
import com.cookingassistant.data.DTO.UserPasswordChangeDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiRepository{


    // ##### AUTH CONTROLLER ###################################################
    // #
    // #
    @POST("users/login")
    suspend fun logIn(@Body loginRequest: LoginRequest):Response<LoginResponse>

    @POST("users/register")
    suspend fun register(@Body registerRequest: RegisterRequest):Response<Unit>
    // #
    // #
    // #########################################################################


    // ##### RESOURCE CONTROLLER ###############################################
    // #
    // #
    @GET("resources/ingredients")
    suspend fun getAllIngredientsList():Response<List<String>>

    @GET("resources/units")
    suspend fun getAllUnitList():Response<List<String>>

    @GET("resources/difficulties")
    suspend fun getAllDifficultiesList():Response<List<DifficultiesGetDTO>>

    @GET("resources/occasions")
    suspend fun getAllOccasionsList():Response<List<OccasionsGetDTO>>

    @GET("resources/categories")
    suspend fun getAllCategoriesList():Response<List<CategoriesGetDTO>>
    // #
    // #
    // #########################################################################


    // ##### USER CONTROLLER ###################################################
    // #
    // #
    @POST("users/favourite/{recipeId}")
    suspend fun addRecipeToFavourites(@Path("recipeId") recipeId:Int):Response<Unit>

    @DELETE("users/favourite-recipes/{recipeId}")
    suspend fun removeRecipeFromFavourites(@Path("recipeId") recipeId:Int):Response<Unit>

    @GET("users/favourite-recipes")
    suspend fun getFavouriteRecipes(
        @Query("SearchPhrase") SearchPhrase: String? = null,
        @Query("IngredientsSearch") IngredientsSearch: List<String>?  = null,
        @Query("SortBy") SortBy: String? = null, //  "Ratings", "TimeInMinutes", "Difficulty", "VoteCount", "Caloricity
        @Query("SortDirection") SortDirection: String? = null, // "Ascending" or "Descending"
        @Query("FilterByDifficulty") FilterByDifficulty: String? = null,
        @Query("FilterByCategoryName") FilterByCategoryName: String? = null,
        @Query("FilterByOccasion") FilterByOccasion: String? = null,
        @Query("PageNumber") PageNumber: Int? = null,
        @Query("PageSize") PageSize: Int? = null
    ):Response<RecipePageResponse>

    @GET("users/my-recipes")
    suspend fun getUserRecipes(
        @Query("SearchPhrase") SearchPhrase: String? = null,
        @Query("IngredientsSearch") IngredientsSearch: List<String>?  = null,
        @Query("SortBy") SortBy: String? = null, //  "Ratings", "TimeInMinutes", "Difficulty", "VoteCount", "Caloricity
        @Query("SortDirection") SortDirection: String? = null, // "Ascending" or "Descending"
        @Query("FilterByDifficulty") FilterByDifficulty: String? = null,
        @Query("FilterByCategoryName") FilterByCategoryName: String? = null,
        @Query("FilterByOccasion") FilterByOccasion: String? = null,
        @Query("PageNumber") PageNumber: Int? = null,
        @Query("PageSize") PageSize: Int? = null
    ):Response<RecipePageResponse>

    @GET("users/favourite-recipes/{recipeId}/is-favourite")
    suspend fun checkIfRecipeInFavourites(@Path("recipeId") recipeId: Int):Response<Boolean>

    @GET("users/recipes/{recipeId}/is-mine")
    suspend fun checkIfRecipeIsCreatedByUser(@Path("recipeId") recipeId: Int):Response<Boolean>

    @GET("users/image")
    suspend fun getUserProfilePicture():Response<ResponseBody>

    @Multipart
    @POST("users/image")
    suspend fun addProfilePicture(@Part image: MultipartBody.Part):Response<Unit>

    @PUT("users/change-password")
    suspend fun changePassword(@Body passwordChangeDTO: UserPasswordChangeDTO): Response<Unit>

    // TODO: TEST
    @POST("users/delete")
    suspend fun deleteUser(@Body userDeleteRequest: UserDeleteRequest): Response<Unit>
    // #
    // #
    // #########################################################################


    // ##### RECIPE CONTROLLER #################################################
    // #
    // #
    @Multipart
    @POST("recipes")
    suspend fun postRecipe(
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody?,
        @Part("Serves") serves: RequestBody,
        @Part("DifficultyId") difficultyId: RequestBody?,
        @Part("TimeInMinutes") timeInMinutes: RequestBody,
        @Part("CategoryId") categoryId: RequestBody,
        @Part("OccasionId") occasionId:RequestBody,
        @Part("Caloricity") caloricity:RequestBody,
        @Part ingredientNames: List<MultipartBody.Part>,
        @Part ingredientQuantities: List<MultipartBody.Part>,
        @Part ingredientUnits: List<MultipartBody.Part>,
        @Part steps: List<MultipartBody.Part>,
        @Part image: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @PUT("recipes/{recipeId}")
    suspend fun modifyRecipe(
        @Path("recipeId") recipeId: Int,
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody?,
        @Part("Serves") serves: RequestBody,
        @Part("DifficultyId") difficultyId: RequestBody?,
        @Part("TimeInMinutes") timeInMinutes: RequestBody,
        @Part("CategoryId") categoryId: RequestBody,
        @Part("OccasionId") occasionId:RequestBody,
        @Part("Caloricity") caloricity:RequestBody,
        @Part ingredientNames: List<MultipartBody.Part>,
        @Part ingredientQuantities: List<MultipartBody.Part>,
        @Part ingredientUnits: List<MultipartBody.Part>,
        @Part steps: List<MultipartBody.Part>,
        @Part image: MultipartBody.Part
    ): Response<Unit>

    @DELETE("recipes/{recipeId}")
    suspend fun deleteRecipe(@Path("recipeId") recipeId: Int):Response<Unit>

    @GET("recipes/{recipeId}")
    suspend fun getRecipeDetails(@Path("recipeId") recipeId:Int):Response<RecipeGetDTO>

    @GET("recipes")
    suspend fun getAllRecipes(
        @Query("SearchPhrase") SearchPhrase: String? = null,
        @Query("IngredientsSearch") IngredientsSearch: String?  = null,
        @Query("SortBy") SortBy: String? = null, //  "Ratings", "TimeInMinutes", "Difficulty", "VoteCount", "Caloricity
        @Query("SortDirection") SortDirection: String? = null, // "Ascending" or "Descending"
        @Query("FilterByDifficulty") FilterByDifficulty: String? = null,
        @Query("FilterByCategoryName") FilterByCategoryName: String? = null,
        @Query("FilterByOccasion") FilterByOccasion: String? = null,
        @Query("PageNumber") PageNumber: Int? = null,
        @Query("PageSize") PageSize: Int? = null
    ): Response<RecipePageResponse>

    @GET("recipes/names")
    suspend fun getAllRecipeNames():Response<List<RecipeNameDTO>>

    @GET("recipes/daily-recipe")
    suspend fun getDailyRecipe():Response<RecipeGetDTO>

    @GET("recipes/image/{recipeId}")
    suspend fun getRecipeImage(@Path("recipeId") recipeId:Int):Response<ResponseBody>

    @GET("recipes/pdf/{recipeId}")
    suspend fun downloadRecipePdf(@Path("recipeId") recipeId: Int): Response<ResponseBody>

    // #
    // #
    // #########################################################################


    // ###### REVIEW CONTROLLER ################################################
    // #
    // #
    @POST("reviews/{recipeId}")
    suspend fun postReview(@Path("recipeId") recipeId: Int, @Body reviewPostDTO: ReviewPostDTO):Response<Unit>


    @PUT("reviews/{recipeId}/modify")
    suspend fun modifyReview(@Path("recipeId") recipeId: Int, @Body reviewPostDTO: ReviewPostDTO):Response<Unit>

    @DELETE("reviews/{recipeId}/delete")
    suspend fun deleteReview(@Path("recipeId") recipeId: Int) :Response<Unit>

    @GET("reviews/{recipeId}/my-review")
    suspend fun getMyReview(@Path("recipeId") recipeId:Int):Response<ReviewGetDTO>

    @GET("reviews/{recipeId}")
    suspend fun getAllReviews(@Path("recipeId") recipeId: Int):Response<List<ReviewGetDTO>>

    @GET("reviews/{reviewId}/image")
    suspend fun getReviewImage(@Path("reviewId") reviewId: Int):Response<ResponseBody>
    // #
    // #
    // #########################################################################

}