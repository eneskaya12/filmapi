package org.example.cinecore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinecore.model.dto.request.MovieCreateRequest;
import org.example.cinecore.model.dto.request.MovieUpdateRequest;
import org.example.cinecore.model.dto.response.GenericResponse;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.dto.response.PagedResponse;
import org.example.cinecore.service.MovieService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Movie management operations")
public class MovieController {
    private final MovieService movieService;

    @Operation(
            summary = "Add a new movie",
            description = "Creates a new movie. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PostMapping
    public ResponseEntity<GenericResponse<Void>> addMovie(@RequestBody @Valid MovieCreateRequest request) {
        movieService.addMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Movie added successfully", null));
    }

    @Operation(
            summary = "Get movie by ID",
            description = "Retrieves movie details by its ID. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<MovieResponse>> getMovieById(@PathVariable Long id) {
        MovieResponse response = movieService.getMovieById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Movie details retrieved successfully", response));
    }

    @Operation(
            summary = "Get all movies",
            description = "Returns a paginated list of all movies. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<GenericResponse<PagedResponse<MovieResponse>>> getAllMovies(
            @ParameterObject @PageableDefault(page = 0, size = 5) Pageable pageable) {
        PagedResponse<MovieResponse> response = movieService.getAllMovies(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Movies retrieved successfully", response));
    }

    @Operation(
            summary = "Update movie",
            description = "Updates an existing movie. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> updateMovie(@PathVariable Long id, @RequestBody @Valid MovieUpdateRequest request) {
        movieService.updateMovie(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Movie updated successfully", null));
    }

    @Operation(
            summary = "Delete movie",
            description = "Deletes a movie by its ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Movie deleted successfully", null));
    }
}
