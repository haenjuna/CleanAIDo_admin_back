package org.zerock.cleanaido_admin_back.support.faq.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.cleanaido_admin_back.common.dto.PageRequestDTO;
import org.zerock.cleanaido_admin_back.common.dto.PageResponseDTO;
import org.zerock.cleanaido_admin_back.common.dto.UploadDTO;
import org.zerock.cleanaido_admin_back.common.util.CustomFileUtil;
import org.zerock.cleanaido_admin_back.support.common.entity.AttachFile;
import org.zerock.cleanaido_admin_back.support.faq.dto.FAQListDTO;
import org.zerock.cleanaido_admin_back.support.faq.dto.FAQReadDTO;
import org.zerock.cleanaido_admin_back.support.faq.dto.FAQRegisterDTO;
import org.zerock.cleanaido_admin_back.support.faq.entity.FAQ;
import org.zerock.cleanaido_admin_back.support.faq.repository.FAQRepository;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class FAQService {
    private final FAQRepository faqRepository;
    private final CustomFileUtil customFileUtil;

    public PageResponseDTO<FAQListDTO> listFAQ(PageRequestDTO pageRequestDTO) {

        if (pageRequestDTO.getPage() < 1) {
            throw new IllegalArgumentException("페이지 번호는 1이상 이어야 합니다.");
        }
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
        PageResponseDTO<FAQListDTO> response = faqRepository.list(pageRequestDTO);

        return response;

    }

    public PageResponseDTO<FAQListDTO> search(PageRequestDTO pageRequestDTO) {
        // SearchDTO에서 keyword를 가져옴
        String keyword = pageRequestDTO.getSearchDTO().getKeyword();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());

        // keyword를 기반으로 검색 수행
        Page<FAQ> resultPage = faqRepository.searchByKeyword(keyword, pageable);

        List<FAQListDTO> dtoList = resultPage.getContent().stream()
                .map(faq -> FAQListDTO.builder()
                        .fno(faq.getFno())
                        .question(faq.getQuestion())
                        .delFlag(faq.isDelFlag())
                        .build())
                .collect(Collectors.toList());

        return new PageResponseDTO<>(dtoList, pageRequestDTO, resultPage.getTotalElements());
    }


    public Long registerFAQ(FAQRegisterDTO dto, UploadDTO uploadDTO) {

        FAQ faq = FAQ.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .build();

        List<String> fileNames = Optional.ofNullable(uploadDTO.getFiles())
                .map(files -> Arrays.stream(files)
                        .filter(file -> !file.isEmpty()) // 실제 파일이 있는 경우만 필터링
                        .collect(Collectors.toList()))
                .filter(validFiles -> !validFiles.isEmpty()) // 빈 리스트는 제외
                .map(customFileUtil::saveFiles) // 유효한 파일이 있으면 저장
                .orElse(Collections.emptyList()); // 유효한 파일이 없으면 빈 리스트

        for (String fileName : fileNames) {
            faq.addFile(fileName);
        }

        faqRepository.save(faq);

        return faq.getFno();
    }

    public FAQReadDTO readFAQ(Long fno) {
        FAQ faq = faqRepository.getFAQ(fno);
        if (faq == null) {
            throw new EntityNotFoundException("게시물을 찾을 수 없습니다." + fno);
        }

        // AttachFile에서 fileName만 추출하여 List<String>으로 변환
        List<String> fileNames = faq.getAttachFiles().stream()
                .map(AttachFile::getFileName)
                .collect(Collectors.toList());

        return FAQReadDTO.builder()
                .fno(faq.getFno())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .delFlag(faq.isDelFlag())
                .fileNames(fileNames) // 추출한 fileNames 설정
                .build();
    }

    public Long updateFAQ(Long fno, FAQRegisterDTO dto, UploadDTO uploadDTO) {

        FAQ faq = faqRepository.findById(fno)
                .orElseThrow(() -> new EntityNotFoundException("FAQ not found" + fno));

        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());

        // 기존 파일 리스트
        List<String> oldFileNames = faq.getAttachFiles().stream()
                .map(AttachFile::getFileName)
                .collect(Collectors.toList());

        // 새로운 파일 리스트
        List<String> newFileNames = Optional.ofNullable(uploadDTO.getFiles())
                .map(files -> Arrays.stream(files)
                        .filter(file -> !file.isEmpty()) // 실제 파일이 있는 경우만 필터링
                        .collect(Collectors.toList()))
                .filter(validFiles -> !validFiles.isEmpty()) // 빈 리스트는 제외
                .map(customFileUtil::saveFiles) // 파일이 있으면 저장
                .orElse(Collections.emptyList()); // 파일이 없으면 빈 리스트

        // 삭제할 파일 리스트
        List<String> filesToDelete = oldFileNames.stream()
                .filter(oldFile -> !newFileNames.contains(oldFile))
                .collect(Collectors.toList());

        // 삭제할 파일이 있다면 실제 파일까지 삭제
        if (!filesToDelete.isEmpty()) {
            customFileUtil.deleteFiles(filesToDelete);
        }

        // 기존 파일 목록 초기화
        faq.clearFile();

        // 새로운 파일 add
        for (String newFileName : newFileNames) {
            faq.addFile(newFileName);
        }

        try {
            faqRepository.save(faq);
        } catch (DataAccessException e) {
            throw new RuntimeException("데이터 베이스 오류가 발생했습니다. " + e.getMessage());
        }

        return faq.getFno();
    }

    public void deleteFAQ(Long fno) {
        FAQ faq = faqRepository.findById(fno)
                .orElseThrow(() -> new EntityNotFoundException("FAQ not found " + fno));
        if (faq.isDelFlag() == true) {
            throw new IllegalStateException("이미 삭제된 FAQ입니다. " + fno);
        }

        // 실제 파일 삭제 처리
        List<String> fileNames = faq.getAttachFiles().stream()
                .map(AttachFile::getFileName)
                .collect(Collectors.toList());
        if (!fileNames.isEmpty()) {
            customFileUtil.deleteFiles(fileNames);
        }

        // 기존 파일 목록 초기화
        faq.clearFile();

        faq.setDelFlag(true);
        faqRepository.save(faq);
    }

}
