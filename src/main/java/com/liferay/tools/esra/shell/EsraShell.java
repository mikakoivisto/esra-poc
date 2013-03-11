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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;

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

		_pluginsSdkDir = new File(_homeDir, ".sdk");

		if (!_pluginsSdkDir.exists()) {
			_pluginsSdkDir.mkdir();
		}

	}

	public void process(String[] args) throws Exception {
		if (args.length == 0) {
			usage();
		}

		String command = args[0];

		if (command.equals("login")) {
			login(args);
		}
	}

	public static void main(String[] args) throws Exception {
		EsraShell esraShell = new EsraShell();

		esraShell.process(args);
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

	protected void usage() {
		System.out.println("lfr <commond> [options]");
		System.out.println("login [-username <username>] [-passwd <password>]");

		System.exit(1);
	}

	private File _homeDir;
	private CommandLineParser _parser = new GnuParser();
	private File _pluginsSdkDir;

}
