package com.panteapaliuc.movie.reviews.repository;

import com.panteapaliuc.movie.reviews.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findReviewsByMovieMovieId(Long MovieId);

    List<Review> findReviewsByUserUsernameAndMovieMovieId(String username, Long movieId);

}
