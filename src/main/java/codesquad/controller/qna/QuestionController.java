package codesquad.controller.qna;

import codesquad.CannotOperateException;
import codesquad.dao.QuestionDao;
import codesquad.model.Question;
import codesquad.model.User;
import codesquad.service.QnaService;
import core.web.argumentresolver.LoginUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private QuestionDao questionDao = QuestionDao.getInstance();
    private QnaService qnaService = QnaService.getInstance();

    @GetMapping("/{questionId}")
    public String show(@PathVariable long questionId, Model model) throws Exception {
        model.addAttribute("question", qnaService.findById(questionId));
        model.addAttribute("answers", qnaService.findAllByQuestionId(questionId));
        return "/qna/show";
    }

    @GetMapping("/new")
    public String createForm(@LoginUser User loginUser, Model model) throws Exception {
        if (loginUser.isGuestUser()) {
            return "redirect:/users/loginForm";
        }
        model.addAttribute("question", new Question());
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) throws Exception {
        if (loginUser.isGuestUser()) {
            return "redirect:/users/loginForm";
        }
        questionDao.insert(question.newQuestion(loginUser));
        return "redirect:/";
    }

    @GetMapping("/{questionId}/edit")
    public String editForm(@LoginUser User loginUser, @PathVariable long questionId, Model model) throws Exception {
        Question question = qnaService.findById(questionId);
        if (!question.isSameUser(loginUser)) {
            throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }
        model.addAttribute("question", question);
        return "/qna/update";
    }

    @PutMapping("/{questionId}")
    public String edit(@LoginUser User loginUser, @PathVariable long questionId, Question editQuestion) throws Exception {
        qnaService.updateQuestion(questionId, editQuestion, loginUser);
        return "redirect:/";
    }

    @DeleteMapping("/{questionId}")
    public String delete(@LoginUser User loginUser, @PathVariable long questionId, Model model) throws Exception {
        try {
            qnaService.deleteQuestion(questionId, loginUser);
            return "redirect:/";
        } catch (CannotOperateException e) {
            model.addAttribute("question", qnaService.findById(questionId));
            model.addAttribute("errorMessage", e.getMessage());
            return "show";
        }
    }
}
