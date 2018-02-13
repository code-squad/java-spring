package codesquad.controller.user;

import codesquad.controller.UserSessionUtils;
import codesquad.dao.UserDao;
import codesquad.model.User;
import core.web.argumentresolver.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserDao userDao = UserDao.getInstance();

    @GetMapping("")
    public String index(@LoginUser User loginUser, Model model) throws Exception {
        if (loginUser.isGuestUser()) {
            return "redirect:/users/loginForm";
        }

        model.addAttribute("users", userDao.findAll());
        return "/user/list";
    }

    @GetMapping("/{userId}")
    public String profile(@PathVariable String userId, Model model) throws Exception {
        model.addAttribute("user", userDao.findByUserId(userId));
        return "/user/profile";
    }

    @GetMapping("/new")
    public String form(Model model) throws Exception {
        model.addAttribute("user", new User());
        return "/user/form";
    }

    @PostMapping("")
    public String create(User user) throws Exception {
        log.debug("User : {}", user);
        userDao.insert(user);
        return "redirect:/";
    }

    @GetMapping("/{userId}/edit")
    public String updateForm(@LoginUser User loginUser, @PathVariable String userId, Model model) throws Exception {
        User user = userDao.findByUserId(userId);
        if (!loginUser.isSameUser(user)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }
        model.addAttribute("user", user);
        return "/user/updateForm";
    }

    @PutMapping("/{userId}")
    public String update(@LoginUser User loginUser, @PathVariable String userId, User newUser) throws Exception {
        User user = userDao.findByUserId(userId);
        if (!loginUser.isSameUser(user)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }

        log.debug("Update User : {}", newUser);
        user.update(newUser);
        userDao.update(user);
        return "redirect:/";
    }

    @GetMapping("/loginForm")
    public String loginForm() throws Exception {
        return "/user/login";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session, Model model) throws Exception {
        User user = userDao.findByUserId(userId);
        if (user == null) {
            model.addAttribute("loginFailed", true);
            return "/user/login";
        }

        if (user.matchPassword(password)) {
            session.setAttribute(UserSessionUtils.USER_SESSION_KEY, user);
            return "redirect:/";
        } else {
            model.addAttribute("loginFailed", true);
            return "/user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) throws Exception {
        session.removeAttribute(UserSessionUtils.USER_SESSION_KEY);
        return "redirect:/";
    }
}
