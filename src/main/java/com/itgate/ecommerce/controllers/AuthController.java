package com.itgate.ecommerce.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.itgate.ecommerce.exception.TokenRefreshException;
import com.itgate.ecommerce.models.*;
import com.itgate.ecommerce.repository.EquipeFederaRepository;
import com.itgate.ecommerce.repository.RoleRepository;
import com.itgate.ecommerce.repository.UserRepository;
import com.itgate.ecommerce.utils.StorageService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.itgate.ecommerce.payload.request.LoginRequest;
import com.itgate.ecommerce.payload.request.SignupRequest;
import com.itgate.ecommerce.payload.request.TokenRefreshRequest;
import com.itgate.ecommerce.payload.response.JwtResponse;
import com.itgate.ecommerce.payload.response.MessageResponse;
import com.itgate.ecommerce.payload.response.TokenRefreshResponse;
import com.itgate.ecommerce.security.jwt.JwtUtils;
import com.itgate.ecommerce.security.services.RefreshTokenService;
import com.itgate.ecommerce.security.services.UserDetailsImpl;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;
  @Autowired
  EquipeFederaRepository equipeFederaRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  private final Path rootLocation = Paths.get("upload-dir");


  @Autowired
  RefreshTokenService refreshTokenService;
  @Autowired
  private JavaMailSender mailSender;
  @Autowired
  private StorageService storageService;
  @GetMapping("/all")
  public List<User> getalluser(){
    return userRepository.findAll();}

  @GetMapping("/getone/{id}")
  public User getone(@PathVariable Long id){

    return userRepository.findById(id).get();
  }
  @PutMapping("/update/{id}")
  public User updateruser(@PathVariable Long id,@RequestParam (value = "file") MultipartFile file){
   User u=userRepository.findById(id).get();
    if(u!= null){
      u.setId(id);
      try {
        String fileName = Integer.toString(new Random().nextInt(1000000000));
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.'), file.getOriginalFilename().length());
        String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf('.'));
        String original = ext+name+fileName;
        Files.copy(file.getInputStream(),this.rootLocation.resolve(original));
        u.setImage(original);

      }catch (Exception e){
        throw new RuntimeException("Fail File Problem BackEnd");
      }
      return userRepository.saveAndFlush(u);
    }
    else{
      throw  new RuntimeException("FAIL!!");
    }
  }
