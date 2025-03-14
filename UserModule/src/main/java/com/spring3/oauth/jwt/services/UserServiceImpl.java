package com.spring3.oauth.jwt.services;

import com.spring3.oauth.jwt.dtos.SignupRequest;
import com.spring3.oauth.jwt.dtos.SubAgentListResponse;
import com.spring3.oauth.jwt.dtos.UserInfoResponse;
import com.spring3.oauth.jwt.models.UserInfo;
import com.spring3.oauth.jwt.models.UserRole;
import com.spring3.oauth.jwt.repositories.AgentRepository;
import com.spring3.oauth.jwt.repositories.RoleRespository;
import com.spring3.oauth.jwt.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.text.StrTokenizer;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AgentRepository agentRepository;
    @Autowired
    RoleRespository roleRespository;
    private final RestTemplate restTemplate = new RestTemplate();
    public static final String DYNAMODB_LOCATION_API = "http://ip-api.com/json/";
    //@Value("${auth.base.url}")
    //private String apiUrl;

    //@Value("${proxy.base.url}")
    //private String proxyApiUrl;

    //@Value("${proxy.update.agent.base.url}")
    //private String proxyupdateAgentApiUrl;

    //@Value("${auth.delete.user.URL}")
    //private String findAndDeleteUserInAuthURL;
    //@Value("${agent.delete.user.URL}")
    private String findAndDeleteUserInAgentServiceURL;
    public
    ModelMapper modelMapper = new ModelMapper();
    public static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    public static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfo saveUser(SignupRequest user, HttpServletRequest httpServletRequest) {
        {
            if(user.getUsername()== null){
                throw new RuntimeException("Parameter account number is not found in request..!!");
            } else if(user.getPassword() == null){
                throw new RuntimeException("Parameter password is not found in request..!!");
            }
            Optional<UserInfo> persitedUser = Optional.of(new UserInfo());
            UserInfo savedUser = null;

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = user.getPassword();
            String encodedPassword = encoder.encode(rawPassword);

            UserInfo uInfo = new UserInfo();
            uInfo.setUsername(user.getUsername());
            uInfo.setPassword(encodedPassword);
            uInfo.setParentId(user.getParentId());
            uInfo.setStatus("Active");

            try {
                uInfo.setCreatedAt(LocalDateTime.now());
                uInfo.setCreatedBy("000");
                uInfo.setUpdatedBy("000");
                uInfo.setUserLocation(this.getIPLocation(this.returnClientIp(httpServletRequest)));
                uInfo.setIpAddress(this.returnClientIp(httpServletRequest));
                uInfo.setUpdatedAt(LocalDateTime.now());
                persitedUser = Optional.of(userRepository.save(uInfo));
                /*if (user.getId() > 0) {
                    Optional<UserInfo> oldUser = userRepository.findById(user.getId());
                    oldUser.get().setCreatedBy(String.valueOf(oldUser.get().getUserId()));
                    if (!oldUser.isEmpty()) {
                        oldUser.get().setUserId(user.getId());
                        oldUser.get().setPassword(user.getPassword());
                        oldUser.get().setUsername(user.getUsername());
                        oldUser.get().setVerificationCode(user.getVerificationCode());
                        oldUser.get().setUpdatedAt(LocalDateTime.now());
                        oldUser.get().setDeviceType(user.getDeviceType());
                        oldUser.get().setUpdatedBy(String.valueOf(oldUser.get().getUserId()));
                        oldUser.get().setRoles(user.getRoles());
                        savedUser = userRepository.save(oldUser.get());
                        persitedUser = userRepository.findById(savedUser.getId());
                    } else {
                        throw new RuntimeException("Can't find record with identifier: " + persitedUser.get().getId());
                    }
                } else {
                    user.setCreatedAt(LocalDateTime.now());
                    persitedUser = Optional.of(userRepository.save(user)); */
                   // persitedUser.get().setUserId((persitedUser.get().getId()));
                //}
                // persitedUser.get().setUId(String.valueOf(persitedUser.get().getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //updateUserDatainAuth(persitedUser.get());
            //addAgentDatainProxy(persitedUser.get(), proxyApiUrl);
            return persitedUser.get();
        }
    }

    /*public String updateUserDatainAuth(UserInfo user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserInfo> request = new HttpEntity<>(user, headers);
       // ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
      // return response.getBody();
    }*/
    public String addAgentDatainProxy(UserInfo user, String url) {
        HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserInfo> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    @Override
    public SubAgentListResponse getSubAgentList(String userId) {
        SubAgentListResponse subAgentListResponse = null;
        List<UserInfo> userInfoList = agentRepository.findSubAgentsByAgentId(userId);
        Optional<UserInfo> userInfo = agentRepository.findById(Long.valueOf(userId));
        if (userInfo != null) {
            subAgentListResponse = new SubAgentListResponse();
            subAgentListResponse.setUserInfo(userInfo.get());
            subAgentListResponse.setSubAgentList(userInfoList);
        }
        return subAgentListResponse;
    }

    @Override
    public UserInfo updateAgentInfo(UserInfo user) {
        UserInfo savedUser = null;

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = user.getPassword();
        String encodedPassword = encoder.encode(rawPassword);

        user.setUsername(user.getUsername());
        user.setPassword(encodedPassword);
        user.setStatus("Active");
        Optional<UserInfo> existingUser = userRepository.findById((long) user.getId());
        if (existingUser != null) {
            //existingUser.get().setCreatedBy(String.valueOf(existingUser.get().getUserId()));
            //existingUser.get().setUserId(user.getId());
            existingUser.get().setPassword(user.getPassword());
            existingUser.get().setUsername(user.getUsername());
           // existingUser.get().setVerificationCode(user.getVerificationCode());
            existingUser.get().setUpdatedAt(LocalDateTime.now());
            existingUser.get().setDeviceType(user.getDeviceType());
            existingUser.get().setUpdatedBy(String.valueOf(0));
            savedUser = userRepository.save(existingUser.get());
        }
      //  updateUserDatainAuth(savedUser);
        //addAgentDatainProxy(savedUser, proxyupdateAgentApiUrl);
        return savedUser;
    }
    @Override
    public UserInfo getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String usernameFromAccessToken = userDetail.getUsername();
        UserInfo user = userRepository.findByUsername(usernameFromAccessToken);
       // UserInfoResponse userResponse = modelMapper.map(user, UserInfoResponse.class);

        if (user.getUsername() != null )
            user.setUsername(user.getUsername());

        return user;
    }

    @Override
    public List<UserInfo> getAllUser() {
        List<UserInfo> users = (List<UserInfo>) userRepository.findAll();
        Type setOfDTOsType = new TypeToken<List<UserInfoResponse>>(){}.getType();
        List<UserInfoResponse> userResponses = modelMapper.map(users, setOfDTOsType);
        for (int i = 0; i < users.size(); i++) {
            userResponses.get(i).setUsername(users.get(i).getUsername());
        }
        return users;
    }

    @Override
    public String returnClientIp(HttpServletRequest request)
    {
        boolean found = false;
        String IPAddress;
        if ((IPAddress = request.getHeader("x-forwarded-for")) != null) {
            StrTokenizer tokenizer = new StrTokenizer(IPAddress, ",");
            while (tokenizer.hasNext())
            {
                IPAddress = tokenizer.nextToken().trim();
                if (isIPv4Valid(IPAddress) && !isIPv4Private(IPAddress)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {IPAddress = request.getRemoteAddr();}
        return IPAddress;
    }

    @Override
    public UserInfoResponse updateUser(UserInfo userInfoRequest, HttpServletRequest httpServletRequest) {

        userInfoRequest.setUpdatedAt(LocalDateTime.now());
        userInfoRequest.setUpdatedBy(userInfoRequest.getUpdatedBy());
        userInfoRequest.setIpAddress(this.returnClientIp(httpServletRequest));
        userInfoRequest.setUserLocation(this.getIPLocation(this.returnClientIp(httpServletRequest)));
        userInfoRequest.setUpdatedBy(String.valueOf(userInfoRequest.getId()));
        UserInfo user = modelMapper.map(userInfoRequest, UserInfo.class);
        user = userRepository.save(user);
        UserInfoResponse userResponse = modelMapper.map(user, UserInfoResponse.class);
        return userResponse;
    }

    @Override
    public UserInfo getUserByUserName(String userId) {
        Optional<UserInfo> userInfo = userRepository.findById(Long.valueOf(userId));
        //UserInfo user = modelMapper.map(userInfo, UserInfoResponse.class);
        return userInfo.get();
    }

    @Override
    public UserInfo revokRole(List<String> roleIds, String userId) {
        Set<UserRole> roleList = new HashSet<>();
        Optional<UserRole> userRole = null;
        UserInfo userInfo = null;
        try {
            // Fetch the user
            Optional<UserInfo> userInfoOpt = userRepository.findById(Long.valueOf(userId));
            if (userInfoOpt.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            userInfo = userInfoOpt.get();

            // Get existing roles
            Set<UserRole> existingRoles = userInfo.getRoles();
            existingRoles.removeIf(role -> roleIds.contains(String.valueOf(role.getId())));

            // Set updated roles and save
            userInfo.setRoles(existingRoles);
            return userRepository.save(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    @Override
    public UserInfo assignRole(List<String> roleIds, String userId) {
        Set<UserRole> roleList = new HashSet<>();
        Optional<UserRole> userRole = null;
        UserInfo userInfo = null;
        try {
            // Fetch the user
            Optional<UserInfo> userInfoOpt = userRepository.findById(Long.valueOf(userId));
            if (userInfoOpt.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
             userInfo = userInfoOpt.get();

            // Get existing roles
            Set<UserRole> existingRoles = userInfo.getRoles();
            if (existingRoles == null) {
                existingRoles = new HashSet<>();
            }

            // Fetch and add new roles
            for (String id : roleIds) {
                Optional<UserRole> userRoleOpt = roleRespository.findById(Long.valueOf(id));
                userRoleOpt.ifPresent(existingRoles::add);
            }

            // Set updated roles and save
            userInfo.setRoles(existingRoles);
            return userRepository.save(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    @Override
    public Optional<UserInfo> getAgentProfile(String userId) {
        return userRepository.findById(Long.valueOf(userId));
    }
    @Override
    public Boolean deleteUser(Long userId) {
        Boolean isDeleted = false;
        try {
            Optional<UserInfo> userInfo = userRepository.findById(userId);
            if(!userInfo.isEmpty()) {
                userRepository.delete(userInfo.get());
                findAndDeleteUserInAgenService(userInfo.get());
              //  findandDeleteUserInAuth(userInfo.get());
                 isDeleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    /*private String findandDeleteUserInAuth(UserInfo user) {

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("userId", String.valueOf(user.getUserId()));
        ResponseEntity<String> response = restTemplate.exchange(findAndDeleteUserInAuthURL, HttpMethod.POST, null, String.class, uriVariables);
        return response.getBody();
    }*/

    private String findAndDeleteUserInAgenService(UserInfo user) {
        Map<String, String> uriVariables = new HashMap<>();
       // uriVariables.put("userId", String.valueOf(user.getUserId()));
        ResponseEntity<String> response = restTemplate.exchange(findAndDeleteUserInAgentServiceURL, HttpMethod.POST, null, String.class, uriVariables);
        return response.getBody();
    }

    public String getIPLocation(String ip) {
        String apiLocation = null;
        String inputLine = "";

        try {
            String apiUrl = DYNAMODB_LOCATION_API + ip;
            URL obj = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            JSONObject json = new JSONObject(response.toString());
            if ( json == null || json.get("status").equals("fail"))
                apiLocation = "NA";
            else
                apiLocation = json.get("city") + ", " + json.get("country");
        } catch (Exception e){
            e.printStackTrace();
        }

        return apiLocation;
    }
    private static boolean isIPv4Valid(String ip) {
        return pattern.matcher(ip).matches();
    }
    public static boolean isIPv4Private(String ip) {
        long longIp = ipV4ToLong(ip);
        return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255")) ||
                (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255")) ||
                longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
    }
    public static long ipV4ToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16) +
                (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
    }
}
