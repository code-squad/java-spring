package codesquad.controller.qna;

import codesquad.CannotOperateException;
import codesquad.dao.AnswerDao;
import codesquad.dao.QuestionDao;
import codesquad.model.Answer;
import codesquad.model.Question;
import codesquad.model.Result;
import codesquad.model.User;
import codesquad.service.QnaService;
import com.google.common.collect.Maps;
import core.jdbc.DataAccessException;
import core.web.argumentresolver.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    private QuestionDao questionDao = QuestionDao.getInstance();
    private AnswerDao answerDao = AnswerDao.getInstance();
    private QnaService qnaService = QnaService.getInstance();

    @DeleteMapping("/{questionId}")
    public Result deleteQuestion(@LoginUser User loginUser, @PathVariable long questionId) throws Exception {
        try {
            qnaService.deleteQuestion(questionId, loginUser);
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
    public Map<String, Object> addAnswer(@LoginUser User loginUser, @PathVariable long questionId, String contents) throws Exception {
        log.debug("questionId : {}, contents : {}", questionId, contents);
        Map<String, Object> values = Maps.newHashMap();
        Answer answer = new Answer(loginUser.getUserId(), contents, questionId);
        Answer savedAnswer = answerDao.insert(answer);
        questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());

        values.put("answer", savedAnswer);
        values.put("result", Result.ok());
        return values;
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public Result deleteAnswer(@LoginUser User loginUser, @PathVariable long answerId) throws Exception {
        Answer answer = answerDao.findById(answerId);
        if (!answer.isSameUser(loginUser)) {
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
