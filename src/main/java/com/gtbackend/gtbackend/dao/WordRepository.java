package com.gtbackend.gtbackend.dao;

import com.gtbackend.gtbackend.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

//write JPA Select query to fetch data from db
@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {

    @Query("SELECT w FROM words w")
    List<Word> findAll();

    @Query("SELECT CONCAT(w.word, ': ', w.definition) FROM words w WHERE w.word LIKE :inputWordLetter% AND LENGTH(w.word) >= 4")
    List<String> getHintWordAndDef(@Param("inputWordLetter") String inputWordLetter, Pageable pageable);

    @Query("SELECT w.definition FROM words w WHERE w.word = :inputWord")
    List<String> getWordAndDef(@Param("inputWord") String inputWord);

    @Query("SELECT w.word, w.definition FROM words w WHERE w.word = 'snake'")
    List<String> getWordAndDefTest();

    @Query("SELECT w.definition FROM words w WHERE w.word = 'snake'")
    List<String> getDefTest();

    @Query("SELECT COUNT(w) > 0 FROM words w WHERE w.word = :inputWord")
    boolean isWordExist(@Param("inputWord") String inputWord);
}
