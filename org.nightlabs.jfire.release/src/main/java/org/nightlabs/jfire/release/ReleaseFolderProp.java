package org.nightlabs.jfire.release;

import java.io.File;

public class ReleaseFolderProp {

	private String path;
	private String artifactIdPrefix;
	private String parentArtifactIdPrefix;
	private String parentsParentArtifactIdPrefix;
	
	private File rootDirFile;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getArtifactIdPrefix() {
		return artifactIdPrefix;
	}
	public void setArtifactIdPrefix(String artifactIdPrefix) {
		this.artifactIdPrefix = artifactIdPrefix;
	}
	public String getParentArtifactIdPrefix() {
		return parentArtifactIdPrefix;
	}
	public void setParentArtifactIdPrefix(String parentArtifactIdPrefix) {
		this.parentArtifactIdPrefix = parentArtifactIdPrefix;
	}
	public String getParentsParentArtifactIdPrefix() {
		return parentsParentArtifactIdPrefix;
	}
	public void setParentsParentArtifactIdPrefix(String parentsParentArtifactId) {
		this.parentsParentArtifactIdPrefix = parentsParentArtifactId;
	}
	
	public File getRootDirFile() {
		return rootDirFile;
	}
	public void setRootDirFile(File rootDirFile) {
		this.rootDirFile = rootDirFile;
	}
	
	
}
