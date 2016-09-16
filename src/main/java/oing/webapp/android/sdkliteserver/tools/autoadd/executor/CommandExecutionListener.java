package oing.webapp.android.sdkliteserver.tools.autoadd.executor;

import oing.webapp.android.sdkliteserver.tools.autoadd.command.Command;

import java.util.EventListener;

public interface CommandExecutionListener extends EventListener {
	void onPrepare();

	void onPreExecute(int totalTasks, int currentIndex, Command command);

	void onPostExecute(int totalTasks, int currentIndex, Command command);

	void onFinalize();
}
