package oing.webapp.android.sdkliteserver.tools.autoadd.executor;

import oing.webapp.android.sdkliteserver.tools.autoadd.command.Command;

import java.util.EventListener;

public interface CommandExecutionListener extends EventListener {
	void onPrepare();

	void onPreExecute(Command command);

	void onPostExecute(Command command);

	void onFinalize();
}
