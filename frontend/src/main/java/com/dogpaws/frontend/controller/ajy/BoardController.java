package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.dto.ajy.BoardResponseDto;
import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.service.ApiRequestService;
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
    public String board(@PathVariable("category")String category, Model model, HttpSession session) {

        UserDto user  = SessionUtil.getUser(session);

            var boardResponse = apiRequestService.fetchData("/api/board/boards/"+category);
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

        if(user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("boardList", boardList);
        model.addAttribute("category", category);
        return "/ajy/board/board_list";

    }

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

        UserDto user  = SessionUtil.getUser(session);
        if(user != null){
            model.addAttribute("user", user);
        }

        model.addAttribute("category", category);
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", board);

        return "/ajy/board/board_detail";
    }


}
