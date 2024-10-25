package org.zerock.cleanaido_admin_back.support.faq.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.cleanaido_admin_back.support.faq.entity.FAQ;
import org.zerock.cleanaido_admin_back.support.faq.repository.search.FAQSearch;

public interface FAQRepository extends JpaRepository<FAQ, Long>, FAQSearch {

    @Override
    Page<FAQ> searchByTitle(String keyword, Pageable pageable); // 제목으로 검색 메서드
}