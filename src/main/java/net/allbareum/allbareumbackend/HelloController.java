package net.allbareum.allbareumbackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/test")
    public String test() {
        return "Allbareum backend test gooooooooood";
    }
}
