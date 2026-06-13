package com.winter107r.cryptoanalyst.infrastructure.persistence.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface NewsJpaRepositoryJpa extends JpaRepository<NewsJpaEntity, String> {

    @Query("SELECT n FROM NewsJpaEntity n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY n.publishedAt DESC")
    List<NewsJpaEntity> searchByKeyword(@Param("kw") String keyword);
}
