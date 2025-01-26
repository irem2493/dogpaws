package com.dogpaws.backend.service.common;

import com.dogpaws.backend.dto.common.FileDto;
import com.dogpaws.backend.entity.File;
import com.dogpaws.backend.repository.jpa.common.FileRepository;
import com.dogpaws.backend.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final FileUploadUtil fileUploadUtil;
    private final ModelMapper modelMapper;

    @Transactional
    public void saveFile(MultipartFile file,
                         String fileGbnCd,
                         String fileRefId,
                         String userId) throws IOException {

        FileDto fileDto = fileUploadUtil.saveFile(file, fileGbnCd, fileRefId, userId);

        try {
            File fileEntity = modelMapper.map(fileDto, File.class);
            fileRepository.save(fileEntity);
        } catch (Exception e) {
            fileUploadUtil.deleteFile(fileDto.getFileUrl());
            throw new RuntimeException("파일 데이터 저장 실패: " + e.getMessage(), e);
        }

    }

    /**
     * 파일 ID로 파일 삭제
     * @param fileId 파일 ID
     */
    @Transactional
    public void deleteFileById(Long fileId) {

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. ID: " + fileId));

        try {
            fileUploadUtil.deleteFile(file.getFileUrl());
            fileRepository.deleteById(fileId);
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage(), e);
        }

    }

    public File findFileByFileRefNoAndFileGubnCode(String fileRefNo, String fileGubnCode) {
        File file = fileRepository.findFileByFileRefNoAndFileGubnCode(fileRefNo, fileGubnCode);
        return file;
    }

}
