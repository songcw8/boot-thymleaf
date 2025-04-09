package org.example.bootthymleaf.controller;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.example.bootthymleaf.model.dto.WordForm;
import org.example.bootthymleaf.model.entity.Word;
import org.example.bootthymleaf.model.repository.WordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final WordRepository wordRepository;

//    @GetMapping //검색, 조회에 대한 부분
//    public String index(Model model, @RequestParam(required = false) String message) {
//      Word word = new Word();
//        word.setText("고양이");
//        wordRepository.save(word);
//        model.addAttribute("data", wordRepository.findAll());
//        model.addAttribute("message", message);
//        return "index";
//    }

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

    @PostMapping("/word")
    public String addWord(WordForm wordForm, RedirectAttributes redirectAttributes, Model model) {
        redirectAttributes.addFlashAttribute("message", "끝말잇기 단어가 추가되었습니다.");
        Word word = new Word();
        word.setText(wordForm.getWord());
        wordRepository.save(word);
        return "redirect:/";
    }
}
