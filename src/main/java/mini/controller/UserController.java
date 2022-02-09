package mini.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import lombok.RequiredArgsConstructor;
import mini.model.User;
import mini.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;


@RestController
@RequestMapping(value = {"/api/users","/api/users/"})
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private  UserServiceImpl userService;


    @GetMapping(value = "/generate")
    public List<User> generate(@RequestParam("count") Long count) throws MalformedURLException, NoSuchAlgorithmException {
        List<User> list=new ArrayList<User>();
        for (int i=0;i<count;i++){
            String [] arr = {"Admin","Role"};
            Random random = new Random();
            int select = random.nextInt(arr.length);

            Faker faker = new Faker();
            String password = "1234"+faker.name().firstName();
            FakeValuesService fakeValuesService = new FakeValuesService(
                    new Locale("en-GB"), new RandomService());
            String email = fakeValuesService.bothify("????##@outlook.com");
            String username=faker.name().fullName().replaceAll("\\s+","").trim();
            User user=new User(faker.name().firstName(),faker.name().lastName(),faker.date().birthday(),faker.address().city(),faker.address().country(),faker.avatar().image(),faker.company().name(),faker.company().profession(),faker.phoneNumber().cellPhone(),username,email,password,arr[select]);

            list.add(user);
        }
        return list;
    }

    @PostMapping(value = "/batch")
    public HashMap upload(@RequestParam("file") MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<User> userList = new ArrayList<>();
        userList = Arrays.asList(mapper.readValue(file.getBytes(), User[].class));

        int added=0;
        for (User u: userList) {
            if(userService.getUser((u.getUsername()))==null){
                userService.saveUser(u);
                added++;
            }

        }
        int duplicated=userList.size()-added;
        HashMap<String,Integer> result=new HashMap<>();
        result.put("Added",added);
        result.put("Duplicated",duplicated);

        return result;
    }
    @GetMapping(value = "/all")
    public List<User> getAll(){
        return userService.getUsers();
    }
    @GetMapping(value = "/deleteAll")
    public void deleteAll(){
        userService.deleteAll();
    }

    @GetMapping("/{id}")
    public User myInfo(@PathVariable String id){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username=null;
        if (principal instanceof UserDetails) {
             username = ((UserDetails)principal).getUsername();
        } else {
             username = principal.toString();
        }
        User currentUser=userService.getUser(username);
        if(id.equals("me"))
        return currentUser;
        else {
            if(currentUser.getRole().equals("Admin"))
                return userService.getUser(id);
            else return null;
        }
    }

}
