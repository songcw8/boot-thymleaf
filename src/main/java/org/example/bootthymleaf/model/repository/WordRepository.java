package org.example.bootthymleaf.model.repository;

import org.example.bootthymleaf.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// UUID 썼으므로 long이 아니라 String
@Repository
public interface WordRepository extends JpaRepository<Word, String> {

    //findAll -> PK (Primary Key : 기본키)
    List<Word> findAllByOrderByCreateAtDesc(); // 최신거부터 정렬
}
