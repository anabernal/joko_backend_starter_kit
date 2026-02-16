package io.github.jokoframework.myproject.profile.manager.impl;

import io.github.jokoframework.myproject.mapper.OrikaBeanMapper;
import io.github.jokoframework.myproject.profile.manager.UserManager;
import io.github.jokoframework.security.util.SecurityUtils;
import io.github.jokoframework.myproject.profile.entities.UserEntity;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.service.UserService;
import io.github.jokoframework.myproject.web.dto.UserAuthDTO;
import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.utils.csv.CsvUtils;
import io.github.jokoframework.utils.dto_mapping.DTOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import io.github.jokoframework.myproject.audit.enums.AuditAction;
import io.github.jokoframework.myproject.audit.enums.ResourceType;
import io.github.jokoframework.myproject.audit.service.AuditEventService;
import io.github.jokoframework.myproject.basic.enums.AccessLevelEnum;
import io.github.jokoframework.myproject.web.request.CreateUserRequestDTO;
import io.github.jokoframework.myproject.web.request.UpdateUserRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Date;
@Component
public class UserManagerImpl implements UserManager {

	@Autowired
	private UserService userService;

    @Autowired
    private OrikaBeanMapper mapper;

	@Override
    public List<UserDTO> findAll() {
        return DTOUtils.fromEntityToDTO(userService.findAll(), UserDTO.class);
    }

    @Override
    public UserDTO getByUsername(String username) throws UserException {
        UserEntity user = userService.getByUsername(username);

        if (user != null) {
            return mapper.map(user, UserDTO.class);
        }
        else {
            throw UserException.userNotFound(username);
        }
    }

    @Override
    public boolean checkEndUser(String username, String password) {
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            return  false;
        }

        UserEntity entity = userService.getByUsername(username);

        return entity != null && SecurityUtils.matchPassword(password, entity.getPassword());
    }

    @Override
    public UserAuthDTO checkUser(String username, String password) throws UserException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            throw UserException.invalidUserPassword();
        }

        UserAuthDTO user = mapper.map(userService.getByUsername(username), UserAuthDTO.class);

        if(user == null){
            throw UserException.invalidUserPassword();
        }

        if(!SecurityUtils.matchPassword(password, user.getPassword())){
            throw UserException.invalidUserPassword();
        }

        return user;
    }

    @Override
    public byte[] exportUsersListToCsv(List<UserDTO> list, List<String> columns) {
        return CsvUtils.convertToCsv(list, columns, UserDTO.class);
    }
    @Autowired
    private AuditEventService auditEventService;


    @Override
    public UserDTO createUser(CreateUserRequestDTO request) throws UserException {
        // Validate username uniqueness
        UserEntity existing = userService.getByUsername(request.getUsername());
        if (existing != null) {
            throw new UserException(UserException.USER_ERROR,
                    "User already exists with username: " + request.getUsername());
        }

        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setPassword(SecurityUtils.hashPassword(request.getPassword()));
        entity.setCreated(new Date());

        // Set profile, default to END_USER if not provided
        if (StringUtils.isNotEmpty(request.getProfile())) {
            entity.setProfile(AccessLevelEnum.valueOf(request.getProfile()));
        } else {
            entity.setProfile(AccessLevelEnum.END_USER);
        }

        UserEntity saved = userService.save(entity);

        // Audit event generated automatically - client does NOT send this
        auditEventService.registerEvent(
                AuditAction.USER_CREATED,
                ResourceType.USER,
                String.valueOf(saved.getId()),
                getCurrentActor(),
                "{\"username\":\"" + saved.getUsername() + "\",\"profile\":\"" + saved.getProfile() + "\"}"
        );

        return mapper.map(saved, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(Long userId, UpdateUserRequestDTO request) throws UserException {
        UserEntity entity = userService.getExistingUser(userId);

        StringBuilder changes = new StringBuilder("{");
        boolean hasChanges = false;

        if (StringUtils.isNotEmpty(request.getPassword())) {
            entity.setPassword(SecurityUtils.hashPassword(request.getPassword()));
            changes.append("\"passwordChanged\":true");
            hasChanges = true;
        }

        if (StringUtils.isNotEmpty(request.getProfile())) {
            if (hasChanges) changes.append(",");
            changes.append("\"oldProfile\":\"").append(entity.getProfile()).append("\"");
            changes.append(",\"newProfile\":\"").append(request.getProfile()).append("\"");
            entity.setProfile(AccessLevelEnum.valueOf(request.getProfile()));
            hasChanges = true;
        }

        changes.append("}");

        UserEntity saved = userService.save(entity);

        // Audit event generated automatically
        auditEventService.registerEvent(
                AuditAction.USER_UPDATED,
                ResourceType.USER,
                String.valueOf(saved.getId()),
                getCurrentActor(),
                hasChanges ? changes.toString() : null
        );

        return mapper.map(saved, UserDTO.class);
    }

    @Override
    public void deleteUser(Long userId) throws UserException {
        UserEntity entity = userService.getExistingUser(userId);
        String username = entity.getUsername();

        // Hard delete - record is permanently removed
        userService.deleteById(userId);

        // Audit event generated automatically
        auditEventService.registerEvent(
                AuditAction.USER_DELETED,
                ResourceType.USER,
                String.valueOf(userId),
                getCurrentActor(),
                "{\"deletedUsername\":\"" + username + "\"}"
        );
    }

    /**
     * Gets the current authenticated username from security context.
     */
    private String getCurrentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
