package de.unistuttgart.ipvs.pmp.resourcegroups.email;

interface IEmailOperations {
	void sendEmail(String to, String subject, String body);
}