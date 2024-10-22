package org.zerock.cleanaido_admin_back.support.qna.controller;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.cleanaido_admin_back.common.dto.PageRequestDTO;
import org.zerock.cleanaido_admin_back.common.dto.PageResponseDTO;
import org.zerock.cleanaido_admin_back.support.qna.dto.QuestionListDTO;
import org.zerock.cleanaido_admin_back.support.qna.entity.Question;
import org.zerock.cleanaido_admin_back.support.qna.service.QNAService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/qna")
@Log4j2
@RequiredArgsConstructor
public class QNAController {

    private final QNAService qnaService;

    @GetMapping("list")
    public ResponseEntity<PageResponseDTO<QuestionListDTO>> list(PageRequestDTO pageRequestDTO) {

        return ResponseEntity.ok(qnaService.listQuestion(pageRequestDTO));

    }

    @GetMapping("read/{qno}")
    public String read(@PathVariable("qno") Long qno, Model model) {

        log.info("Reading question: " + qno);

        Optional<QuestionListDTO> result = qnaService.read(qno); // QNASearch 인터페이스 사용

        QuestionListDTO questionListDTO = result.orElseThrow(() -> new EntityNotFoundException("Question not found"));

        model.addAttribute("question", questionListDTO);

        log.info("Read question: " + questionListDTO);

        return "/qna/read"; // qna 읽기 페이지로 이동
    }
}
