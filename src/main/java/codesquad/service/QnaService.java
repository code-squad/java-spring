package codesquad.service;

import codesquad.CannotOperateException;
import codesquad.dao.AnswerDao;
import codesquad.dao.QuestionDao;
import codesquad.model.Answer;
import codesquad.model.Question;
import codesquad.model.User;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public class QnaService {
    private static QnaService qnaService;

    private QuestionDao questionDao = QuestionDao.getInstance();
    private AnswerDao answerDao = AnswerDao.getInstance();

    private QnaService() {
    }

    public static QnaService getInstance() {
        if (qnaService == null) {
            qnaService = new QnaService();
        }
        return qnaService;
    }

    public Question findById(long questionId) {
        return questionDao.findById(questionId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        return answerDao.findAllByQuestionId(questionId);
    }

    public void deleteQuestion(long questionId, User user) throws CannotOperateException {
        Question question = questionDao.findById(questionId);
        if (question == null) {
            throw new EmptyResultDataAccessException("존재하지 않는 질문입니다.", 1);
        }

        if (!question.isSameUser(user)) {
            throw new CannotOperateException("다른 사용자가 쓴 글을 삭제할 수 없습니다.");
        }

        List<Answer> answers = answerDao.findAllByQuestionId(questionId);
        if (answers.isEmpty()) {
            questionDao.delete(questionId);
            return;
        }

        boolean canDelete = true;
        for (Answer answer : answers) {
            String writer = question.getWriter();
            if (!writer.equals(answer.getWriter())) {
                canDelete = false;
                break;
            }
        }

        if (!canDelete) {
            throw new CannotOperateException("다른 사용자가 추가한 댓글이 존재해 삭제할 수 없습니다.");
        }

        questionDao.delete(questionId);
    }

    public void updateQuestion(long questionId, Question newQuestion, User user) throws CannotOperateException {
        Question question = questionDao.findById(questionId);
        if (question == null) {
            throw new EmptyResultDataAccessException("존재하지 않는 질문입니다.", 1);
        }

        if (!question.isSameUser(user)) {
            throw new CannotOperateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }

        question.update(newQuestion);
        questionDao.update(question);
    }
}
