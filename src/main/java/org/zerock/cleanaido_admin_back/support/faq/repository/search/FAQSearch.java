package org.zerock.cleanaido_admin_back.support.faq.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.cleanaido_admin_back.support.faq.dto.FAQListDTO;
import org.zerock.cleanaido_admin_back.support.faq.entity.FAQ;

import java.util.List;

public interface FAQSearch {
    Page<FAQ> list(Pageable pageable);

    FAQ getFAQ(Long fno);

    List<FAQListDTO> convertToDTOList(Pageable pageable);

    Page<FAQ> searchByTitle(String keyword, Pageable pageable);

}