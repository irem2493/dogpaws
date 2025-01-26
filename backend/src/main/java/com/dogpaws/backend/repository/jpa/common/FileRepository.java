package com.dogpaws.backend.repository.jpa.common;

import com.dogpaws.backend.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    File findFileByFileRefNoAndFileGubnCode(String fileRefNo, String fileGubnCode);


}