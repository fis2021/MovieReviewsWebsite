package com.panteapaliuc.movie.reviews.service;

import com.panteapaliuc.movie.reviews.exception.MovieNotFoundException;
import com.panteapaliuc.movie.reviews.model.Image;
import com.panteapaliuc.movie.reviews.model.Movie;
import com.panteapaliuc.movie.reviews.model.Tag;
import com.panteapaliuc.movie.reviews.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final TagService tagService;
    private final ImageService imageService;

    public Movie addMovie(Movie movie)
    {
        if(movie.getMovieTags() == null)
            movie.setMovieTags(Collections.emptySet());
        for (Tag tag:movie.getMovieTags()) {
            if(!tagService.checkTagExists(tag))
                tagService.addTag(tag);
        }

        return movieRepository.save(movie);
    }

    public Movie addMoviePoster(MultipartFile posterImgFile, Long movieId) throws IOException
    {
        Movie movie= movieRepository.findMovieByMovieId(movieId)
                .orElseThrow(() -> new MovieNotFoundException(String.format("Movie with the id %d not found!", movieId)));

        if(movie.getPosterImg() != null)
            imageService.delImage(movie.getPosterImg());

        movie.setPosterImg(imageService.addImage(
                new Image(
                        posterImgFile.getOriginalFilename(),
                        posterImgFile.getContentType(),
                        posterImgFile.getBytes()
                ))
        );
        return movieRepository.save(movie);
    }

    public List<Movie> findAllMovies()
    {
        return movieRepository.findMoviesByIsEnabled(true);
    }

    public List<Movie> findAllMovieRequests()
    {
        return movieRepository.findMoviesByIsEnabled(false);
    }

    public Movie updateMovie(Movie movie)
    {
        return addMovie(movie);
    }

    public void enableMovie(Long movieId)
    {
        Movie movie = findMovie(movieId);
        movie.setIsEnabled(true);
        movieRepository.save(movie);
    }

    public Movie findMovie(Long movieId)
    {
        return movieRepository.findMovieByMovieId(movieId)
                .orElseThrow(() -> new MovieNotFoundException(String.format("Movie with the id %d not found!", movieId)));
    }

    public void deleteMovie(Long movieId)
    {
        movieRepository.deleteMovieByMovieId(movieId);
    }

    public List<Movie> findMoviesByTags(List<String> tagKeys){
        List<Movie> moviesByTags = movieRepository.findMoviesByMovieTagsTagKey(tagKeys.iterator().next());
        for (String tagKey: tagKeys.stream().skip(1).collect(Collectors.toList())) {
            moviesByTags = moviesByTags.stream()
                    .distinct()
                    .filter(movieRepository.findMoviesByMovieTagsTagKey(tagKey)::contains)
                    .collect(Collectors.toList());
            movieRepository.findMoviesByMovieTagsTagKey(tagKey);
        }
        return moviesByTags;
    }


}
