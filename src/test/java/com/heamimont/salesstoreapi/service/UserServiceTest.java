package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.user.*;
import com.heamimont.salesstoreapi.exceptions.ResourceAlreadyExistsException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.mapper.UserMapper;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    //Do not delete this, needed for user deletion to check for existing orders
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDTO userResponseDTO;
    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;
    
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        userId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.USER);
        user.setPassword("encodedPassword");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(UUID.randomUUID());
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setEmail("test@example.com");
        userResponseDTO.setFirstName("Test");
        userResponseDTO.setLastName("User");
        userResponseDTO.setRole(Role.USER);

        createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("newuser");
        createUserDTO.setEmail("newuser@example.com");
        createUserDTO.setFirstName("New");
        createUserDTO.setLastName("User");
        createUserDTO.setPassword("Password1");

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setEmail("updateduser@example.com");
        updateUserDTO.setFirstName("Updated");
        updateUserDTO.setLastName("User");
        updateUserDTO.setPassword("NewPass1");
        updateUserDTO.setRole(Role.ADMIN);
    }

    @Test
    void getAllUsers_returnsUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_existingUser_returnsDTO() {


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUserById(userId);

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_nonExistingUser_throwsResourceNotFoundException() {
        when(userRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void createUser_successful() {
        when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("newuser@example.com")).thenReturn(false);
        when(userMapper.toEntity(createUserDTO)).thenReturn(user);
        when(passwordEncoder.encode("Password1")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.createUser(createUserDTO);

        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_usernameExists_throwsResourceCreationException() {
        when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(createUserDTO));
    }

    @Test
    void updateUser_successful() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameIgnoreCase("updateduser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("updateduser@example.com")).thenReturn(false);
        doAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            UpdateUserDTO dtoArg = invocation.getArgument(1);
            userArg.setUsername(dtoArg.getUsername());
            userArg.setEmail(dtoArg.getEmail());
            userArg.setFirstName(dtoArg.getFirstName());
            userArg.setLastName(dtoArg.getLastName());
            userArg.setRole(dtoArg.getRole());
            return null;
        }).when(userMapper).updateEntity(any(User.class), eq(updateUserDTO));
        when(passwordEncoder.encode("NewPass1")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateUser(userId, updateUserDTO);

        assertEquals("testuser", result.getUsername()); // userMapper mocked to return the same DTO
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_nonExistingUser_throwsResourceNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, updateUserDTO));
    }

    @Test
    void deleteUser_existingUser_successful() {
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_nonExistingUser_throwsResourceNotFoundException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
    }
}
