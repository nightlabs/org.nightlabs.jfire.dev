package org.nightlabs.jfire.release;

import java.util.HashMap;
import java.util.Map;

public class ReleaseProps {

	private String newMavenVersion;
	private String copyrightURL;
	private String copyrightNotice;
	private String licenceURL;

	
	
	private String newOsgiVersionWithoutSuffix;
	private String newOsgiVersionWithQualifier;
	private String newOsgiVersionWithSnapshot;
	
	private Map<String, String> properties;
	
	private Map<String, String> replaceProperties = new HashMap<String, String>();
	
	
	private ReleaseFolderProp[] folders;
	
	public ReleaseFolderProp[] getFolders() {
		return folders;
	}
	
	public String getNewMavenVersion() {
		return newMavenVersion;
	}

	public void setNewMavenVersion(String newMavenVersion) {
		this.newMavenVersion = newMavenVersion;
	}

	public String getNewOsgiVersionWithoutSuffix() {
		return newOsgiVersionWithoutSuffix;
	}

	public void setNewOsgiVersionWithoutSuffix(String newOsgiVersionWithoutSuffix) {
		this.newOsgiVersionWithoutSuffix = newOsgiVersionWithoutSuffix;
	}

	public String getNewOsgiVersionWithQualifier() {
		return newOsgiVersionWithQualifier;
	}

	public void setNewOsgiVersionWithQualifier(String newOsgiVersionWithQualifier) {
		this.newOsgiVersionWithQualifier = newOsgiVersionWithQualifier;
	}

	public String getNewOsgiVersionWithSnapshot() {
		return newOsgiVersionWithSnapshot;
	}

	public void setNewOsgiVersionWithSnapshot(String newOsgiVersionWithSnapshot) {
		this.newOsgiVersionWithSnapshot = newOsgiVersionWithSnapshot;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getCopyrightURL() {
		return copyrightURL;
	}

	public void setCopyrightURL(String copyrightURL) {
		this.copyrightURL = copyrightURL;
	}

	public String getCopyrightNotice() {
		return copyrightNotice;
	}

	public void setCopyrightNotice(String copyrightNotice) {
		this.copyrightNotice = copyrightNotice;
	}

	public String getLicenceURL() {
		return licenceURL;
	}

	public void setLicenceURL(String licenceURL) {
		this.licenceURL = licenceURL;
	}
	
	public void buildEmptyFolders(int i) {
		folders = new ReleaseFolderProp[i];
		for (int j = 0; j < i; j++) {
			folders[j] = new ReleaseFolderProp();
		}
	}

	public Map<String, String> getReplaceProperties() {
		return replaceProperties;
	}
	
	public void setReplaceProperties(Map<String, String> replaceProperties) {
		this.replaceProperties = replaceProperties;
	}
	
}
