package redes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
	
    @GetMapping(path = "/compartilhar/{nomepagina}")
    public ModelAndView paginaPost(@PathVariable String nomepagina) {
    	ModelAndView model = new ModelAndView("compartilhar");
		model.addObject("nomepagina", nomepagina);
		return model;
    }
    
}
