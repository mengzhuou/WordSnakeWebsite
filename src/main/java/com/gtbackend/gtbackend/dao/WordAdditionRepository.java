package com.gtbackend.gtbackend.dao;

import com.gtbackend.gtbackend.model.WordAddition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordAdditionRepository extends JpaRepository<WordAddition, Integer> {
    @Query("SELECT COUNT(w) > 0 FROM WordAddition  w WHERE w.word = :word")
    boolean isWordForAdditionExist(@Param("word") String word);
    @Query(value = "SELECT * FROM word_additions", nativeQuery = true)
    List<String> getFromWordAddition();
}


