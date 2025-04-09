package org.example.bootthymleaf.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bootthymleaf.model.dto.UpdateWordForm;
import org.example.bootthymleaf.model.dto.WordForm;
import org.example.bootthymleaf.model.entity.Word;
import org.example.bootthymleaf.model.repository.WordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final WordRepository wordRepository;
    
    @GetMapping
    public String index(Model model) {
        model.addAttribute("data", wordRepository.findAll());

        model.addAttribute("words", wordRepository.findAllByOrderByCreateAtDesc());
        //타임리프에서 이미 폼을 이미 정의된 걸로 쓰려면 model을 통해서 전달해야한다.
        model.addAttribute("wordForm", new WordForm());
        return "index";
    }

    @PostMapping("/reset")
    public String resetWords(RedirectAttributes redirectAttributes, Model model) {
        wordRepository.deleteAll();
        // Model을 쓸 수 없는 이유 -> forword

        //검색이나 조회, 외부에 노출
        //redirectAttributes.addAttribute("message", "전체 삭제되었습니다람쥐");

        //구매에 대한 오류나 정보 같은
        redirectAttributes.addFlashAttribute("message", "끝말잇기 내역이 삭제되었습니다.");
        return "redirect:/";
    }

//    @PostMapping("/word")
//    public String addWord(WordForm wordForm, RedirectAttributes redirectAttributes, Model model) {
//        redirectAttributes.addFlashAttribute("message", "끝말잇기 단어가 추가되었습니다.");
//        Word word = new Word();
//        word.setText(wordForm.getWord());
//        wordRepository.save(word);
//        return "redirect:/";
//    }
//
//    @PostMapping("/update")
//    @Transactional // 최종해결
//    public String updateWord(@ModelAttribute UpdateWordForm form, RedirectAttributes redirectAttributes) {
//        // JPA는 업데이트용 메서드나 기능이 따로 없어요
//        // JPA는 수정용이 따로 없어요
//        // -> 교체 개념이에요 => put <-> patch : 멱등성 (TIL)
//        // JPA : Jakarta Persistence API
//        Word oldWord = wordRepository.findById(form.getUuid()).orElseThrow();
//        oldWord.setText(form.getNewWord());
//        wordRepository.save(oldWord);
//        redirectAttributes.addFlashAttribute("message", "%s: 단어가 정상적으로 교체되었습니다.".formatted(oldWord.getText()));
//        return "redirect:/";
//    }

    @PostMapping("/word")
    public String addWord(WordForm wordForm, RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) {
        String csrf = checkCSRF(request);
        if (csrf != null) return csrf;

        String word = wordForm.getWord();

        // 한글 검증 - 정규식을 사용하여 한글만 포함되어 있는지 확인
        String x = checkKorean(redirectAttributes, word);
        if (x != null) return x;

        redirectAttributes.addFlashAttribute("message", "끝말잇기 단어가 추가되었습니다.");
        Word wordEntity = new Word();
        wordEntity.setText(word);
        wordRepository.save(wordEntity);
        return "redirect:/";
    }

    @PostMapping("/update")
    @Transactional
    public String updateWord(@ModelAttribute UpdateWordForm form, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String csrf = checkCSRF(request);
        if (csrf != null) return csrf;

        String newWord = form.getNewWord();

        // 한글 검증 - 정규식을 사용하여 한글만 포함되어 있는지 확인
        String x = checkKorean(redirectAttributes, newWord);
        if (x != null) return x;

        Word oldWord = wordRepository.findById(form.getUuid()).orElseThrow();
        oldWord.setText(newWord);
        wordRepository.save(oldWord);
        redirectAttributes.addFlashAttribute("message", "%s: 단어가 정상적으로 교체되었습니다.".formatted(oldWord.getText()));
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteWord(@RequestParam("id") String uuid, @RequestParam("word") String text, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String csrf = checkCSRF(request);
        if (csrf != null) return csrf;

        wordRepository.deleteById(uuid);
        redirectAttributes.addFlashAttribute("message", "%s: 단어가 정상적으로 삭제되었습니다.".formatted(text));
        return "redirect:/";
    }

    private static String checkKorean(RedirectAttributes redirectAttributes, String word) {
        if (!word.matches("^[가-힣]+$")) {
            redirectAttributes.addFlashAttribute("message", "어허~ 한글만 입력 가능합니다.");
            return "redirect:/";
        }
        return null;
    }

    private static String checkCSRF(HttpServletRequest request) {
        // Referer 또는 Origin 헤더 확인 (브라우저에서 오는 요청에는 보통 있음)
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");

        // 허용할 도메인 (자신의 도메인)
        //String allowedDomain = "https://boot-thymleaf.onrender.com";
        String allowedDomain = "http://localhost:8080";

        if ((referer == null || !referer.contains(allowedDomain)) &&
                (origin == null || !origin.contains(allowedDomain))) {
            return "redirect:/";
        }
        return null;
    }
}
