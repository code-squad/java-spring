package codesquad.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import codesquad.dao.QuestionDao;

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
