package com.startupweekend.ubc.sldshw.datamodel;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class SldShwUser {
	private String username;
	private String password;
	private List<GrantedAuthority> authorityList;

	public SldShwUser(String username) {
		this(username, "", null);
	}

	public SldShwUser(String username, String password,
			List<GrantedAuthority> authorityList) {
				this.username = username;
				this.password = password;
				this.authorityList = authorityList;
	}
}
