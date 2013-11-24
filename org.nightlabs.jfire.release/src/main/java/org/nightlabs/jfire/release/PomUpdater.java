package org.nightlabs.jfire.release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

class PomUpdater {

	private static final Logger logger = LoggerFactory.getLogger(PomUpdater.class);
	
	private File pomFile;
	private ReleaseProps releaseProps;
	private ReleaseFolderProp folderProps;

	private Document document;
	private Writer writer;
	private StringBuilder buf = new StringBuilder();

	public PomUpdater(File pomFile) {
		this.pomFile = pomFile;
	}

	public void update() throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		document = dBuilder.parse(pomFile);

		if (
				(folderProps.getArtifactIdPrefix() != null && !getArtifactId().startsWith(folderProps.getArtifactIdPrefix())) 
				|| 
				(
						getParentArtifactId() == null
						&&
						(folderProps.getParentArtifactIdPrefix() != null && !getParentArtifactId().startsWith(folderProps.getParentArtifactIdPrefix()))
						&&
						(folderProps.getParentsParentArtifactIdPrefix() != null && !getParentArtifactId().startsWith(folderProps.getParentsParentArtifactIdPrefix()))
				)
			)
			return;

		boolean replaceFile = false;

		LinkedList<String> tagStack = new LinkedList<String>();

