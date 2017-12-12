package redes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PrincipalController {

	@RequestMapping("/")
    public String paginaInicial() {
        return "redirect:/connect/facebook";
    }
	
	@RequestMapping("/privacidade")
    public String privacidade() {
        return "privacidade";
    }
}
