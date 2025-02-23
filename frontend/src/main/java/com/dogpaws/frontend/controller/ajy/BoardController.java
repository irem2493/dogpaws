package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.dto.ajy.BoardResponseDto;
import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.service.ApiRequestService;
<<<<<<< HEAD
import com.dogpaws.frontend.utils.PagingBtn;
=======
>>>>>>> origin/REQ-68-관리자
import com.dogpaws.frontend.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestParam;
=======
>>>>>>> origin/REQ-68-관리자

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final ApiRequestService apiRequestService;

    @GetMapping("/{category}")
<<<<<<< HEAD
    public String getBoards(
            @PathVariable("category") String category,
            @RequestParam(name="page", defaultValue="1") int currentPage,
            Model model,
            HttpSession session
    ) {
        UserDto user = SessionUtil.getUser(session);

        var boardResponse = apiRequestService.fetchData("/api/board/boards/" + category);
        var bList = boardResponse.getBody();
=======
    public String board(@PathVariable("category")String category, Model model, HttpSession session) {

        UserDto user  = SessionUtil.getUser(session);

            var boardResponse = apiRequestService.fetchData("/api/board/boards/"+category);
            var bList = boardResponse.getBody();
>>>>>>> origin/REQ-68-관리자

        if (!(bList instanceof Map)) {
            throw new RuntimeException("잘못된 응답 형식입니다.");
        }
<<<<<<< HEAD
=======

        // 2️⃣ 내부 "body" 필드 추출
>>>>>>> origin/REQ-68-관리자
        Map<String, Object> bodyMap = (Map<String, Object>) bList;
        Object innerBodyObj = bodyMap.get("body");
        if (!(innerBodyObj instanceof List)) {
            throw new RuntimeException("잘못된 응답 형식입니다.");
        }
<<<<<<< HEAD
        List<Map<String, Object>> boardList = (List<Map<String, Object>>) innerBodyObj;

        // 페이징 처리를 위한 값 설정
        int totalCount = boardList.size(); // 전체 게시물 수
        int pageSize = 10;                 // 한 페이지당 보여줄 게시물 수
        int pageBtnCount = 5;              // 한 화면에 표시할 페이지 버튼 수

        // PagingBtn 객체 생성 (내부에서 전체 페이지, 시작/끝 페이지 등을 계산)
        PagingBtn pagingBtn = new PagingBtn(totalCount, currentPage, pageSize, pageBtnCount);

        // boardList에서 현재 페이지에 해당하는 데이터 추출
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);
        List<Map<String, Object>> pagedBoardList = boardList.subList(startIndex, endIndex);

        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("boardList", pagedBoardList);
        model.addAttribute("pagingBtn", pagingBtn);
        model.addAttribute("category", category);

        return "/ajy/board/board_list";
    }

=======

        // 3️⃣ 게시글 리스트를 가져와서 모델에 추가
        List<Map<String, Object>> boardList = (List<Map<String, Object>>) innerBodyObj;

        if(user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("boardList", boardList);
        model.addAttribute("category", category);
        return "/ajy/board/board_list";

    }
>>>>>>> origin/REQ-68-관리자

    @GetMapping("/boardRegister/{category}")
    public String boardRegister(@PathVariable("category")String category, Model model, HttpSession session) {
        log.info("category: {}", category);
        UserDto user  = SessionUtil.getUser(session);
        if(user != null) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("category", category);
            return "/ajy/board/board_register";
        }

        return "redirect:/login";

    }

    @GetMapping("/boardDetail/{boardId}/{category}")
    public String boardDetail(@PathVariable("boardId") String boardId,
                              @PathVariable("category") String category,
<<<<<<< HEAD
                              @RequestParam(name="page", defaultValue="1") int currentPage,
=======
>>>>>>> origin/REQ-68-관리자
                              Model model, HttpSession session) {

        var boardResponse = apiRequestService.fetchData("/api/board/" + boardId);
        var boardResponseBody = boardResponse.getBody();

        // `boardResponseBody`가 Map인지 확인 후 변환
        if (!(boardResponseBody instanceof Map)) {
            model.addAttribute("error", "잘못된 응답 형식입니다.");
            return "redirect:/board/"+category; // 에러 페이지로 이동
        }

        Map<String, Object> bodyMap = (Map<String, Object>) boardResponseBody;

        // `"body"` 값이 존재하는지 확인
        Object innerBodyObj = bodyMap.get("body");
        if (!(innerBodyObj instanceof Map)) {
            model.addAttribute("error", "게시글 정보를 가져올 수 없습니다.");
            return "redirect:/board/"+category; // 에러 페이지로 이동
        }

        Map<String, Object> board = (Map<String, Object>) innerBodyObj;

<<<<<<< HEAD

        //댓글
        var commentsResponse = apiRequestService.fetchData("/api/comment/comments/" + boardId+ "/" + category);
        var cList = commentsResponse.getBody();

        if (!(cList instanceof Map)) {
            throw new RuntimeException("잘못된 응답 형식입니다.");
        }

        // 2️⃣ 내부 "body" 필드 추출
        Map<String, Object> bodyMap2 = (Map<String, Object>) cList;
        Object innerBodyObj2 = bodyMap2.get("body");
        if (!(innerBodyObj2 instanceof List)) {
            throw new RuntimeException("잘못된 응답 형식입니다.");
        }

        // 3️⃣ 게시글 리스트를 가져와서 모델에 추가
        List<Map<String, Object>> commentList = (List<Map<String, Object>>) innerBodyObj2;

        // 페이징 처리를 위한 값 설정
        int totalCount = commentList.size(); // 전체 게시물 수
        int pageSize = 5;                 // 한 페이지당 보여줄 게시물 수
        int pageBtnCount = 5;              // 한 화면에 표시할 페이지 버튼 수

        // PagingBtn 객체 생성 (내부에서 전체 페이지, 시작/끝 페이지 등을 계산)
        PagingBtn pagingBtn = new PagingBtn(totalCount, currentPage, pageSize, pageBtnCount);

        // boardList에서 현재 페이지에 해당하는 데이터 추출
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);
        List<Map<String, Object>> pagedCommetList = commentList.subList(startIndex, endIndex);


