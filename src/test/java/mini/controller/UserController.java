package mini.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import mini.model.User;
import mini.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@RestController
@RequestMapping(value = {"/api/users","/api/users/"})
public class UserController {


    UserServiceImpl userService;


    @GetMapping(value = "/generate")
    public List<User> generate(@RequestParam("count") Long count) throws MalformedURLException, NoSuchAlgorithmException {
        List<User> list=new ArrayList<User>();
        for (int i=0;i<count;i++){
            String [] arr = {"Admin","User"};
            Random random = new Random();
            int select = random.nextInt(arr.length);

            Faker faker = new Faker();
            String password = "1234"+faker.name().firstName();
            FakeValuesService fakeValuesService = new FakeValuesService(
                    new Locale("en-GB"), new RandomService());
            String email = fakeValuesService.bothify("????##??@outlook.com");
            User user=new User(faker.name().firstName(),faker.name().lastName(),faker.date().birthday(),faker.address().city(),faker.address().country(),faker.avatar().image(),faker.company().name(),faker.company().profession(),faker.phoneNumber().cellPhone(),faker.name().fullName(),email,password,arr[select]);
            list.add(user);
        }
        return list;
    }

    @PostMapping(value = "/batch")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
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
        String message="Added : "+added+"\n"
                +"Duplicated : "+(userList.size()-added);
        return message;
    }
    @GetMapping(value = "/all")
    public List<User> getAll(){
        return userService.getUsers();
    }

}
