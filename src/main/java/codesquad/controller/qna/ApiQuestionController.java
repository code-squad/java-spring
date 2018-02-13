package codesquad.controller.qna;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import codesquad.CannotOperateException;
import codesquad.controller.UserSessionUtils;
import codesquad.dao.AnswerDao;
import codesquad.dao.QuestionDao;
import codesquad.model.Answer;
import codesquad.model.Question;
import codesquad.model.Result;
import codesquad.model.User;
import codesquad.service.QnaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Maps;

import core.jdbc.DataAccessException;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
	private Logger log = LoggerFactory.getLogger(ApiQuestionController.class);
	
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	private QnaService qnaService = QnaService.getInstance();

    @DeleteMapping("/{questionId}")
	public Result deleteQuestion(HttpSession session, @PathVariable long questionId) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return Result.ok();
		} catch (CannotOperateException e) {
			return Result.fail(e.getMessage());
		}
	}

    @GetMapping("")
	public List<Question> list() throws Exception {
		return questionDao.findAll();
	}

    @PostMapping("/{questionId}/answers")
	public Map<String, Object> addAnswer(HttpSession session, @PathVariable long questionId, String contents) throws Exception {
		log.debug("questionId : {}, contents : {}", questionId, contents);
    	Map<String, Object> values = Maps.newHashMap();
		if (!UserSessionUtils.isLogined(session)) {
			values.put("result", Result.fail("Login is required"));
			return values;
		}
    	
    	User loginUser = UserSessionUtils.getUserFromSession(session);
    	Answer answer = new Answer(loginUser.getUserId(), contents, questionId);
    	Answer savedAnswer = answerDao.insert(answer);
		questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());
		
		values.put("answer", savedAnswer);
		values.put("result", Result.ok());
		return values;
	}

	@DeleteMapping("/{questionId}/answers/{answerId}")
    public Result deleteAnswer(HttpSession session, @PathVariable long answerId) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
		
		Answer answer = answerDao.findById(answerId);
		if (!answer.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			return Result.fail("다른 사용자가 쓴 글을 삭제할 수 없습니다.");
		}
		
		try {
			answerDao.delete(answerId);
			return Result.ok();
		} catch (DataAccessException e) {
			return Result.fail(e.getMessage());
		}
	}
}
