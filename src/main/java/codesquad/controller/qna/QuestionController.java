package codesquad.controller.qna;

import javax.servlet.http.HttpSession;

import codesquad.CannotOperateException;
import codesquad.controller.UserSessionUtils;
import codesquad.model.Question;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import codesquad.dao.QuestionDao;

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
	public String createForm(HttpSession session) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		return "/qna/form";
	}

	@PostMapping("")
	public String create(HttpSession session, Question question) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		questionDao.insert(question.newQuestion(UserSessionUtils.getUserFromSession(session)));
		return "redirect:/";
	}

	@GetMapping("/{questionId}/edit")
	public String editForm(HttpSession session, @PathVariable long questionId, Model model) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		Question question = qnaService.findById(questionId);
		if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		model.addAttribute("question", question);
		return "/qna/update";
	}

	@PutMapping("/{questionId}")
	public String edit(HttpSession session, @PathVariable long questionId, Question editQuestion) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		qnaService.updateQuestion(questionId, editQuestion, UserSessionUtils.getUserFromSession(session));
		return "redirect:/";
	}

	@DeleteMapping("/{questionId}")
	public String delete(HttpSession session, @PathVariable long questionId, Model model) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return "redirect:/";
		} catch (CannotOperateException e) {
			model.addAttribute("question", qnaService.findById(questionId));
			model.addAttribute("errorMessage", e.getMessage());
			return "show";
		}
	}
}
