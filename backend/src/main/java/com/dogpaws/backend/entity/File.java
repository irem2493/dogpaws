package com.dogpaws.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_file")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_no")
    private int fileNo;            // 파일번호

    @Column(name="file_gubn_code", length = 50)
    private String fileGubnCode;   // 파일구분

    @Column(name="file_ref_no")
    private String fileRefNo;     // 파일 영향받는 아이디

    @Column(name="file_old_name", length = 255)
    private String fileOldName;    // 파일 관리명

    @Column(name="file_new_name", length = 255)
    private String fileNewName;    // 파일명

    @Column(name="file_ext", length = 50)
    private String fileExt;        // 파일 확장자

    @Column(name="file_size")
    private Integer fileSize;      // 파일 크기 (단위: MB)

    @Column(name="file_url", length = 255)
    private String fileUrl;
}

