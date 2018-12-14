package system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for error's pages.
 */
@Controller
@RequestMapping(value = "/errors")
public class ErrorController {

    @GetMapping
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

        ModelAndView errorPage = new ModelAndView("errors");
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400:
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            case 401:
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            case 404:
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            case 500:
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
            default:
                errorMsg = "Something was wrong";
                break;
        }
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }
}