//updateprofiladmin
    @PutMapping("/updateuser/{id}")
    public User updateruser1(@PathVariable Long id,@RequestBody User rf){
        User rf1=userRepository.findById(id).get();
        if(rf1!= null){
            rf.setId(id);
            rf.setConfirm(rf1.getConfirm());
           rf.setImage(rf1.getImage());
           rf.setRoles(rf1.getRoles());
           rf.setPassword(rf1.getPassword());
            return userRepository.saveAndFlush(rf);
        }
        else{
            throw  new RuntimeException("FAIL!!");
        }
    }
   // updateprofiluser
  @PutMapping("/updateuser1/{id}")
  public User updateruser12(@PathVariable Long id,@RequestBody User rf){
    User rf1=userRepository.findById(id).get();
    if(rf1!= null){
      rf.setId(id);
      rf.setConfirm(rf1.getConfirm());
      rf.setImage(rf1.getImage());
      rf.setRoles(rf1.getRoles());
      rf.setPassword(rf1.getPassword());
      rf.setUsername(rf1.getUsername());
      return userRepository.saveAndFlush(rf);
    }
    else{
      throw  new RuntimeException("FAIL!!");
    }
  }
  @DeleteMapping("/delete/{id}")
  public HashMap<String ,String > deleteuser(@PathVariable Long id){
    HashMap<String ,String> message=new HashMap<>();
    try{
      userRepository.deleteById(id);
      message.put("etat","User deleted");

    }catch (Exception e){
      message.put("etat","User not deleted");
    }
    return message;
  }
  @PutMapping("/desactiver/{id}")
  public User desactiver(@PathVariable Long id){
    User user=userRepository.findById(id).get();
    if(user!= null){
      user.setId(id);
      user.setConfirm(false);
      return userRepository.saveAndFlush(user);
    }
    else{
      throw  new RuntimeException("FAIL!!");
    }
  }
  @PutMapping("/activer/{id}")
  public User activer(@PathVariable Long id){
    User user=userRepository.findById(id).get();
    if(user!= null){
      user.setId(id);
      user.setConfirm(true);
      return userRepository.saveAndFlush(user);
    }
    else{
      throw  new RuntimeException("FAIL!!");
    }
  }
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    Optional<User> u = userRepository.findByUsername(loginRequest.getUsername());
    if (u.get().getConfirm() == true) {


    SecurityContextHolder.getContext().setAuthentication(authentication);


    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);

    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
            .collect(Collectors.toList());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
            userDetails.getUsername(), userDetails.getEmail(), roles,true));
  } else {
    return new ResponseEntity("user not confirmed ", HttpStatus.NOT_FOUND);
  }
  }

  @PostMapping("/signup1/{id_equipefeder}")
  public ResponseEntity<?> registerUser(@Valid SignupRequest signUpRequest,@PathVariable Long id_equipefeder ,@RequestParam (value = "file") MultipartFile file)throws MessagingException
  {


    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),signUpRequest.getAdress(),signUpRequest.getCin(),signUpRequest.getNumlicence(), signUpRequest.getTel(), signUpRequest.getNom(), signUpRequest.getPrenom());

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {



            Role resERole = roleRepository.findByName(ERole.ROLE_RESPEQUIPE)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(resERole);


        }









    //image


    try {
      String fileName = Integer.toString(new Random().nextInt(1000000000));
      String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.'), file.getOriginalFilename().length());
      String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf('.'));
      String original = ext + name+fileName;
      Files.copy(file.getInputStream(),this.rootLocation.resolve(original));
      user.setImage(original);




    }catch (Exception e){
      throw new RuntimeException("Fail File Problem BackEnd");
    }

    EquipeFederation equipeFederation = equipeFederaRepository.findById(id_equipefeder).get();
    user.setEquipeFederation(equipeFederation);

      String from ="admin@gmaail.com";
      String to =signUpRequest.getEmail();
      MimeMessage message=mailSender.createMimeMessage();
      MimeMessageHelper helper=new MimeMessageHelper(message);
      helper.setSubject("complete Resgistration");
      helper.setFrom(from);
      helper.setTo(to);
      //helper.setText("<HTML><body><a href =\"http://localhost:8080/respfederation/updateconfirm?email="+signUpRequest.getEmail()+"\">Verify</a></body></HTML>",true);
      helper.setText(signUpRequest.getPassword()+   "   "+ signUpRequest.getUsername());
      mailSender.send(message);

    user.setConfirm(true);
    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("responsable equipe registered successfully!"));
  }

  @PostMapping("/signup2")
  public ResponseEntity<?> registeradmin(@Valid  SignupRequest signUpRequest,@RequestParam (value = "file") MultipartFile file) {
	
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }
  
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }
    List<Role> l = roleRepository.findAll();
	if (l.size() == 0) {
		ERole admin = ERole.ROLE_ADMIN;
		ERole res = ERole.ROLE_RESPEQUIPE;
		ERole RESP = ERole.ROLE_RESPFEDERATION;
		ERole ru = ERole.ROLE_USER;
		Role r1 = new Role(admin);
		Role r2 = new Role(res);
		Role r3 = new Role(RESP);
		Role r4 = new Role(ru);
		ArrayList<Role> lr = new ArrayList<>();
		lr.add(r1);
		lr.add(r2);
		lr.add(r3);
		lr.add(r4);
		roleRepository.saveAll(lr);
	}
    // Create new user's account
    User admin = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),signUpRequest.getAdress(), signUpRequest.getCin(),signUpRequest.getNumlicence(), signUpRequest.getTel(),signUpRequest.getNom(), signUpRequest.getPrenom());


    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {

      Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(adminRole);

        try {
            String fileName = Integer.toString(new Random().nextInt(1000000000));
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.'), file.getOriginalFilename().length());
            String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf('.'));
            String original = ext + name+fileName;
            Files.copy(file.getInputStream(),this.rootLocation.resolve(original));
            admin.setImage(original);


        }catch (Exception e){
            throw new RuntimeException("Fail File Problem BackEnd");
        }
    }
    admin.setConfirm(true);
    admin.setRoles(roles);
    userRepository.save(admin);

    return ResponseEntity.ok(new MessageResponse("admin registered successfully!"));
  }



  @PostMapping("/signup3")
  public ResponseEntity<?> registerResFed(@Valid SignupRequest signUpRequest, @RequestParam (value = "file") MultipartFile file)throws MessagingException
  {

    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User responsableFederation= new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),signUpRequest.getAdress(), signUpRequest.getCin(),signUpRequest.getNumlicence(), signUpRequest.getTel(),signUpRequest.getNom(), signUpRequest.getPrenom());

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {

      Role RespfederationRole = roleRepository.findByName(ERole.ROLE_RESPFEDERATION)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(RespfederationRole);


    }



    //image


    try {
      String fileName = Integer.toString(new Random().nextInt(1000000000));
      String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.'), file.getOriginalFilename().length());
      String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf('.'));
      String original = ext + name+fileName;
      Files.copy(file.getInputStream(),this.rootLocation.resolve(original));
      responsableFederation.setImage(original);
     // EquipeFederation equipeFederation = equipeFederaRepository.findById(id_equipefeder).get();
     // responsableFederation.setEquipeFederation(equipeFederation);

    }catch (Exception e){
      throw new RuntimeException("Fail File Problem BackEnd");
    }

    responsableFederation.setConfirm(true);


      String from ="admin@gmaail.com";
      String to =signUpRequest.getEmail();
      MimeMessage message=mailSender.createMimeMessage();
      MimeMessageHelper helper=new MimeMessageHelper(message);
      helper.setSubject("Information de votre compte");
      helper.setFrom(from);
      helper.setTo(to);
      //helper.setText("<HTML><body><a href =\"http://localhost:8080/respfederation/updateconfirm?email="+signUpRequest.getEmail()+"\">Verify</a></body></HTML>",true);
      helper.setText(signUpRequest.getPassword()+   "   "+ signUpRequest.getUsername());
      mailSender.send(message);

    responsableFederation.setRoles(roles);
    userRepository.save(responsableFederation);

    return ResponseEntity.ok(new MessageResponse("responsable federation registered successfully!"));
  }
















  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        })
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
            "Refresh token is not in database!"));
  }
  
  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = userDetails.getId();
    refreshTokenService.deleteByUserId(userId);
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }
  @PostMapping("/forgetPassword")
  public HashMap<String, String> resetpassword(String email) throws MessagingException {
    HashMap message = new HashMap<>();
    User userexciting = userRepository.findByEmail(email);
    if (userexciting == null) {
      message.put("user", "user not found");
      return message;
    }

    UUID token = UUID.randomUUID();
    userexciting.setPasswordResetToken(token.toString());
    userexciting.setId(userexciting.getId());
    //mail
    //mail confirmation

    MimeMessage message1 = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message1);
    helper.setSubject("forgot password");
    helper.setFrom("admin@gmzil.com");
    helper.setTo(userexciting.getEmail());
    helper.setText("<HTML><body><a href =\"http://localhost:4200/savepassword/" + userexciting.getPasswordResetToken() + "\">Verify</a></body></HTML>", true);
    mailSender.send(message1);
    userRepository.saveAndFlush(userexciting);
    message.put("user","user found and email is send");
    return message;

  }
  @PostMapping("/savePassword/{passwordResetToken}")
  public HashMap<String ,String > savePassword(@PathVariable String passwordResetToken, String newPassword) {
    User userexciting = userRepository.findByPasswordResetToken(passwordResetToken);
    HashMap<String, String> message = new HashMap<>();
    if (userexciting != null) {
      userexciting.setId(userexciting.getId());
      userexciting.setPassword(new BCryptPasswordEncoder().encode(newPassword));
      userexciting.setPasswordResetToken(null);
      userRepository.save(userexciting);
      message.put("reset password","processed");
      return message;

    }
    else{
      message.put("reset password","failed");
      return message;
    }

}
  @GetMapping("/files/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable String filename ){
    Resource file= storageService.loadFile(filename);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                    + file.getFilename() + "\"")
            .body(file);

  }

 @PostMapping("/change-Password")
public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request){
   String username=authentication.getName();
   Optional<User> user =userRepository.findByUsername(username);
   if(user==null) {
    throw new IllegalArgumentException("Invalide user");
    }
   User u = user.get();
   if(!encoder.matches(request.getOldPassword(),u.getPassword())){
      return new ResponseEntity<>("invalide old password", HttpStatus.EXPECTATION_FAILED);


  }
   u.setPassword(encoder.encode(request.getNewPassword()));
    return ResponseEntity.ok(userRepository.save(u));
 }


}