		File tmpFile = new File(pomFile.getParentFile(), pomFile.getName() + ".tmp");
		OutputStream out = null;
		InputStream in = new FileInputStream(pomFile);
		try {
			out = new FileOutputStream(tmpFile);
			writer = new OutputStreamWriter(out, "UTF-8");
			InputStreamReader reader = new InputStreamReader(in, "UTF-8");
			StreamTokenizer st = new StreamTokenizer(reader);
			st.resetSyntax();
			st.wordChars('A', 'Z');
			st.wordChars('a', 'z');
			st.wordChars('0', '9');
			st.ordinaryChar('-');
			st.ordinaryChar('<');
			st.ordinaryChar('>');
			st.ordinaryChar('/');

			StringBuilder tagSB = new StringBuilder();
			int[] lastTtype = new int[4];
			boolean isInComment = false;
			boolean isInTag = false;
			boolean isInEndTag = false;
			while (StreamTokenizer.TT_EOF != st.nextToken()) {
				if (st.sval != null) {
					buf.append(st.sval);

					if (isInTag)
						tagSB.append(st.sval);
				}
				else if (st.ttype >= 0) {

					if (isInComment && st.ttype == '>') {
						flushBuf();
						if (lastTtype[0] == '-' && lastTtype[1] == '-')
							isInComment = false;
					}
					else if (!isInComment) {
						if (st.ttype == '<') {
							isInTag = true;
							tagSB.setLength(0);

							if (buf.length() > 0) {
								if ( tagStack.size() == 2
										&& tagStack.get(0).equals("project")
										&& tagStack.get(1).equals("version")
										&& getArtifactId().startsWith(folderProps.getArtifactIdPrefix())) {
									buf.setLength(0);
									buf.append(releaseProps.getNewMavenVersion());
									replaceFile = true;
								}
								if ( tagStack.size() == 3
										&& tagStack.get(0).equals("project")
										&& tagStack.get(1).equals("parent")
										&& tagStack.get(2).equals("version")
										&& 
										  (
												  (folderProps.getParentArtifactIdPrefix() != null && getParentArtifactId().startsWith(folderProps.getParentArtifactIdPrefix())) ||
												  (folderProps.getParentsParentArtifactIdPrefix() != null && getParentArtifactId().startsWith(folderProps.getParentsParentArtifactIdPrefix()))
										   )
									) {
									buf.setLength(0);
									buf.append(releaseProps.getNewMavenVersion());
									replaceFile = true;
								}
								if (tagStack.size() == 3
										&& tagStack.get(0).equals("project")
										&& tagStack.get(1).equals("properties")
										) {
									replaceFile |= checkPropertyReplace(tagStack);
								}
							}

						}
						else if (isInTag && st.ttype == '/' && lastTtype[0] == '<') {
							isInEndTag = true;
						}
						else if (isInTag && st.ttype == '>') {
							flushBuf();
							if (lastTtype[0] != '/') { // ignore empty tags (having no end-tag)
								String tag = tagSB.toString();
								tagSB.setLength(0);
								String tagName = tag.trim();

								int idx = tagName.indexOf(' ');
								if (idx >= 0)
									tagName = tagName.substring(0, idx);

								idx = tagName.indexOf('\t');
								if (idx >= 0)
									tagName = tagName.substring(0, idx);

								idx = tagName.indexOf('\n');
								if (idx >= 0)
									tagName = tagName.substring(0, idx);

								if (!tagName.isEmpty() && !tagName.startsWith("?")) {
									if (isInEndTag) {
										if (tagName.equals(tagStack.peekLast()))
											tagStack.pollLast();
										else
											throw new IllegalStateException("Document \"" + pomFile.getAbsolutePath() + "\" is not well-formed! End-tag \"" + tagName + "\" without corresponding begin-tag!");
									}
									else {
										tagStack.addLast(tagName);
									}
								}
							}
							isInEndTag = false;
							isInTag = false;
						}
						else if (st.ttype == '-') {
							if (lastTtype[0] == '-' && lastTtype[1] == '!' && lastTtype[2] == '<')
								isInComment = true;

							if (isInTag)
								tagSB.append((char) st.ttype);
						}
						else if (isInTag) {
							tagSB.append((char) st.ttype);
						}
					}
					buf.append((char) st.ttype);
					if (st.ttype == '>')
						flushBuf();
				}

				for (int i = lastTtype.length - 2; i >= 0; i--) {
					lastTtype[i + 1] = lastTtype[i];
				}
				lastTtype[0] = st.ttype;
			}
			flushBuf();

			reader.close();
			writer.close();

			if (replaceFile) {
				logger.info("Replacing changed file " + pomFile);
				pomFile.delete();
				if (!tmpFile.renameTo(pomFile))
					throw new IOException("Failed to rename '" + tmpFile.getAbsolutePath() + "' to '" + pomFile.getAbsolutePath() + "'!!!");
			}

		} finally {
			in.close();
			if (out != null)
				out.close();

			tmpFile.delete();
		}
	}
	

	private boolean checkPropertyReplace(LinkedList<String> tagStack) {
		String propName = tagStack.getLast();
		String newValue = releaseProps.getReplaceProperties().get(propName);
		if (newValue != null && !newValue.isEmpty()) {
			buf.setLength(0);
			buf.append(newValue);
			return true;
		}
		return false;
	}

	private void flushBuf() throws IOException {
		writer.write(buf.toString());
		buf.setLength(0);
	}

	public PomUpdater setReleaseProps(ReleaseProps releaseProps) {
		this.releaseProps = releaseProps;
		return this;
	}
	
	public PomUpdater setFolderProps(ReleaseFolderProp folderProps) {
		this.folderProps = folderProps;
		return this;
	}

	private String artifactId;
	private String parentArtifactId;

	protected String getArtifactId() {
		if (artifactId == null) {
			NodeList nodeList = document.getElementsByTagName("artifactId");
			for (int a = 0; a < nodeList.getLength(); ++a) {
				Node node = nodeList.item(a);
				if (node.getParentNode() != null && "project".equals(node.getParentNode().getNodeName()) && node.getParentNode().getParentNode() == document) {
					NodeList childNodes = node.getChildNodes();
					for (int b = 0; b < childNodes.getLength(); ++b) {
						Node child = childNodes.item(b);
						if (child instanceof Text) {
							String wholeText = ((Text)child).getWholeText();
							artifactId = wholeText;
							return artifactId;
						}
					}
				}
			}
			artifactId = "";
		}
		return artifactId;
	}

	protected String getParentArtifactId() {
		if (parentArtifactId == null) {
			NodeList nodeList = document.getElementsByTagName("artifactId");
			for (int a = 0; a < nodeList.getLength(); ++a) {
				Node node = nodeList.item(a);
				if (node.getParentNode() != null && "parent".equals(node.getParentNode().getNodeName()) && node.getParentNode().getParentNode() != null && "project".equals(node.getParentNode().getParentNode().getNodeName()) && node.getParentNode().getParentNode().getParentNode()  == document) {
					NodeList childNodes = node.getChildNodes();
					for (int b = 0; b < childNodes.getLength(); ++b) {
						Node child = childNodes.item(b);
						if (child instanceof Text) {
							String wholeText = ((Text)child).getWholeText();
							parentArtifactId = wholeText;
							return parentArtifactId;
						}
					}
				}
			}
			parentArtifactId = "";
		}
		return parentArtifactId;
	}
}
