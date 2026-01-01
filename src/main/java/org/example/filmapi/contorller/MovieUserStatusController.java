package org.example.filmapi.contorller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.filmapi.model.dto.request.MovieUserStatusRequest;
import org.example.filmapi.model.dto.response.GenericResponse;
import org.example.filmapi.model.dto.response.MovieUserStatusResponse;
import org.example.filmapi.service.MovieUserStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/profile/movies")
@RequiredArgsConstructor
@Tag(name = "User Movie Status", description = "User's movie status operations (favorites, watched)")
public class MovieUserStatusController {
    private final MovieUserStatusService movieUserStatusService;

    @Operation(
            summary = "Update movie status",
            description = "Update favorite/watched status for a movie. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PutMapping("/{movieId}/status")
    public GenericResponse<Void> updateStatus(
            @PathVariable Long movieId,
            @RequestBody MovieUserStatusRequest request) {
        movieUserStatusService.updateStatus(movieId, request);
        return new GenericResponse<>(true, "Movie status updated successfully", null);
    }

    @Operation(
            summary = "Get movie status",
            description = "Get user's status for a specific movie. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/{movieId}/status")
    public GenericResponse<MovieUserStatusResponse> getStatus(@PathVariable Long movieId) {
        MovieUserStatusResponse response = movieUserStatusService.getStatus(movieId);
        return new GenericResponse<>(true, "Movie status retrieved successfully", response);
    }

    @Operation(
            summary = "Get all my movies",
            description = "Get all movies with user's status. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public GenericResponse<List<MovieUserStatusResponse>> getMyMovies() {
        List<MovieUserStatusResponse> response = movieUserStatusService.getMyMovies();
        return new GenericResponse<>(true, "Movies retrieved successfully", response);
    }

    @Operation(
            summary = "Get my favorite movies",
            description = "Get all movies marked as favorite. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite movies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/favorites")
    public GenericResponse<List<MovieUserStatusResponse>> getMyFavorites() {
        List<MovieUserStatusResponse> response = movieUserStatusService.getMyFavorites();
        return new GenericResponse<>(true, "Favorite movies retrieved successfully", response);
    }

    @Operation(
            summary = "Get my watched movies",
            description = "Get all movies marked as watched. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watched movies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/watched")
    public GenericResponse<List<MovieUserStatusResponse>> getMyWatched() {
        List<MovieUserStatusResponse> response = movieUserStatusService.getMyWatched();
        return new GenericResponse<>(true, "Watched movies retrieved successfully", response);
    }
}