=======
>>>>>>> origin/REQ-68-관리자
        UserDto user  = SessionUtil.getUser(session);
        if(user != null){
            model.addAttribute("user", user);
        }

        model.addAttribute("category", category);
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", board);
<<<<<<< HEAD
        model.addAttribute("cList", commentList);
        model.addAttribute("commentList", pagedCommetList);
        model.addAttribute("pagingBtn", pagingBtn);
=======
>>>>>>> origin/REQ-68-관리자

        return "/ajy/board/board_detail";
    }

    @GetMapping("/boardEdit/{boardId}/{category}")
    public String boardEdit(@PathVariable("category")String category, @PathVariable("boardId")Integer boardId, Model model, HttpSession session) {
        log.info("category: {}", category);

        var boardResponse = apiRequestService.fetchData("/api/board/" + boardId);
        var boardResponseBody = boardResponse.getBody();

        // `boardResponseBody`가 Map인지 확인 후 변환
        if (!(boardResponseBody instanceof Map)) {
            model.addAttribute("error", "잘못된 응답 형식입니다.");
            return "redirect:/board/"+category; // 에러 페이지로 이동
        }

        Map<String, Object> bodyMap = (Map<String, Object>) boardResponseBody;

        // `"body"` 값이 존재하는지 확인
        Object innerBodyObj = bodyMap.get("body");
        if (!(innerBodyObj instanceof Map)) {
            model.addAttribute("error", "게시글 정보를 가져올 수 없습니다.");
            return "redirect:/board/"+category; // 에러 페이지로 이동
        }

        Map<String, Object> board = (Map<String, Object>) innerBodyObj;


        UserDto user  = SessionUtil.getUser(session);
        if(user != null) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("category", category);
            model.addAttribute("boardId", boardId);
            model.addAttribute("board", board);
            return "/ajy/board/board_register";
        }

        return "redirect:/login";

    }

<<<<<<< HEAD
    @GetMapping("/mypage/boardlist/{category}")
    public String mypageBoardList(@PathVariable("category")String category, Model model, HttpSession session) {
        UserDto user  = SessionUtil.getUser(session);
        if(user != null) {

            var boardResponse = apiRequestService.fetchData("/api/board/mypage/"+user.getUsername()+"/"+category);
            var bList = boardResponse.getBody();

            if (!(bList instanceof Map)) {
                throw new RuntimeException("잘못된 응답 형식입니다.");
            }

            // 2️⃣ 내부 "body" 필드 추출
            Map<String, Object> bodyMap = (Map<String, Object>) bList;
            Object innerBodyObj = bodyMap.get("body");
            if (!(innerBodyObj instanceof List)) {
                throw new RuntimeException("잘못된 응답 형식입니다.");
            }

            // 3️⃣ 게시글 리스트를 가져와서 모델에 추가
            List<Map<String, Object>> boardList = (List<Map<String, Object>>) innerBodyObj;

            model.addAttribute("user", user);
            model.addAttribute("category", category);
            model.addAttribute("boardList", boardList);
        }
        return "/ajy/mypage/boardlist";
    }

=======
>>>>>>> origin/REQ-68-관리자

}
