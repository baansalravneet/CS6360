package com.librarysystem.app;

import com.librarysystem.db.Database;
import com.librarysystem.gui.GUI;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LibrarySystemApplication {

	public static void main(String[] args) {
		// initialize GUI
		new SpringApplicationBuilder(GUI.class)
				.headless(false)
				.web(WebApplicationType.NONE)
				.run(args);

		// initialize database
		new SpringApplicationBuilder(Database.class)
				.run(args);

	}

}
