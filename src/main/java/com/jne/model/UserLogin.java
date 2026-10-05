package com.jne.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_login")
public class UserLogin {

		
		@Id
		private String email;
		
		private String access_dt;
		private String access_feeder;
		private String access_owner;
		private String access_subdivision;
		private String access_substation;
		private LocalDateTime createddate;
		private String designation;
		private LocalDateTime lastlogin;
		private Integer roleid;
		private String username;
		private String userpassword;
		private String welcomemsg;
		private Integer writeaccess;
		public UserLogin() {
			super();
			// TODO Auto-generated constructor stub
		}
		public UserLogin(String email, String access_dt, String access_feeder, String access_owner,
				String access_subdivision, String access_substation, LocalDateTime createddate, String designation,
				LocalDateTime lastlogin, Integer roleid, String username, String userpassword, String welcomemsg,
				Integer writeaccess) {
			super();
			this.email = email;
			this.access_dt = access_dt;
			this.access_feeder = access_feeder;
			this.access_owner = access_owner;
			this.access_subdivision = access_subdivision;
			this.access_substation = access_substation;
			this.createddate = createddate;
			this.designation = designation;
			this.lastlogin = lastlogin;
			this.roleid = roleid;
			this.username = username;
			this.userpassword = userpassword;
			this.welcomemsg = welcomemsg;
			this.writeaccess = writeaccess;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getAccess_dt() {
			return access_dt;
		}
		public void setAccess_dt(String access_dt) {
			this.access_dt = access_dt;
		}
		public String getAccess_feeder() {
			return access_feeder;
		}
		public void setAccess_feeder(String access_feeder) {
			this.access_feeder = access_feeder;
		}
		public String getAccess_owner() {
			return access_owner;
		}
		public void setAccess_owner(String access_owner) {
			this.access_owner = access_owner;
		}
		public String getAccess_subdivision() {
			return access_subdivision;
		}
		public void setAccess_subdivision(String access_subdivision) {
			this.access_subdivision = access_subdivision;
		}
		public String getAccess_substation() {
			return access_substation;
		}
		public void setAccess_substation(String access_substation) {
			this.access_substation = access_substation;
		}
		public LocalDateTime getCreateddate() {
			return createddate;
		}
		public void setCreateddate(LocalDateTime createddate) {
			this.createddate = createddate;
		}
		public String getDesignation() {
			return designation;
		}
		public void setDesignation(String designation) {
			this.designation = designation;
		}
		public LocalDateTime getLastlogin() {
			return lastlogin;
		}
		public void setLastlogin(LocalDateTime lastlogin) {
			this.lastlogin = lastlogin;
		}
		public Integer getRoleid() {
			return roleid;
		}
		public void setRoleid(Integer roleid) {
			this.roleid = roleid;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getUserpassword() {
			return userpassword;
		}
		public void setUserpassword(String userpassword) {
			this.userpassword = userpassword;
		}
		public String getWelcomemsg() {
			return welcomemsg;
		}
		public void setWelcomemsg(String welcomemsg) {
			this.welcomemsg = welcomemsg;
		}
		public Integer getWriteaccess() {
			return writeaccess;
		}
		public void setWriteaccess(Integer writeaccess) {
			this.writeaccess = writeaccess;
		}
		@Override
		public String toString() {
			return "UserLogin [email=" + email + ", access_dt=" + access_dt + ", access_feeder=" + access_feeder
					+ ", access_owner=" + access_owner + ", access_subdivision=" + access_subdivision
					+ ", access_substation=" + access_substation + ", createddate=" + createddate + ", designation="
					+ designation + ", lastlogin=" + lastlogin + ", roleid=" + roleid + ", username=" + username
					+ ", userpassword=" + userpassword + ", welcomemsg=" + welcomemsg + ", writeaccess=" + writeaccess
					+ "]";
		}
		
		

}
