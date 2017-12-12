package redes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FacebookController {

	private Facebook facebook;
    private ConnectionRepository connectionRepository;

    @Autowired
    public FacebookController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = new FacebookTemplate("EAACEdEose0cBAGGSkhKHzfDKLZAwlkpGkFO2Qn6thyCZCAdKPV4cVTgtG62clSwWW7uvdR1DlKS1jQ5lEMr72keMkupwek9PQtGmgXz2MbgdOdrqqbGg3w4AQjVoRRspjylpOC6EpoWrEfh4kZCrxlhR7ZCYnm4c1ws17fDCGV9AjCNlx8G736aG28qDohK6NXTXzjZBnxQZDZD");
        this.connectionRepository = connectionRepository;
    }

    @GetMapping(path = "/infoFacebook")
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null)
            return "redirect:/connect/facebook";

        /*String [] fields = { "id", "about", "age_range", "birthday", "context", "cover", "currency", "devices", "education", "email", "favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type", "is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format", "political", "quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other", "sports", "test_group", "timezone", "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift", "website", "work", "music"};
        User userProfile = facebook.fetchObject("me", User.class, fields);
        model.addAttribute("facebookProfile", userProfile);
        PagedList<Post> feed = facebook.feedOperations().getFeed();
        model.addAttribute("feed", feed);*/
        PagedList<Page> paginasMusicas = facebook.likeOperations().getMusic();
        model.addAttribute("pm", paginasMusicas);
        PagedList<String> amigosIds = facebook.friendOperations().getFriendIds();
        model.addAttribute("ids", amigosIds);
        
        
        return "resultado";
    }
}
