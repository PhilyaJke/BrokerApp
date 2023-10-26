package accelerator.group.brokerapp.Service.SecurityService;

import accelerator.group.brokerapp.Entity.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private String Username;
    private String Password;
    private List<SimpleGrantedAuthority> grantedAuthorityList;
    private Boolean flag;

    public SecurityUser(String username, String password, List<SimpleGrantedAuthority> grantedAuthorityList, Boolean flag) {
        Username = username;
        Password = password;
        this.grantedAuthorityList = grantedAuthorityList;
        this.flag = flag;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return Password;
    }

    @Override
    public String getUsername() {
        return Username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return flag;
    }

    @Override
    public boolean isAccountNonLocked() {
        return flag;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return flag;
    }

    @Override
    public boolean isEnabled() {
        return flag;
    }

    public static UserDetails fromUser(accelerator.group.brokerapp.Entity.User user){
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getRole().getAuthorities()
        );
    }
}
