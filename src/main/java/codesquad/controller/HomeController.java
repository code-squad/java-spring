package codesquad.controller;

import codesquad.dao.QuestionDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private QuestionDao questionDao = QuestionDao.getInstance();

    @GetMapping("/")
    public ModelAndView home() throws Exception {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("questions", questionDao.findAll());
        return mav;
    }
}
