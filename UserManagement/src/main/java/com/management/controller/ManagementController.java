package com.management.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.dto.LoginDto;
import com.management.dto.SignUpDto;
import com.management.exception.ResourceNotFoundException;
import com.management.model.User;
import com.management.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class ManagementController {
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                loginDto.getUsernameOrEmail(), loginDto.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
    	   if(userRepository.existsByUsername(loginDto.getUsernameOrEmail())) {
    	        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
    	        }
    	        if(userRepository.existsByEmail(loginDto.getEmail())) {
    	            return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
    	            }
    	        return new ResponseEntity<>("User Not Foung!.", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){

        // add check for username exists in a DB
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // create user object
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setContact(signUpDto.getContact());

     

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }
    
    
    @GetMapping("/users")
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
    
    @GetMapping("/user/{id}")
	public ResponseEntity<User> getUserById(@PathVariable(value = "id") int userId)
			throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + userId));
		return ResponseEntity.ok().body(user);
	}
    
    @PostMapping("/userss")
	public User createUser(@RequestBody User user) {
		return userRepository.save(user);
	}
    
    
    @PutMapping("/user/{id}")
	public ResponseEntity<User> updateUser(@PathVariable(value = "id") int userId,
			@RequestBody User userDetails) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + userId));

		 user.setUsername(userDetails.getUsername());
	        user.setEmail(userDetails.getEmail());
	        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
	        user.setContact(userDetails.getContact());
		final User updatedEmployee = userRepository.save(user);
		return ResponseEntity.ok(updatedEmployee);
	}
    
    @DeleteMapping("{id}")
	public ResponseEntity<String> deleteUser(@PathVariable("id") int id){
		
		// delete employee from DB
    	userRepository.deleteById(id);;
		
		return new ResponseEntity<String>("Employee deleted successfully!.", HttpStatus.OK);
	}
    

}
