package com.startupweekend.ubc.sldshw;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.startupweekend.ubc.sldshw.datamodel.SldShwUser;

public class SldShwUserDetailsService implements UserDetailsService {

	private Map<String, SldShwUser> userMap = new ConcurrentHashMap<String, SldShwUser>();

	public SldShwUserDetailsService() {
		userMap.put("i.mail.goldfish@gmail.com",
				new SldShwUser("i.mail.goldfish@gmail.com", "password",
						AuthorityUtils.createAuthorityList("PRESENTER")));
		userMap.put("shawn.tuatara@gmail.com",
				new SldShwUser("shawn.tuatara@gmail.com", "password",
						AuthorityUtils.createAuthorityList("USER")));
	}

	public void addUsername(String username) {
		if (userMap.containsKey(username)) {
			return;
		} else {
			userMap.put(
					username,
					new SldShwUser(username, "", AuthorityUtils
							.createAuthorityList("USER")));
		}
	}

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// Retrieve the user from wherever you store it, e.g. a database
		SldShwUser user = userMap.get(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username/password.");
		}

		return new User(user.getUsername(), user.getPassword(),
				user.getAuthorityList());
	}
}
