package ru.practicum.explorewithme.api.client.user;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.api.client.user.dto.UserDto;

@FeignClient(name = "user-service", path = "/internal/users", configuration = UserClientConfiguration.class)
public interface UserClient {

    /**
     * Retrieves a list of users by their IDs. Essential for bulk enrichment.
     */
    @GetMapping("/by-ids")
    List<UserDto> getUsersByIds(@RequestParam("ids") List<Long> ids);

    /**
     * Retrieves a single user by their ID.
     */
    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    /**
     * An efficient way to check for existence.
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/{id}")
    void checkUserExists(@PathVariable("id") Long id);
}