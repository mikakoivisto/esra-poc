/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.tools.esra.shell;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tools.ant.launch.Launcher;
import org.apache.tools.ant.taskdefs.Expand;

/**
 * @author Mika Koivisto
 */
public class EsraShell {

	public EsraShell() {
		String homeDirPath = System.getenv("ESRA_HOME");

		if (homeDirPath == null) {
			throw new RuntimeException("Unable to resolve ESRA_HOME");
		}

		_homeDir = new File(homeDirPath);

		if (!_homeDir.exists()) {
			_homeDir.mkdirs();
		}

		_downloadsDir = new File(_homeDir, ".downloads");

		if (!_downloadsDir.exists()) {
			_downloadsDir.mkdirs();
		}

		_pluginsSdkDir = new File(_homeDir, ".sdk");

		if (!_pluginsSdkDir.exists()) {
			_pluginsSdkDir.mkdir();
		}

		_httpClient = new DefaultHttpClient();
	}

	public void process(String[] args) throws Exception {
		if (args.length == 0) {
			usage();
		}

		String command = args[0];

		if (command.equals("login")) {
			login(args);
		}
		else if (command.equals("update")) {
			update();
		}
		else if (command.equals("install-sdk")) {
			installSdk();
		}
		else {
			runAnt(args);
		}
	}

	public static void main(String[] args) throws Exception {
		EsraShell esraShell = new EsraShell();

		esraShell.process(args);
	}

	protected void installSdk() {
		String sdkUrl = "http://downloads.sourceforge.net/project/lportal/Liferay%20Portal/6.1.1%20GA2/liferay-plugins-sdk-6.1.1-ce-ga2-20121004092655026.zip?use_mirror=iweb";

		HttpGet httpGet = new HttpGet(sdkUrl);

		try {
			//System.out.println("Making request... ");

			HttpResponse httpResponse = _httpClient.execute(httpGet);

			HttpEntity httpEntity = httpResponse.getEntity();

			//System.out.println("Get content ...");
			InputStream is = httpEntity.getContent();

			OutputStream os = new FileOutputStream(new File(_downloadsDir, "plugins-sdk.zip"));

			//System.out.println("Downloading " + httpEntity.getContentLength() + " bytes");

			IOUtils.copy(is, os);

			Expand expand = new Expand();

			expand.setSrc(new File(_downloadsDir, "plugins-sdk.zip"));
			expand.setDest(_pluginsSdkDir);

			expand.execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			httpGet.releaseConnection();
		}
	}

	protected void login(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("username", true, "username xxx");
		options.addOption("passwd", true, "password xxx");

		CommandLine cmd = _parser.parse(options, args);

		String username = null;
		String password = null;

		if (cmd.hasOption("username")) {
			username = cmd.getOptionValue("username");
		}

		if (cmd.hasOption("passwd")) {
			password = cmd.getOptionValue("passwd");
		}

		Console cons = System.console();

		if ((cons != null) && ((username == null) || (password == null))) {
			if (username == null) {
				username = cons.readLine("%s", "Username: ");
			}

			if (password == null) {
				char[] passwd = cons.readPassword("%s", "Password: ");

				password = new String(passwd);
			}
		}

		System.out.println(
			"Login with username '" + username + "' and password '" +
				password + "'");
	}

	protected void runAnt(String[] args) {
		Launcher.main(args);
	}

	protected void update() {
		File downloadsDir = new File(_homeDir, ".downloads");

		if (!downloadsDir.exists()) {
			downloadsDir.mkdirs();
		}

		// Check if update exists

		// Download file
		System.out.println("Downloading update...");

		// Extract upgrade
		System.out.println("Extracting update...");

		//System.out.println("Run " + new File(_homeDir, "upgrade/upgrade.sh").getAbsolutePath() +" to complete upgrade.");
	}

	protected void usage() {
		System.out.println("lfr <commond> [options]");
		System.out.println("login [-username <username>] [-passwd <password>]");

		System.exit(1);
	}

	private File _downloadsDir;
	private File _homeDir;
	private HttpClient _httpClient;
	private CommandLineParser _parser = new GnuParser();
	private File _pluginsSdkDir;

}
